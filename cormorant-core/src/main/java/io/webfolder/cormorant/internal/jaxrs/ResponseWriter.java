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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.valueOf;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.ZonedDateTime.now;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.USER_AGENT;
import static javax.ws.rs.core.HttpHeaders.VARY;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;

import io.webfolder.cormorant.api.Util;
import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.resource.ContentFormat;
import io.webfolder.cormorant.api.resource.Resource;
import io.webfolder.cormorant.internal.response.AccountGetResponse;
import io.webfolder.cormorant.internal.response.AccountGetResponseBody;
import io.webfolder.cormorant.internal.response.AccountGetResponseContext;
import io.webfolder.cormorant.internal.response.AccountHeadResponse;
import io.webfolder.cormorant.internal.response.AccountPostResponse;
import io.webfolder.cormorant.internal.response.ContainerDeleteResponse;
import io.webfolder.cormorant.internal.response.ContainerGetResponse;
import io.webfolder.cormorant.internal.response.ContainerGetResponseContext;
import io.webfolder.cormorant.internal.response.ContainerHeadResponse;
import io.webfolder.cormorant.internal.response.ContainerPostResponse;
import io.webfolder.cormorant.internal.response.ContainerPutResponse;
import io.webfolder.cormorant.internal.response.CormorantResponse;
import io.webfolder.cormorant.internal.response.ObjectCopyResponse;
import io.webfolder.cormorant.internal.response.ObjectDeleteResponse;
import io.webfolder.cormorant.internal.response.ObjectHeadResponse;
import io.webfolder.cormorant.internal.response.ObjectPostResponse;
import io.webfolder.cormorant.internal.response.ObjectPutResponse;

@SuppressWarnings("rawtypes")
public class ResponseWriter implements MessageBodyWriter, Util {

    private final String                    applicationJson  = "application/json; charset=utf-8";

    private final String                    applicationXml   = "application/xml; charset=utf-8";

    private final String                    textPlain        = "text/plain; charset=utf-8";

    private final Map<Class<?>, Set<Field>> headerMappings   = getHeaderMappings();

    private static final String  TRANSFER_ENCODING           = "Transfer-Encoding";

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpHeaders requestHeaders;

    @Override
    public boolean isWriteable(
                        final Class        type       ,
                        final Type         genericType,
                        final Annotation[] annotations,
                        final MediaType    mediaType  ) {
        return headerMappings.containsKey(type);
    }

