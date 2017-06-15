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

public class ObjectPostRequest {
    @PathParam("account")
    private String account;

    @PathParam("container")
    private String container;

    @PathParam("object")
    private String object;

    @QueryParam("bulk-delete")
    private String bulkDelete;

    @QueryParam("extract-archive")
    private String extractArchive;

    @HeaderParam("X-Auth-Token")
    private String authToken;

    @HeaderParam("X-Service-Token")
    private String serviceToken;

    @HeaderParam("X-Object-Meta-name")
    private String objectMetaName;

    @HeaderParam("X-Delete-At")
    private Integer deleteAt;

    @HeaderParam("Content-Disposition")
    private String contentDisposition;

    @HeaderParam("Content-Encoding")
    private String contentEncoding;

    @HeaderParam("X-Delete-After")
    private Integer deleteAfter;

    @HeaderParam("Content-Type")
    private String contentType;

    @HeaderParam("X-Trans-Id-Extra")
    private String transIdExtra;

    @HeaderParam("X-Object-Manifest")
    private String objectManifest;

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
     * <p>When the {@literal bulk-delete} query parameter is present in the POST request, multiple objects or containers can be deleted with a single request. See `Bulk Delete &lt;<a href="http://docs.openstack.org/developer/swift/middleware.html">http://docs.openstack.org/developer/swift/middleware.html</a>?highlight= bulk#bulk-delete&gt;`_ for how this feature is used.</p>
     */
    public String getBulkDelete() {
        return bulkDelete;
    }

    /**
     * <p>When the {@literal bulk-delete} query parameter is present in the POST request, multiple objects or containers can be deleted with a single request. See `Bulk Delete &lt;<a href="http://docs.openstack.org/developer/swift/middleware.html">http://docs.openstack.org/developer/swift/middleware.html</a>?highlight= bulk#bulk-delete&gt;`_ for how this feature is used.</p>
     */
    public void setBulkDelete(String bulkDelete) {
        this.bulkDelete = bulkDelete;
    }

    /**
     * <p>When the {@literal extract-archive} query parameter is present in the POST request, an archive (tar file) is uploaded and extracted to create multiple objects. See `Extract Archive &lt;<a href="http://docs.openstack.org/developer/swift/middleware.html">http://docs.openstack.org/developer/swift/middleware.html</a>?highlight= bulk#extract-archive&gt;`_ for how this feature is used.</p>
     */
    public String getExtractArchive() {
        return extractArchive;
    }

    /**
     * <p>When the {@literal extract-archive} query parameter is present in the POST request, an archive (tar file) is uploaded and extracted to create multiple objects. See `Extract Archive &lt;<a href="http://docs.openstack.org/developer/swift/middleware.html">http://docs.openstack.org/developer/swift/middleware.html</a>?highlight= bulk#extract-archive&gt;`_ for how this feature is used.</p>
     */
    public void setExtractArchive(String extractArchive) {
        this.extractArchive = extractArchive;
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
     * <p>A service token. See `OpenStack Service Using Composite Tokens &lt;<a href="http://docs.openstack.org/developer/swift/overview_auth.html#openstack">http://docs.openstack.org/developer/swift/overview_auth.html#openstack</a>- service-using-composite-tokens&gt;`_ for more information.</p>
     */
    public String getServiceToken() {
        return serviceToken;
    }

    /**
     * <p>A service token. See `OpenStack Service Using Composite Tokens &lt;<a href="http://docs.openstack.org/developer/swift/overview_auth.html#openstack">http://docs.openstack.org/developer/swift/overview_auth.html#openstack</a>- service-using-composite-tokens&gt;`_ for more information.</p>
     */
    public void setServiceToken(String serviceToken) {
        this.serviceToken = serviceToken;
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
     * <p>Extra transaction information. Use the {@literal X-Trans-Id-Extra} request header to include extra information to help you debug any errors that might occur with large object upload and other Object Storage transactions.  The server appends the first 32 characters of the {@literal X-Trans-Id-Extra} request header value to the transaction ID value in the generated {@literal X-Trans-Id} response header. You must UTF-8-encode and then URL-encode the extra transaction information before you include it in the {@literal X-Trans-Id-Extra} request header.  For example, you can include extra transaction information when you upload <a href="http://docs.openstack.org/developer/swift/api/large_objects.html">large objects</a> such as images. When you upload each segment and the manifest, include the same value in the {@literal X-Trans-Id-Extra} request header. If an error occurs, you can find all requests that are related to the large object upload in the Object Storage logs.  You can also use {@literal X-Trans-Id-Extra} strings to help operators debug requests that fail to receive responses. The operator can search for the extra information in the logs.</p>
     */
    public String getTransIdExtra() {
        return transIdExtra;
    }

    /**
     * <p>Extra transaction information. Use the {@literal X-Trans-Id-Extra} request header to include extra information to help you debug any errors that might occur with large object upload and other Object Storage transactions.  The server appends the first 32 characters of the {@literal X-Trans-Id-Extra} request header value to the transaction ID value in the generated {@literal X-Trans-Id} response header. You must UTF-8-encode and then URL-encode the extra transaction information before you include it in the {@literal X-Trans-Id-Extra} request header.  For example, you can include extra transaction information when you upload <a href="http://docs.openstack.org/developer/swift/api/large_objects.html">large objects</a> such as images. When you upload each segment and the manifest, include the same value in the {@literal X-Trans-Id-Extra} request header. If an error occurs, you can find all requests that are related to the large object upload in the Object Storage logs.  You can also use {@literal X-Trans-Id-Extra} strings to help operators debug requests that fail to receive responses. The operator can search for the extra information in the logs.</p>
     */
    public void setTransIdExtra(String transIdExtra) {
        this.transIdExtra = transIdExtra;
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
}
