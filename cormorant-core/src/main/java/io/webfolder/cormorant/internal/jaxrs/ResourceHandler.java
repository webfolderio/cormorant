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

import static java.lang.Long.parseLong;
import static java.lang.Long.toHexString;
import static java.lang.String.valueOf;
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteBuffer.wrap;
import static java.nio.channels.Channels.newChannel;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.sort;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.regex.Pattern.compile;
import static javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT;
import static javax.ws.rs.core.Response.Status.NOT_MODIFIED;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.PARTIAL_CONTENT;
import static javax.ws.rs.core.Response.Status.PRECONDITION_FAILED;
import static javax.ws.rs.core.Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import io.webfolder.cormorant.api.model.Segment;
import io.webfolder.cormorant.api.service.ObjectService;

class ResourceHandler<T> {

    private static final Pattern RANGE_PATTERN        = compile("^bytes=[0-9]*-[0-9]*(,[0-9]*-[0-9]*)*$");
    private static final long    ONE_SECOND_IN_MILLIS = SECONDS.toMillis(1);
    private static final int     BUFFER_SIZE          = 16 * 1024;
    private static final byte[]  NEW_LINE             = "\r\n".getBytes(UTF_8);
    private static final char    CHAR_SLASH           = '/';
    private static final Pattern LEADING_SLASH        = compile("^/+");

    private final ObjectService<T> objectService;

    private final Resource<T> resource;

    public ResourceHandler(
            final ObjectService<T> objectService,
            final Resource<T>      response) {
        this.objectService = objectService;
        this.resource      = response;
    }

    public Status handle(
                    final HttpServletRequest  request,
                    final HttpServletResponse response) throws IOException {

        if (preconditionFailed(request, resource)) {
            return PRECONDITION_FAILED;
        }

        response.setHeader("ETag", resource.getETag());
        response.setDateHeader("Last-Modified", resource.getLastModified());

        if (notModified(request, resource)) {
            return NOT_MODIFIED;
        }

        final List<Range> ranges = getRanges(request, resource);

        if (ranges == null) {
            response.setHeader("Content-Range", "bytes */" + resource.getLength());
            return REQUESTED_RANGE_NOT_SATISFIABLE;
        }

        final Status status;

        if ( ! ranges.isEmpty() ) {
            status = PARTIAL_CONTENT;
        } else {
            status = OK;
            final String boundary = toHexString(current().nextLong());
            ranges.add(new Range(0, resource.getLength() - 1, boundary)); // Full content.
        }

        final String contentType = setContentHeaders(request, response, resource, ranges);

        write(response, resource, ranges, contentType);

        return status;
    }

    protected boolean preconditionFailed(
                            final HttpServletRequest request,
                            final Resource<T>        resource) {
        final String match      = request.getHeader("If-Match");
        final long   unmodified = request.getDateHeader("If-Unmodified-Since");
        return ( match != null ) ? ! matches(match, resource.getETag())
                : ( unmodified != -1 && isModified(unmodified, resource.getLastModified()) );
    }

    protected boolean notModified(
                            final HttpServletRequest request,
                            final Resource<T>        resource) {
        final String noMatch  = request.getHeader("If-None-Match");
        final long   modified = request.getDateHeader("If-Modified-Since");
        return ( noMatch != null ) ? matches(noMatch, resource.getETag())
                : (modified != -1 && ! isModified(modified, resource.getLastModified()));
    }

    protected List<Range> getRanges(
                            final HttpServletRequest request,
                            final Resource<T>        resource) {
        final List<Range> ranges      = new ArrayList<>(1);
        final String      rangeHeader = request.getHeader("Range");

        if (rangeHeader == null) {
            return ranges;
        } else if ( ! RANGE_PATTERN.matcher(rangeHeader).matches() ) {
            return null; // Syntax error.
        }

        final String ifRange = request.getHeader("If-Range");

        if ( ifRange != null && ! ifRange.equals(resource.getETag()) ) {
            try {
                long ifRangeTime = request.getDateHeader("If-Range");

                if ( ifRangeTime != -1 && isModified(ifRangeTime, resource.getLastModified()) ) {
                    return ranges;
                }
            } catch (IllegalArgumentException e) {
                // If-Range header is invalid. Return full content then.
                return ranges;
            }
        }

        final String boundary = toHexString(current().nextLong());

        for (final String rangeHeaderPart : rangeHeader.split("=")[1].split(",")) {
            final Range range = parseRange(rangeHeaderPart.trim(), resource.getLength(), boundary);

            if (range == null) {
                return null; // Logic error.
            }

            ranges.add(range);
        }

        return ranges;
    }

    protected Range parseRange(
                        final String range,
                        final long   length,
                        final String boundary) {
        long start = toLong(range, 0, range.indexOf('-'));
        long end   = toLong(range, range.indexOf('-') + 1, range.length());

        if (start == -1) {
            start = length - end;
            end = length - 1;
        } else if (end == -1 || end > length - 1) {
            end = length - 1;
        }

        if (start > end) {
            return null; // Logic error.
        }

        return new Range(start, end, boundary);
    }

