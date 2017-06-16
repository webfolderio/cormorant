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

import static io.webfolder.cormorant.api.fs.PathNullStream.EMPTY_STREAM;
import static java.lang.Integer.MAX_VALUE;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.util.Collections.emptySet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.model.ListContainerOptions;
import io.webfolder.cormorant.api.resource.ResourceStream;
import io.webfolder.cormorant.api.service.ChecksumService;
import io.webfolder.cormorant.api.service.ContainerService;
import io.webfolder.cormorant.api.service.MetadataService;

public class PathContainerService implements ContainerService<Path> {

    private static final String SLASH = "/";

    private final Path                  root           ;

    private final int                   pathMaxCount   ;

    private final ChecksumService<Path> checksumService;

    private final MetadataService       metadataService;

    public PathContainerService(
                    final Path                  root,
                    final int                   pathMaxCount,
                    final ChecksumService<Path> checksumService,
                    final MetadataService       metadaService) {
        this.root                 = root.toAbsolutePath().normalize();
        this.pathMaxCount         = pathMaxCount    ;
        this.checksumService      = checksumService ;
        this.metadataService      = metadaService   ;
    }

    @Override
    public ResourceStream<Path> listObjects(
                                        final String               accountName  ,
                                        final String               containerName,
                                        final ListContainerOptions options      ) {
        ResourceStream<Path> stream = EMPTY_STREAM;

        final Path containerPath = getContainer(accountName, containerName);

        if (containerPath == null) {
            return stream;
        }

        Path visitorPath = containerPath;

        boolean recursive = true;

        final String delimiter = options.getDelimiter();
        final String prefix    = options.getPrefix();
        final String path      = options.getPath();

        if ( prefix    == null &&
             delimiter == null &&
             prefix    == null &&
             path      != null ) {
            recursive = false;
            if ( path != null ) {
                for (String next : path.split(SLASH)) {
                    visitorPath = visitorPath.resolve(next);
                }
            }
        } else if (SLASH.equals(delimiter)) {
            recursive = false;
            if ( prefix != null ) {
                for (String next : prefix.split(SLASH)) {
                    visitorPath = visitorPath.resolve(next);
                }
            }
        }

        if ( ! recursive && ! exists(visitorPath) ) {
            stream = EMPTY_STREAM;
        } else {
            PathVisitor visitor = new PathVisitor(options, visitorPath, pathMaxCount);
            try {
                walkFileTree(visitorPath,
                                    emptySet(), recursive ? MAX_VALUE : 1, visitor);
            } catch (IOException e) {
                throw new CormorantException("Unable to list objects.", e);
            }
            stream = new PathStream(visitor,
                                new PathAdapter(containerPath, checksumService));
        }

        return stream;
    }

    @Override
    public boolean contains(final String accountName, final String containerName) {
        final Path path = getContainer(accountName, containerName);
        return    path != null && exists(path, NOFOLLOW_LINKS) ? true : false;
    }

    @Override
    public void create(final String accountName, final String containerName) {
        final Path path = getContainer(accountName, containerName);
        if ( path != null && ! exists(path, NOFOLLOW_LINKS) ) {
            if (isRegularFile(path, NOFOLLOW_LINKS)) {
                final String error = "Unable to create container. File [" + containerName + "] already exists.";
                throw new CormorantException(error);
            } else {
                try {
                    createDirectory(path);
                } catch (IOException e) {
                    throw new CormorantException("Unable to create container [" + containerName + "].", e);
                }
            }
        }
    }

    @Override
    public boolean delete(final String accountName, final String containerName) {
        final Path path = getContainer(accountName, containerName);
        if ( path != null &&
                exists(path, NOFOLLOW_LINKS) &&
                isDirectory(path, NOFOLLOW_LINKS) ) {
            FileSizeVisitor visitor = new FileSizeVisitor(2, false);
            try {
                Files.walkFileTree(path, visitor);
            } catch (IOException e) {
                throw new CormorantException(e);
            }
            final boolean empty = visitor.getObjectCount() == 0;
            if (empty) {
                try {
                    Files.walkFileTree(path, new DirectoryDeleteVisitor());
                    return true;
                } catch (IOException e) {
                    throw new CormorantException("Unable to delete container [" + containerName + "].", e);
                }
            }
        }
        metadataService.delete(containerName);
        return false;
    }

    @Override
    public Path getContainer(String accountName, String containerName) {
        final Path path = root.resolve(containerName).toAbsolutePath().normalize();
        if (path.startsWith(root)) {
            return path;
        }
        return null;
    }

    @Override
    public long getMaxQuotaBytes(String accountName, String containerName) {
        final Long value = metadataService.getPropertyLong(containerName, "quota-bytes");
        if (value == null) {
            return Long.MAX_VALUE;
        } else {
            return value.longValue();
        }
    }

    @Override
    public long getMaxQuotaCount(String accountName, String containerName) {
        final Long value = metadataService.getPropertyLong(containerName, "quota-count");
        if (value == null) {
            return Long.MAX_VALUE;
        } else {
            return value.longValue();
        }
    }
}