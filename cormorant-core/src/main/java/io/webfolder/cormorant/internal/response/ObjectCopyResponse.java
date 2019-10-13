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

import javax.ws.rs.HeaderParam;

public class ObjectCopyResponse implements CormorantResponse {

    @HeaderParam("Content-Length")
    private String contentLength;

    @HeaderParam("X-Copied-From-Last-Modified")
    private String copiedFromLastModified;

    @HeaderParam("X-Copied-From")
    private String copiedFrom;

    @HeaderParam("X-Copied-From-Account")
    private String copiedFromAccount;

    @HeaderParam("Last-Modified")
    private String lastModified;

    @HeaderParam("ETag")
    private String eTag;

    @HeaderParam("X-Timestamp")
    private Long timestamp;

    @HeaderParam("X-Trans-Id")
    private String transId;

    @HeaderParam("Date")
    private String date;

    @HeaderParam("Content-Type")
    private String contentType;

    /**
     * <p>If the operation succeeds, this value is zero (0) or the length of informational or error text in the response body.</p>
     */
    public String getContentLength() {
        return contentLength;
    }

    /**
     * <p>If the operation succeeds, this value is zero (0) or the length of informational or error text in the response body.</p>
     */
    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * <p>For a copied object, the date and time in <a href="https://en.wikipedia.org/wiki/Unix_time">UNIX Epoch time stamp format</a> when the container and object name from which the new object was copied was last modified.  For example, {@literal 1440619048} is equivalent to {@literal Mon, Wed, 26 Aug 2015 19:57:28 GMT}.</p>
     */
    public String getCopiedFromLastModified() {
        return copiedFromLastModified;
    }

    /**
     * <p>For a copied object, the date and time in <a href="https://en.wikipedia.org/wiki/Unix_time">UNIX Epoch time stamp format</a> when the container and object name from which the new object was copied was last modified.  For example, {@literal 1440619048} is equivalent to {@literal Mon, Wed, 26 Aug 2015 19:57:28 GMT}.</p>
     */
    public void setCopiedFromLastModified(String copiedFromLastModified) {
        this.copiedFromLastModified = copiedFromLastModified;
    }

    /**
     * <p>For a copied object, shows the container and object name from which the new object was copied. The value is in the {@literal {container}/{object}} format.</p>
     */
    public String getCopiedFrom() {
        return copiedFrom;
    }

    /**
     * <p>For a copied object, shows the container and object name from which the new object was copied. The value is in the {@literal {container}/{object}} format.</p>
     */
    public void setCopiedFrom(String copiedFrom) {
        this.copiedFrom = copiedFrom;
    }

    /**
     * <p>For a copied object, shows the account from which the new object was copied.</p>
     */
    public String getCopiedFromAccount() {
        return copiedFromAccount;
    }

    /**
     * <p>For a copied object, shows the account from which the new object was copied.</p>
     */
    public void setCopiedFromAccount(String copiedFromAccount) {
        this.copiedFromAccount = copiedFromAccount;
    }

    /**
     * <p>The date and time when the object was created or its metadata was changed. The date and time is formaatted as shown in this example: {@literal Fri, 12 Aug 2016 14:24:16 GMT}</p>
     * <p>The time is always in UTC.</p>
     */
    public String getLastModified() {
        return lastModified;
    }

    /**
     * <p>The date and time when the object was created or its metadata was changed. The date and time is formaatted as shown in this example: {@literal Fri, 12 Aug 2016 14:24:16 GMT}</p>
     * <p>The time is always in UTC.</p>
     */
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * <p>The MD5 checksum of the copied object content. The value is not quoted.</p>
     */
    public String getETag() {
        return eTag;
    }

    /**
     * <p>The MD5 checksum of the copied object content. The value is not quoted.</p>
     */
    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    /**
     * <p>The date and time in <a href="https://en.wikipedia.org/wiki/Unix_time">UNIX Epoch time stamp format</a> when the account, container, or object was initially created as a current version.  For example, {@literal 1440619048} is equivalent to {@literal Mon, Wed, 26 Aug 2015 19:57:28 GMT}.</p>
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * <p>The date and time in <a href="https://en.wikipedia.org/wiki/Unix_time">UNIX Epoch time stamp format</a> when the account, container, or object was initially created as a current version.  For example, {@literal 1440619048} is equivalent to {@literal Mon, Wed, 26 Aug 2015 19:57:28 GMT}.</p>
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * <p>A unique transaction ID for this request. Your service provider might need this value if you report a problem.</p>
     */
    public String getTransId() {
        return transId;
    }

    /**
     * <p>A unique transaction ID for this request. Your service provider might need this value if you report a problem.</p>
     */
    public void setTransId(String transId) {
        this.transId = transId;
    }

    /**
     * <p>The date and time the system responded to the request, using the preferred format of <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">RFC 7231</a> as shown in this example {@literal Thu, 16 Jun 2016 15:10:38 GMT}. The time is always in UTC.</p>
     */
    public String getDate() {
        return date;
    }

    /**
     * <p>The date and time the system responded to the request, using the preferred format of <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">RFC 7231</a> as shown in this example {@literal Thu, 16 Jun 2016 15:10:38 GMT}. The time is always in UTC.</p>
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * <p>If the operation succeeds, this value is the MIME type of the object. If the operation fails, this value is the MIME type of the error text in the response body.</p>
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * <p>If the operation succeeds, this value is the MIME type of the object. If the operation fails, this value is the MIME type of the error text in the response body.</p>
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
