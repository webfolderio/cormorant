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

import static java.util.Locale.ENGLISH;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.model.Container;
import io.webfolder.cormorant.api.model.ListContainerOptions;
import io.webfolder.cormorant.api.resource.Resource;
import io.webfolder.cormorant.api.service.AccountService;
import io.webfolder.cormorant.api.service.ContainerService;
import io.webfolder.cormorant.api.service.MetadataService;
import io.webfolder.cormorant.internal.request.ContainerDeleteRequest;
import io.webfolder.cormorant.internal.request.ContainerGetRequest;
import io.webfolder.cormorant.internal.request.ContainerHeadRequest;
import io.webfolder.cormorant.internal.request.ContainerPostRequest;
import io.webfolder.cormorant.internal.request.ContainerPutRequest;
import io.webfolder.cormorant.internal.response.ContainerDeleteResponse;
import io.webfolder.cormorant.internal.response.ContainerGetResponseContext;
import io.webfolder.cormorant.internal.response.ContainerHeadResponse;
import io.webfolder.cormorant.internal.response.ContainerPostResponse;
import io.webfolder.cormorant.internal.response.ContainerPutResponse;

@Path("/v1/{account}/{container}")
@RolesAllowed({ "cormorant-container" })
@DeclareRoles({ "cormorant-container" })
public class ContainerController<T> {

    private static final String META_PREFIX        = "x-container-meta-"       ;

    private static final String META_REMOVE_PREFIX = "x-remove-container-meta-";

    private final AccountService      accountService  ;

    private final ContainerService<T> containerService;

    private final MetadataService     metadataService ;

    @Context
    private       HttpHeaders         httpHeaders     ;

    public ContainerController(
                            final AccountService accountService,
                            final ContainerService<T> containerService,
                            final MetadataService metadataService) {
        this.accountService = accountService;
        this.containerService = containerService;
        this.metadataService = metadataService;
    }

    /**
     * Shows details for a container and lists objects, sorted by name, in the container.
     */
    @GET
    public Response get(@BeanParam final ContainerGetRequest request) throws IOException {
        boolean contains = false;
        if ( request.getAccount() != null            &&
             request.getContainer() != null          &&
             ! request.getAccount().trim().isEmpty() &&
             ! request.getContainer().trim().isEmpty() ) {
            contains = containerService.contains(request.getAccount(), request.getContainer());
            if ( ! contains ) {
                return status(NOT_FOUND).build();
            }
        }
        if ( request.getLimit() != null && request.getLimit() < 0 ) {
            final String error = "limit must be >= 0";
            throw new CormorantException(error);
        }
        ListContainerOptions options = new ListContainerOptions(
                                                    request.getPath(),
                                                    request.getDelimiter(),
                                                    request.getPrefix(),
                                                    request.getLimit(),
                                                    request.getMarker(),
                                                    request.getEndMarker(),
                                                    request.getReverse());
        Resource<?> resources = null;
        if ( request.getContainer() != null &&
                    ! request.getContainer().trim().isEmpty() ) {
            resources = containerService.listObjects(request.getAccount(),
                                                        request.getContainer(),
                                                        options);
        }
        final ContainerGetResponseContext<?> context = new ContainerGetResponseContext<>(resources);
        final Container container = accountService.getContainer(request.getAccount(), request.getContainer());
        context.getResponse().setAcceptRanges("bytes");
        context.getResponse().setContainerBytesUsed(container.getBytesUsed());
        context.getResponse().setContainerObjectCount(container.getObjectCount());
        context.getResponse().setStoragePolicy("default");
        context.getResponse().setTimestamp(container.getTimestamp());
        return ok().entity(context).build();
    }

    /**
     * Creates a container.
     * 
     * You do not need to check whether a container already exists before issuing a PUT operation because the operation is idempotent:
     * It creates a container or updates an existing container, as appropriate.
     */
    @PUT
    public Response put(@BeanParam final ContainerPutRequest request) throws SQLException {
        String name = request.getContainer();
        if (name.isEmpty()) {
            throw new CormorantException("Container name must not be empty.");
        }
        if (name.length() < 1 || name.length() > 256) {
            throw new CormorantException("Container name length must in range between 1 to 256.");
        }
        containerService.create(request.getAccount(), request.getContainer());
        final ContainerPutResponse response = new ContainerPutResponse();
        updateMetadata(request.getContainer());
        response.setContentType(TEXT_PLAIN);
        return status(CREATED)
                    .entity(response)
                    .build();
    }

