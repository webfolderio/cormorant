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

public class ContainerGetRequest {
    @PathParam("account")
    private String account;

    @PathParam("container")
    private String container;

    @QueryParam("limit")
    private Integer limit;

    @QueryParam("marker")
    private String marker;

    @QueryParam("end_marker")
    private String endMarker;

    @QueryParam("prefix")
    private String prefix;

    @QueryParam("format")
    private String format;

    @QueryParam("delimiter")
    private String delimiter;

    @QueryParam("path")
    private String path;

    @HeaderParam("X-Auth-Token")
    private String authToken;

    @HeaderParam("X-Service-Token")
    private String serviceToken;

    @HeaderParam("X-Newest")
    private Boolean newest;

    @HeaderParam("Accept")
    private String accept;

    @HeaderParam("X-Container-Meta-Temp-URL-Key")
    private String containerMetaTempURLKey;

    @HeaderParam("X-Container-Meta-Temp-URL-Key-2")
    private String containerMetaTempURLKey2;

    @HeaderParam("X-Trans-Id-Extra")
    private String transIdExtra;

    @HeaderParam("X-Storage-Policy")
    private String storagePolicy;

    @QueryParam("reverse")
    private Boolean reverse;

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
     * <p>For an integer value n , limits the number of results to n .</p>
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * <p>For an integer value n , limits the number of results to n .</p>
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * <p>For a string value, x , returns container names that are greater than the marker value.</p>
     */
    public String getMarker() {
        return marker;
    }

    /**
     * <p>For a string value, x , returns container names that are greater than the marker value.</p>
     */
    public void setMarker(String marker) {
        this.marker = marker;
    }

    /**
     * <p>For a string value, x , returns container names that are less than the marker value.</p>
     */
    public String getEndMarker() {
        return endMarker;
    }

    /**
     * <p>For a string value, x , returns container names that are less than the marker value.</p>
     */
    public void setEndMarker(String endMarker) {
        this.endMarker = endMarker;
    }

    /**
     * <p>Prefix value. Named items in the response begin with this value.</p>
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * <p>Prefix value. Named items in the response begin with this value.</p>
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * <p>The response format. Valid values are {@literal json}, {@literal xml}, or {@literal plain}. The default is {@literal plain}.  If you append the {@literal format=xml} or {@literal format=json} query parameter to the storage account URL, the response shows extended container information serialized in that format.  If you append the {@literal format=plain} query parameter, the response lists the container names separated by newlines.</p>
     */
    public String getFormat() {
        return format;
    }

    /**
     * <p>The response format. Valid values are {@literal json}, {@literal xml}, or {@literal plain}. The default is {@literal plain}.  If you append the {@literal format=xml} or {@literal format=json} query parameter to the storage account URL, the response shows extended container information serialized in that format.  If you append the {@literal format=plain} query parameter, the response lists the container names separated by newlines.</p>
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * <p>Delimiter value, which returns the object names that are nested in the container. If you do not set a prefix and set the delimiter to "/" you may get unexpected results where all the objects are returned instead of only those with the delimiter set.</p>
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * <p>Delimiter value, which returns the object names that are nested in the container. If you do not set a prefix and set the delimiter to "/" you may get unexpected results where all the objects are returned instead of only those with the delimiter set.</p>
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * <p>For a string value, returns the object names that are nested in the pseudo path.</p>
     */
    public String getPath() {
        return path;
    }

    /**
     * <p>For a string value, returns the object names that are nested in the pseudo path.</p>
     */
    public void setPath(String path) {
        this.path = path;
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
     * <p>Instead of using the {@literal format} query parameter, set this header to {@literal application/json}, {@literal application/xml}, or {@literal text/xml}.</p>
     */
    public String getAccept() {
        return accept;
    }

    /**
     * <p>Instead of using the {@literal format} query parameter, set this header to {@literal application/json}, {@literal application/xml}, or {@literal text/xml}.</p>
     */
    public void setAccept(String accept) {
        this.accept = accept;
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

    public Boolean getReverse() {
        return reverse;
    }

    public void setReverse(Boolean reverse) {
        this.reverse = reverse;
    }
}
