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
