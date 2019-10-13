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
package io.webfolder.cormorant.internal.jaxrs;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyNavigableSet;
import static java.util.Locale.ENGLISH;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

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

    private static final String META_PREFIX        = "x-account-meta-";

    private static final String META_REMOVE_PREFIX = "x-remove-account-meta-";

    private static final String BYTES_RESPONSE     = "bytes";

    private final AccountService accountService;

    private final MetadataService metadataService;

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
    public AccountGetResponseContext get(@BeanParam final AccountGetRequest request) throws IOException {
        AccountGetResponseContext context = new AccountGetResponseContext();
        NavigableSet<Container> containers = emptyNavigableSet();
        if ( request.getAccount() != null &&
                ! request.getAccount().trim().isEmpty() ) {
            containers = accountService.listContainers(request.getAccount());
        }
        Integer limit = request.getLimit();
        if ( limit != null && limit.intValue() > 0 ) {
            TreeSet<Container> set = new TreeSet<>();
            int count = 0;
            for (Container container : containers) {
                if ( limit >= 0 && count >= limit ) {
                    break;
                }
                set.add(container);
                count += 1;
            }
            containers = set;
        }
        if (TRUE.equals(request.getReverse())) {
            containers = containers.descendingSet();
        }
        final boolean inclusive = false;
        if ( request.getMarker() != null && request.getEndMarker() != null ) {
            containers = containers.subSet(new Container(request.getMarker()), inclusive, new Container(request.getEndMarker()), inclusive);
        } else if ( request.getMarker() != null && request.getEndMarker() == null ) {
            containers = containers.tailSet(new Container(request.getMarker()), inclusive);
        } else if ( request.getMarker() == null && request.getEndMarker() != null ) {
            containers = containers.headSet(new Container(request.getEndMarker()), inclusive);
        }
        for (Container next : containers) {
            AccountGetResponseBody body = new AccountGetResponseBody();
            body.setBytes(next.getBytesUsed());
            body.setCount(next.getObjectCount());
            body.setName(next.getName());
            body.setLastModified(next.getLastModified());
            context.getBody().add(body);
        }
        Account account = accountService.getAccount(request.getAccount());
        if ( account != null ) {
            context.getResponse().setTimestamp(account.getTimestamp());
            context.getResponse().setAccountBytesUsed(account.getTotalBytesUsed());
            context.getResponse().setAccountContainerCount(account.getTotalContainerCount());
            context.getResponse().setAccountObjectCount(account.getTotalObjectCount());
        }
        context.getResponse().setAcceptRanges(BYTES_RESPONSE);
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
    public Response head(@BeanParam final AccountHeadRequest request) throws IOException, SQLException {
        final AccountHeadResponse response = new AccountHeadResponse();
        response.setContentType(TEXT_PLAIN);
        response.setAcceptRanges(BYTES_RESPONSE);
        final ResponseBuilder builder = ok().entity(response);
        if ( request.getAccount() != null &&
                ! request.getAccount().trim().isEmpty() ) {
            Account account = accountService.getAccount(request.getAccount());
            if ( account != null ) {
                response.setTimestamp(account.getTimestamp());
                response.setAccountBytesUsed(account.getTotalBytesUsed());
                response.setAccountObjectCount(account.getTotalObjectCount());
                response.setAccountContainerCount(account.getTotalContainerCount());
                for (Map.Entry<String, Object> entry : metadataService.getValues(account.getName()).entrySet()) {
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
    public Response post(@BeanParam final AccountPostRequest request) throws IOException, SQLException {
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
                        if (metadataService.contains(account, name)) {
                            // The API removes the metadata item from the account.
                            metadataService.delete(account, name);
                        }
                    } else {
                        // A metadata key value.
                        // The metadata key already exists for the account.
                        if (metadataService.contains(account, name)) {
                            // The API updates the metadata key value for the account.
                            metadataService.update(account, name, value);
                        } else {
                            // A metadata key value.
                            // The metadata key does not already exist for the account.
                            // The API adds the metadata key and value pair, or item, to the account.
                            metadataService.add(account, name, value);
                        }
                    }
                }
                if (key.startsWith(META_REMOVE_PREFIX)) {
                    String name = key.substring(META_REMOVE_PREFIX.length(), key.length());
                    if (metadataService.contains(account, name)) {
                        metadataService.delete(account, name);
                    }
                }
            }
        }
        AccountPostResponse response = new AccountPostResponse();
        response.setContentType(TEXT_PLAIN);
        return status(NO_CONTENT).entity(response).build();
    }
}
