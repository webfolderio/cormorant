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

import static java.nio.file.Files.*;
import static io.webfolder.cormorant.api.property.MetadataServiceFactory.MANIFEST_EXTENSION;
import static java.nio.channels.FileChannel.open;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.size;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Collections.emptyList;
import static java.util.regex.Pattern.compile;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.service.ContainerService;
import io.webfolder.cormorant.api.service.ObjectService;

public class PathObjectService implements ObjectService<Path> {

    private static final String  BACKSLASH     = "\\";

    private static final String  SLASH         = "/";

    private static final Pattern LEADING_SLASH = compile("^/+");

    private final ContainerService<Path> containerService;

    public PathObjectService(final ContainerService<Path> containerService) {
        this.containerService = containerService;
    }

    @Override
    public Path createTempObject(String accontName, Path container) {
        try {
            return createTempFile("cormorant", ".new");
        } catch (IOException e) {
            throw new CormorantException("Unable to create temp file.", e);
        }
    }

    @Override
    public void deleteTempObject(String accountName, Path container, Path tempObject) {
        try {
            Files.delete(tempObject);
        } catch (IOException e) {
            throw new CormorantException("Unable to delete temp file [" + tempObject.toString() + "].", e);
        }
    }

    @Override
    public Path moveTempObject(
                final String accountName,
                final Path   tempObject,
                final Path   targetContainer,
                final String targetObjectPath) {
        try {
            final Path target       = targetContainer.resolve(targetObjectPath).toAbsolutePath().normalize();
            final Path targetParent = target.getParent();
            if ( ! exists(targetParent, NOFOLLOW_LINKS) ) {
                createDirectories(targetParent);
            }
            return move(tempObject, target, ATOMIC_MOVE);
        } catch (IOException e) {
            throw new CormorantException(e);
        }
    }

    @Override
    public WritableByteChannel getWritableChannel(Path path) {
        try {
            return open(path, WRITE, CREATE, NOFOLLOW_LINKS);
        } catch (IOException e) {
            throw new CormorantException(e);
        }
    }

    @Override
    public ReadableByteChannel getReadableChannel(Path path) {
        try {
            return open(path, READ, NOFOLLOW_LINKS);
        } catch (IOException e) {
            throw new CormorantException(e);
        }
    }

    @Override
    public Path getObject(final String accountName, final String containerName, final String objectPath) {
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
        if ( Files.isDirectory(objectAbsolutePath, NOFOLLOW_LINKS) ) {
            return null;
        }
        if ( ! exists(objectAbsolutePath, NOFOLLOW_LINKS) || ! isReadable(objectAbsolutePath) ) {
            Path manifest = objectAbsolutePath.getParent().resolve(objectAbsolutePath.getFileName() + MANIFEST_EXTENSION);
            if ( exists(manifest, NOFOLLOW_LINKS) && isReadable(manifest) ) {
                return manifest;
            }
            return null;
        }
        return objectAbsolutePath;
    }

    @Override
    public long getSize(Path object) {
        try {
            return size(object);
        } catch (IOException e) {
            throw new CormorantException(e);
        }
    }

    @Override
    public void delete(final Path container, final Path object) {
        try {
            Files.delete(object);
        } catch (IOException e) {
            throw new CormorantException("Unable to delete path [" + object.toString() + "].", e);
        }
    }

    @Override
    public String relativize(final Path container, final Path object) {
        return container.relativize(object).toString().replace(BACKSLASH, SLASH);
    }

    @Override
    public String toPath(Path container, Path object) {
        return container.getParent().relativize(object).toString().replace(BACKSLASH, SLASH);
    }

    @Override
    public String getNamespace(final Path container, final Path object) {
        final String namespace = relativize(container, object);
        return container.getFileName().toString() + SLASH + namespace.replace(BACKSLASH, SLASH);
    }

    @Override
    public long getLastModified(Path object) {
        try {
            return getLastModifiedTime(object, NOFOLLOW_LINKS).toMillis();
        } catch (IOException e) {
            throw new CormorantException(e);
        }
    }

    @Override
    public Path createDirectory(String accountName, Path container, String objectPath) {
        if (objectPath == null || objectPath.trim().isEmpty()) {
            throw new CormorantException("Invalid directory name.");
        }
        final Path directory = container.resolve(objectPath);
        if ( ! directory.startsWith(container) ) {
            throw new CormorantException("Invalid directory path.");
        }
        try {
            return createDirectories(directory);
        } catch (IOException e) {
            throw new CormorantException("Unable to create folder.", e);
        }
    }

