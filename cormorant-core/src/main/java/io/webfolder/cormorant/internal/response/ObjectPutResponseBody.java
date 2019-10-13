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
package io.webfolder.cormorant.internal.response;

public class ObjectPutResponseBody {

    private String lastModified;

    /**
     * The date and time when the object was last modified.
     *
     * The date and time stamp format is {@link <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601</a>
     *<p>
     * CCYY-MM-DDThh:mm:ss±hh:mm
     *<p>
     *
     * For example, {@literal 2015-08-27T09:49:58-05:00}.
     *
     * The {@literal ±hh:mm} value, if included, is the time zone as an offset
     * from UTC. In the previous example, the offset value is {@literal -05:00}.
     */
    public String getLastModified() {
        return lastModified;
    }

    /**
     * The date and time when the object was last modified.
     *
     * The date and time stamp format is {@link <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601</a>
     *<p>
     * CCYY-MM-DDThh:mm:ss±hh:mm
     *<p>
     *
     * For example, {@literal 2015-08-27T09:49:58-05:00}.
     *
     * The {@literal ±hh:mm} value, if included, is the time zone as an offset
     * from UTC. In the previous example, the offset value is {@literal -05:00}.
     */
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
}
