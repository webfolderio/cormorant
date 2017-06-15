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

import static java.lang.Long.MAX_VALUE;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;
import static java.nio.file.Files.size;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileSizeVisitor implements FileVisitor<Path> {

    private long objectCount;

    private long bytesUsed;

    private final long limit;

    private final boolean countDirectory;

    private final boolean recursive;

    public FileSizeVisitor() {
        this(0L, 0L);
    }

    public FileSizeVisitor(final boolean recursive) {
        this(0L, 0L, MAX_VALUE, false, false);
    }

    public FileSizeVisitor(final long limit, final boolean countDirectory) {
        this(0L, 0L, limit, countDirectory, true);
    }

    public FileSizeVisitor(final long objectCount, final long bytesUsed) {
        this(objectCount, bytesUsed, MAX_VALUE, false, true);
    }

    public FileSizeVisitor(
                        final long    objectCounter,
                        final long    bytesUsedCounter,
                        final long    limit,
                        final boolean countDirectory,
                        final boolean recursive) {
        this.objectCount    = objectCounter;
        this.bytesUsed      = bytesUsedCounter;
        this.limit          = limit;
        this.countDirectory = countDirectory;
        this.recursive      = recursive;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (countDirectory) {
            objectCount += 1;
        }
        if ( ! recursive && objectCount > 1 ) {
            return TERMINATE;
        }
        return objectCount < limit ? CONTINUE : TERMINATE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        objectCount += 1;
        if (attrs.size() == 0) {
            bytesUsed += size(file);
        } else {
            bytesUsed += attrs.size();
        }
        return objectCount < limit ? CONTINUE : TERMINATE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return CONTINUE;
    }

    public long getObjectCount() {
        return objectCount;
    }

    public long getBytesUsed() {
        return bytesUsed;
    }

    @Override
    public String toString() {
        return "FileCounter [objectCount=" + objectCount + ", bytesUsed=" + bytesUsed + "]";
    }
}