    protected String setContentHeaders(
                            final HttpServletRequest  request,
                            final HttpServletResponse response,
                            final Resource<T>         resource,
                            final List<Range>         ranges) {

        if ( resource.getContentDisposition() != null ) {
            response.setHeader("Content-Disposition", resource.getContentDisposition());
        }

        response.setHeader("Accept-Ranges", "bytes");

        if (ranges.size() == 1) {
            final Range range = ranges.get(0);
            response.setContentType(resource.getContentType());
            if ( ! resource.isDynamicLargeObject() ) {
                response.setContentLengthLong(range.getLength());
            } else {
                final long size = objectService.getDyanmicObjectSize(resource.getContainer(), resource.getObject());
                response.setContentLengthLong(size);
            }

            final boolean all = range.getStart() == 0 && (range.getEnd() + 1) == resource.getLength();

            if ( ! all ) {
                response.setStatus(SC_PARTIAL_CONTENT);
                response.setHeader("Content-Range", "bytes " + range.getStart() + '-' + range.getEnd() + '/' + resource.getLength());
            }
        } else {
            response.setContentType("multipart/byteranges; boundary=" + ranges.get(0).getBoundary());
        }

        final String part1 = leadingZeros(valueOf(toHexString(current().nextLong())), 21);
        final String part2 = leadingZeros(valueOf(toHexString(current().nextLong())), 10);
        response.setHeader("X-Trans-Id", "tx" + part1 + "-" + (part2.length() > 10 ? part2.substring(0, 10) : part2));
        response.setHeader("X-Timestamp", valueOf(resource.getCreationTime()));

        for (Entry<String, String> entry : resource.getHeaders().entrySet()) {
            response.setHeader(entry.getKey(), valueOf(entry.getValue()));
        }

        return resource.getContentType();
    }

    protected String leadingZeros(final String str, final int length) {
        if (str.length() >= length) {
            return str;
        } else {
            return String.format("%0" + (length-str.length()) + "d%s", 0, str);
        }
    }

    protected void write(
                        final HttpServletResponse response,
                        final Resource<T>         resource,
                        final List<Range>         ranges,
                        final String              contentType)
            throws IOException {
        if (ranges.size() == 1) {
            final Range range = ranges.get(0);
            if ( ! resource.isManifest() && ! resource.isDynamicLargeObject() ) {
                try (final FileChannel readableChannel = (FileChannel) objectService.getReadableChannel(resource.getObject());
                        final WritableByteChannel writableChannel = newChannel(response.getOutputStream())) {
                    readableChannel.transferTo(range.getStart(), range.getLength(), writableChannel);
                }
            } else {
                List<T> objects = new ArrayList<>();

                if (resource.isDynamicLargeObject()) {
                    objects = objectService.listDynamicLargeObject(resource.getContainer(), resource.getObject());
                } else {
                    for (Segment<T> next : resource.getSegments()) {
                        objects.add(next.getObject());
                    }
                }
                try (final WritableByteChannel writableChannel = newChannel(response.getOutputStream())) {
                    for (T next : objects) {
                        try (FileChannel readableChannel = (FileChannel) objectService.getReadableChannel(next)) {
                            long size = objectService.getSize(next);
                            readableChannel.transferTo(0L, size, writableChannel);
                        }
                    }
                }
            }
        } else {
            try (final FileChannel readableChannel = (FileChannel) objectService.getReadableChannel(resource.getObject());
                    final WritableByteChannel writableChannel = newChannel(response.getOutputStream())) {
                ByteBuffer NL = wrap(NEW_LINE);
                for (final Range range : ranges) {
                    NL.clear();
                    writableChannel.write(NL);
                    writableChannel.write(wrap(("--" + range.getBoundary()).getBytes(UTF_8)));
                    NL.clear();
                    writableChannel.write(NL);
                    writableChannel.write(wrap(("Content-Type: " + contentType).getBytes(UTF_8)));
                    NL.clear();
                    writableChannel.write(NL);
                    writableChannel.write(wrap(("Content-Range: bytes " + range.getStart() + '-' + range.getEnd() + '/' + range.getLength()).getBytes(UTF_8)));
                    NL.clear();
                    writableChannel.write(NL);
                    NL.clear();
                    writableChannel.write(NL);
                    NL.clear();

                    readableChannel.position(0L);

                    final ByteBuffer buffer = allocateDirect(BUFFER_SIZE);
                    long size = 0;
                    while ( readableChannel.read(buffer, range.getStart() + size) != -1 ) {
                        buffer.flip();
                        if (size + buffer.limit() > range.getLength()) {
                            buffer.limit((int) (range.getLength() - size));
                        }
                        size += writableChannel.write(buffer);
                        if (size >= range.getLength()) {
                            break;
                        }
                        buffer.clear();
                    }
                }

                writableChannel.write(NL);
                NL.clear();
                writableChannel.write(wrap(("--" + ranges.get(0).getBoundary() + "--").getBytes(UTF_8)));
                writableChannel.write(NL);
            }
        }
    }

    protected boolean matches(
                        final String matchHeader,
                        final String eTag) {
        final String[] matchValues = matchHeader.split("\\s*,\\s*");
        sort(matchValues);
        return binarySearch(matchValues, eTag) > -1 || binarySearch(matchValues, "*") > -1;
    }

    protected boolean isModified(
                        final long modifiedHeader,
                        final long lastModified) {
        return (modifiedHeader + ONE_SECOND_IN_MILLIS <= lastModified);
    }

    protected long toLong(
                        final String value,
                        final int    beginIndex,
                        final int    endIndex) {
        final String str = value.substring(beginIndex, endIndex);
        return str.isEmpty() ? -1 : parseLong(str);
    }

    protected String removeLeadingSlash(String path) {
        if (path == null) {
            return null;
        }
        String normalizedPath = path;
        if (normalizedPath.charAt(0) == CHAR_SLASH) {
            normalizedPath = path.substring(1, path.length());
        }
        if (normalizedPath.charAt(0) == CHAR_SLASH) {
            normalizedPath = LEADING_SLASH.matcher(normalizedPath).replaceAll("");
        }
        return normalizedPath;
    }
}
