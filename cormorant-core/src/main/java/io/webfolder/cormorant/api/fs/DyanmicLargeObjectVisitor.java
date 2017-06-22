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

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

class DyanmicLargeObjectVisitor extends SimpleFileVisitor<Path> {

    private final List<Path> files = new ArrayList<>();

    private final String prefix;

    private long dirCounter;

    public DyanmicLargeObjectVisitor(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        dirCounter += 1;
        return dirCounter > 1 ? TERMINATE : CONTINUE;
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
        return files;
    }
}
