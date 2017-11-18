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

import java.util.concurrent.atomic.AtomicLong;

public class Container implements Comparable<Container> {

    private String name;

    private Long timestamp;

    private Long lastModified;

    private AtomicLong objectCount;

    private AtomicLong bytesUsed;

    public Container(
                final String name,
                final Long timestamp,
                final Long lastModified,
                final Long objectCount,
                final Long bytesUsed) {
        this.name         = name;
        this.timestamp    = timestamp;
        this.lastModified = lastModified;
        this.objectCount  = new AtomicLong(objectCount);
        this.bytesUsed    = new AtomicLong(bytesUsed);
    }

    public Container(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public Long getObjectCount() {
        return Long.valueOf(objectCount.get());
    }

    public Long getBytesUsed() {
        return Long.valueOf(bytesUsed.get());
    }

    public Long addBytesUsed(final long delta) {
        return bytesUsed.addAndGet(delta);
    }

    public Long removeBytesUsed(final long delta) {
        return bytesUsed.updateAndGet(value -> value - delta);
    }

    public Long decrementObjectCount() {
        return objectCount.decrementAndGet();
    }

    public Long incrementObjectCount() {
        return objectCount.incrementAndGet();
    }

    @Override
    public int compareTo(Container o) {
        return getName().compareTo(o.getName());
    }
}