    @Override
    public long getSize(
                    final Object       response   ,
                    final Class        type       ,
                    final Type         genericType,
                    final Annotation[] annotations,
                    final MediaType mediaType     ) {
        return -1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeTo(final Object         retValue       ,
                        final Class          type           ,
                        final Type           genericType    ,
                        final Annotation[]   annotations    ,
                        final MediaType      mediaType      ,
                        final MultivaluedMap responseHeaders,
                        final OutputStream   entityStream)
                                    throws IOException,
                                           WebApplicationException {

        if ( ! "chunked".equalsIgnoreCase(requestHeaders.getHeaderString(TRANSFER_ENCODING)) ) {
            responseHeaders.remove(CONTENT_TYPE);
        }

        String contentType = textPlain;
        Object response    = retValue;
        String content     = null;

        final MultivaluedMap<String, String> qparams = uriInfo.getQueryParameters();

        String format = qparams.getFirst("format");

        if (format == null) {
            format = "plain";
        }

        final ContentFormat contentFormat = ContentFormat.valueOf(format);

        switch (contentFormat) {
            case json: contentType = applicationJson; break;
            case xml : contentType = applicationXml ; break;
            default  : contentType = textPlain      ; break;
        }

        final String account = uriInfo.getPathParameters(false).getFirst("account");

        if (AccountGetResponseContext.class.isInstance(retValue)) {
            final AccountGetResponseContext agr = (AccountGetResponseContext) retValue;

            response = agr.getResponse();

            final StringBuilder builder = new StringBuilder();
            if ("json".equals(format)) {

                builder.append("[");
                List<AccountGetResponseBody> list = agr.getBody();
                for (int i = 0; i < list.size(); i++) {
                    AccountGetResponseBody next = list.get(i);
                    builder.append("{");
                    builder.append("\"name\":\"").append(next.getName()).append("\"").append(",");
                    builder.append("\"count\":").append(next.getCount()).append(",");
                    builder.append("\"bytes\":").append(next.getBytes());
                    builder.append("}");
                    if (i + 1 < list.size()) {
                        builder.append(",");
                    }
                }
                builder.append("]");

                content = builder.toString();

                agr.getResponse().setContentType(applicationJson);
            } else if ("xml".equals(format)) {
                builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\r\n");
                builder.append("<account name=\"" + account + "\">");
                List<AccountGetResponseBody> list = agr.getBody();
                for (int i = 0; i < list.size(); i++) {
                    AccountGetResponseBody next = list.get(i);
                    builder.append("<container>");
                    builder.append("<name>").append(next.getName()).append("</name>");
                    builder.append("<count>").append(next.getCount()).append("</count>");
                    builder.append("<bytes>").append(next.getBytes()).append("</bytes>");
                    builder.append("</container>");
                }
                builder.append("</account>");

                content = builder.toString();

                agr.getResponse().setContentType(applicationXml);
            } else {
                List<AccountGetResponseBody> list = agr.getBody();
                for (int i = 0; i < list.size(); i++) {
                    AccountGetResponseBody next = list.get(i);
                    builder.append(next.getName()).append("\r\n");
                }
                content = builder.toString();
                agr.getResponse().setContentType(textPlain);
            }
        } else if (ContainerGetResponseContext.class.isInstance(retValue)) {
            final ContainerGetResponseContext cgr = (ContainerGetResponseContext) retValue;

            response = cgr.getResponse();

            String seperator = "";
            String prefix    = "";
            String suffix    = "";

            final String container = uriInfo.getPathParameters(false).getFirst("container");

            switch (contentFormat) {
                case json:
                    seperator   = ",";
                    prefix      = "[";
                    suffix      = "]";
                    contentType = applicationJson;
                break;
                case xml:
                    seperator   = "\r\n";
                    prefix      = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<container name=\"" + container + "\">";
                    contentType = applicationXml;
                break;
                default:
                    contentType = textPlain;
                    seperator   = "\r\n";
                break;
            }

            final StringJoiner joiner = new StringJoiner(seperator, prefix, suffix);

            final String userAgent = requestHeaders.getHeaderString(USER_AGENT);
            // ncw/swift/blob/master/swift_test.go#TestObjectsDirectory fails if dir name ends with /
            // @see https://github.com/ncw/swift/blob/master/swift_test.go#L1355
            final Boolean appendForwardSlash = "goswift/1.0".equals(userAgent) ? FALSE : TRUE;

            Resource stream = cgr.getBody();
            for (Object next : stream) {
                String json;
                try {
                    json = stream.convert(next, contentFormat, appendForwardSlash);
                } catch (SQLException e) {
                    throw new CormorantException(e);
                }
                if ( json != null ) {
                    joiner.add(json);
                }
            }

            content = joiner.toString();

            if (ContentFormat.xml.equals(contentFormat)) {
                content = content + "</container>";
            }

            content = content.trim();

            cgr.getResponse().setContentType(contentType);

        } else if (ObjectDeleteResponse.class.isInstance(retValue)) {
            ((ObjectDeleteResponse) retValue).setContentType("text/plain");
            content = "OK";
            // When deleting SLO using multipart manifest, the response contains
            // not 'content-length' but 'transfer-encoding' header. This is the
            // special case, therefore the existence of response headers is checked
            // @see test_object_slo.py#test_delete_large_object
            if ("delete".equals(uriInfo.getQueryParameters().getFirst("multipart-manifest"))) {
                ((ObjectDeleteResponse) retValue).setTransferEncoding("chunked");
            } else {
                ((ObjectDeleteResponse) retValue).setContentLength(valueOf(content.length()));
            }
        }

        final CormorantResponse cormorantResponse = (CormorantResponse) response;
        cormorantResponse.setDate(DATE_FORMATTER.format(now()));
        cormorantResponse.setTransId(generateTxId());

        for (Field field : headerMappings.get(response.getClass())) {
            final Object value;
            try {
                value = field.get(response);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new CormorantException(e);
            }
            if ( value != null ) {
                final String name = field.getAnnotation(HeaderParam.class).value();
                responseHeaders.add(name, value);
            }
        }

        responseHeaders.add(VARY, "X-Auth-Token");

        if ( content != null && ! content.trim().isEmpty() ) {
            try (OutputStream os = entityStream) {
                os.write(content.getBytes(UTF_8));
                os.flush();
            }
        }
    }

    protected Map<Class<?>, Set<Field>> getHeaderMappings() {
        final Map<Class<?>, Set<Field>> mappings = new HashMap<>();
        mappings.put(AccountGetResponse.class         , getHeaderNames(AccountGetResponse.class));
        mappings.put(AccountGetResponseContext.class  , getHeaderNames(AccountGetResponseContext.class));
        mappings.put(AccountHeadResponse.class        , getHeaderNames(AccountHeadResponse.class));
        mappings.put(AccountPostResponse.class        , getHeaderNames(AccountPostResponse.class));
        mappings.put(ContainerGetResponse.class       , getHeaderNames(ContainerGetResponse.class));
        mappings.put(ContainerDeleteResponse.class    , getHeaderNames(ContainerDeleteResponse.class));
        mappings.put(ContainerGetResponseContext.class, getHeaderNames(ContainerGetResponseContext.class));
        mappings.put(ContainerHeadResponse.class      , getHeaderNames(ContainerHeadResponse.class));
        mappings.put(ContainerPostResponse.class      , getHeaderNames(ContainerPostResponse.class));
        mappings.put(ContainerPutResponse.class       , getHeaderNames(ContainerPutResponse.class));
        mappings.put(ObjectCopyResponse.class         , getHeaderNames(ObjectCopyResponse.class));
        mappings.put(ObjectDeleteResponse.class       , getHeaderNames(ObjectDeleteResponse.class));
        mappings.put(ObjectHeadResponse.class         , getHeaderNames(ObjectHeadResponse.class));
        mappings.put(ObjectPostResponse.class         , getHeaderNames(ObjectPostResponse.class));
        mappings.put(ObjectPutResponse.class          , getHeaderNames(ObjectPutResponse.class));
        return unmodifiableMap(mappings);
    }

    protected Set<Field> getHeaderNames(final Class<?> klass) {
        final Set<Field> headers = new HashSet<>();
        for (Field field : klass.getDeclaredFields()) {
            if (field.isAnnotationPresent(HeaderParam.class)) {
                field.setAccessible(true);
                headers.add(field);
            }
        }
        return unmodifiableSet(headers);
    }
}
