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
