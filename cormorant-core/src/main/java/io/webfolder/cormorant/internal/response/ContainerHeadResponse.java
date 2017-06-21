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

public class ContainerHeadResponse implements CormorantResponse {

    @HeaderParam("X-Container-Meta-name")
    private String containerMetaName;

    @HeaderParam("Content-Length")
    private String contentLength;

    @HeaderParam("X-Container-Object-Count")
    private Long containerObjectCount;

    @HeaderParam("X-Container-Bytes-Used")
    private Long containerBytesUsed;

    @HeaderParam("X-Container-Write")
    private String containerWrite;

    @HeaderParam("X-Container-Meta-Quota-Bytes")
    private String containerMetaQuotaBytes;

    @HeaderParam("X-Container-Meta-Quota-Count")
    private String containerMetaQuotaCount;

    @HeaderParam("Accept-Ranges")
    private String acceptRanges;

    @HeaderParam("X-Container-Read")
    private String containerRead;

    @HeaderParam("X-Container-Meta-Access-Control-Expose-Headers")
    private String containerMetaAccessControlExposeHeaders;

    @HeaderParam("X-Container-Meta-Temp-URL-Key")
    private String containerMetaTempURLKey;

    @HeaderParam("X-Container-Meta-Temp-URL-Key-2")
    private String containerMetaTempURLKey2;

    @HeaderParam("X-Timestamp")
    private Long timestamp;

    @HeaderParam("X-Container-Meta-Access-Control-Allow-Origin")
    private String containerMetaAccessControlAllowOrigin;

    @HeaderParam("X-Container-Meta-Access-Control-Max-Age")
    private String containerMetaAccessControlMaxAge;

    @HeaderParam("X-Container-Sync-Key")
    private String containerSyncKey;

    @HeaderParam("X-Container-Sync-To")
    private String containerSyncTo;

    @HeaderParam("Date")
    private String date;

    @HeaderParam("X-Trans-Id")
    private String transId;

    @HeaderParam("Content-Type")
    private String contentType;

    @HeaderParam("X-Versions-Location")
    private String versionsLocation;

    @HeaderParam("X-History-Location")
    private String historyLocation;

    @HeaderParam("X-Storage-Policy")
    private String storagePolicy;

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
     * <p>Headers the Object Storage service exposes to the browser (technically, through the {@literal user-agent} setting), in the request response, separated by spaces.  By default the Object Storage service returns the following headers:</p>
     * <bullet_list bullet="-"><list_item><p>All “simple response headers” as listed on <a href="http://www.w3.org/TR/cors/#simple-response-header">http://www.w3.org/TR/cors/#simple-response-header</a>.</p>
     * </list_item><list_item><p>The headers {@literal etag}, {@literal x-timestamp}, {@literal x-trans-id}.</p>
     * </list_item><list_item><p>All metadata headers ({@literal X-Container-Meta-*} for containers and {@literal X-Object-Meta-*} for objects).</p>
     * </list_item><list_item><p>headers listed in {@literal X-Container-Meta-Access-Control-Expose-Headers}.</p>
     * </list_item></bullet_list> */
    public String getContainerMetaAccessControlExposeHeaders() {
        return containerMetaAccessControlExposeHeaders;
    }

    /**
     * <p>Headers the Object Storage service exposes to the browser (technically, through the {@literal user-agent} setting), in the request response, separated by spaces.  By default the Object Storage service returns the following headers:</p>
     * <bullet_list bullet="-"><list_item><p>All “simple response headers” as listed on <a href="http://www.w3.org/TR/cors/#simple-response-header">http://www.w3.org/TR/cors/#simple-response-header</a>.</p>
     * </list_item><list_item><p>The headers {@literal etag}, {@literal x-timestamp}, {@literal x-trans-id}.</p>
     * </list_item><list_item><p>All metadata headers ({@literal X-Container-Meta-*} for containers and {@literal X-Object-Meta-*} for objects).</p>
     * </list_item><list_item><p>headers listed in {@literal X-Container-Meta-Access-Control-Expose-Headers}.</p>
     * </list_item></bullet_list> */
    public void setContainerMetaAccessControlExposeHeaders(String containerMetaAccessControlExposeHeaders) {
        this.containerMetaAccessControlExposeHeaders = containerMetaAccessControlExposeHeaders;
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
     * <p>Originating URLs allowed to make cross-origin requests (CORS), separated by spaces. This heading applies to the container only, and all objects within the container with this header applied are CORS-enabled for the allowed origin URLs.  A browser (user-agent) typically issues a `preflighted request &lt;<a href="https://developer.mozilla.org/en">https://developer.mozilla.org/en</a>- US/docs/HTTP/Access_control_CORS&gt;`_ , which is an OPTIONS call that verifies the origin is allowed to make the request. The Object Storage service returns 200 if the originating URL is listed in this header parameter, and issues a 401 if the originating URL is not allowed to make a cross-origin request. Once a 200 is returned, the browser makes a second request to the Object Storage service to retrieve the CORS-enabled object.</p>
     */
    public String getContainerMetaAccessControlAllowOrigin() {
        return containerMetaAccessControlAllowOrigin;
    }

    /**
     * <p>Originating URLs allowed to make cross-origin requests (CORS), separated by spaces. This heading applies to the container only, and all objects within the container with this header applied are CORS-enabled for the allowed origin URLs.  A browser (user-agent) typically issues a `preflighted request &lt;<a href="https://developer.mozilla.org/en">https://developer.mozilla.org/en</a>- US/docs/HTTP/Access_control_CORS&gt;`_ , which is an OPTIONS call that verifies the origin is allowed to make the request. The Object Storage service returns 200 if the originating URL is listed in this header parameter, and issues a 401 if the originating URL is not allowed to make a cross-origin request. Once a 200 is returned, the browser makes a second request to the Object Storage service to retrieve the CORS-enabled object.</p>
     */
    public void setContainerMetaAccessControlAllowOrigin(String containerMetaAccessControlAllowOrigin) {
        this.containerMetaAccessControlAllowOrigin = containerMetaAccessControlAllowOrigin;
    }

    /**
     * <p>Maximum time for the origin to hold the preflight results. A browser may make an OPTIONS call to verify the origin is allowed to make the request. Set the value to an integer number of seconds after the time that the request was received.</p>
     */
    public String getContainerMetaAccessControlMaxAge() {
        return containerMetaAccessControlMaxAge;
    }

    /**
     * <p>Maximum time for the origin to hold the preflight results. A browser may make an OPTIONS call to verify the origin is allowed to make the request. Set the value to an integer number of seconds after the time that the request was received.</p>
     */
    public void setContainerMetaAccessControlMaxAge(String containerMetaAccessControlMaxAge) {
        this.containerMetaAccessControlMaxAge = containerMetaAccessControlMaxAge;
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
     * <p>If present, this value is the MIME type of the informational or error text in the response body.</p>
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * <p>If present, this value is the MIME type of the informational or error text in the response body.</p>
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
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
}
