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
package io.webfolder.cormorant.api;

import java.nio.file.Path;

import io.webfolder.cormorant.api.metadata.MetadataStorage;
import static io.webfolder.cormorant.api.metadata.MetadataStorage.SQLite;

public class CormorantConfiguration {

    private Path objectStore;

    private Path metadataStore;

    private String accountName;

    private MetadataStorage storage;

    private int pathMaxCount;

    public static class Builder {

        private CormorantConfiguration configuration = new CormorantConfiguration();

        public Builder() {
            storage(SQLite).
            pathMaxCount(10_000);
        }

        public Builder objectStore(Path objectStore) {
            configuration.objectStore = objectStore;
            return this;
        }

        public Builder metadataStore(Path metadataStore) {
            configuration.metadataStore = metadataStore;
            return this;
        }

        public Builder accountName(String accountName) {
            configuration.accountName = accountName;
            return this;
        }

        public Builder pathMaxCount(int pathMaxCount) {
            configuration.pathMaxCount = pathMaxCount;
            return this;
        }

        public Builder storage(MetadataStorage storage) {
            configuration.storage = storage;
            return this;
        }

        public CormorantConfiguration build() {
            return configuration;
        }
    }

    public Path getObjectStore() {
        return objectStore;
    }

    public Path getMetadataStore() {
        return metadataStore;
    }

    public String getAccountName() {
        return accountName;
    }

    public int getPathMaxCount() {
        return pathMaxCount;
    }

    public MetadataStorage getStorage() {
        return storage;
    }
}
