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

public class ContainerGetResponse implements CormorantResponse {

    @HeaderParam("X-Container-Meta-name")
    private String containerMetaName;

    @HeaderParam("Content-Length")
    private String contentLength;

    @HeaderParam("X-Container-Object-Count")
    private Long containerObjectCount;

    @HeaderParam("X-Container-Bytes-Used")
    private Long containerBytesUsed;

    @HeaderParam("Accept-Ranges")
    private String acceptRanges;

    @HeaderParam("X-Container-Meta-Temp-URL-Key")
    private String containerMetaTempURLKey;

    @HeaderParam("X-Container-Meta-Temp-URL-Key-2")
    private String containerMetaTempURLKey2;

    @HeaderParam("X-Container-Meta-Quota-Count")
    private String containerMetaQuotaCount;

    @HeaderParam("X-Container-Meta-Quota-Bytes")
    private String containerMetaQuotaBytes;

    @HeaderParam("X-Storage-Policy")
    private String storagePolicy;

    @HeaderParam("X-Container-Read")
    private String containerRead;

    @HeaderParam("X-Container-Write")
    private String containerWrite;

    @HeaderParam("X-Container-Sync-Key")
    private String containerSyncKey;

    @HeaderParam("X-Container-Sync-To")
    private String containerSyncTo;

    @HeaderParam("X-Versions-Location")
    private String versionsLocation;

    @HeaderParam("X-History-Location")
    private String historyLocation;

    @HeaderParam("X-Timestamp")
    private Long timestamp;

    @HeaderParam("X-Trans-Id")
    private String transId;

    @HeaderParam("Content-Type")
    private String contentType;

    @HeaderParam("Date")
    private String date;

    /**
     * <p>The custom container metadata item, where {@literal name} is the name of the metadata item.  One {@literal X-Container-Meta-name} response header appears for each metadata item (for each {@literal name}).</p>
     */
    public String getContainerMetaName() {
        return containerMetaName;
    }

    /**
     * <p>The custom container metadata item, where {@literal name} is the name of the metadata item.  One {@literal X-Container-Meta-name} response header appears for each metadata item (for each {@literal name}).</p>
     */
    public void setContainerMetaName(String containerMetaName) {
        this.containerMetaName = containerMetaName;
    }

    /**
     * <p>If the operation succeeds, the length of the response body in bytes. On error, this is the length of the error text.</p>
     */
    public String getContentLength() {
        return contentLength;
    }

    /**
     * <p>If the operation succeeds, the length of the response body in bytes. On error, this is the length of the error text.</p>
     */
    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * <p>The number of objects.</p>
     */
    public Long getContainerObjectCount() {
        return containerObjectCount;
    }

    /**
     * <p>The number of objects.</p>
     */
    public void setContainerObjectCount(Long containerObjectCount) {
        this.containerObjectCount = containerObjectCount;
    }

    /**
     * <p>The total number of bytes used.</p>
     */
    public Long getContainerBytesUsed() {
        return containerBytesUsed;
    }

    /**
     * <p>The total number of bytes used.</p>
     */
    public void setContainerBytesUsed(Long containerBytesUsed) {
        this.containerBytesUsed = containerBytesUsed;
    }

    /**
     * <p>The type of ranges that the object accepts.</p>
     */
    public String getAcceptRanges() {
        return acceptRanges;
    }

    /**
     * <p>The type of ranges that the object accepts.</p>
     */
    public void setAcceptRanges(String acceptRanges) {
        this.acceptRanges = acceptRanges;
    }

    /**
     * <p>The secret key value for temporary URLs. If not set, this header is not returned in the response.</p>
     */
    public String getContainerMetaTempURLKey() {
        return containerMetaTempURLKey;
    }

    /**
     * <p>The secret key value for temporary URLs. If not set, this header is not returned in the response.</p>
     */
    public void setContainerMetaTempURLKey(String containerMetaTempURLKey) {
        this.containerMetaTempURLKey = containerMetaTempURLKey;
    }

    /**
     * <p>The second secret key value for temporary URLs. If not set, this header is not returned in the response.</p>
     */
    public String getContainerMetaTempURLKey2() {
        return containerMetaTempURLKey2;
    }

    /**
     * <p>The second secret key value for temporary URLs. If not set, this header is not returned in the response.</p>
     */
    public void setContainerMetaTempURLKey2(String containerMetaTempURLKey2) {
        this.containerMetaTempURLKey2 = containerMetaTempURLKey2;
    }

