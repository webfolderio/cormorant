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
package io.webfolder.cormorant.api.model;

public class Segment<T> {

    private final String contentType;

    private final long size;

    private final T object;

    public Segment(String contentType, long size, T object) {
        this.contentType = contentType;
        this.size = size;
        this.object = object;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }

    public T getObject() {
        return object;
    }
}