    @Override
    public boolean isDirectory(final Path container, final Path object) {
        final Path dir = container.resolve(object);
        return dir.startsWith(container) && Files.isDirectory(dir) ? true : false;
    }


    @Override
    public Path getDirectory(Path container, String directoryPath) {
        final Path dir = container.resolve(directoryPath);
        if (isDirectory(container, dir)) {
            return dir;
        }
        return null;
    }

    @Override
    public boolean isEmptyDirectory(final Path container, final Path object) {
        final Path dir = container.resolve(object);
        if (Files.isDirectory(dir)) {
            FileSizeVisitor visitor = new FileSizeVisitor(2, true);
            try {
                Files.walkFileTree(dir, visitor);
            } catch (IOException e) {
                throw new CormorantException(e);
            }
            return visitor.getObjectCount() == 1 ? true : false;
        }
        return false;
    }

    @Override
    public Path copyObject(String account, Path sourceObject, Path targetContainer, String targetObjectPath) {
        final Path targetObject = targetContainer.resolve(targetObjectPath);
        final Path targetParent = targetObject.getParent();
        try {
            if ( ! Files.exists(targetParent, NOFOLLOW_LINKS) ) {
                Files.createDirectories(targetParent);
            }
            if (sourceObject.equals(targetObject)) {
                return targetObject;
            } else {
                Files.copy(sourceObject, targetObject, REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new CormorantException("Unable to copy object.", e);
        }
        return targetObject;
    }

    @Override
    public Path copyObject(String destinationAccount, Path destinationContainer, String destinationObjectPath,
            String sourceAccount, Path sourceContainer, Path sourceObject) {
        final Path targetObject = destinationContainer.resolve(destinationObjectPath);
        final Path targetParent = targetObject.getParent();
        try {
            if ( ! Files.exists(targetParent, NOFOLLOW_LINKS) ) {
                Files.createDirectories(targetParent);
            }
            if (sourceObject.equals(targetObject)) {
                return targetObject;
            } else {
                return Files.copy(sourceObject, targetObject, REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new CormorantException("Unable to copy object.", e);
        }
    }

    @Override
    public boolean isValidPath(Path container, String objectPath) {
        try {
            return container.resolve(objectPath).startsWith(container);
        } catch (InvalidPathException e) {
            return false;
        }
    }

    @Override
    public boolean isMultipartManifest(final Path object) {
        return object != null &&
                object.getFileName().toString().endsWith(MANIFEST_EXTENSION) ? true : false;
    }

    @Override
    public boolean isFile(Path object) {
        return true;
    }

    @Override
    public long getCreationTime(Path object) {
        BasicFileAttributeView attributeView = Files.getFileAttributeView(object, BasicFileAttributeView.class, NOFOLLOW_LINKS);
        try {
            BasicFileAttributes attributes = attributeView.readAttributes();
            return attributes.creationTime().toMillis();
        } catch (IOException e) {
            throw new CormorantException(e);
        }
    }

    @Override
    public List<Path> listDynamicLargeObject(Path object) {
        if ( ! Files.isDirectory(object) ) {
            return emptyList();
        }
        FileVisitor visitor = new FileVisitor();
        try {
            Files.walkFileTree(object, visitor);
        } catch (IOException e) {
            throw new CormorantException(e);
        }
        return visitor.getFiles();
    }

    @Override
    public long getDyanmicObjectSize(Path object) {
        if ( ! Files.isDirectory(object) ) {
            return 0L;
        }
        FileSizeVisitor visitor = new FileSizeVisitor(false);
        try {
            Files.walkFileTree(object, visitor);
        } catch (IOException e) {
            throw new CormorantException(e);
        }
        return visitor.getBytesUsed();
    }

    @Override
    public boolean exist(Path container, Path object) {
        return Files.exists(object);
    }

    protected String removeLeadingSlash(String path) {
        if (path == null) {
            return null;
        }
        String normalizedPath = path;
        if (normalizedPath.charAt(0) == SLASH.charAt(0)) {
            normalizedPath = path.substring(1, path.length());
        }
        if (normalizedPath.charAt(0) == SLASH.charAt(0)) {
            normalizedPath = LEADING_SLASH.matcher(normalizedPath).replaceAll("");
        }
        return normalizedPath;
    }

    @Override
    public void sortLexicographically(List<Path> objects) {
        Collections.sort(objects);
    }
}
