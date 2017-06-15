/**
 * cormorant - Object Storage Server
 * Copyright © 2017 WebFolder OÜ (cormorant@webfolder.io)
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

import java.nio.file.Path;
import java.util.Iterator;

import io.webfolder.cormorant.api.resource.ContentFormat;
import io.webfolder.cormorant.api.resource.ResourceStream;

public final class PathNullStream implements ResourceStream<Path> {

    private static final class EmptyIterator implements Iterator<Path> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Path next() {
            return null;
        }
    }

    private static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    public static final ResourceStream<Path> EMPTY_STREAM = new PathNullStream();

    @Override
    public Iterator<Path> iterator() {
        return EMPTY_ITERATOR;
    }

    @Override
    public String convert(Path o, ContentFormat contentFormat) {
        return null;
    }
}
