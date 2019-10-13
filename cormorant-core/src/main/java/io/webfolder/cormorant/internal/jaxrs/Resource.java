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

import java.util.List;
import java.util.Map;

import io.webfolder.cormorant.api.model.Segment;

class Resource<T> {

    private final T      container;

    private final T      object;

    private final long   length;

    private final long   lastModified;

    private final long   creationTime;

    private final String  eTag;

    private final String  contentType;

    private final String  contentDisposition;

    private final boolean staticLargeObject;

    private final boolean dynamicLargeObject;

    private final Map<String, String> headers;

    private final List<Segment<T>> segments;

    public Resource(
                final T                   container,
                final T                   object,
                final long                length,
                final long                lastModified,
                final long                creationTime,
                final String              eTag,
                final String              contentType,
                final String              contentDisposition,
                final boolean             staticLargeObject,
                final boolean             dynamicLargeObject,
                final Map<String, String> headers,
                final List<Segment<T>>    segments) {
        this.container          = container;
        this.object             = object;
        this.length             = length;
        this.lastModified       = lastModified;
        this.creationTime       = creationTime;
        this.eTag               = eTag;
        this.contentType        = contentType;
        this.contentDisposition = contentDisposition;
        this.staticLargeObject  = staticLargeObject;
        this.dynamicLargeObject = dynamicLargeObject;
        this.headers            = headers;
        this.segments           = segments;
    }

    public T getObject() {
        return object;
    }

    public long getLength() {
        return length;
    }

    public long getLastModified() {
        return lastModified;
    }

    public String getETag() {
        return eTag;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentDisposition() {
        return contentDisposition;
    }

    public boolean isStaticLargeObject() {
        return staticLargeObject;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public boolean isDynamicLargeObject() {
        return dynamicLargeObject;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public List<Segment<T>> getSegments() {
        return segments;
    }

    public T getContainer() {
        return container;
    }
}
