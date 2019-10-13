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

import java.nio.file.Path;
import java.util.Iterator;

import io.webfolder.cormorant.api.resource.ContentFormat;
import io.webfolder.cormorant.api.resource.Resource;

public final class EmptyResource implements Resource<Path> {

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

    public static final Resource<Path> EMPTY_RESOURCE = new EmptyResource();

    @Override
    public Iterator<Path> iterator() {
        return EMPTY_ITERATOR;
    }

    @Override
    public String convert(Path o, ContentFormat contentFormat, Boolean appendForwardSlash) {
        return null;
    }
}
