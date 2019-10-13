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

public interface CormorantResponse {

    /**
     * <p>The date and time the system responded to the request, using the preferred format of {@link <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">RFC 7231</a>} as shown in this example {@literal Thu, 16 Jun 2016 15:10:38 GMT}. The time is always in UTC.</p>
     */
    String getDate();

    /**
     * <p>The date and time the system responded to the request, using the preferred format of {@link <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">RFC 7231</a>} as shown in this example {@literal Thu, 16 Jun 2016 15:10:38 GMT}. The time is always in UTC.</p>
     */
    void setDate(String date);

    /**
     * <p>A unique transaction ID for this request. Your service provider might need this value if you report a problem.</p>
     */
    String getTransId();

    /**
     * <p>A unique transaction ID for this request. Your service provider might need this value if you report a problem.</p>
     */
    void setTransId(String transId);

    /**
     * <p>The date and time in {@link <a href="https://en.wikipedia.org/wiki/Unix_time">UNIX Epoch time stamp format</a>} when the account, container, or object was initially created as a current version.  For example, {@literal 1440619048} is equivalent to {@literal Mon, Wed, 26 Aug 2015 19:57:28 GMT}.</p>
     */
    public Long getTimestamp();

    /**
     * <p>The date and time in {@link <a href="https://en.wikipedia.org/wiki/Unix_time">UNIX Epoch time stamp format</a>} when the account, container, or object was initially created as a current version.  For example, {@literal 1440619048} is equivalent to {@literal Mon, Wed, 26 Aug 2015 19:57:28 GMT}.</p>
     */
    public void setTimestamp(Long timestamp);
}
