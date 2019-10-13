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

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;
import static java.util.Collections.sort;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class DyanmicLargeObjectVisitor extends SimpleFileVisitor<Path> {

    private final List<Path> files = new ArrayList<>();

    private final String prefix;

    private long dirCounter;

    private boolean sorted;

    private final int maxDepth = 10;

    private static final Comparator<Path> FILE_NAME_COMPORATOR = new FileNameComporator();
    
    private static final class FileNameComporator implements Comparator<Path> {

        @Override
        public int compare(Path o1, Path o2) {
            return o1.getFileName().toString().compareTo(o2.getFileName().toString());
        }
        
    }

    public DyanmicLargeObjectVisitor(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        dirCounter += 1;
        return dirCounter > maxDepth ? TERMINATE : CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if ( prefix != null ) {
            final String fileName = file.getFileName().toString();
            if ( ! fileName.equals(prefix) &&
                    fileName.startsWith(prefix) ) {
                files.add(file);
            }
        } else {
            files.add(file);
        }
        return CONTINUE;
    }

    public List<Path> getFiles() {
        if ( ! sorted ) {
            sorted = true;
            sort(files, FILE_NAME_COMPORATOR);
        }
        return files;
    }
}
