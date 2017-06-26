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
package io.webfolder.cormorant.internal.jaxrs;

import static io.webfolder.cormorant.api.Json.read;
import static io.webfolder.cormorant.api.property.MetadataServiceFactory.MANIFEST_EXTENSION;
import static java.lang.Boolean.TRUE;
import static java.lang.Double.compare;
import static java.lang.Long.parseLong;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.nio.channels.Channels.newChannel;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneId.of;
import static java.time.ZonedDateTime.ofInstant;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Locale.ENGLISH;
import static java.util.regex.Pattern.compile;
import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_ENCODING;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.ETAG;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.LENGTH_REQUIRED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.REQUEST_ENTITY_TOO_LARGE;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import javax.ws.rs.core.Response.Status;

import io.webfolder.cormorant.api.Json;
import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.model.Container;
import io.webfolder.cormorant.api.model.Segment;
import io.webfolder.cormorant.api.service.AccountService;
import io.webfolder.cormorant.api.service.ChecksumService;
import io.webfolder.cormorant.api.service.ContainerService;
import io.webfolder.cormorant.api.service.MetadataService;
import io.webfolder.cormorant.api.service.ObjectService;
import io.webfolder.cormorant.api.service.UrlDecoder;
import io.webfolder.cormorant.internal.request.ObjectCopyRequest;
import io.webfolder.cormorant.internal.request.ObjectDeleteRequest;
import io.webfolder.cormorant.internal.request.ObjectGetRequest;
import io.webfolder.cormorant.internal.request.ObjectHeadRequest;
import io.webfolder.cormorant.internal.request.ObjectPostRequest;
import io.webfolder.cormorant.internal.request.ObjectPutRequest;
import io.webfolder.cormorant.internal.response.ObjectCopyResponse;
import io.webfolder.cormorant.internal.response.ObjectDeleteResponse;
import io.webfolder.cormorant.internal.response.ObjectHeadResponse;
import io.webfolder.cormorant.internal.response.ObjectPostResponse;
import io.webfolder.cormorant.internal.response.ObjectPutResponse;

@Path("/v1/{account}/{container}")
@RolesAllowed({ "cormorant-object" })
@DeclareRoles({ "cormorant-object" })
public class ObjectController<T> {

    private static final ZoneId  GMT                    = of("GMT");

    private static final DateTimeFormatter FORMATTER    = ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'")
                                                                .withLocale(ENGLISH)
                                                                .withZone(GMT);

    private static final char    FORWARD_SLASH          = '/';

    private static final String  META_PREFIX            = "x-object-meta-";

    private static final String  X_DELETE_AT            = "X-Delete-At";

    private static final String  X_DELETE_AFTER         = "X-Delete-After";

    private static final String  X_OBJECT_MANIFEST      = "X-Object-Manifest";

    private static final String  TRANSFER_ENCODING      = "Transfer-Encoding";

    private static final String  X_STATIC_LARGE_OBJECT  = "X-Static-Large-Object";

    private static final int     UNPROCESSABLE_ENTITY   = 422;

    private static final int     MAX_MANIFEST_SIZE      = 2 * 1024 * 1024           ; // 2 MB

    private static final int     MAX_MANIFEST_SEGMENTS  = 1000                      ;

    private static final long    MAX_UPLOAD_SIZE        = 5L * 1024L * 1024L * 1024L; //  5 GB

    private static final String  META_REMOVE_PREFIX     = "x-remove-object-meta-";

    private static final Pattern LEADING_SLASH          = compile("^/+");

    private static final String ACCEPT_RANGES           = "Accept-Ranges";

    private static final String BYTES_RESPONSE          = "bytes";

    private static final String MD5_OF_EMPTY_STRING     = "d41d8cd98f00b204e9800998ecf8427e";

    private static final String DIRECTORY               = "application/directory";

    private final AccountService      accountService;

    private final ContainerService<T> containerService;

    private final ObjectService<T>    objectService;

    private final ChecksumService<T>  checksumService;

    private final MetadataService     systemMetadataService;

    private final MetadataService     metadataService;

    private final UrlDecoder          urlDecoder;

    @Context
    private HttpHeaders httpHeaders;

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

    public ObjectController(
                    final AccountService      accountService  ,
                    final ContainerService<T> containerService,
                    final ObjectService<T>    objectService   ,
                    final ChecksumService<T>  checksumService ,
                    final MetadataService     metadataService ,
                    final MetadataService     systemMetadata  ,
                    final UrlDecoder          urlDecoder) {
        this.accountService        = accountService  ;
        this.containerService      = containerService;
        this.objectService         = objectService   ;
        this.checksumService       = checksumService ;
        this.metadataService       = metadataService ;
        this.systemMetadataService = systemMetadata  ;
        this.urlDecoder            = urlDecoder      ;
    }

