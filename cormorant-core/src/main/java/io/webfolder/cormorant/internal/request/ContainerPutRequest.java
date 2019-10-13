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
package io.webfolder.cormorant.internal.request;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;

public class ContainerPutRequest {
    @PathParam("account")
    private String account;

    @PathParam("container")
    private String container;

    @HeaderParam("X-Auth-Token")
    private String authToken;

    @HeaderParam("X-Service-Token")
    private String serviceToken;

    @HeaderParam("X-Container-Read")
    private String containerRead;

    @HeaderParam("X-Container-Write")
    private String containerWrite;

    @HeaderParam("X-Container-Sync-To")
    private String containerSyncTo;

    @HeaderParam("X-Container-Sync-Key")
    private String containerSyncKey;

    @HeaderParam("X-Versions-Location")
    private String versionsLocation;

    @HeaderParam("X-History-Location")
    private String historyLocation;

    @HeaderParam("X-Container-Meta-name")
    private String containerMetaName;

    @HeaderParam("X-Container-Meta-Access-Control-Allow-Origin")
    private String containerMetaAccessControlAllowOrigin;

    @HeaderParam("X-Container-Meta-Access-Control-Max-Age")
    private String containerMetaAccessControlMaxAge;

    @HeaderParam("X-Container-Meta-Access-Control-Expose-Headers")
    private String containerMetaAccessControlExposeHeaders;

    @HeaderParam("X-Container-Meta-Quota-Bytes")
    private String containerMetaQuotaBytes;

    @HeaderParam("X-Container-Meta-Quota-Count")
    private String containerMetaQuotaCount;

    @HeaderParam("X-Container-Meta-Temp-URL-Key")
    private String containerMetaTempURLKey;

    @HeaderParam("X-Container-Meta-Temp-URL-Key-2")
    private String containerMetaTempURLKey2;

    @HeaderParam("X-Trans-Id-Extra")
    private String transIdExtra;

    @HeaderParam("X-Storage-Policy")
    private String storagePolicy;

    /**
     * <p>The unique name for the account. An account is also known as the project or tenant.</p>
     */
    public String getAccount() {
        return account;
    }

    /**
     * <p>The unique name for the account. An account is also known as the project or tenant.</p>
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * <p>The unique (within an account) name for the container.  The container name must be from 1 to 256 characters long and can start with any character and contain any pattern. Character set must be UTF-8. The container name cannot contain a slash ({@literal /}) character because this character delimits the container and object name. For example, the path {@literal /v1/account/www/pages} specifies the {@literal www} container, not the {@literal www/pages} container.</p>
     */
    public String getContainer() {
        return container;
    }

    /**
     * <p>The unique (within an account) name for the container.  The container name must be from 1 to 256 characters long and can start with any character and contain any pattern. Character set must be UTF-8. The container name cannot contain a slash ({@literal /}) character because this character delimits the container and object name. For example, the path {@literal /v1/account/www/pages} specifies the {@literal www} container, not the {@literal www/pages} container.</p>
     */
    public void setContainer(String container) {
        this.container = container;
    }

    /**
     * <p>Authentication token. If you omit this header, your request fails unless the account owner has granted you access through an access control list (ACL).</p>
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * <p>Authentication token. If you omit this header, your request fails unless the account owner has granted you access through an access control list (ACL).</p>
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * <p>A service token.</p>
     */
    public String getServiceToken() {
        return serviceToken;
    }

    /**
     * <p>A service token.</p>
     */
    public void setServiceToken(String serviceToken) {
        this.serviceToken = serviceToken;
    }

    /**
     * <p>Sets a container access control list (ACL) that grants read access. The scope of the access is specific to the container. The ACL grants the ability to perform GET or HEAD operations on objects in the container or to perform a GET or HEAD operation on the container itself.</p>
     * <p>The format and scope of the ACL is dependent on the authorization system used by the Object Storage service.</p>
     */
    public String getContainerRead() {
        return containerRead;
    }

    /**
     * <p>Sets a container access control list (ACL) that grants read access. The scope of the access is specific to the container. The ACL grants the ability to perform GET or HEAD operations on objects in the container or to perform a GET or HEAD operation on the container itself.</p>
     * <p>The format and scope of the ACL is dependent on the authorization system used by the Object Storage service.</p>
     */
    public void setContainerRead(String containerRead) {
        this.containerRead = containerRead;
    }

