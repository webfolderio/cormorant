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

    private final boolean manifest;

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
                final boolean             manifest,
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
        this.manifest           = manifest;
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

    public boolean isManifest() {
        return manifest;
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
