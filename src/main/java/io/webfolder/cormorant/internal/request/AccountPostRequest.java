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

public class AccountPostRequest {
    @PathParam("account")
    private String account;

    @HeaderParam("X-Auth-Token")
    private String authToken;

    @HeaderParam("X-Service-Token")
    private String serviceToken;

    @HeaderParam("X-Account-Meta-Temp-URL-Key")
    private String accountMetaTempURLKey;

    @HeaderParam("X-Account-Meta-Temp-URL-Key-2")
    private String accountMetaTempURLKey2;

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
     * <p>The secret key value for temporary URLs.</p>
     */
    public String getAccountMetaTempURLKey() {
        return accountMetaTempURLKey;
    }

    /**
     * <p>The secret key value for temporary URLs.</p>
     */
    public void setAccountMetaTempURLKey(String accountMetaTempURLKey) {
        this.accountMetaTempURLKey = accountMetaTempURLKey;
    }

    /**
     * <p>A second secret key value for temporary URLs. The second key enables you to rotate keys by having two active keys at the same time.</p>
     */
    public String getAccountMetaTempURLKey2() {
        return accountMetaTempURLKey2;
    }

    /**
     * <p>A second secret key value for temporary URLs. The second key enables you to rotate keys by having two active keys at the same time.</p>
     */
    public void setAccountMetaTempURLKey2(String accountMetaTempURLKey2) {
        this.accountMetaTempURLKey2 = accountMetaTempURLKey2;
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
}
