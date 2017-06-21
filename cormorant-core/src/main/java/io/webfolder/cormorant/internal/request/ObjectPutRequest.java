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
package io.webfolder.cormorant.internal.request;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class ObjectPutRequest {
    @PathParam("account")
    private String account;

    @PathParam("container")
    private String container;

    @PathParam("object")
    private String object;

    @QueryParam("multipart-manifest")
    private String multipartManifest;

    @QueryParam("temp_url_sig")
    private String tempUrlSig;

    @QueryParam("temp_url_expires")
    private Integer tempUrlExpires;

    @HeaderParam("X-Object-Manifest")
    private String objectManifest;

    @HeaderParam("X-Auth-Token")
    private String authToken;

    @HeaderParam("X-Service-Token")
    private String serviceToken;

    @HeaderParam("Content-Length")
    private Long contentLength;

    @HeaderParam("Transfer-Encoding")
    private String transferEncoding;

    @HeaderParam("Content-Type")
    private String contentType;

    @HeaderParam("X-Detect-Content-Type")
    private Boolean detectContentType;

    @HeaderParam("X-Copy-From")
    private String copyFrom;

    @HeaderParam("ETag")
    private String eTag;

    @HeaderParam("Content-Disposition")
    private String contentDisposition;

    @HeaderParam("Content-Encoding")
    private String contentEncoding;

    @HeaderParam("X-Delete-At")
    private Integer deleteAt;

    @HeaderParam("X-Delete-After")
    private Integer deleteAfter;

    @HeaderParam("X-Object-Meta-name")
    private String objectMetaName;

    @HeaderParam("If-None-Match")
    private String ifNoneMatch;

    @HeaderParam("X-Trans-Id-Extra")
    private String transIdExtra;

    @HeaderParam("X-Fresh-Metadata")
    private Boolean freshMetadata;

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
     * <p>If you include the {@literal multipart-manifest=put} query parameter, the object is a static large object manifest and the body contains the manifest.</p>
     */
    public String getMultipartManifest() {
        return multipartManifest;
    }

    /**
     * <p>If you include the {@literal multipart-manifest=put} query parameter, the object is a static large object manifest and the body contains the manifest.</p>
     */
    public void setMultipartManifest(String multipartManifest) {
        this.multipartManifest = multipartManifest;
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
     * <p>Set to specify that this is a dynamic large object manifest object. The value is the container and object name prefix of the segment objects in the form {@literal container/prefix}. You must UTF-8-encode and then URL-encode the names of the container and prefix before you include them in this header.</p>
     */
    public String getObjectManifest() {
        return objectManifest;
    }

    /**
     * <p>Set to specify that this is a dynamic large object manifest object. The value is the container and object name prefix of the segment objects in the form {@literal container/prefix}. You must UTF-8-encode and then URL-encode the names of the container and prefix before you include them in this header.</p>
     */
    public void setObjectManifest(String objectManifest) {
        this.objectManifest = objectManifest;
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
     * <p>Set to the length of the object content (i.e. the length in bytes of the request body). Do not set if chunked transfer encoding is being used.</p>
     */
    public Long getContentLength() {
        return contentLength;
    }

    /**
     * <p>Set to the length of the object content (i.e. the length in bytes of the request body). Do not set if chunked transfer encoding is being used.</p>
     */
    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * <p>Set to {@literal chunked} to enable chunked transfer encoding. If used, do not set the {@literal Content-Length} header to a non-zero value.</p>
     */
    public String getTransferEncoding() {
        return transferEncoding;
    }

    /**
     * <p>Set to {@literal chunked} to enable chunked transfer encoding. If used, do not set the {@literal Content-Length} header to a non-zero value.</p>
     */
    public void setTransferEncoding(String transferEncoding) {
        this.transferEncoding = transferEncoding;
    }

    /**
     * <p>Sets the MIME type for the object.</p>
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * <p>Sets the MIME type for the object.</p>
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * <p>If set to {@literal true}, Object Storage guesses the content type based on the file extension and ignores the value sent in the {@literal Content-Type} header, if present.</p>
     */
    public Boolean getDetectContentType() {
        return detectContentType;
    }

    /**
     * <p>If set to {@literal true}, Object Storage guesses the content type based on the file extension and ignores the value sent in the {@literal Content-Type} header, if present.</p>
     */
    public void setDetectContentType(Boolean detectContentType) {
        this.detectContentType = detectContentType;
    }

    /**
     * <p>If set, this is the name of an object used to create the new object by copying the {@literal X-Copy-From} object. The value is in form {@literal {container}/{object}}. You must UTF-8-encode and then URL-encode the names of the container and object before you include them in the header.  Using PUT with {@literal X-Copy-From} has the same effect as using the COPY operation to copy an object. Using {@literal Range} header with {@literal X-Copy-From} will create a new partial copied object with bytes set by {@literal Range}.</p>
     */
    public String getCopyFrom() {
        return copyFrom;
    }

    /**
     * <p>If set, this is the name of an object used to create the new object by copying the {@literal X-Copy-From} object. The value is in form {@literal {container}/{object}}. You must UTF-8-encode and then URL-encode the names of the container and object before you include them in the header.  Using PUT with {@literal X-Copy-From} has the same effect as using the COPY operation to copy an object. Using {@literal Range} header with {@literal X-Copy-From} will create a new partial copied object with bytes set by {@literal Range}.</p>
     */
    public void setCopyFrom(String copyFrom) {
        this.copyFrom = copyFrom;
    }

    /**
     * <p>The MD5 checksum value of the request body. For example, the MD5 checksum value of the object content. For manifest objects, this value is the MD5 checksum of the concatenated string of ETag values for each of the segments in the manifest. You are strongly recommended to compute the MD5 checksum value and include it in the request. This enables the Object Storage API to check the integrity of the upload. The value is not quoted.</p>
     */
    public String getETag() {
        return eTag;
    }

    /**
     * <p>The MD5 checksum value of the request body. For example, the MD5 checksum value of the object content. For manifest objects, this value is the MD5 checksum of the concatenated string of ETag values for each of the segments in the manifest. You are strongly recommended to compute the MD5 checksum value and include it in the request. This enables the Object Storage API to check the integrity of the upload. The value is not quoted.</p>
     */
    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    /**
     * <p>If set, specifies the override behavior for the browser. For example, this header might specify that the browser use a download program to save this file rather than show the file, which is the default.</p>
     */
    public String getContentDisposition() {
        return contentDisposition;
    }

    /**
     * <p>If set, specifies the override behavior for the browser. For example, this header might specify that the browser use a download program to save this file rather than show the file, which is the default.</p>
     */
    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    /**
     * <p>If set, the value of the {@literal Content-Encoding} metadata.</p>
     */
    public String getContentEncoding() {
        return contentEncoding;
    }

    /**
     * <p>If set, the value of the {@literal Content-Encoding} metadata.</p>
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
     * <p>The number of seconds after which the system removes the object. Internally, the Object Storage system stores this value in the {@literal X-Delete-At} metadata item.</p>
     */
    public Integer getDeleteAfter() {
        return deleteAfter;
    }

    /**
     * <p>The number of seconds after which the system removes the object. Internally, the Object Storage system stores this value in the {@literal X-Delete-At} metadata item.</p>
     */
    public void setDeleteAfter(Integer deleteAfter) {
        this.deleteAfter = deleteAfter;
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
     * <p>In combination with {@literal Expect: 100-Continue}, specify an {@literal "If-None-Match: *"} header to query whether the server already has a copy of the object before any data is sent.</p>
     */
    public String getIfNoneMatch() {
        return ifNoneMatch;
    }

    /**
     * <p>In combination with {@literal Expect: 100-Continue}, specify an {@literal "If-None-Match: *"} header to query whether the server already has a copy of the object before any data is sent.</p>
     */
    public void setIfNoneMatch(String ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
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

    /**
     * <p>Enables object creation that omits existing user metadata.  If set to {@literal true}, the COPY request creates an object without existing user metadata.  Default value is {@literal false}.</p>
     */
    public Boolean getFreshMetadata() {
        return freshMetadata;
    }

    /**
     * <p>Enables object creation that omits existing user metadata.  If set to {@literal true}, the COPY request creates an object without existing user metadata.  Default value is {@literal false}.</p>
     */
    public void setFreshMetadata(Boolean freshMetadata) {
        this.freshMetadata = freshMetadata;
    }
}