    /**
     * <p>The maximum object count of the container. If not set, this header is not returned by this operation.</p>
     */
    public String getContainerMetaQuotaCount() {
        return containerMetaQuotaCount;
    }

    /**
     * <p>The maximum object count of the container. If not set, this header is not returned by this operation.</p>
     */
    public void setContainerMetaQuotaCount(String containerMetaQuotaCount) {
        this.containerMetaQuotaCount = containerMetaQuotaCount;
    }

    /**
     * <p>The maximum size of the container, in bytes. If not set, this header is not returned by this operation.</p>
     */
    public String getContainerMetaQuotaBytes() {
        return containerMetaQuotaBytes;
    }

    /**
     * <p>The maximum size of the container, in bytes. If not set, this header is not returned by this operation.</p>
     */
    public void setContainerMetaQuotaBytes(String containerMetaQuotaBytes) {
        this.containerMetaQuotaBytes = containerMetaQuotaBytes;
    }

    /**
     * <p>In requests, specifies the name of the storage policy to use for the container. In responses, is the storage policy name. The storage policy of the container cannot be changed.</p>
     */
    public String getStoragePolicy() {
        return storagePolicy;
    }

    /**
     * <p>In requests, specifies the name of the storage policy to use for the container. In responses, is the storage policy name. The storage policy of the container cannot be changed.</p>
     */
    public void setStoragePolicy(String storagePolicy) {
        this.storagePolicy = storagePolicy;
    }

    /**
     * <p>The ACL that grants read access. If there is no ACL, this header is not returned by this operation.</p>
     */
    public String getContainerRead() {
        return containerRead;
    }

    /**
     * <p>The ACL that grants read access. If there is no ACL, this header is not returned by this operation.</p>
     */
    public void setContainerRead(String containerRead) {
        this.containerRead = containerRead;
    }

    /**
     * <p>The ACL that grants write access. If there is no ACL, this header is not returned by this operation.</p>
     */
    public String getContainerWrite() {
        return containerWrite;
    }

    /**
     * <p>The ACL that grants write access. If there is no ACL, this header is not returned by this operation.</p>
     */
    public void setContainerWrite(String containerWrite) {
        this.containerWrite = containerWrite;
    }

    /**
     * <p>The secret key for container synchronization. If not set, this header is not returned by this operation.</p>
     */
    public String getContainerSyncKey() {
        return containerSyncKey;
    }

    /**
     * <p>The secret key for container synchronization. If not set, this header is not returned by this operation.</p>
     */
    public void setContainerSyncKey(String containerSyncKey) {
        this.containerSyncKey = containerSyncKey;
    }

    /**
     * <p>The destination for container synchronization. If not set, this header is not returned by this operation.</p>
     */
    public String getContainerSyncTo() {
        return containerSyncTo;
    }

    /**
     * <p>The destination for container synchronization. If not set, this header is not returned by this operation.</p>
     */
    public void setContainerSyncTo(String containerSyncTo) {
        this.containerSyncTo = containerSyncTo;
    }

    /**
     * <p>If present, this container has versioning enabled and the value is the UTF-8 encoded name of another container.</p>
     */
    public String getVersionsLocation() {
        return versionsLocation;
    }

    /**
     * <p>If present, this container has versioning enabled and the value is the UTF-8 encoded name of another container.</p>
     */
    public void setVersionsLocation(String versionsLocation) {
        this.versionsLocation = versionsLocation;
    }

    /**
     * <p>If present, this container has versioning enabled and the value is the UTF-8 encoded name of another container.</p>
     */
    public String getHistoryLocation() {
        return historyLocation;
    }

    /**
     * <p>If present, this container has versioning enabled and the value is the UTF-8 encoded name of another container.</p>
     */
    public void setHistoryLocation(String historyLocation) {
        this.historyLocation = historyLocation;
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
     * <p>If the operation succeeds, this value is the MIME type of the list response. The MIME type is determined by the listing format specified by the request and will be one of {@literal text/plain}, {@literal application/json}, {@literal application/xml}, or {@literal text/xml}. If the operation fails, this value is the MIME type of the error text in the response body.</p>
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * <p>If the operation succeeds, this value is the MIME type of the list response. The MIME type is determined by the listing format specified by the request and will be one of {@literal text/plain}, {@literal application/json}, {@literal application/xml}, or {@literal text/xml}. If the operation fails, this value is the MIME type of the error text in the response body.</p>
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
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
}
