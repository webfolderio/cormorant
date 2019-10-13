/**
 * The MIT License
 * Copyright © 2017, 2019 WebFolder OÜ
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.webfolder.cormorant.api.fs;

import static io.webfolder.cormorant.api.fs.EmptyResource.EMPTY_RESOURCE;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Long.parseLong;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.util.Collections.emptySet;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.model.ListContainerOptions;
import io.webfolder.cormorant.api.resource.Resource;
import io.webfolder.cormorant.api.service.ContainerService;
import io.webfolder.cormorant.api.service.MetadataService;
import io.webfolder.cormorant.api.service.ObjectService;

public class PathContainerService implements ContainerService<Path> {

    private static final String FORWARD_SLASH = "/";

    private final Path            root           ;

    private final int             pathMaxCount   ;

    private final MetadataService metadataService;

    private final MetadataService systemMetadataService;

    private ObjectService<Path>   objectService;

    public PathContainerService(
                    final Path            root,
                    final int             pathMaxCount,
                    final MetadataService metadaService,
                    final MetadataService systemMetadataService) {
        this.root                  = root.toAbsolutePath().normalize();
        this.pathMaxCount          = pathMaxCount                     ;
        this.metadataService       = metadaService                    ;
        this.systemMetadataService = systemMetadataService            ;
    }

    @Override
    public Resource<Path> listObjects(
                                        final String               accountName  ,
                                        final String               containerName,
                                        final ListContainerOptions options      ) throws IOException {
        Resource<Path> stream = EMPTY_RESOURCE;

        final Path container = getContainer(accountName, containerName);

        if (container == null) {
            return stream;
        }

        Path visitorPath = container;

        boolean recursive = true;

        final String delimiter = options.getDelimiter();
        final String prefix    = options.getPrefix();
        final String path      = options.getPath();

        if ( path != null ) {
            recursive = false;
            visitorPath = visitorPath.resolve(path);
        }

        if (FORWARD_SLASH.equals(delimiter)) {
            recursive = false;
            if ( prefix != null ) {
                visitorPath = visitorPath.resolve(prefix);
            }
        }

        visitorPath = visitorPath.toAbsolutePath();

        if ( ! visitorPath.startsWith(container) ) {
            throw new CormorantException("Invalid path [" + visitorPath.toString() + "]");
        }

        if ( ! recursive && ! exists(visitorPath) ) {
            stream = EMPTY_RESOURCE;
        } else {
            PathVisitor visitor = new PathVisitor(options, visitorPath, pathMaxCount);
            walkFileTree(visitorPath,
                                emptySet(), recursive ? MAX_VALUE : 1, visitor);
            stream = new PathResource(visitor,
                                new PathAdapter(container, objectService, systemMetadataService));
        }

        return stream;
    }

    @Override
    public boolean contains(final String accountName, final String containerName) {
        final Path path = getContainer(accountName, containerName);
        return path != null && exists(path, NOFOLLOW_LINKS) ? true : false;
    }

    @Override
    public void create(final String accountName, final String containerName) {
        final Path path = getContainer(accountName, containerName);
        if ( path != null && ! exists(path, NOFOLLOW_LINKS) ) {
            if (isRegularFile(path, NOFOLLOW_LINKS)) {
                final String error = "Unable to create container. Path [" + containerName + "] already exists.";
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
    public boolean delete(final String accountName, final String containerName) throws SQLException {
        final Path path = getContainer(accountName, containerName);
        if ( path != null &&
                exists(path, NOFOLLOW_LINKS) &&
                isDirectory(path, NOFOLLOW_LINKS) ) {
            FileSizeVisitor visitor = new FileSizeVisitor(2, false);
            try {
                walkFileTree(path, visitor);
            } catch (IOException e) {
                throw new CormorantException(e);
            }
            final boolean empty = visitor.getObjectCount() == 0;
            if (empty) {
                try {
                    walkFileTree(path, new DirectoryDeleteVisitor());
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
    public long getMaxQuotaBytes(String accountName, String containerName) throws SQLException {
        final String value = metadataService.get(containerName, "quota-bytes");
        if (value == null) {
            return Long.MAX_VALUE;
        } else {
            return parseLong(value);
        }
    }

    @Override
    public long getMaxQuotaCount(String accountName, String containerName) throws SQLException {
        final String value = metadataService.get(containerName, "quota-count");
        if (value == null) {
            return Long.MAX_VALUE;
        } else {
            return parseLong(value);
        }
    }

    @Override
    public void setObjectService(ObjectService<Path> objectService) {
        this.objectService = objectService;
    }
}
