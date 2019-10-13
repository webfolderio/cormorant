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
package io.webfolder.cormorant.internal.jaxrs;

import static java.lang.Math.min;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class LimitedInputStream extends FilterInputStream {

    private long left;

    private long mark = -1;

    LimitedInputStream(final InputStream in, final long limit) {
        super(in);
        left = limit;
    }

    @Override
    public int available() throws IOException {
        return (int) min(in.available(), left);
    }

    @Override
    public synchronized void mark(final int readLimit) {
        in.mark(readLimit);
        mark = left;
    }

    @Override
    public int read() throws IOException {
        if (left == 0) {
            return -1;
        }
        int result = in.read();
        if (result != -1) {
            --left;
        }
        return result;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (left == 0) {
            return -1;
        }
        int length = (int) min(len, left);
        int result = in.read(b, off, length);
        if ( result != -1 ) {
            left -= result;
        }
        return result;
    }

    @Override
    public synchronized void reset() throws IOException {
        if ( ! in.markSupported() ) {
            throw new IOException("Mark not supported.");
        }
        if (mark == -1) {
            throw new IOException("Mark not set.");
        }
        in.reset();
        left = mark;
    }

    @Override
    public long skip(final long n) throws IOException {
        final long skip    = min(n, left);
        final long skipped = in.skip(skip);
        left -= skipped;
        return skipped;
    }
}
