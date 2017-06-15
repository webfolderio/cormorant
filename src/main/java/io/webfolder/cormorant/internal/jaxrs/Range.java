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

class Range {

    private final long start;

    private final long end;

    private final long length;

    private final String boundary;

    public Range(final long   start,
                 final long   end,
                 final String boundary) {
        this.start     = start;
        this.end      = end;
        this.length   = end - start + 1;
        this.boundary = boundary;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getLength() {
        return length;
    }

    public String getBoundary() {
        return boundary;
    }

    @Override
    public String toString() {
        return "Range [start=" + start + ", end=" + end + ", length=" + length + ", boundary=" + boundary + "]";
    }
}
