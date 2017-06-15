/**
 * cormorant - Object Storage Server
 * Copyright © 2017 WebFolder OÜ (cormorant@webfolder.io)
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
package io.webfolder.cormorant.internal.jaxrs;

import static java.util.Collections.emptyList;
import static java.util.Locale.ENGLISH;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

import java.util.List;
import java.util.Map;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import io.webfolder.cormorant.api.model.Account;
import io.webfolder.cormorant.api.model.Container;
import io.webfolder.cormorant.api.service.AccountService;
import io.webfolder.cormorant.api.service.MetadataService;
import io.webfolder.cormorant.internal.request.AccountGetRequest;
import io.webfolder.cormorant.internal.request.AccountHeadRequest;
import io.webfolder.cormorant.internal.request.AccountPostRequest;
import io.webfolder.cormorant.internal.response.AccountGetResponseBody;
import io.webfolder.cormorant.internal.response.AccountGetResponseContext;
import io.webfolder.cormorant.internal.response.AccountHeadResponse;
import io.webfolder.cormorant.internal.response.AccountPostResponse;

/**
 * Lists containers for an account.
 * 
 * <p>Creates, updates, shows, and deletes account metadata.</p>
 */
@Path("/v1/{account}")
@RolesAllowed({ "cormorant-account" })
@DeclareRoles({ "cormorant-account" })
public class AccountController {

    private static final String META_PREFIX = "x-account-meta-";

    private static final String META_REMOVE_PREFIX = "x-remove-account-meta-";

    private final AccountService accountService;

    private final MetadataService metadataService;

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpHeaders httpHeaders;

    /**
     * Shows details for an account and lists containers, sorted by name, in the account.
     */
    public AccountController(final AccountService accountService, final MetadataService metadataService) {
        this.accountService = accountService;
        this.metadataService = metadataService;
    }

    /**
     * Show account details and list containers
     */
    @GET
    public AccountGetResponseContext get(@BeanParam final AccountGetRequest request) {
        AccountGetResponseContext context = new AccountGetResponseContext();
        List<Container> containers = emptyList();
        if ( request.getAccount() != null &&
                ! request.getAccount().trim().isEmpty() ) {
            containers = accountService.listContainers(request.getAccount());
        }
        for (Container next : containers) {
            AccountGetResponseBody body = new AccountGetResponseBody();
            body.setBytes(next.getBytesUsed());
            body.setCount(next.getObjectCount());
            body.setName(next.getName());
            body.setLastModified(next.getLastModified());
            context.getBody().add(body);
        }
        return context;
    }

    /**
     * Show account metadata
     * 
     * <p>Metadata for the account includes:</p>
     * 
     * <li>Number of containers</li>
     * <li>Number of objects</li>
     * <li>Total number of bytes that are stored in Object Storage for the account</li>
     */
    @HEAD
    public Response head(@BeanParam final AccountHeadRequest request) {
        final AccountHeadResponse response = new AccountHeadResponse();
        final ResponseBuilder builder = ok().entity(response);
        if ( request.getAccount() != null &&
                ! request.getAccount().trim().isEmpty() ) {
            Account account = accountService.getAccount(request.getAccount());
            if ( account != null ) {
                response.setTimestamp(account.getTimestamp());
                response.setAccountBytesUsed(account.getTotalBytesUsed());
                response.setAccountObjectCount(account.getTotalObjectCount());
                response.setAccountContainerCount(account.getTotalContainerCount());
                for (Map.Entry<String, Object> entry : metadataService.getProperties(account.getName()).entrySet()) {
                    final String key = entry.getKey();
                    final Object headerValue = entry.getValue();
                    final String headerName = "X-Account-Meta-" + key;
                    builder.header(headerName, headerValue);
                }
            }
        }
        return builder.status(NO_CONTENT).build();
    }

    /**
     * Create, update, or delete account metadata
     * 
     * <p>
     * To create, update, or delete custom metadata, use the <strong>X-Account-Meta-{name}</strong> request header,
     * where <strong>{name}</strong> is the name of the metadata item.
     * </p>
     */
    @POST
    public Response post(@BeanParam final AccountPostRequest request) {
        String account = request.getAccount();
        if ( account != null && accountService.getAccount(account) != null ) {
            MultivaluedMap<String, String> headers = httpHeaders.getRequestHeaders();
            for (String key : headers.keySet()) {
                // Metadata keys (the name of the metadata) must be treated as case-insensitive at all times.
                key = key.toLowerCase(ENGLISH);
                if (key.startsWith(META_PREFIX)) {
                    final String name  = key.substring(META_PREFIX.length(), key.length());
                    final String value = headers.getFirst(key);
                    // A metadata key without a value.
                    // The metadata key already exists for the account.
                    if (value == null || value.isEmpty()) {
                        if (metadataService.containsProperty(account, name)) {
                            // The API removes the metadata item from the account.
                            metadataService.removeProperty(account, name);
                        } else {
                            // The API ignores the metadata key.
                        }
                    } else {
                        // A metadata key value.
                        // The metadata key already exists for the account.
                        if (metadataService.containsProperty(account, name)) {
                            // The API updates the metadata key value for the account.
                            metadataService.updateProperty(account, name, value);
                        } else {
                            // A metadata key value.
                            // The metadata key does not already exist for the account.
                            // The API adds the metadata key and value pair, or item, to the account.
                            metadataService.addProperty(account, name, value);
                        }
                    }
                }
                if (key.startsWith(META_REMOVE_PREFIX)) {
                    String name = key.substring(META_REMOVE_PREFIX.length(), key.length());
                    if (metadataService.containsProperty(account, name)) {
                        metadataService.removeProperty(account, name);
                    }
                }
            }
        }
        AccountPostResponse response = new AccountPostResponse();
        return status(NO_CONTENT).entity(response).build();
    }
}
