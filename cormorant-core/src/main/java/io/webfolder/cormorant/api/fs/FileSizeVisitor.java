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

import static java.lang.Long.MAX_VALUE;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileSizeVisitor extends SimpleFileVisitor<Path> {

    private long objectCount;

    private long bytesUsed;

    private final long limit;

    private final boolean countDirectory;

    private final boolean recursive;

    public FileSizeVisitor() {
        this(0L, 0L);
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
        bytesUsed += attrs.size();
        return objectCount < limit ? CONTINUE : TERMINATE;
    }

    public long getObjectCount() {
        return objectCount;
    }

    public long getBytesUsed() {
        return bytesUsed;
    }
}
