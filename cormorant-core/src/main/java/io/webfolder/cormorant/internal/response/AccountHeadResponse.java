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

public class AccountHeadResponse implements CormorantResponse {

    @HeaderParam("Content-Length")
    private String contentLength;

    @HeaderParam("X-Account-Meta-Temp-URL-Key")
    private String accountMetaTempURLKey;

    @HeaderParam("X-Account-Meta-Temp-URL-Key-2")
    private String accountMetaTempURLKey2;

    @HeaderParam("X-Timestamp")
    private Long timestamp;

    @HeaderParam("X-Trans-Id")
    private String transId;

    @HeaderParam("Date")
    private String date;

    @HeaderParam("X-Account-Bytes-Used")
    private Long accountBytesUsed;

    @HeaderParam("X-Account-Object-Count")
    private Long accountObjectCount;

    @HeaderParam("X-Account-Container-Count")
    private Integer accountContainerCount;

    @HeaderParam("X-Account-Storage-Policy-name-Bytes-Used")
    private Integer accountStoragePolicyNameBytesUsed;

    @HeaderParam("X-Account-Storage-Policy-name-Container-Count")
    private Integer accountStoragePolicyNameContainerCount;

    @HeaderParam("X-Account-Storage-Policy-name-Object-Count")
    private Integer accountStoragePolicyNameObjectCount;

    @HeaderParam("X-Account-Meta-Quota-Bytes")
    private String accountMetaQuotaBytes;

    @HeaderParam("Content-Type")
    private String contentType;

    @HeaderParam("Accept-Ranges")
    private String acceptRanges;

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
     * <p>The secret key value for temporary URLs. If not set, this header is not returned in the response.</p>
     */
    public String getAccountMetaTempURLKey() {
        return accountMetaTempURLKey;
    }

    /**
     * <p>The secret key value for temporary URLs. If not set, this header is not returned in the response.</p>
     */
    public void setAccountMetaTempURLKey(String accountMetaTempURLKey) {
        this.accountMetaTempURLKey = accountMetaTempURLKey;
    }

    /**
     * <p>The second secret key value for temporary URLs. If not set, this header is not returned in the response.</p>
     */
    public String getAccountMetaTempURLKey2() {
        return accountMetaTempURLKey2;
    }

    /**
     * <p>The second secret key value for temporary URLs. If not set, this header is not returned in the response.</p>
     */
    public void setAccountMetaTempURLKey2(String accountMetaTempURLKey2) {
        this.accountMetaTempURLKey2 = accountMetaTempURLKey2;
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
     * <p>The total number of bytes that are stored in Object Storage for the account.</p>
     */
    public Long getAccountBytesUsed() {
        return accountBytesUsed;
    }

    /**
     * <p>The total number of bytes that are stored in Object Storage for the account.</p>
     */
    public void setAccountBytesUsed(Long accountBytesUsed) {
        this.accountBytesUsed = accountBytesUsed;
    }

    /**
     * <p>The number of objects in the account.</p>
     */
    public Long getAccountObjectCount() {
        return accountObjectCount;
    }

    /**
     * <p>The number of objects in the account.</p>
     */
    public void setAccountObjectCount(Long accountObjectCount) {
        this.accountObjectCount = accountObjectCount;
    }

    /**
     * <p>The number of containers.</p>
     */
    public Integer getAccountContainerCount() {
        return accountContainerCount;
    }

    /**
     * <p>The number of containers.</p>
     */
    public void setAccountContainerCount(Integer accountContainerCount) {
        this.accountContainerCount = accountContainerCount;
    }

    /**
     * <p>The total number of bytes that are stored in in a given storage policy, where {@literal name} is the name of the storage policy.</p>
     */
    public Integer getAccountStoragePolicyNameBytesUsed() {
        return accountStoragePolicyNameBytesUsed;
    }

    /**
     * <p>The total number of bytes that are stored in in a given storage policy, where {@literal name} is the name of the storage policy.</p>
     */
    public void setAccountStoragePolicyNameBytesUsed(Integer accountStoragePolicyNameBytesUsed) {
        this.accountStoragePolicyNameBytesUsed = accountStoragePolicyNameBytesUsed;
    }

    /**
     * <p>The number of containers in the account that use the given storage policy where {@literal name} is the name of the storage policy.</p>
     */
    public Integer getAccountStoragePolicyNameContainerCount() {
        return accountStoragePolicyNameContainerCount;
    }

    /**
     * <p>The number of containers in the account that use the given storage policy where {@literal name} is the name of the storage policy.</p>
     */
    public void setAccountStoragePolicyNameContainerCount(Integer accountStoragePolicyNameContainerCount) {
        this.accountStoragePolicyNameContainerCount = accountStoragePolicyNameContainerCount;
    }

    /**
     * <p>The number of objects in given storage policy where {@literal name} is the name of the storage policy.</p>
     */
    public Integer getAccountStoragePolicyNameObjectCount() {
        return accountStoragePolicyNameObjectCount;
    }

    /**
     * <p>The number of objects in given storage policy where {@literal name} is the name of the storage policy.</p>
     */
    public void setAccountStoragePolicyNameObjectCount(Integer accountStoragePolicyNameObjectCount) {
        this.accountStoragePolicyNameObjectCount = accountStoragePolicyNameObjectCount;
    }

    /**
     * <p>If present, this is the limit on the total size in bytes of objects stored in the account. Typically this value is set by an administrator.</p>
     */
    public String getAccountMetaQuotaBytes() {
        return accountMetaQuotaBytes;
    }

    /**
     * <p>If present, this is the limit on the total size in bytes of objects stored in the account. Typically this value is set by an administrator.</p>
     */
    public void setAccountMetaQuotaBytes(String accountMetaQuotaBytes) {
        this.accountMetaQuotaBytes = accountMetaQuotaBytes;
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

    public String getAcceptRanges() {
        return acceptRanges;
    }

    public void setAcceptRanges(String acceptRanges) {
        this.acceptRanges = acceptRanges;
    }
}
