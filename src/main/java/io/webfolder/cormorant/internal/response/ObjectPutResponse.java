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
package io.webfolder.cormorant.internal.response;

import javax.ws.rs.HeaderParam;

public class ObjectPutResponse implements CormorantResponse {

    @HeaderParam("Content-Length")
    private Long contentLength;

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

    @HeaderParam("Last-Modified")
    private String lastModified;

    /**
     * <p>If the operation succeeds, this value is zero (0) or the length of informational or error text in the response body.</p>
     */
    public Long getContentLength() {
        return contentLength;
    }

    /**
     * <p>If the operation succeeds, this value is zero (0) or the length of informational or error text in the response body.</p>
     */
    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * <p>The MD5 checksum of the uploaded object content. The value is not quoted. If it is an SLO, it would be MD5 checksum of the segments' etags.</p>
     */
    public String getETag() {
        return eTag;
    }

    /**
     * <p>The MD5 checksum of the uploaded object content. The value is not quoted. If it is an SLO, it would be MD5 checksum of the segments' etags.</p>
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
}
