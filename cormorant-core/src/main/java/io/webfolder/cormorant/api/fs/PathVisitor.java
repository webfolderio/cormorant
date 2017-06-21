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

import static java.lang.Boolean.TRUE;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import io.webfolder.cormorant.api.model.ListContainerOptions;

class PathVisitor implements FileVisitor<Path>, DirectoryStream<Path> {

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

    public PathVisitor(
                    final ListContainerOptions options,
                    final Path root,
                    final int pathMaxCount) {
        this.delimiter = options.getDelimiter();
        this.prefix    = options.getPrefix();
        this.root      = root;
        this.limit     = options.getLimit()     == null ? pathMaxCount           : options.getLimit();
        this.marker    = options.getMarker()    != null ? options.getMarker()    : null;
        this.endMarker = options.getEndMarker() != null ? options.getEndMarker() : null;
        this.reverse   = TRUE.equals(options.getReverse());
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
        if ( delimiter == null &&
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
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return CONTINUE;
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

    @Override
    public void close() {
    }
}
