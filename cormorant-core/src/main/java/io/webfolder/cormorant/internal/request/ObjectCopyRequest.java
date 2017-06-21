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

public class ObjectCopyRequest {
    @PathParam("account")
    private String account;

    @PathParam("container")
    private String container;

    @PathParam("object")
    private String object;

    @QueryParam("multipart-manifest")
    private String multipartManifest;

    @HeaderParam("X-Auth-Token")
    private String authToken;

    @HeaderParam("X-Service-Token")
    private String serviceToken;

    @HeaderParam("Destination")
    private String destination;

    @HeaderParam("Destination-Account")
    private String destinationAccount;

    @HeaderParam("Content-Type")
    private String contentType;

    @HeaderParam("Content-Encoding")
    private String contentEncoding;

    @HeaderParam("Content-Disposition")
    private String contentDisposition;

    @HeaderParam("X-Object-Meta-name")
    private String objectMetaName;

    @HeaderParam("X-Fresh-Metadata")
    private Boolean freshMetadata;

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
     * <p>If you include the {@literal multipart-manifest=get} query parameter and the object is a large object, the object contents are not copied. Instead, the manifest is copied to the new object.</p>
     */
    public String getMultipartManifest() {
        return multipartManifest;
    }

    /**
     * <p>If you include the {@literal multipart-manifest=get} query parameter and the object is a large object, the object contents are not copied. Instead, the manifest is copied to the new object.</p>
     */
    public void setMultipartManifest(String multipartManifest) {
        this.multipartManifest = multipartManifest;
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
     * <p>The container and object name of the destination object in the form of {@literal /container/object}. You must UTF-8-encode and then URL-encode the names of the destination container and object before you include them in this header.</p>
     */
    public String getDestination() {
        return destination;
    }

    /**
     * <p>The container and object name of the destination object in the form of {@literal /container/object}. You must UTF-8-encode and then URL-encode the names of the destination container and object before you include them in this header.</p>
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * <p>Specifies the account name where the object is copied to. If not specified, the object is copied to the account which owns the object (i.e., the account in the path).</p>
     */
    public String getDestinationAccount() {
        return destinationAccount;
    }

    /**
     * <p>Specifies the account name where the object is copied to. If not specified, the object is copied to the account which owns the object (i.e., the account in the path).</p>
     */
    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
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