    @GET
    @Path("/{object: .*}")
    public Response get(
                        @BeanParam final ObjectGetRequest request) {

        T container = containerService.getContainer(request.getAccount(), request.getContainer());

        if (container == null) {
            return status(NOT_FOUND).build();
        }

        T object = objectService.getObject(request.getAccount(), request.getContainer(), request.getObject());

        boolean dynamicLargeObject = false;
        String dynamicLargeObjectEtag = null;

        List<T> dynamicLargeObjects = emptyList();

        String objectManifest = null;

        // static large object
        final boolean manifest = objectService.isMultipartManifest(object);

        // dynamic large object that has X_OBJECT_MANIFEST
        if ( object != null ) {
            final String  namespace     = objectService.getNamespace(container, object);
                         objectManifest = removeLeadingSlash(systemMetadataService.getProperty(namespace, X_OBJECT_MANIFEST));
            if ( objectManifest != null ) {
                final String manifestContainer = objectManifest.substring(0, objectManifest.indexOf(FORWARD_SLASH));
                container = containerService.getContainer(request.getAccount(), manifestContainer);
                final String manifestPath = objectManifest.substring(objectManifest.indexOf(FORWARD_SLASH) + 1, objectManifest.length());
                final T manifestDirectory = objectService.getDirectory(container, manifestPath);
                if ( manifestDirectory != null ) {
                    object = manifestDirectory;
                }
                dynamicLargeObject = true;
                dynamicLargeObjects    = objectService.listDynamicLargeObject(container, object);
                dynamicLargeObjectEtag = checksumService.calculateChecksum(dynamicLargeObjects);
            }
        }

        if ( object == null && ! manifest ) {

            // dynamic large object without X_OBJECT_MANIFEST
            if (objectService.isValidPath(container, request.getObject())) {
                final T directory = objectService.getDirectory(container, request.getObject());
                if ( directory != null ) {
                    final String  manifestNamespace = objectService.getNamespace(container, directory);
                                     objectManifest = systemMetadataService.getProperty(manifestNamespace, X_OBJECT_MANIFEST);
                    if ( objectManifest != null ) {
                        final String directoryPath = removeLeadingSlash(objectManifest);
                        final String containerName = directoryPath.indexOf(FORWARD_SLASH) > 0 ? directoryPath.substring(0, directoryPath.indexOf(FORWARD_SLASH)) : null;
                        if ( containerName != null ) {
                            T dynamicLargeObjectContainer = containerService.getContainer(request.getAccount(), containerName);
                            if (objectService.isValidPath(dynamicLargeObjectContainer, directoryPath)) {
                                final String objectPath = directoryPath.substring(directoryPath.indexOf(FORWARD_SLASH) + 1, directoryPath.length());
                                object = objectService.getDirectory(dynamicLargeObjectContainer, objectPath);
                                container = dynamicLargeObjectContainer;
                                dynamicLargeObject = true;
                                dynamicLargeObjects    = objectService.listDynamicLargeObject(container, object);
                                dynamicLargeObjectEtag = checksumService.calculateChecksum(dynamicLargeObjects);
                            }
                        }
                    }
                }
            }

            if ( ! dynamicLargeObject ) {
                return status(NOT_FOUND).build();
            }
        }

        if ( "get".equalsIgnoreCase(request.getMultipartManifest()) && ! manifest ) {
            throw new CormorantException("Invalid multipart manifest request. Object [" +
                            request.getObject() + "] is not a multipart manifest.");
        }

        final String  namespace          = objectService.getNamespace(container, object);
        final long    lastModified       = objectService.getLastModified(object);
        final long    creationTime       = objectService.getCreationTime(object);
        final String  etag               = dynamicLargeObjectEtag != null ? dynamicLargeObjectEtag : checksumService.calculateChecksum(container, object);
        final String  contentDisposition = systemMetadataService.getProperty(namespace, CONTENT_DISPOSITION);

        final Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, Object> entry : metadataService.getProperties(namespace).entrySet()) {
            final String key         = entry.getKey();
            final Object headerValue = entry.getValue();
            final String headerName  = "X-Object-Meta-" + key;
            headers.put(headerName, valueOf(headerValue));
        }

        final Map<String, Object> systemMetadata = systemMetadataService.getProperties(namespace);

        if ( systemMetadata.containsKey(CONTENT_ENCODING)) {
            headers.put(CONTENT_ENCODING, (String) systemMetadata.get(CONTENT_ENCODING));
        }
        if ( systemMetadata.containsKey(CONTENT_DISPOSITION)) {
            headers.put(CONTENT_DISPOSITION, (String) systemMetadata.get(CONTENT_DISPOSITION));
        }
        if ( systemMetadata.containsKey(X_DELETE_AT)) {
            headers.put(X_DELETE_AT, (String) systemMetadata.get(X_DELETE_AT));
        }
        if ( systemMetadata.containsKey(X_DELETE_AFTER)) {
            headers.put(X_DELETE_AFTER, (String) systemMetadata.get(X_DELETE_AFTER));
        }
        if ( systemMetadata.containsKey(X_OBJECT_MANIFEST)) {
            headers.put(X_OBJECT_MANIFEST, (String) systemMetadata.get(X_OBJECT_MANIFEST));
        }

        if ( objectManifest != null ) {
            headers.put(X_OBJECT_MANIFEST, objectManifest);
        }
        
        if (systemMetadata.containsKey(TRANSFER_ENCODING)) {
            headers.put(TRANSFER_ENCODING, (String) systemMetadata.get(TRANSFER_ENCODING));
        }

        final String contentType;
        final Long   size;

        List<Segment<T>> staticSegments;
        
        final boolean staticLargeObject = objectService.isMultipartManifest(object);
        if (staticLargeObject) {
            headers.put(X_STATIC_LARGE_OBJECT, "True");
            staticSegments = objectService.listStaticLargeObject(request.getAccount(), object);
            if ( !staticSegments.isEmpty() ) {
                long totalSize = 0L;
                for (Segment<T> next : staticSegments) {
                    totalSize += next.getSize();
                }
                size = totalSize;
                contentType = staticSegments.get(0).getContentType();
            } else {
                size = 0L;
                contentType = null;
            }
        } else {
            staticSegments = emptyList();
            contentType = systemMetadataService.getProperty( ! dynamicLargeObjects.isEmpty() ?
                                    objectService.getNamespace(container, dynamicLargeObjects.get(0)) : namespace, CONTENT_TYPE);
            size = dynamicLargeObject ? objectService.getDyanmicObjectSize(container, object) : objectService.getSize(object);
        }

        final boolean largeObject = dynamicLargeObject || manifest;
        final String objectEtag;
        if (largeObject) {
            objectEtag = "\"" + (size == 0L ? MD5_OF_EMPTY_STRING : etag) + "\"";
        } else {
            objectEtag = size == 0L ? MD5_OF_EMPTY_STRING : etag;
        }

        final Resource<T> resource = new Resource<>(container   , object,
                                                    size        , lastModified,
                                                    creationTime, objectEtag,
                                                    contentType , contentDisposition,
                                                    manifest    , dynamicLargeObject,
                                                    headers     , staticSegments);

        final ResourceHandler<T> handler = new ResourceHandler<>(
                                                objectService,
                                                resource);

        final Status status;
        try {
            status = handler.handle(this.request, this.response);
        } catch (IOException e) {
            throw new CormorantException(e);
        }

