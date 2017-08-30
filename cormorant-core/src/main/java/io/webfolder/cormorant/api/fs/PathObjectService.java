/**
 * cormorant - Object Storage Server
 * Copyright © 2017 WebFolder OÜ (support@webfolder.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.webfolder.cormorant.api.fs;

import static io.webfolder.cormorant.api.Json.read;
import static io.webfolder.cormorant.api.metadata.MetadataServiceFactory.MANIFEST_EXTENSION;
import static io.webfolder.otmpfile.SecureTempFile.SUPPORT_O_TMPFILE;
import static java.lang.String.format;
import static java.nio.channels.Channels.newInputStream;
import static java.nio.channels.Channels.newReader;
import static java.nio.channels.FileChannel.open;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.move;
import static java.nio.file.Files.readAttributes;
import static java.nio.file.Files.size;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.security.MessageDigest.getInstance;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Locale.ENGLISH;
import static java.util.concurrent.TimeUnit.DAYS;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static net.jodah.expiringmap.ExpirationPolicy.CREATED;
import static net.jodah.expiringmap.ExpiringMap.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.math.BigInteger;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import io.webfolder.cormorant.api.Json;
import io.webfolder.cormorant.api.Util;
import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.model.Segment;
import io.webfolder.cormorant.api.service.ContainerService;
import io.webfolder.cormorant.api.service.MetadataService;
import io.webfolder.cormorant.api.service.ObjectService;
import io.webfolder.otmpfile.SecureTempFile;

public class PathObjectService implements ObjectService<Path>, Util {

    private static final char    FORWARD_SLASH     = '\\';

    private static final char    BACKWARD_SLASH    = '/';

    private static final String  X_OBJECT_MANIFEST = "X-Object-Manifest";

    private static final String  DEFAULT_MIME_TYPE = "application/octet-stream";

    private static final String  DIRECTORY         = "application/directory";

    private static final String  MD5_CHECKSUM      = "MD5";

    private static final int     BUFFER_SIZE       = 1024 * 256;

    private final Map<String, String> mimeTypes    = loadMimeTypes();

    private final Map<Object, String> cache;

    private final ContainerService<Path> containerService;

    private final MetadataService systemMetadataService;

    private final boolean useSecureTempFile = SUPPORT_O_TMPFILE;

    public PathObjectService(
                final ContainerService<Path> containerService,
                final MetadataService        systemMetadataService) {
        this.containerService      = containerService;
        this.systemMetadataService = systemMetadataService;
        cache = builder()
                .expirationPolicy(CREATED)
                .expiration(1, DAYS)
                .maxSize(100_000)
            .build();
    }

    @Override
    public TempObject<Path> createTempObject(String accontName, Path container) throws IOException, SQLException {
        if (useSecureTempFile) {
            return new SecureTempObject(new SecureTempFile());
        } else {
            return new PathTempObject(createTempFile("cormorant", ".new"));
        }
    }

    @Override
    public void deleteTempObject(String accountName, Path container, Path tempObject) throws IOException {
        Files.delete(tempObject);
    }

    @Override
    public Path moveTempObject(
                final String accountName,
                final Path   tempObject,
                final Path   targetContainer,
                final String targetObjectPath) throws IOException, SQLException {
        final Path target       = targetContainer.resolve(targetObjectPath).toAbsolutePath().normalize();
        final Path targetParent = target.getParent();
        if ( ! exists(targetParent, NOFOLLOW_LINKS) ) {
            createDirectories(targetParent);
        }
        return move(tempObject, target, ATOMIC_MOVE);
    }

    @Override
    public WritableByteChannel getWritableChannel(Path path) throws IOException, SQLException {
        return open(path, WRITE, CREATE, NOFOLLOW_LINKS);
    }

    @Override
    public ReadableByteChannel getReadableChannel(Path path) throws IOException, SQLException {
        return open(path, READ, NOFOLLOW_LINKS);
    }

    @Override
    public Path getObject(final String accountName, final String containerName, final String objectPath) throws IOException, SQLException {
        final boolean exist = containerService.contains(accountName, containerName);
        if ( ! exist ) {
            return null;
        }
        final Path   container          = containerService.getContainer(accountName, containerName);
        final String path               = removeLeadingSlash(objectPath);
        final Path   objectAbsolutePath = container.resolve(path).toAbsolutePath().normalize();
        if ( ! objectAbsolutePath.startsWith(container) ) {
            return null;
        }
        final Path manifest = objectAbsolutePath.getParent().resolve(objectAbsolutePath.getFileName() + MANIFEST_EXTENSION);
        if (exists(manifest, NOFOLLOW_LINKS) && isReadable(manifest)) {
            return manifest;
        }
        if (Files.isDirectory(objectAbsolutePath, NOFOLLOW_LINKS)) {
            return null;
        }
        if (Files.exists(objectAbsolutePath)) {
            return objectAbsolutePath;
        } else {
            return null;
        }
    }

    @Override
    public long getSize(Path object) throws IOException, SQLException {
        return size(object);
    }

    @Override
    public void delete(final Path container, final Path object) throws IOException, SQLException {
        Files.delete(object);
    }

    @Override
    public String relativize(final Path container, final Path object) throws IOException, SQLException {
        return container.relativize(object).toString().replace(FORWARD_SLASH, BACKWARD_SLASH);
    }

    @Override
    public String toPath(Path container, Path object) throws IOException, SQLException {
        return container.getParent().relativize(object).toString().replace(FORWARD_SLASH, BACKWARD_SLASH);
    }

    @Override
    public String getNamespace(final Path container, final Path object) throws IOException, SQLException {
        final String namespace = relativize(container, object);
        return container.getFileName().toString() + BACKWARD_SLASH + namespace.replace(FORWARD_SLASH, BACKWARD_SLASH);
    }

    @Override
    public long getLastModified(Path object) throws IOException, SQLException {
        return getLastModifiedTime(object, NOFOLLOW_LINKS).toMillis();
    }

    @Override
    public Path createDirectory(String accountName, Path container, String objectPath) throws IOException, SQLException {
        if (objectPath == null || objectPath.trim().isEmpty()) {
            throw new CormorantException("Invalid directory name.");
        }
        final Path directory = container.resolve(objectPath);
        if ( ! directory.startsWith(container) ) {
            throw new CormorantException("Invalid directory path.");
        }
        return createDirectories(directory);
    }

    @Override
    public boolean isDirectory(final Path container, final Path object) throws IOException, SQLException {
        final Path dir = container.resolve(object);
        return dir.startsWith(container) && Files.isDirectory(dir) ? true : false;
    }


    @Override
    public Path getDirectory(Path container, String directoryPath) throws IOException, SQLException {
        final Path dir = container.resolve(directoryPath);
        if (isDirectory(container, dir)) {
            return dir;
        }
        return null;
    }

    @Override
    public boolean isEmptyDirectory(final Path container, final Path object) throws IOException, SQLException {
        final Path dir = container.resolve(object);
        if (Files.isDirectory(dir)) {
            FileSizeVisitor visitor = new FileSizeVisitor(2, true);
            Files.walkFileTree(dir, visitor);
            return visitor.getObjectCount() == 1 ? true : false;
        }
        return false;
    }

    @Override
    public Path copyObject(final String destinationAccount   ,
                           final Path   destinationContainer ,
                           final String destinationObjectPath,
                           final String sourceAccount        ,
                           final Path   sourceContainer      ,
                           final Path   sourceObject         ,
                           final String multipartManifest) throws IOException, SQLException {
        final Path targetObject = destinationContainer.resolve(destinationObjectPath);
        final Path targetParent = targetObject.getParent();
        if ( ! Files.exists(targetParent, NOFOLLOW_LINKS) ) {
            Files.createDirectories(targetParent);
        }
        
        final List<Path> dynamicLargeObjects = listDynamicLargeObject(sourceContainer, sourceObject);
        final boolean    dynamicLargeObject  = ! dynamicLargeObjects.isEmpty();
        final boolean    staticLargeObject   = isStaticLargeObject(sourceObject);

        if (sourceObject.equals(targetObject)) {
            return targetObject;
        } else if (dynamicLargeObject) {
            Vector<InputStream> streams = new Vector<>();
            for (Path next : dynamicLargeObjects) {
                InputStream is = Files.newInputStream(next, READ);
                streams.add(is);
            }
            try (SequenceInputStream sequenceInputStream = new SequenceInputStream(streams.elements())) {
                Files.copy(sequenceInputStream, targetObject, REPLACE_EXISTING);
                return targetObject;
            }
        } else if ( staticLargeObject && ! "get".equals(multipartManifest) ) {
            List<Segment<Path>> segments = listStaticLargeObject(sourceAccount, sourceObject);
            Vector<InputStream> streams = new Vector<>();
            for (Segment<Path> next : segments) {
                InputStream is = Files.newInputStream(next.getObject(), READ);
                streams.add(is);
            }
            try (SequenceInputStream sequenceInputStream = new SequenceInputStream(streams.elements())) {
                Files.copy(sequenceInputStream, targetObject, REPLACE_EXISTING);
                return targetObject;
            }
        } else {
            return Files.copy(sourceObject, targetObject, REPLACE_EXISTING);
        }
    }

    @Override
    public boolean isValidPath(Path container, String objectPath) throws IOException, SQLException {
        try {
            return container.resolve(objectPath).startsWith(container);
        } catch (InvalidPathException e) {
            return false;
        }
    }

    @Override
    public boolean isStaticLargeObject(final Path object) throws IOException, SQLException {
        return object != null &&
                object.getFileName().toString().endsWith(MANIFEST_EXTENSION) ? true : false;
    }

    @Override
    public long getCreationTime(Path object) throws IOException, SQLException {
        BasicFileAttributeView attributeView = Files.getFileAttributeView(object, BasicFileAttributeView.class, NOFOLLOW_LINKS);
        BasicFileAttributes attributes = attributeView.readAttributes();
        return attributes.creationTime().toMillis();
    }

    @Override
    public List<Path> listDynamicLargeObject(Path container, Path object) throws IOException, SQLException {
        if (isStaticLargeObject(object)) {
            return emptyList();
        }
        if ( Files.isDirectory(object) ) {
            DyanmicLargeObjectVisitor visitor = new DyanmicLargeObjectVisitor(null);
            Files.walkFileTree(object, visitor);
            return visitor.getFiles();
        } else if (Files.isRegularFile(object, NOFOLLOW_LINKS)) {
            final String namespace = getNamespace(container, object);
            final String objectManifest = systemMetadataService.get(namespace, X_OBJECT_MANIFEST);
            if (objectManifest != null) {
                final int start = objectManifest.lastIndexOf(BACKWARD_SLASH);
                if (start >= 0) {
                    DyanmicLargeObjectVisitor visitor = new DyanmicLargeObjectVisitor(null);
                    final Path manifestContainer = containerService.getContainer(null,
                                                        objectManifest.substring(0, objectManifest.indexOf(BACKWARD_SLASH)));
                    if ( manifestContainer != null ) {
                        Path manifestDir = manifestContainer.resolve(objectManifest.substring(objectManifest.indexOf(BACKWARD_SLASH) + 1, objectManifest.length()));
                        if ( exist(container, object) && ! Files.isDirectory(object) && getSize(object) == 0 ) {
                            manifestDir = object.getParent();
                            final String prefix = objectManifest.substring(start + 1, objectManifest.length());
                            visitor = new DyanmicLargeObjectVisitor(prefix);
                        }
                        if ( Files.isDirectory(manifestDir)) {
                            Files.walkFileTree(manifestDir, visitor);
                            return visitor.getFiles();
                        }
                    }
                }
            }
        }
        return emptyList();
    }

    @SuppressWarnings("unchecked")
    public List<Segment<Path>> listStaticLargeObject(final String accountName, final Path manifestObject) throws IOException, SQLException {
        final List<Segment<Path>> segments = new ArrayList<>();
        try (final BufferedReader reader = new BufferedReader(newReader(getReadableChannel(manifestObject), UTF_8.name()))) {
            final StringBuilder builder = new StringBuilder();
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                builder.append(line);
            }
            final Json json = read(builder.toString());
            final List<Object> list = json.asList();
            for (Object next : list) {
                final Map<String, Object> map = (Map<String, Object>) next;
                final String path = removeLeadingSlash(map.get("path").toString()).replace(BACKWARD_SLASH, FORWARD_SLASH);
                if ( path != null && ! path.trim().isEmpty() ) {
                    final String containerName = path.indexOf(FORWARD_SLASH) > 0 ? path.substring(0, path.indexOf(FORWARD_SLASH)) : null;
                    final String objectPath = path.substring(path.indexOf(FORWARD_SLASH) + 1, path.length());
                    Path container = containerService.getContainer(accountName, containerName);
                    if ( container != null ) {
                        final Path object = getObject(accountName, containerName, objectPath);
                        if ( object != null ) {
                            final long size = getSize(object);
                            final String contentType = getMimeType(container, object, true);
                            Segment<Path> segment = new Segment<>(contentType, size, object);
                            segments.add(segment);
                        }
                    }
                }
            }
        }
        return segments;
    }

    @Override
    public long getDyanmicObjectSize(Path container, Path object) throws IOException, SQLException {
        long size = 0L;
        for (Path next : listDynamicLargeObject(container, object)) {
            size += getSize(next);
        }
        return size;
    }

    @Override
    public String getMimeType(
                        final Path    container,
                        final Path    object,
                        final boolean autoDetect) throws IOException, SQLException {
        final boolean isDir = Files.isDirectory(object);
        if (isDir) {
            return DIRECTORY;
        } else if (autoDetect) {
            final String fileName  = object.getFileName().toString();
            final int    start     = fileName.lastIndexOf('.');
            final String extension = start > 0 ? fileName
                                                    .substring(start + 1, fileName.length())
                                                    .toLowerCase(ENGLISH) : null;
            final String mimeType = mimeTypes.get(extension);
            return mimeType != null ? mimeType : DEFAULT_MIME_TYPE;
        } else {
            final String namespace = getNamespace(container, object);
            String mimeType = systemMetadataService.get(namespace, CONTENT_TYPE);
            return mimeType != null ? mimeType : DEFAULT_MIME_TYPE;
        }
    }

    @Override
    public boolean exist(Path container, Path object) throws IOException, SQLException {
        return Files.exists(object);
    }

    @Override
    public String calculateChecksum(List<Path> objects) throws IOException, SQLException {
        StringBuilder fileId = new StringBuilder();
        for (final Path next : objects) {
            BasicFileAttributes attributes = readAttributes(next, BasicFileAttributes.class, NOFOLLOW_LINKS);
            String pathId = next.toString();
            Object fileKey = attributes.fileKey();
            if ( fileKey != null ) {
                fileId.append(fileKey.toString());
            } else {
                fileId.append(next.toString());
            }
            fileId.append(pathId);
            fileId.append(attributes.size());
            fileId.append(attributes.lastModifiedTime().toMillis());
        }
        String cachedHash = cache.get(fileId.toString());
        if ( cachedHash != null ) {
            return cachedHash;
        }
        MessageDigest md;
        try {
            md = getInstance(MD5_CHECKSUM);
        } catch (NoSuchAlgorithmException e) {
            throw new CormorantException(e);
        }
        fileId = new StringBuilder();
        for (final Path next : objects) {
            BasicFileAttributes attributes = readAttributes(next, BasicFileAttributes.class, NOFOLLOW_LINKS);
            String pathId = next.toString();
            Object fileKey = attributes.fileKey();
            if ( fileKey != null ) {
                fileId.append(fileKey.toString());
            } else {
                fileId.append(next.toString());
            }
            fileId.append(pathId);
            fileId.append(attributes.size());
            fileId.append(attributes.lastModifiedTime().toMillis());

            try (InputStream is = newInputStream(open(next, NOFOLLOW_LINKS, READ))) {
                final byte[] buffer = new byte[BUFFER_SIZE];
                int read;
                while((read = is.read(buffer)) > 0) {
                    md.update(buffer, 0, read);
                }
            }
        }
        final byte[] hash = md.digest();
        final String hashHexValue = format("%032x", new BigInteger(1, hash));
        cache.put(fileId.toString(), hashHexValue);
        return hashHexValue;
    }

    protected Map<String, String> loadMimeTypes() {
        final Map<String, String> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(
                                            new InputStreamReader(
                                                    getClass()
                                                    .getResourceAsStream("/io/webfolder/cormorant/mime.types"), UTF_8))) {
            Iterator<String> iter = reader.lines().iterator();
            while (iter.hasNext()) {
                final String line = iter.next().trim();
                if (line.startsWith("#")) {
                    continue;
                }
                final int      start      = line.indexOf(" ");
                final String   mimeType   = line.substring(0, start).trim();
                final String[] extensions = line.substring(start + 1, line.length()).trim().split(" ");
                for (String extension : extensions) {
                    map.put(extension.trim().toLowerCase(ENGLISH), mimeType);
                }
            }
        } catch (IOException e) {
            throw new CormorantException("Unable to read mime types", e);
        }
        return unmodifiableMap(map);
    }
}
