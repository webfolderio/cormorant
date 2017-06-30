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

import static java.lang.String.format;
import static java.nio.channels.Channels.newInputStream;
import static java.nio.channels.FileChannel.open;
import static java.nio.file.Files.readAttributes;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.READ;
import static java.security.MessageDigest.getInstance;
import static java.util.concurrent.TimeUnit.DAYS;
import static net.jodah.expiringmap.ExpirationPolicy.CREATED;
import static net.jodah.expiringmap.ExpiringMap.builder;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.service.ChecksumService;

public class FileChecksumService implements ChecksumService<Path> {

    private static final String CHECKSUM_ALGORITHM = "MD5";

    private static final int    BUFFER_SIZE        = 1024 * 64;

    private final Map<Object, String> cache;

    public FileChecksumService() {
        cache = builder()
                    .expirationPolicy(CREATED)
                    .expiration(1, DAYS)
                    .maxSize(100_000)
                .build();
    }

    @Override
    public String calculateChecksum(
                        final Path container,
                        final Path object) throws IOException, SQLException {
        StringBuilder cacheKey = new StringBuilder();
        BasicFileAttributes attributes = readAttributes(object, BasicFileAttributes.class, NOFOLLOW_LINKS);
        String pathId = object.toString();
        Object fileKey = attributes.fileKey();
        if ( fileKey != null ) {
            cacheKey.append(fileKey.toString());
        } else {
            cacheKey.append(object.toString());
        }
        cacheKey.append(pathId);
        cacheKey.append(attributes.size());
        cacheKey.append(attributes.lastModifiedTime().toMillis());
        String hash = cache.get(cacheKey.toString());
        if ( hash != null ) {
            return hash;
        }
        hash = calculateChecksum(object);
        cache.put(cacheKey.toString(), hash);
        return hash;
    }

    @Override
    public String calculateChecksum(List<Path> objects) throws IOException {
        StringBuilder cacheKey = new StringBuilder();
        for (final Path next : objects) {
            BasicFileAttributes attributes = readAttributes(next, BasicFileAttributes.class, NOFOLLOW_LINKS);
            String pathId = next.toString();
            Object fileKey = attributes.fileKey();
            if ( fileKey != null ) {
                cacheKey.append(fileKey.toString());
            } else {
                cacheKey.append(next.toString());
            }
            cacheKey.append(pathId);
            cacheKey.append(attributes.size());
            cacheKey.append(attributes.lastModifiedTime().toMillis());
        }
        String hashedChecksum = cache.get(cacheKey.toString());
        if ( hashedChecksum != null ) {
            return hashedChecksum;
        }
        MessageDigest md;
        try {
            md = getInstance(CHECKSUM_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new CormorantException(e);
        }
        cacheKey = new StringBuilder();
        for (final Path next : objects) {

            BasicFileAttributes attributes = readAttributes(next, BasicFileAttributes.class, NOFOLLOW_LINKS);
            String pathId = next.toString();
            Object fileKey = attributes.fileKey();
            if ( fileKey != null ) {
                cacheKey.append(fileKey.toString());
            } else {
                cacheKey.append(next.toString());
            }
            cacheKey.append(pathId);
            cacheKey.append(attributes.size());
            cacheKey.append(attributes.lastModifiedTime().toMillis());

            try (InputStream is = newInputStream(open(next, NOFOLLOW_LINKS, READ))) {
                final byte[] buffer = new byte[BUFFER_SIZE];
                int read;
                while((read = is.read(buffer)) > 0) {
                    md.update(buffer, 0, read);
                }
            }
        }
        final byte[] hash = md.digest();
        final String checksum = format("%032x", new BigInteger(1, hash));
        cache.put(cacheKey.toString(), checksum);
        return checksum;
    }

    protected String calculateChecksum(final Path object) {
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
}
