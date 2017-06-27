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

import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.nio.channels.Channels.newInputStream;
import static java.nio.channels.FileChannel.open;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.READ;
import static java.security.MessageDigest.getInstance;
import static java.util.Collections.unmodifiableMap;
import static java.util.Locale.ENGLISH;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.ETAG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.service.ChecksumService;
import io.webfolder.cormorant.api.service.MetadataService;
import io.webfolder.cormorant.api.service.ObjectService;

public class FileChecksumService implements ChecksumService<Path> {

    private static final String  DEFAULT_MIME_TYPE = "application/octet-stream";

    private static final String DIRECTORY          = "application/directory";

    private static final String CHECKSUM_ALGORITHM = "MD5";

    private static final int    BUFFER_SIZE        = 1024 * 64;

    private final Map<String, String> mimeTypes    = loadMimeTypes();

    private final MetadataService headerService;

    private ObjectService<Path> objectService;

    public FileChecksumService(final MetadataService headerService) {
        this.headerService = headerService;
    }

    @Override
    public String calculateChecksum(final Path object) {
        try (InputStream is = newInputStream(open(object, NOFOLLOW_LINKS, READ))) {
            final MessageDigest md     = getInstance(CHECKSUM_ALGORITHM);
            final byte[]        buffer = new byte[BUFFER_SIZE];
            int read;
            while((read = is.read(buffer)) > 0) {
                md.update(buffer, 0, read);
            }
            final byte[] hash     = md.digest();
            final String checksum = format("%032x", new BigInteger(1, hash));
            return checksum;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new CormorantException(e);
        }
    }

    @Override
    public String getMimeType(
                        final Path    container,
                        final Path    object,
                        final boolean autoDetect) {
        final boolean isDir = Files.isDirectory(object);
        if (isDir) {
            return DIRECTORY;
        } else {
            if (autoDetect) {
                final String fileName  = object.getFileName().toString();
                final int    start     = fileName.lastIndexOf('.');
                final String extension = start > 0 ? fileName
                                                        .substring(start + 1, fileName.length())
                                                        .toLowerCase(ENGLISH) : null;
                final String mimeType = mimeTypes.get(extension);
                return mimeType != null ? mimeType : DEFAULT_MIME_TYPE;
            } else {
                final String namespace = objectService.getNamespace(container, object);
                String mimeType = headerService.getProperty(namespace, CONTENT_TYPE);
                return mimeType != null ? mimeType : DEFAULT_MIME_TYPE;
            }
        }
    }

    @Override
    public String calculateChecksum(
                        final Path container,
                        final Path object) {
        final String  namespace         = objectService.getNamespace(container, object);
        final String  precalculatedETag = headerService.getProperty(namespace, ETAG);
        final String  contentLength     = headerService.getProperty(namespace, CONTENT_LENGTH);
        if ( precalculatedETag == null          ||
             precalculatedETag.trim().isEmpty() ||
             contentLength == null              ||
             contentLength.trim().isEmpty() ) {
            return calculateChecksum(object);
        } else {
            final long    expectedSize = parseLong(contentLength);
            final long    actualSize   = objectService.getSize(object);
            final boolean modified     = expectedSize != actualSize;
            if ( ! modified ) {
                return precalculatedETag;
            } else {
                // ----------------------------------------------------------------
                // Inconsistency
                // ----------------------------------------------------------------
                //
                // Is object modified externally? Yes, probably.
                //
                // Or is it due to broken upload? It might be.
                // Although object upload is atomic something might be goes wrong.
                //
                // ----------------------------------------------------------------
                throw new CormorantException("Invalid Object size: [" +
                                expectedSize + "]. Actual object size is: [" + actualSize + "]." +
                                "Please update the ETag and size with correct values.");
            }
        }
    }

    public void setObjectService(final ObjectService<Path> objectService) {
        this.objectService = objectService;
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

    @Override
    public String calculateChecksum(List<Path> objects) {
        MessageDigest md;
        try {
            md = getInstance(CHECKSUM_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new CormorantException(e);
        }
        for (final Path object : objects) {
            try (InputStream is = newInputStream(open(object, NOFOLLOW_LINKS, READ))) {
                final byte[] buffer = new byte[BUFFER_SIZE];
                int read;
                while((read = is.read(buffer)) > 0) {
                    md.update(buffer, 0, read);
                }
            } catch (IOException e) {
                throw new CormorantException(e);
            }
        }
        final byte[] hash = md.digest();
        final String checksum = format("%032x", new BigInteger(1, hash));
        return checksum;
    }
}