    /**
     * <p>Sets a container access control list (ACL) that grants write access. The scope of the access is specific to the container. The ACL grants the ability to perform PUT, POST and DELETE operations on objects in the container. It does not grant write access to the container metadata.</p>
     * <p>The format of the ACL is dependent on the authorization system used by the Object Storage service.</p>
     */
    public String getContainerWrite() {
        return containerWrite;
    }

    /**
     * <p>Sets a container access control list (ACL) that grants write access. The scope of the access is specific to the container. The ACL grants the ability to perform PUT, POST and DELETE operations on objects in the container. It does not grant write access to the container metadata.</p>
     * <p>The format of the ACL is dependent on the authorization system used by the Object Storage service.</p>
     */
    public void setContainerWrite(String containerWrite) {
        this.containerWrite = containerWrite;
    }

    /**
     * <p>Sets the destination for container synchronization. Used with the secret key indicated in the {@literal X -Container-Sync-Key} header. If you want to stop a container from synchronizing, send a blank value for the {@literal X-Container-Sync-Key} header.</p>
     */
    public String getContainerSyncTo() {
        return containerSyncTo;
    }

    /**
     * <p>Sets the destination for container synchronization. Used with the secret key indicated in the {@literal X -Container-Sync-Key} header. If you want to stop a container from synchronizing, send a blank value for the {@literal X-Container-Sync-Key} header.</p>
     */
    public void setContainerSyncTo(String containerSyncTo) {
        this.containerSyncTo = containerSyncTo;
    }

    /**
     * <p>Sets the secret key for container synchronization. If you remove the secret key, synchronization is halted.</p>
     */
    public String getContainerSyncKey() {
        return containerSyncKey;
    }

    /**
     * <p>Sets the secret key for container synchronization. If you remove the secret key, synchronization is halted.</p>
     */
    public void setContainerSyncKey(String containerSyncKey) {
        this.containerSyncKey = containerSyncKey;
    }

    /**
     * <p>The URL-encoded UTF-8 representation of the container that stores previous versions of objects. If neither this nor {@literal X-History-Location} is set, versioning is disabled for this container. {@literal X-Versions-Location} and {@literal X-History-Location} cannot both be set at the same time.</p>
     */
    public String getVersionsLocation() {
        return versionsLocation;
    }

    /**
     * <p>The URL-encoded UTF-8 representation of the container that stores previous versions of objects. If neither this nor {@literal X-History-Location} is set, versioning is disabled for this container. {@literal X-Versions-Location} and {@literal X-History-Location} cannot both be set at the same time.</p>
     */
    public void setVersionsLocation(String versionsLocation) {
        this.versionsLocation = versionsLocation;
    }

    /**
     * <p>The URL-encoded UTF-8 representation of the container that stores previous versions of objects. If neither this nor {@literal X-Versions-Location} is set, versioning is disabled for this container. {@literal X-History-Location} and {@literal X-Versions-Location} cannot both be set at the same time.</p>
     */
    public String getHistoryLocation() {
        return historyLocation;
    }

    /**
     * <p>The URL-encoded UTF-8 representation of the container that stores previous versions of objects. If neither this nor {@literal X-Versions-Location} is set, versioning is disabled for this container. {@literal X-History-Location} and {@literal X-Versions-Location} cannot both be set at the same time.</p>
     */
    public void setHistoryLocation(String historyLocation) {
        this.historyLocation = historyLocation;
    }

    /**
     * <p>The container metadata, where {@literal name} is the name of metadata item.  You must specify an {@literal X-Container-Meta-name} header for each metadata item (for each {@literal name}) that you want to add or update.</p>
     */
    public String getContainerMetaName() {
        return containerMetaName;
    }

