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

public class ObjectGetRequest {
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

    @HeaderParam("X-Newest")
    private Boolean newest;

    @QueryParam("temp_url_sig")
    private String tempUrlSig;

    @QueryParam("temp_url_expires")
    private Integer tempUrlExpires;

    @QueryParam("filename")
    private String filename;

    @QueryParam("multipart-manifest")
    private String multipartManifest;

    @HeaderParam("Range")
    private String range;

    @HeaderParam("If-Match")
    private String ifMatch;

    @HeaderParam("If-None-Match")
    private String ifNoneMatch;

    @HeaderParam("If-Modified-Since")
    private String ifModifiedSince;

    @HeaderParam("If-Unmodified-Since")
    private String ifUnmodifiedSince;

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
     * <p>If you include the {@literal multipart-manifest=get} query parameter and the object is a large object, the object contents are not returned. Instead, the manifest is returned in the {@literal X-Object-Manifest} response header for dynamic large objects or in the response body for static large objects.</p>
     */
    public String getMultipartManifest() {
        return multipartManifest;
    }

    /**
     * <p>If you include the {@literal multipart-manifest=get} query parameter and the object is a large object, the object contents are not returned. Instead, the manifest is returned in the {@literal X-Object-Manifest} response header for dynamic large objects or in the response body for static large objects.</p>
     */
    public void setMultipartManifest(String multipartManifest) {
        this.multipartManifest = multipartManifest;
    }

    /**
     * <p>The ranges of content to get.  You can use the {@literal Range} header to get portions of data by using one or more range specifications. To specify many ranges, separate the range specifications with a comma.  The types of range specifications are:  - <strong>Byte range specification</strong>. Use FIRST_BYTE_OFFSET to specify the   start of the data range, and LAST_BYTE_OFFSET to specify the end.   You can omit the LAST_BYTE_OFFSET and if you do, the value   defaults to the offset of the last byte of data. - <strong>Suffix byte range specification</strong>. Use LENGTH bytes to specify the length of the data range.  The following forms of the header specify the following ranges of data:</p>
     * <bullet_list bullet="-"><list_item><p>{@literal Range: bytes=-5}. The last five bytes.</p>
     * </list_item><list_item><p>{@literal Range: bytes=10-15}. The six bytes of data after a 10-byte   offset.</p>
     * </list_item><list_item><p>{@literal Range: bytes=10-15,-5}. A multi-part response that contains the last five bytes and the six bytes of data after a 10-byte offset. The {@literal Content-Type} response header contains   {@literal multipart/byteranges}.</p>
     * </list_item><list_item><p>{@literal Range: bytes=4-6}. Bytes 4 to 6 inclusive.</p>
     * </list_item><list_item><p>{@literal Range: bytes=2-2}. Byte 2, the third byte of the data.</p>
     * </list_item><list_item><p>{@literal Range: bytes=6-}. Byte 6 and after.</p>
     * </list_item><list_item><p>{@literal Range: bytes=1-3,2-5}. A multi-part response that contains   bytes 1 to 3 inclusive, and bytes 2 to 5 inclusive. The {@literal Content-Type} response header contains {@literal multipart/byteranges}.</p>
     * </list_item></bullet_list> */
    public String getRange() {
        return range;
    }

    /**
     * <p>The ranges of content to get.  You can use the {@literal Range} header to get portions of data by using one or more range specifications. To specify many ranges, separate the range specifications with a comma.  The types of range specifications are:  - <strong>Byte range specification</strong>. Use FIRST_BYTE_OFFSET to specify the   start of the data range, and LAST_BYTE_OFFSET to specify the end.   You can omit the LAST_BYTE_OFFSET and if you do, the value   defaults to the offset of the last byte of data. - <strong>Suffix byte range specification</strong>. Use LENGTH bytes to specify the length of the data range.  The following forms of the header specify the following ranges of data:</p>
     * <bullet_list bullet="-"><list_item><p>{@literal Range: bytes=-5}. The last five bytes.</p>
     * </list_item><list_item><p>{@literal Range: bytes=10-15}. The six bytes of data after a 10-byte   offset.</p>
     * </list_item><list_item><p>{@literal Range: bytes=10-15,-5}. A multi-part response that contains the last five bytes and the six bytes of data after a 10-byte offset. The {@literal Content-Type} response header contains   {@literal multipart/byteranges}.</p>
     * </list_item><list_item><p>{@literal Range: bytes=4-6}. Bytes 4 to 6 inclusive.</p>
     * </list_item><list_item><p>{@literal Range: bytes=2-2}. Byte 2, the third byte of the data.</p>
     * </list_item><list_item><p>{@literal Range: bytes=6-}. Byte 6 and after.</p>
     * </list_item><list_item><p>{@literal Range: bytes=1-3,2-5}. A multi-part response that contains   bytes 1 to 3 inclusive, and bytes 2 to 5 inclusive. The {@literal Content-Type} response header contains {@literal multipart/byteranges}.</p>
     * </list_item></bullet_list> */
    public void setRange(String range) {
        this.range = range;
    }

    /**
     * <p>See <a href="http://www.ietf.org/rfc/rfc2616.txt">Request for Comments: 2616</a>.</p>
     */
    public String getIfMatch() {
        return ifMatch;
    }

    /**
     * <p>See <a href="http://www.ietf.org/rfc/rfc2616.txt">Request for Comments: 2616</a>.</p>
     */
    public void setIfMatch(String ifMatch) {
        this.ifMatch = ifMatch;
    }

    /**
     * <p>A client that has one or more entities previously obtained from the resource can verify that none of those entities is current by including a list of their associated entity tags in the {@literal If-None-Match header} field. See <a href="http://www.ietf.org/rfc/rfc2616.txt">Request for Comments: 2616</a> for details.</p>
     */
    public String getIfNoneMatch() {
        return ifNoneMatch;
    }

    /**
     * <p>A client that has one or more entities previously obtained from the resource can verify that none of those entities is current by including a list of their associated entity tags in the {@literal If-None-Match header} field. See <a href="http://www.ietf.org/rfc/rfc2616.txt">Request for Comments: 2616</a> for details.</p>
     */
    public void setIfNoneMatch(String ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    /**
     * <p>See <a href="http://www.ietf.org/rfc/rfc2616.txt">Request for Comments: 2616</a>.</p>
     */
    public String getIfModifiedSince() {
        return ifModifiedSince;
    }

    /**
     * <p>See <a href="http://www.ietf.org/rfc/rfc2616.txt">Request for Comments: 2616</a>.</p>
     */
    public void setIfModifiedSince(String ifModifiedSince) {
        this.ifModifiedSince = ifModifiedSince;
    }

    /**
     * <p>See <a href="http://www.ietf.org/rfc/rfc2616.txt">Request for Comments: 2616</a>.</p>
     */
    public String getIfUnmodifiedSince() {
        return ifUnmodifiedSince;
    }

    /**
     * <p>See <a href="http://www.ietf.org/rfc/rfc2616.txt">Request for Comments: 2616</a>.</p>
     */
    public void setIfUnmodifiedSince(String ifUnmodifiedSince) {
        this.ifUnmodifiedSince = ifUnmodifiedSince;
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