        final ResponseBuilder builder = status(status);
        return builder.build();
    }

    @PUT
    @Path("/{object: .*}")
    public Response put(
                        @BeanParam final ObjectPutRequest request,
                                   final InputStream      is) {

        final String  transferEncoding = request.getTransferEncoding();
        final Long    contentLength    = request.getContentLength();
        final boolean chunked          = isChunked(transferEncoding);

        if (transferEncoding == null && contentLength == null) {
            throw new CormorantException("Missing Transfer-Encoding or Content-Length request header.", LENGTH_REQUIRED);
        }

        if ( ! chunked && contentLength < 0 ) {
            throw new CormorantException("Content-Length must be >= 0.");
        }

        if ( ! chunked && contentLength > MAX_UPLOAD_SIZE ) {
            throw new CormorantException("Content-Length must be < 5 GB.");
        }

        if (request.getObject().endsWith(MANIFEST_EXTENSION)) {
            throw new CormorantException("Invalid object name [" + request.getObject() + "]. [.multipart] extension is disallowed.");
        }

        checkQuota(contentLength, request.getAccount(), request.getContainer());

        final ObjectPutResponse response = new ObjectPutResponse();

        final ResponseBuilder builder = status(CREATED).entity(response);

        if ("application/directory".equalsIgnoreCase(request.getContentType())) {
            createDirectory(request, response, is);
        } else if ("put".equalsIgnoreCase(request.getMultipartManifest())) {
            uploadManifest(request, response, is);
        } else {
            upload(request, response, is);
        }

        return builder.build();
    }

    @HEAD
    @Path("/{object: .*}")
    public Response head(@BeanParam final ObjectHeadRequest request) {
        T object = objectService.getObject(request.getAccount(), request.getContainer(), request.getObject());
        boolean dir = false;
        if (object == null) {
            final T container = containerService.getContainer(request.getAccount(), request.getContainer());
            if ( container != null ) {
                final T directory = objectService.getDirectory(container, request.getObject());
                if ( directory != null ) {
                    object = directory;
                    dir = true;
                }
            }
        }
        T container = containerService.getContainer(request.getAccount(), request.getContainer());
        if (object == null) {
            return status(NOT_FOUND).build();
        }
        final String namespace = objectService.getNamespace(container, object);
        if (dir) {
            final boolean deleted = "deleted".equals(systemMetadataService.getProperty(namespace, "X-Cormorant-Deleted"));
            if (deleted) {
                return status(NOT_FOUND).build();
            }
        }
        final ObjectHeadResponse response = new ObjectHeadResponse();
        final ResponseBuilder    builder  = ok().entity(response);
        for (Map.Entry<String, Object> entry : metadataService.getProperties(namespace).entrySet()) {
            final String key         = entry.getKey();
            final Object headerValue = entry.getValue();
            final String headerName  = "X-Object-Meta-" + key;
            builder.header(headerName, headerValue);
        }
        final Map<String, Object> properties = systemMetadataService.getProperties(namespace);

        final boolean dynamicLargeObject = properties.containsKey(X_OBJECT_MANIFEST);
        final boolean staticLargeObject  = objectService.isMultipartManifest(object);
        final boolean largeObject        = dynamicLargeObject || staticLargeObject;

        properties.put(CONTENT_LENGTH, objectService.getSize(object));

        // Etag value of a large object is enclosed in double-quotations.
        if (largeObject) {
            String etag = (String) properties.get(ETAG);
            if (dynamicLargeObject) {
                if ( object != null ) {
                    final String objectManifest = removeLeadingSlash(systemMetadataService.getProperty(namespace, X_OBJECT_MANIFEST));
                    if ( objectManifest != null ) {
                        container = containerService.getContainer(request.getAccount(), objectManifest.substring(0, objectManifest.indexOf(FORWARD_SLASH)));
                        final String manifestPath = objectManifest.substring(objectManifest.indexOf(FORWARD_SLASH) + 1, objectManifest.length());
                        T manifestDirectory = objectService.getDirectory(container, manifestPath);
                        if ( manifestDirectory != null ) {
                            object = manifestDirectory;
                        }
                    }
                }
                final List<T> objects = objectService.listDynamicLargeObject(container, object);
                if ( ! objects.isEmpty() ) {
                    etag = checksumService.calculateChecksum(objects);
                }
                final long size = objectService.getDyanmicObjectSize(container, object);
                properties.put(CONTENT_LENGTH, size);
            }
            if (etag == null) {
                etag = checksumService.calculateChecksum(container, object);
            }
            if ( etag != null &&
                    ! etag.trim().isEmpty() &&
                    ! etag.startsWith("\"") &&
                    ! etag.endsWith("\"") ) {
                properties.put(ETAG, "\"" + etag + "\"");
            }
        }

        if ( "0".equals(properties.get(CONTENT_LENGTH)) || ! properties.containsKey(CONTENT_LENGTH) ) {
            properties.put(ETAG,
                    (properties.containsKey(ETAG) && properties.get(ETAG).toString().contains("\"") ? "\"" : "") + MD5_OF_EMPTY_STRING +
                    (properties.containsKey(ETAG) && properties.get(ETAG).toString().contains("\"") ? "\"" : ""));
        }

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            final String headerName  = entry.getKey();
            final Object headerValue = entry.getValue();
            builder.header(headerName, headerValue);
        }

        if (properties.containsKey(CONTENT_TYPE)) {
            response.setContentType((String) properties.get(CONTENT_TYPE));
        }

        if (dir) {
            response.setContentType(DIRECTORY);
        }

        final long timestamp = objectService.getCreationTime(object);
        response.setTimestamp(timestamp);

        if (staticLargeObject) {
            builder.header(X_STATIC_LARGE_OBJECT, "True");
            if ( ! properties.containsKey(CONTENT_TYPE) || response.getContentType() == null ) {
                response.setContentType(APPLICATION_JSON);
            }
        }

        final String lastModified = FORMATTER.format(ofInstant(ofEpochMilli(objectService.getLastModified(object)), GMT));
        builder.header(HttpHeaders.LAST_MODIFIED, lastModified);
        builder.header(ACCEPT_RANGES, BYTES_RESPONSE);
        return builder.entity(response).build();
    }

    @SuppressWarnings("unchecked")
    @DELETE
    @Path("/{object: .*}")
    public Response delete(@BeanParam final ObjectDeleteRequest request) {
        final T container = containerService.getContainer(request.getAccount(), request.getContainer());
        if (container == null) {
            return status(NO_CONTENT).build();
        }
        T object = objectService.getObject(request.getAccount(), request.getContainer(), request.getObject());
        boolean isDirectory = false;
        if (object == null) {
            final T directory = objectService.getDirectory(container, request.getObject());
            if ( directory != null ) {
                object = directory;
                isDirectory = true;
            }
        }
        if ( ! isDirectory && object == null ) {
            return status(NOT_FOUND).build();
        }
        final boolean deleteStaticLargeObject = "delete".equals(request.getMultipartManifest())
                                                        && objectService.isMultipartManifest(object);
        final boolean emptyDirectory = isDirectory && objectService.isEmptyDirectory(container, object);
        if ( isDirectory && ! emptyDirectory ) {
            final String namespace = objectService.getNamespace(container, object);
            final String deleted   = systemMetadataService.getProperty(namespace, "X-Cormorant-Deleted");
            if (deleted == null) {
                systemMetadataService.delete(namespace);
                metadataService.delete(namespace);
                systemMetadataService.addProperty(namespace, "X-Cormorant-Deleted", "true");
            }
            return status(NO_CONTENT)
                    .build();

        } else {
            if (deleteStaticLargeObject) {
                try {
                    try (ReadableByteChannel channel = objectService.getReadableChannel(object)) {
                        try (Scanner scanner = new Scanner(Channels.newInputStream(channel))) {
                            scanner.useDelimiter("\\A");
                            if (scanner.hasNext()) {
                                String content = scanner.next();
                                Json json = read(content);
                                List<Object> list = json.asList();
                                for (Object next : list) {
                                    Map<String, Object> map = (Map<String, Object>) next;
                                    String path = (String) map.get("path");
                                    final String directoryPath = removeLeadingSlash(path);
                                    final String containerName = directoryPath.indexOf(FORWARD_SLASH) > 0 ? directoryPath.substring(0, directoryPath.indexOf(FORWARD_SLASH)) : null;
                                    final T manifestContainer = containerService.getContainer(request.getAccount(), containerName);
                                    if ( manifestContainer != null ) {
                                        String objectPath = directoryPath.substring(directoryPath.indexOf(FORWARD_SLASH) + 1, directoryPath.length());
                                        T manifestObject = objectService.getObject(request.getAccount(), containerName, objectPath);
                                        if ( manifestObject != null ) {
                                            if ( objectService.exist(manifestContainer, manifestObject) &&
                                                    ! objectService.isDirectory(manifestContainer, manifestObject) ) {

                                                final long size = objectService.getSize(manifestObject);

                                                objectService.delete(manifestContainer, manifestObject);

                                                final String namespace = objectService.getNamespace(manifestContainer, manifestObject);
                                                systemMetadataService.delete(namespace);
                                                metadataService.delete(namespace);

                                                Container containerInfo = accountService.getContainer(request.getAccount(), containerName);
                                                containerInfo.decrementObjectCount();
                                                containerInfo.removeBytesUsed(size);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new CormorantException(e);
                }
            }

            final String namespace = objectService.getNamespace(container, object);

            final long size = objectService.getSize(object);

            final String objectManifest = systemMetadataService.getProperty(namespace, X_OBJECT_MANIFEST);
            final boolean dynamicLargeObject = objectManifest != null;
            if (dynamicLargeObject && size == 0) {
                final String manifestContainerName = objectManifest.substring(0, objectManifest.indexOf(FORWARD_SLASH));
                final T manifestContainer = containerService.getContainer(request.getAccount(), manifestContainerName);
                for (T next : objectService.listDynamicLargeObject(manifestContainer, object)) {
                    objectService.delete(manifestContainer, next);
                    objectService.getNamespace(manifestContainer, object);
                    final String nextNamespace = objectService.getNamespace(manifestContainer, next);
                    systemMetadataService.delete(nextNamespace);
                    metadataService.delete(nextNamespace);
                }
            }

            objectService.delete(container, object);
            systemMetadataService.delete(namespace);
            metadataService.delete(namespace);

            if ( ! isDirectory && ! objectService.isMultipartManifest(object) ) {
                Container containerInfo = accountService.getContainer(request.getAccount(), request.getContainer());
                containerInfo.decrementObjectCount();
                containerInfo.removeBytesUsed(size);
            }

            return ok()
                    .entity(new ObjectDeleteResponse())
                    .status(deleteStaticLargeObject ? OK : NO_CONTENT)
                    .build();
        }
    }

    @POST
    @Path("/{object: .*}")
    public Response post(@BeanParam final ObjectPostRequest request) {
        final T object;
        // dynamic large object
        final T dynamicLargeObjectContainer;
        if ( request.getObjectManifest() != null ) {
            final String directoryPath = removeLeadingSlash(request.getObjectManifest());
            final String containerName = directoryPath.indexOf(FORWARD_SLASH) > 0 ? directoryPath.substring(0, directoryPath.indexOf(FORWARD_SLASH)) : null;
            if ( containerName != null ) {
                dynamicLargeObjectContainer = containerService.getContainer(request.getAccount(), containerName);
                if (objectService.isValidPath(dynamicLargeObjectContainer, directoryPath)) {
                    final String objectPath = directoryPath.substring(directoryPath.indexOf(FORWARD_SLASH) + 1, directoryPath.length());
                    object = objectService.getDirectory(dynamicLargeObjectContainer, objectPath);
                } else {
                    object = null;
                }
            } else {
                dynamicLargeObjectContainer = null;
                object = null;
            }
        } else {
            object = objectService.getObject(request.getAccount(), request.getContainer(), request.getObject());
            dynamicLargeObjectContainer = null;
        }
        if (object == null) {
            return status(NOT_FOUND).build();
        } else {
            final ObjectPostResponse response  = new ObjectPostResponse();
            final ResponseBuilder    builder   = status(ACCEPTED).entity(response);
            final T                  container = dynamicLargeObjectContainer != null ?
                                                 dynamicLargeObjectContainer         :
                                                 containerService.getContainer(request.getAccount(), request.getContainer());
            final String             namespace = objectService.getNamespace(container, object);
            updateMetadata(namespace);

            // In addition to the custom metadata,
            // client can update the Content-Type, Content-Encoding, Content-Disposition, and X-Delete-At
            final String contentType = httpHeaders.getHeaderString(CONTENT_TYPE);

            Map<String, Object> systemMetadata = new HashMap<>();
            if ( contentType != null && ! contentType.isEmpty() ) {
                systemMetadata.put(CONTENT_TYPE, contentType);
            }
            final String contentEncoding = httpHeaders.getHeaderString(CONTENT_ENCODING);
            if ( contentEncoding != null ) {
                systemMetadata.put(CONTENT_ENCODING, contentEncoding);
            }
            final String contentDisposition = httpHeaders.getHeaderString(CONTENT_DISPOSITION);
            if ( contentDisposition != null ) {
                systemMetadata.put(CONTENT_DISPOSITION, contentDisposition);
            }
            final String deleteAt = httpHeaders.getHeaderString(X_DELETE_AT);
            if ( deleteAt != null ) {
                systemMetadata.put(X_DELETE_AT, deleteAt);
            }
            final String deleteAfter = httpHeaders.getHeaderString(X_DELETE_AFTER);
            if ( deleteAfter != null ) {
                final long delay = parseLong(deleteAfter);
                if (delay > 0) {
                    // convert X-Delete-After header into an X-Delete-At header using its current time plus the value given.
                    systemMetadata.put(X_DELETE_AT, valueOf(delay + currentTimeMillis()));
                }
            }
            final String objectManifest = httpHeaders.getHeaderString(X_OBJECT_MANIFEST);
            if ( dynamicLargeObjectContainer != null && objectManifest != null ) {
                systemMetadata.put(X_OBJECT_MANIFEST, objectManifest);
            }
            systemMetadataService.setProperties(namespace, systemMetadata);

            putSystemMetadata(namespace, builder);

            return builder.build();
        }
    }

    @COPY
    @Path("/{object: .*}")
    public Response copy(@BeanParam ObjectCopyRequest request) {
        final String targetAccount = request.getDestinationAccount() == null ||
                                            request.getDestinationAccount().trim().isEmpty() ?
                                            request.getAccount() : request.getDestinationAccount();

        final String targetPath = urlDecoder.decode(request.getDestination());
        final int    start      = targetPath.indexOf(FORWARD_SLASH);

        if (start < 0) {
            throw new CormorantException("Failed to copy object [" + targetPath + "]. Missing container name.");
        }

        final String targetContainerName = targetPath.substring(0, start);
        final String targetObjectPath    = targetPath.substring(start + 1, targetPath.length());
        final T      targetContainer     = containerService.getContainer(targetAccount, targetContainerName);

        if (targetContainer == null) {
            throw new CormorantException("Failed to copy object [" + targetPath + "]. " +
                            "Destination Container [" + targetContainer + "] does not exist.");
        }

        final boolean validTargetPath = objectService.isValidPath(targetContainer, targetObjectPath);
        if ( ! validTargetPath ) {
            throw new CormorantException("Failed to copy object [" +
                            targetPath + "]. Invalid destination path [" + targetObjectPath + "].");
        }        
        
        final T sourceContainer = containerService.getContainer(request.getAccount(), request.getContainer());
        if (sourceContainer == null) {
            throw new CormorantException("Failed to copy object. Source container [" + request.getContainer()  + "] does not exist.");
        }

        boolean validSourcePath = objectService.isValidPath(sourceContainer, request.getObject());

        if ( ! validSourcePath ) {
            throw new CormorantException("Failed to copy object [" +
                            targetPath + "]. Invalid source path [" + request.getObject() + "].");
        }

        final T sourceObject = objectService.getObject(request.getAccount(), request.getContainer(), request.getObject());

        if (sourceObject == null) {
            T sourceDirectory = objectService.getDirectory(sourceContainer, request.getObject());
            if (objectService.isDirectory(sourceContainer, sourceDirectory)) {
                // copy directory
                if (objectService.getObject(targetAccount, targetContainerName, targetObjectPath) == null) {
                    final T targetDirectory = objectService.createDirectory(targetAccount, targetContainer, targetObjectPath);
                    final String targetNamespace = objectService.getNamespace(targetContainer, targetDirectory);
                    systemMetadataService.delete(targetNamespace);
                    metadataService.delete(targetNamespace);
                    final String  sourceNamespace = objectService.getNamespace(sourceContainer, sourceDirectory);
                    final Map<String, Object> systemMetadata = systemMetadataService.getProperties(sourceNamespace);
                    systemMetadataService.setProperties(targetNamespace, systemMetadata);
                    if ( request.getFreshMetadata() == null || ! TRUE.equals(request.getFreshMetadata()) ) {
                        final Map<String, Object> metadata = metadataService.getProperties(sourceNamespace);
                        if ( ! metadata.isEmpty() ) {
                            metadataService.setProperties(targetNamespace, metadata);
                        }
                    }
                    return status(CREATED).entity(new ObjectCopyResponse()).build();
                }
            }
            throw new CormorantException("Failed to copy object [" +
                            targetPath + "]. Source object [" + request.getObject() + "] not found.");
        }

        final Long sourceContentLength = new Long(objectService.getSize(sourceObject));

        checkQuota(sourceContentLength, targetAccount, targetContainerName);

        final T targetObject = objectService.copyObject(targetAccount,
                                                        targetContainer,
                                                        targetObjectPath,
                                                        request.getAccount(),
                                                        sourceContainer,
                                                        sourceObject,
                                                        request.getMultipartManifest());

        final String  sourceNamespace = objectService.getNamespace(sourceContainer, sourceObject);
        final String targetNamespace  = objectService.getNamespace(targetContainer, targetObject);

        final boolean copySelf = targetObject.equals(sourceObject);

        if ( ! copySelf ) {
            systemMetadataService.delete(targetNamespace);
            metadataService.delete(targetNamespace);
        }

        final Map<String, Object> systemMetadata = systemMetadataService.getProperties(sourceNamespace);
        systemMetadataService.setProperties(targetNamespace, systemMetadata);

        if ( request.getFreshMetadata() == null || ! TRUE.equals(request.getFreshMetadata()) ) {
            final Map<String, Object> sourceMetadata = metadataService.getProperties(sourceNamespace);
            if ( ! sourceMetadata.isEmpty() ) {
                HashMap<String, Object> targetAllMetadata = new HashMap<>(metadataService.getProperties(targetNamespace));
                targetAllMetadata.putAll(sourceMetadata);
                metadataService.setProperties(targetNamespace, targetAllMetadata);
            }
        }

        updateMetadata(targetNamespace);

        final String checksum           = checksumService.calculateChecksum(targetContainer, targetObject);
        final String sourceLastModified = FORMATTER.format(ofInstant(ofEpochMilli(objectService.getLastModified(sourceObject)), GMT));
        final String targetLastModified = FORMATTER.format(ofInstant(ofEpochMilli(objectService.getLastModified(targetObject)), GMT));

        final ObjectCopyResponse response = new ObjectCopyResponse();
        response.setCopiedFromAccount(request.getAccount());
        response.setCopiedFrom(objectService.toPath(sourceContainer, sourceObject));
        response.setCopiedFromLastModified(sourceLastModified);
        response.setETag(checksum);
        response.setLastModified(targetLastModified);

        final String contentType = httpHeaders.getHeaderString(CONTENT_TYPE);
        if ( contentType != null ) {
            systemMetadata.put(CONTENT_TYPE, contentType);
            systemMetadataService.updateProperty(targetNamespace, CONTENT_TYPE, contentType);
        }

        final ResponseBuilder builder = status(CREATED).entity(response);

        final String namespace = objectService.getNamespace(targetContainer, targetObject);
        putSystemMetadata(namespace, builder);

        for (Map.Entry<String, Object> entry : metadataService.getProperties(targetNamespace).entrySet()) {
            final String key         = entry.getKey();
            final Object headerValue = entry.getValue();
            final String headerName  = "X-Object-Meta-" + key;
            builder.header(headerName, headerValue);
        }

        return builder.build();
    }

    protected void checkQuota(final Long contentLength, final String accountName, final String containerName) {
        final long      maxQuotaBytes = containerService.getMaxQuotaBytes(accountName, containerName);
        final long      maxQuotaCount = containerService.getMaxQuotaCount(accountName, containerName);
        final Container container     = accountService.getContainer(accountName, containerName);

        // Unable to reject chunked transfer uploads.
        // The system cannot know if the request will exceed the quota so the system allows the request.
        // However, once the quota is exceeded, any subsequent uploads that use chunked transfer encoding fail.
        if ( contentLength != null ) {
            if (container.getBytesUsed() + contentLength > maxQuotaBytes) {
                throw new CormorantException("The request size exceeded the configured maximum quota bytes [" + maxQuotaBytes + "].",
                                REQUEST_ENTITY_TOO_LARGE);
            }
            if (container.getObjectCount().longValue() + 1 > maxQuotaCount) {
                throw new CormorantException("The request size exceeded the configured maximum quota count [" + maxQuotaCount + "].",
                                REQUEST_ENTITY_TOO_LARGE);
            }
        }
    }

    protected void putSystemMetadata(final String namespace, final ResponseBuilder builder) {
        if ( systemMetadataService.getProperty(namespace, CONTENT_TYPE) != null ) {
            response.setContentType(systemMetadataService.getProperty(namespace, CONTENT_TYPE));
        }
        if ( systemMetadataService.getProperty(namespace, CONTENT_ENCODING) != null ) {
            builder.header(CONTENT_ENCODING, systemMetadataService.getProperty(namespace, CONTENT_ENCODING));
        }
        if ( systemMetadataService.getProperty(namespace, CONTENT_DISPOSITION) != null ) {
            builder.header(CONTENT_DISPOSITION, systemMetadataService.getProperty(namespace, CONTENT_DISPOSITION));
        }
        if ( systemMetadataService.getProperty(namespace, X_DELETE_AT) != null ) {
            builder.header(X_DELETE_AT, systemMetadataService.getProperty(namespace, X_DELETE_AT));
        }
        if ( systemMetadataService.getProperty(namespace, X_DELETE_AFTER) != null ) {
            builder.header(X_DELETE_AFTER, systemMetadataService.getProperty(namespace, X_DELETE_AFTER));
        }
        if ( systemMetadataService.getProperty(namespace, X_OBJECT_MANIFEST) != null ) {
            builder.header(X_OBJECT_MANIFEST, systemMetadataService.getProperty(namespace, X_OBJECT_MANIFEST));
        }
    }

    protected void updateMetadata(String namespace) {
        final MultivaluedMap<String, String> headers = httpHeaders.getRequestHeaders();
        for (String key : headers.keySet()) {
            // Metadata keys (the name of the metadata) must be treated as case-insensitive at all times.
            key = key.toLowerCase(ENGLISH);
            if (key.startsWith(META_PREFIX)) {
                final String name  = key.substring(META_PREFIX.length(), key.length());
                final String value = headers.getFirst(key);
                // A metadata key without a value.
                // The metadata key already exists for the account.
                if (value == null) {
                    if (metadataService.containsProperty(namespace, name)) {
                        // The API removes the metadata item from the account.
                        metadataService.removeProperty(namespace, name);
                    }
                } else {
                    // A metadata key value.
                    // The metadata key already exists for the account.
                    if (metadataService.containsProperty(namespace, name)) {
                        // The API updates the metadata key value for the account.
                        metadataService.updateProperty(namespace, name, value);
                    } else {
                        // A metadata key value.
                        // The metadata key does not already exist for the account.
                        // The API adds the metadata key and value pair, or item, to the account.
                        metadataService.addProperty(namespace, name, value);
                    }
                }
            }
            if (key.startsWith(META_REMOVE_PREFIX)) {
                String name = key.substring(META_REMOVE_PREFIX.length(), key.length());
                if (metadataService.containsProperty(namespace, name)) {
                    metadataService.removeProperty(namespace, name);
                }
            }
        }
    }

    protected void upload(
                        final ObjectPutRequest  request,
                        final ObjectPutResponse response,
                        final InputStream       is) {

        final T sourceContainer;
        final T sourceObject;

        final String  copyFrom = removeLeadingSlash(request.getCopyFrom());
        final boolean copy     = copyFrom != null && ! copyFrom.trim().isEmpty();

        final T targetContainer;

        final boolean chunked = isChunked(request.getTransferEncoding());

        if ( copy ) {
            final int start = copyFrom.indexOf(FORWARD_SLASH);
            if (start < 0) {
                throw new CormorantException("Failed to copy object [" + copyFrom + "]. Missing container name. " +
                                             "[X-Copy-From] header value must be in form {container}/{object}.");
            }
            final String copyFromContainer  = copyFrom.substring(0, start);
            final String copyFromObjectPath = copyFrom.substring(start + 1, copyFrom.length());
            targetContainer = containerService.getContainer(request.getAccount(), request.getContainer());
            if (targetContainer == null) {
                throw new CormorantException("Failed to copy object [" + copyFrom + "]. Container [" +
                                                copyFromContainer + "] does not exist.");
            }
            boolean validSourceObject = objectService.isValidPath(targetContainer, copyFromObjectPath);
            if ( ! validSourceObject ) {
                throw new CormorantException("Failed to copy object [" + copyFrom + "]. Invalid Object path [" +
                                                copyFromObjectPath + "].");
            }
            sourceContainer = containerService.getContainer(request.getAccount(), copyFromContainer);
            sourceObject = objectService.getObject(request.getAccount(), copyFromContainer, copyFromObjectPath);
            if (sourceObject == null) {
                throw new CormorantException("Failed to copy object from [" + copyFrom + "]. Object not found.");
            }
        } else {
            sourceContainer = containerService.getContainer(request.getAccount(), request.getContainer());
            final Long    maxTransferSize = chunked ? MAX_UPLOAD_SIZE : request.getContentLength();
            final T       tempObject      = objectService.createTempObject(request.getAccount(), sourceContainer);

            try (final ReadableByteChannel readableChannel = newChannel(is);
                                        final WritableByteChannel writableChannel = objectService.getWritableChannel(tempObject)) {
                write(readableChannel, writableChannel, 0L, maxTransferSize);
            } catch (IOException e) {
                throw new CormorantException(e);
            }

            sourceObject    = tempObject;
            targetContainer = sourceContainer;
        }

        if (sourceContainer == null) {
            throw new CormorantException("Container [" + request.getContainer() + "] not found.");
        }

        final boolean validPath = objectService.isValidPath(sourceContainer, request.getObject());

        if ( ! validPath ) {
            throw new CormorantException("Invalid object path [" + request.getObject() + "].");
        }

        final Container           containerInfo  = accountService.getContainer(request.getAccount(), request.getContainer());
        final Map<String, Object> systemMetadata = new HashMap<>();
        final String              etag           = checksumService.calculateChecksum(sourceObject);
        final String              requestETag    = httpHeaders.getHeaderString(ETAG);
        // ----------------------------------------------------------------------------------
        // Ensure object integrity
        // ----------------------------------------------------------------------------------
        // When client include ETag header in a a request to store an object,
        // cormorant calculates checksum (MD5) hash of the data it receives and
        // compares that to the header value. If the values do not match, cormorant returns a
        // 422 (Uprocessable Entity) status code and does not store the object.
        // ----------------------------------------------------------------------------------
        if ( requestETag != null &&
                ! requestETag.trim().isEmpty() &&
                ! requestETag.equalsIgnoreCase(etag) ) {
            if ( ! copy ) {
                objectService.deleteTempObject(request.getAccount(), sourceContainer, sourceObject);
            }
            throw new CormorantException("ETag request header [" + requestETag + "] does not match with [" + etag + "].",
                            UNPROCESSABLE_ENTITY);
        }

        final long    tempObjectSize = objectService.getSize(sourceObject);
        final T       directory      = objectService.getDirectory(targetContainer, request.getObject());
        final boolean dynamicObject  = tempObjectSize == 0 && directory != null;

        final T targetObject;
        if (dynamicObject) {
            targetObject = directory;
        } else {
            if ( ! copy ) {
                targetObject = objectService.moveTempObject(request.getAccount(), sourceObject, targetContainer, request.getObject());
            } else {
                targetObject = objectService.copyObject(request.getAccount(),
                                                        targetContainer,
                                                        request.getObject(),
                                                        request.getAccount(),
                                                        sourceContainer,
                                                        sourceObject,
                                                        null);
            }
        }

        containerInfo.incrementObjectCount();

        final Long size = dynamicObject ? objectService.getDyanmicObjectSize(targetContainer, targetObject) : objectService.getSize(targetObject);
        containerInfo.addBytesUsed(size);
        final String contentType = ! dynamicObject && TRUE.equals(request.getDetectContentType())     ?
                                     checksumService.getMimeType(sourceContainer, targetObject, true) :
                                     httpHeaders.getHeaderString(CONTENT_TYPE)                        ;

        final String targetNamespace = objectService.getNamespace(targetContainer, targetObject);

        final Map<String, Object> sourceSystemMetadata;
        if (copy) {
            final String              sourceNamespace      = objectService.getNamespace(sourceContainer, sourceObject);
            final Map<String, Object> sourceMetadata       = metadataService.getProperties(sourceNamespace);
                                      sourceSystemMetadata = systemMetadataService.getProperties(sourceNamespace);

            systemMetadataService.delete(targetNamespace);
            metadataService.delete(targetNamespace);
            if ( ! sourceMetadata.isEmpty() ) {
                if ( request.getFreshMetadata() == null || ! TRUE.equals(request.getFreshMetadata()) ) {
                    final Map<String, Object> metadata = metadataService.getProperties(sourceNamespace);
                    if ( ! metadata.isEmpty() ) {
                        metadataService.setProperties(targetNamespace, sourceMetadata);
                    }
                }
            }
        } else {
            sourceSystemMetadata = emptyMap();
        }

        systemMetadata.put(CONTENT_DISPOSITION, copy ? sourceSystemMetadata.get(CONTENT_DISPOSITION) : httpHeaders.getHeaderString(CONTENT_DISPOSITION));
        systemMetadata.put(CONTENT_TYPE       , copy ? sourceSystemMetadata.get(CONTENT_TYPE) : contentType);
        systemMetadata.put(CONTENT_ENCODING   , copy ? sourceSystemMetadata.get(CONTENT_ENCODING) : httpHeaders.getHeaderString(CONTENT_ENCODING));

        String objectManifest = httpHeaders.getHeaderString(X_OBJECT_MANIFEST);
        if ( objectManifest != null && ! objectManifest.isEmpty() ) {
            systemMetadata.put(X_OBJECT_MANIFEST, objectManifest);
        }

        final boolean copySelf = copy && sourceObject.equals(targetObject);
        if (copySelf) {
            if ( httpHeaders.getHeaderString(CONTENT_DISPOSITION) != null ) {
                systemMetadata.put(CONTENT_DISPOSITION, httpHeaders.getHeaderString(CONTENT_DISPOSITION));
            }
            if ( contentType != null ) {
                systemMetadata.put(CONTENT_TYPE, contentType);
            }
            if (httpHeaders.getHeaderString(CONTENT_ENCODING) != null) {
                systemMetadata.put(CONTENT_ENCODING, httpHeaders.getHeaderString(CONTENT_ENCODING));
            }
            if (httpHeaders.getHeaderString(X_DELETE_AT) != null) {
                systemMetadata.put(X_DELETE_AT, httpHeaders.getHeaderString(X_DELETE_AT));
            }
            if (httpHeaders.getHeaderString(X_DELETE_AFTER) != null) {
                final long delay = parseLong(httpHeaders.getHeaderString(X_DELETE_AFTER));
                if (delay > 0) {
                    // convert X-Delete-After header into an X-Delete-At header using its current time plus the value given.
                    systemMetadata.put(X_DELETE_AT, valueOf(delay + currentTimeMillis()));
                }
            }
        }

        if ( ! dynamicObject ) {
            systemMetadata.put(CONTENT_LENGTH, size);
            systemMetadata.put(ETAG, etag);
        } else {
            systemMetadata.remove(CONTENT_LENGTH);
            systemMetadata.remove(ETAG);
        }

        if (chunked) {
            systemMetadata.remove(CONTENT_LENGTH);
            systemMetadata.remove(ETAG);
        }

        if ( "0".equals(systemMetadata.get(CONTENT_LENGTH)) ||  ! systemMetadata.containsKey(CONTENT_LENGTH) ) {
            systemMetadata.put(ETAG, MD5_OF_EMPTY_STRING);
        }

        updateMetadata(targetNamespace);

        systemMetadataService.setProperties(targetNamespace, systemMetadata);

        final String lastModified = FORMATTER.format(ofInstant(ofEpochMilli(objectService.getLastModified(targetObject)), GMT));
        response.setLastModified(lastModified);
        response.setContentType(copy ? (String) systemMetadata.get(CONTENT_TYPE) : contentType);

        if ( ! dynamicObject ) {
            response.setETag(copy ?(String) systemMetadata.get(ETAG) : etag);
        } else {
            final List<T> objects                = objectService.listDynamicLargeObject(targetContainer, targetObject);
            final String  dynamicLargeObjectEtag = checksumService.calculateChecksum(objects);
            response.setETag(dynamicLargeObjectEtag);
        }
    }

    protected String removeLeadingSlash(String path) {
        if (path == null) {
            return null;
        }
        String normalizedPath = path;
        if (normalizedPath.charAt(0) == FORWARD_SLASH) {
            normalizedPath = path.substring(1, path.length());
        }
        if (normalizedPath.charAt(0) == FORWARD_SLASH) {
            normalizedPath = LEADING_SLASH.matcher(normalizedPath).replaceAll("");
        }
        return normalizedPath;
    }

    protected void createDirectory(
                        final ObjectPutRequest  request,
                        final ObjectPutResponse response,
                        final InputStream       is) {
        final T container      = containerService.getContainer(request.getAccount(), request.getContainer());
        final T object         = objectService.createDirectory(request.getAccount(), container, request.getObject());
        final String namespace = objectService.getNamespace(container, object);
        systemMetadataService.delete(namespace);
        metadataService.delete(namespace);
        response.setETag(MD5_OF_EMPTY_STRING);
    }

    protected void uploadManifest(
                        final ObjectPutRequest  request,
                        final ObjectPutResponse response,
                        final InputStream       is) {
        final Long contentLength = request.getContentLength();
        if (contentLength == null || contentLength > MAX_MANIFEST_SIZE) {
            throw new CormorantException("Content-Length must be <= 2 MB for multipart manifest body.");
        }
        final byte[] data = new byte[contentLength.intValue()];
        try (DataInputStream ds = new DataInputStream(new LimitedInputStream(is, MAX_MANIFEST_SIZE))) {
            ds.readFully(data);
        } catch (EOFException e) {
            throw new CormorantException("Content-Length must be <= 64 KB for multipart manifest body.", e);
        } catch (IOException e) {
            throw new CormorantException(e);
        }
        final Json json;
        try {
            json = read(new String(data, UTF_8));
        } catch (Throwable t) {
            throw new CormorantException("Failed to parse malformed json request.", t);
        }
        if ( ! json.isArray() ) {
            throw new CormorantException("Multipart manifest body must be json array.");
        }
        final List<T>      files    = new ArrayList<>();
        final List<Object> segments = json.asList();

        if (segments.size() > MAX_MANIFEST_SEGMENTS) {
            throw new CormorantException("Segment count must be < " + MAX_MANIFEST_SEGMENTS + " for multipart manifest object.");
        }

        for (Object next : segments) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> map                = (Map<String, Object>) next;
            final String              path               = (String) map.get("path");
            final String              normalizedPath     = path.charAt(0) == FORWARD_SLASH ? path.substring(1) : path;
            final String              multipartPath      = normalizedPath.substring(normalizedPath.indexOf(FORWARD_SLASH), normalizedPath.length());
            final String              multipartContainer = request.getContainer();

            final T segmentObject = objectService.getObject(request.getAccount(), multipartContainer, multipartPath);
            if (segmentObject == null) {
                throw new CormorantException("Invalid manifest data. Segment [" + path + "] not found.");
            }
            if (map.containsKey("etag")) {
                final String expectedChecksum = (String) map.get("etag");
                final T      container        = containerService.getContainer(request.getAccount(), multipartContainer);
                final String actualChecksum   = checksumService.calculateChecksum(container, segmentObject);
                if ( ! expectedChecksum.equals(actualChecksum) ) {
                    throw new CormorantException("Segment etag [" +
                                    expectedChecksum + "] does not match with actual etag [" +
                                    actualChecksum  + "]. Segment path: [" + path  + "].", UNPROCESSABLE_ENTITY);
                }
            }
            if (map.containsKey("size_bytes")) {
                final Long expectedBytes = parseLong(valueOf(map.get("size_bytes")));
                if (expectedBytes.longValue() < 1) {
                    throw new CormorantException("Segment [" + path + "] size must be at least 1 byte.",
                                    UNPROCESSABLE_ENTITY);
                }
                final Long actualSize = objectService.getSize(segmentObject);
                if ( compare(expectedBytes, actualSize) != 0 ) {
                    throw new CormorantException("Segment [" + map.get("path") +
                                    "] size mismatch. Expected segment size is [" + expectedBytes +
                                    "] but got [" + actualSize  + "].", UNPROCESSABLE_ENTITY);
                }
            }
            files.add(segmentObject);
        }
        final T container  = containerService.getContainer(request.getAccount(), request.getContainer());
        final T tempObject = objectService.createTempObject(request.getAccount(), container);
        try (ReadableByteChannel readableChannel = newChannel(new ByteArrayInputStream(data));
                    WritableByteChannel writableChannel = objectService.getWritableChannel(tempObject)) {
            write(readableChannel, writableChannel, 0L, contentLength);
            final T object = objectService.moveTempObject(request.getAccount(),
                                                            tempObject, container,
                                                            request.getObject() + MANIFEST_EXTENSION);
            final String eTag = checksumService.calculateChecksum(object);
            response.setETag("\"" + eTag + "\"");
            response.setContentType(APPLICATION_JSON);
            response.setLastModified(valueOf(objectService.getLastModified(object)));
        } catch (IOException e) {
            throw new CormorantException(e);
        }
    }

    protected void write(
                    final ReadableByteChannel readableChannel,
                    final WritableByteChannel writableChannel,
                    final Long                start          ,
                    final Long                maxTransferSize) throws IOException {
        final long count = maxTransferSize != null ? maxTransferSize.longValue() : MAX_UPLOAD_SIZE;
        ((FileChannel) writableChannel).transferFrom(readableChannel, start, count);
    }

    protected boolean isChunked(final String transferEncoding) {
        if (transferEncoding == null || transferEncoding.trim().isEmpty()) {
            return false;
        }
        final String[] encodings = transferEncoding.split(",");
        // If there are more than one transfer encoding value, the last
        // one must be chunked, see RFC 2616 Sec. 3.6
        return "chunked".equalsIgnoreCase(encodings[encodings.length - 1]);
    }
}