    /**
     * <p>The container metadata, where {@literal name} is the name of metadata item.  You must specify an {@literal X-Container-Meta-name} header for each metadata item (for each {@literal name}) that you want to add or update.</p>
     */
    public void setContainerMetaName(String containerMetaName) {
        this.containerMetaName = containerMetaName;
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
     * <p>Sets maximum size of the container, in bytes. Typically these values are set by an administrator. Returns a 413 response (request entity too large) when an object PUT operation exceeds this quota value. This value does not take effect immediately.</p>
     */
    public String getContainerMetaQuotaBytes() {
        return containerMetaQuotaBytes;
    }

    /**
     * <p>Sets maximum size of the container, in bytes. Typically these values are set by an administrator. Returns a 413 response (request entity too large) when an object PUT operation exceeds this quota value. This value does not take effect immediately.</p>
     */
    public void setContainerMetaQuotaBytes(String containerMetaQuotaBytes) {
        this.containerMetaQuotaBytes = containerMetaQuotaBytes;
    }

    /**
     * <p>Sets maximum object count of the container. Typically these values are set by an administrator. Returns a 413 response (request entity too large) when an object PUT operation exceeds this quota value. This value does not take effect immediately.</p>
     */
    public String getContainerMetaQuotaCount() {
        return containerMetaQuotaCount;
    }

    /**
     * <p>Sets maximum object count of the container. Typically these values are set by an administrator. Returns a 413 response (request entity too large) when an object PUT operation exceeds this quota value. This value does not take effect immediately.</p>
     */
    public void setContainerMetaQuotaCount(String containerMetaQuotaCount) {
        this.containerMetaQuotaCount = containerMetaQuotaCount;
    }

    /**
     * <p>The secret key value for temporary URLs.</p>
     */
    public String getContainerMetaTempURLKey() {
        return containerMetaTempURLKey;
    }

    /**
     * <p>The secret key value for temporary URLs.</p>
     */
    public void setContainerMetaTempURLKey(String containerMetaTempURLKey) {
        this.containerMetaTempURLKey = containerMetaTempURLKey;
    }

    /**
     * <p>A second secret key value for temporary URLs. The second key enables you to rotate keys by having two active keys at the same time.</p>
     */
    public String getContainerMetaTempURLKey2() {
        return containerMetaTempURLKey2;
    }

    /**
     * <p>A second secret key value for temporary URLs. The second key enables you to rotate keys by having two active keys at the same time.</p>
     */
    public void setContainerMetaTempURLKey2(String containerMetaTempURLKey2) {
        this.containerMetaTempURLKey2 = containerMetaTempURLKey2;
    }

    /**
     * <p>Extra transaction information. Use the {@literal X-Trans-Id-Extra} request header to include extra information to help you debug any errors that might occur with large object upload and other Object Storage transactions.  The server appends the first 32 characters of the {@literal X-Trans-Id-Extra} request header value to the transaction ID value in the generated {@literal X-Trans-Id} response header. You must UTF-8-encode and then URL-encode the extra transaction information before you include it in the {@literal X-Trans-Id-Extra} request header.  For example, you can include extra transaction information when you upload large objects such as images. When you upload each segment and the manifest, include the same value in the {@literal X-Trans-Id-Extra} request header. If an error occurs, you can find all requests that are related to the large object upload in the Object Storage logs.  You can also use {@literal X-Trans-Id-Extra} strings to help operators debug requests that fail to receive responses. The operator can search for the extra information in the logs.</p>
     */
    public String getTransIdExtra() {
        return transIdExtra;
    }

    /**
     * <p>Extra transaction information. Use the {@literal X-Trans-Id-Extra} request header to include extra information to help you debug any errors that might occur with large object upload and other Object Storage transactions.  The server appends the first 32 characters of the {@literal X-Trans-Id-Extra} request header value to the transaction ID value in the generated {@literal X-Trans-Id} response header. You must UTF-8-encode and then URL-encode the extra transaction information before you include it in the {@literal X-Trans-Id-Extra} request header.  For example, you can include extra transaction information when you upload large objects such as images. When you upload each segment and the manifest, include the same value in the {@literal X-Trans-Id-Extra} request header. If an error occurs, you can find all requests that are related to the large object upload in the Object Storage logs.  You can also use {@literal X-Trans-Id-Extra} strings to help operators debug requests that fail to receive responses. The operator can search for the extra information in the logs.</p>
     */
    public void setTransIdExtra(String transIdExtra) {
        this.transIdExtra = transIdExtra;
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
