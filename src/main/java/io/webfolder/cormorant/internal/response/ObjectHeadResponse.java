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
package io.webfolder.cormorant.internal.response;

import javax.ws.rs.HeaderParam;

public class ObjectHeadResponse implements CormorantResponse {

    @HeaderParam("Content-Length")
    private String contentLength;

    @HeaderParam("X-Object-Meta-name")
    private String objectMetaName;

    @HeaderParam("Content-Disposition")
    private String contentDisposition;

    @HeaderParam("Content-Encoding")
    private String contentEncoding;

    @HeaderParam("X-Delete-At")
    private Integer deleteAt;

    @HeaderParam("X-Object-Manifest")
    private String objectManifest;

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

    @HeaderParam("X-Static-Large-Object")
    private Boolean staticLargeObject;

    @HeaderParam("Content-Type")
    private String contentType;

    /**
     * <p>HEAD operations do not return content. The {@literal Content-Length} header value is not the size of the response body but is the size of the object, in bytes.</p>
     */
    public String getContentLength() {
        return contentLength;
    }

    /**
     * <p>HEAD operations do not return content. The {@literal Content-Length} header value is not the size of the response body but is the size of the object, in bytes.</p>
     */
    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * <p>The object metadata, where {@literal name} is the name of the metadata item.  You must specify an {@literal X-Object-Meta-name} header for each metadata {@literal name} item that you want to add or update.</p>
     */
    public String getObjectMetaName() {
        return objectMetaName;
    }

    /**
     * <p>The object metadata, where {@literal name} is the name of the metadata item.  You must specify an {@literal X-Object-Meta-name} header for each metadata {@literal name} item that you want to add or update.</p>
     */
    public void setObjectMetaName(String objectMetaName) {
        this.objectMetaName = objectMetaName;
    }

    /**
     * <p>If present, specifies the override behavior for the browser. For example, this header might specify that the browser use a download program to save this file rather than show the file, which is the default.  If not set, this header is not returned by this operation.</p>
     */
    public String getContentDisposition() {
        return contentDisposition;
    }

    /**
     * <p>If present, specifies the override behavior for the browser. For example, this header might specify that the browser use a download program to save this file rather than show the file, which is the default.  If not set, this header is not returned by this operation.</p>
     */
    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    /**
     * <p>If present, the value of the {@literal Content-Encoding} metadata.  If not set, the operation does not return this header.</p>
     */
    public String getContentEncoding() {
        return contentEncoding;
    }

    /**
     * <p>If present, the value of the {@literal Content-Encoding} metadata.  If not set, the operation does not return this header.</p>
     */
    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    /**
     * <p>The date and time in <a href="https://en.wikipedia.org/wiki/Unix_time">UNIX Epoch time stamp format</a> when the system removes the object.  For example, {@literal 1440619048} is equivalent to {@literal Mon, Wed, 26 Aug 2015 19:57:28 GMT}.</p>
     */
    public Integer getDeleteAt() {
        return deleteAt;
    }

    /**
     * <p>The date and time in <a href="https://en.wikipedia.org/wiki/Unix_time">UNIX Epoch time stamp format</a> when the system removes the object.  For example, {@literal 1440619048} is equivalent to {@literal Mon, Wed, 26 Aug 2015 19:57:28 GMT}.</p>
     */
    public void setDeleteAt(Integer deleteAt) {
        this.deleteAt = deleteAt;
    }

    /**
     * <p>If present, this is a dynamic large object manifest object. The value is the container and object name prefix of the segment objects in the form {@literal container/prefix}.</p>
     */
    public String getObjectManifest() {
        return objectManifest;
    }

    /**
     * <p>If present, this is a dynamic large object manifest object. The value is the container and object name prefix of the segment objects in the form {@literal container/prefix}.</p>
     */
    public void setObjectManifest(String objectManifest) {
        this.objectManifest = objectManifest;
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
     * <p>For objects smaller than 5 GB, this value is the MD5 checksum of the object content. The value is not quoted.  For manifest objects, this value is the MD5 checksum of the concatenated string of ETag values for each of the segments in the manifest, and not the MD5 checksum of the content that was downloaded. Also the value is enclosed in double-quote characters.  You are strongly recommended to compute the MD5 checksum of the response body as it is received and compare this value with the one in the ETag header. If they differ, the content was corrupted, so retry the operation.</p>
     */
    public String getETag() {
        return eTag;
    }

    /**
     * <p>For objects smaller than 5 GB, this value is the MD5 checksum of the object content. The value is not quoted.  For manifest objects, this value is the MD5 checksum of the concatenated string of ETag values for each of the segments in the manifest, and not the MD5 checksum of the content that was downloaded. Also the value is enclosed in double-quote characters.  You are strongly recommended to compute the MD5 checksum of the response body as it is received and compare this value with the one in the ETag header. If they differ, the content was corrupted, so retry the operation.</p>
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
     * <p>Set to {@literal true} if this object is a static large object manifest object.</p>
     */
    public Boolean getStaticLargeObject() {
        return staticLargeObject;
    }

    /**
     * <p>Set to {@literal true} if this object is a static large object manifest object.</p>
     */
    public void setStaticLargeObject(Boolean staticLargeObject) {
        this.staticLargeObject = staticLargeObject;
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
