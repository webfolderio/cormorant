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

import static java.lang.Boolean.TRUE;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import io.webfolder.cormorant.api.model.ListContainerOptions;

class PathVisitor extends SimpleFileVisitor<Path> implements Iterable<Path> {

    private static final String  BACKSLASH = "\\";

    private static final String      SLASH = "/";

    private final TreeSet<Path> files = new TreeSet<>();

    private final Path root;

    private final String delimiter;

    private final String prefix;

    private final AtomicLong count = new AtomicLong();

    private final Integer limit;

    private final String marker;

    private final String endMarker;

    private final boolean reverse;

    private Path prefixPath;

    public PathVisitor(
                    final ListContainerOptions options,
                    final Path root,
                    final int pathMaxCount) {
        this.delimiter  = options.getDelimiter();
        this.prefix     = options.getPrefix();
        this.root       = root;
        this.limit      = options.getLimit()     == null ? pathMaxCount           : options.getLimit();
        this.marker     = options.getMarker()    != null ? options.getMarker()    : null;
        this.endMarker  = options.getEndMarker() != null ? options.getEndMarker() : null;
        this.reverse    = TRUE.equals(options.getReverse());
        this.prefixPath = prefix != null ? root.resolve(prefix).toAbsolutePath() : null;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        FileVisitResult visit = visit(dir);
        if ( limit >= 0 && count.get() >= limit ) {
            return TERMINATE;
        } else {
            return visit;
        }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        FileVisitResult visit = visit(file);
        if ( limit >= 0 && count.get() >= limit ) {
            return TERMINATE;
        } else {
            return visit;
        }
    }

    protected FileVisitResult visit(final Path file) {
        if ( prefixPath != null && prefixPath.equals(file) ) {
            return CONTINUE;
        } else if ( delimiter == null &&
                prefix != null &&
                ! getRelative(file).startsWith(prefix) ) {
            return CONTINUE;
        } else if ( ! root.equals(file) ) {
            count.incrementAndGet();
            files.add(file);
        }
        return CONTINUE;
    }

    protected String getRelative(final Path file) {
        return root.relativize(file).toString().replace(BACKSLASH, SLASH);
    }

    @Override
    public Iterator<Path> iterator() {
        final boolean inclusive = false;
        NavigableSet<Path> set = files;
        if ( marker != null && endMarker != null ) {
            final Path pMarker = root.resolve(marker).toAbsolutePath().normalize();
            final Path pEndMarker = root.resolve(endMarker).toAbsolutePath().normalize();
            set = files.subSet(pMarker, inclusive, pEndMarker, inclusive);
        } else if ( marker != null && endMarker == null ) {
            final Path pMarker = root.resolve(marker).toAbsolutePath().normalize();
            set = files.tailSet(pMarker, inclusive);
        } else if ( marker == null && endMarker != null ) {
            final Path pEndMarker = root.resolve(endMarker).toAbsolutePath().normalize();
            set = files.headSet(pEndMarker, inclusive);
        }
        return reverse ? set.descendingIterator() : set.iterator();
    }
}
