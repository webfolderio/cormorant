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
import javax.ws.rs.QueryParam;

public class ObjectHeadRequest {
    @PathParam("account")
    private String account;

    @PathParam("container")
    private String container;

    @PathParam("object")
    private String object;

    @HeaderParam("X-Auth-Token")
    private String authToken;

    @HeaderParam("X-Service-Token")
    private String serviceToken;

    @QueryParam("temp_url_sig")
    private String tempUrlSig;

    @QueryParam("temp_url_expires")
    private Integer tempUrlExpires;

    @QueryParam("filename")
    private String filename;

    @QueryParam("multipart-manifest")
    private String multipartManifest;

    @HeaderParam("X-Newest")
    private Boolean newest;

    @HeaderParam("X-Trans-Id-Extra")
    private String transIdExtra;

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
     * <p>The unique name for the object.</p>
     */
    public String getObject() {
        return object;
    }

    /**
     * <p>The unique name for the object.</p>
     */
    public void setObject(String object) {
        this.object = object;
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
     * <p>Used with temporary URLs to sign the request with an HMAC-SHA1 cryptographic signature that defines the allowed HTTP method, expiration date, full path to the object, and the secret key for the temporary URL.</p>
     */
    public String getTempUrlSig() {
        return tempUrlSig;
    }

    /**
     * <p>Used with temporary URLs to sign the request with an HMAC-SHA1 cryptographic signature that defines the allowed HTTP method, expiration date, full path to the object, and the secret key for the temporary URL.</p>
     */
    public void setTempUrlSig(String tempUrlSig) {
        this.tempUrlSig = tempUrlSig;
    }

    /**
     * <p>The date and time in <a href="https://en.wikipedia.org/wiki/Unix_time">UNIX Epoch time stamp format</a> or <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601 UTC timestamp</a> when the signature for temporary URLs expires. For example, {@literal 1440619048} or {@literal 2015-08-26T19:57:28Z} is equivalent to {@literal Mon, Wed, 26 Aug 2015 19:57:28 GMT}.</p>
     */
    public Integer getTempUrlExpires() {
        return tempUrlExpires;
    }

    /**
     * <p>The date and time in <a href="https://en.wikipedia.org/wiki/Unix_time">UNIX Epoch time stamp format</a> or <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601 UTC timestamp</a> when the signature for temporary URLs expires. For example, {@literal 1440619048} or {@literal 2015-08-26T19:57:28Z} is equivalent to {@literal Mon, Wed, 26 Aug 2015 19:57:28 GMT}.</p>
     */
    public void setTempUrlExpires(Integer tempUrlExpires) {
        this.tempUrlExpires = tempUrlExpires;
    }

    /**
     * <p>Overrides the default file name. Object Storage generates a default file name for GET temporary URLs that is based on the object name. Object Storage returns this value in the {@literal Content-Disposition} response header. Browsers can interpret this file name value as a file attachment to save.</p>
     */
    public String getFilename() {
        return filename;
    }

    /**
     * <p>Overrides the default file name. Object Storage generates a default file name for GET temporary URLs that is based on the object name. Object Storage returns this value in the {@literal Content-Disposition} response header. Browsers can interpret this file name value as a file attachment to save.</p>
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * <p>If you include the {@literal multipart-manifest=get} query parameter and the object is a large object, the object metadata is not returned. Instead, the response headers will include the manifest metadata and for dynamic large objects the {@literal X-Object-Manifest} response header.</p>
     */
    public String getMultipartManifest() {
        return multipartManifest;
    }

    /**
     * <p>If you include the {@literal multipart-manifest=get} query parameter and the object is a large object, the object metadata is not returned. Instead, the response headers will include the manifest metadata and for dynamic large objects the {@literal X-Object-Manifest} response header.</p>
     */
    public void setMultipartManifest(String multipartManifest) {
        this.multipartManifest = multipartManifest;
    }

    /**
     * <p>If set to true , Object Storage queries all replicas to return the most recent one. If you omit this header, Object Storage responds faster after it finds one valid replica. Because setting this header to true is more expensive for the back end, use it only when it is absolutely needed.</p>
     */
    public Boolean getNewest() {
        return newest;
    }

    /**
     * <p>If set to true , Object Storage queries all replicas to return the most recent one. If you omit this header, Object Storage responds faster after it finds one valid replica. Because setting this header to true is more expensive for the back end, use it only when it is absolutely needed.</p>
     */
    public void setNewest(Boolean newest) {
        this.newest = newest;
    }

    /**
     * <p>Extra transaction information. Use the {@literal X-Trans-Id-Extra} request header to include extra information to help you debug any errors that might occur with large object upload and other Object Storage transactions.  The server appends the first 32 characters of the {@literal X-Trans-Id-Extra} request header value to the transaction ID value in the generated {@literal X-Trans-Id} response header. You must UTF-8-encode and then URL-encode the extra transaction information before you include it in the {@literal X-Trans-Id-Extra} request header.</p>
     */
    public String getTransIdExtra() {
        return transIdExtra;
    }

    /**
     * <p>Extra transaction information. Use the {@literal X-Trans-Id-Extra} request header to include extra information to help you debug any errors that might occur with large object upload and other Object Storage transactions.  The server appends the first 32 characters of the {@literal X-Trans-Id-Extra} request header value to the transaction ID value in the generated {@literal X-Trans-Id} response header. You must UTF-8-encode and then URL-encode the extra transaction information before you include it in the {@literal X-Trans-Id-Extra} request header.</p>
     */
    public void setTransIdExtra(String transIdExtra) {
        this.transIdExtra = transIdExtra;
    }
}