    /**
     * Creates, updates, or deletes custom metadata for a container.
     * 
     * To create, update, or delete a custom metadata item, use the X-Container-Meta-{name} header, where {name} is the name of the metadata item.
     */
    @POST
    public Response post(@BeanParam final ContainerPostRequest request) throws IOException, SQLException {
        final String account = request.getAccount();
        final String container = request.getContainer();
        final boolean foundContainer = accountService.containsContainer(account, container);
        if ( account != null && container != null && foundContainer ) {
            updateMetadata(container);
        }
        ContainerPostResponse response = new ContainerPostResponse();
        response.setContentType(TEXT_PLAIN);
        return status(foundContainer ? NO_CONTENT : NOT_FOUND).entity(response).build();
    }

    /**
     * Shows container metadata, including the number of objects and the total bytes of all objects stored in the container.
     * @return 
     */
    @HEAD
    public Response head(@BeanParam final ContainerHeadRequest request) throws IOException, SQLException {
        final ContainerHeadResponse response = new ContainerHeadResponse();
        Container container = accountService.getContainer(request.getAccount(), request.getContainer());
        if ( container != null ) {
            final ResponseBuilder builder = status(NO_CONTENT).entity(response);
            response.setContentType(TEXT_PLAIN);
            response.setContainerBytesUsed(container.getBytesUsed());
            response.setContainerObjectCount(container.getObjectCount());
            response.setAcceptRanges("bytes");
            response.setTimestamp(container.getTimestamp());
            response.setStoragePolicy("default");
            for (Map.Entry<String, Object> entry : metadataService.getValues(request.getContainer()).entrySet()) {
                final String key         = entry.getKey();
                final Object headerValue = entry.getValue();
                final String headerName  = "X-Container-Meta-" + key;
                builder.header(headerName, headerValue);
            }
            return builder.build();
        } else {
            final ResponseBuilder builder = status(NOT_FOUND);
            return builder.build();
        }
    }

    protected void updateMetadata(final String container) throws SQLException {
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
                    if (metadataService.contains(container, name)) {
                        // The API removes the metadata item from the account.
                        metadataService.delete(container, name);
                    }
                } else {
                    // A metadata key value.
                    // The metadata key already exists for the account.
                    if (metadataService.contains(container, name)) {
                        // The API updates the metadata key value for the account.
                        metadataService.update(container, name, value);
                    } else {
                        // A metadata key value.
                        // The metadata key does not already exist for the account.
                        // The API adds the metadata key and value pair, or item, to the account.
                        metadataService.add(container, name, value);
                    }
                }
            }
            if (key.startsWith(META_REMOVE_PREFIX)) {
                String name = key.substring(META_REMOVE_PREFIX.length(), key.length());
                if (metadataService.contains(container, name)) {
                    metadataService.delete(container, name);
                }
            }
        }
    }

    /**
     * Deletes an empty container.
     * 
     * This operation fails unless the container is empty. An empty container has no objects.
     */
    @DELETE
    public Response delete(@BeanParam final ContainerDeleteRequest request) throws IOException, SQLException {
        boolean contains = containerService.contains(request.getAccount(), request.getContainer());
        if ( ! contains ) {
            throw new CormorantException("Container [" + request.getContainer() + "] does not exist.", NOT_FOUND);
        }
        containerService.delete(request.getAccount(), request.getContainer());
        final boolean successful = accountService.getContainer(request.getAccount(), request.getContainer()) == null;
        if ( ! successful ) {
            throw new CormorantException("There was a conflict when trying to complete your request.", CONFLICT);
        } else {
            final ContainerDeleteResponse response = new ContainerDeleteResponse();
            response.setContentType(TEXT_PLAIN);
            metadataService.delete(request.getContainer());
            return status(NO_CONTENT).entity(response).build();
        }
    }
}
