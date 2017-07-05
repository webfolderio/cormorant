package io.webfolder.cormorant.api;

import java.nio.file.Path;

import io.webfolder.cormorant.api.metadata.MetadataStorage;
import static io.webfolder.cormorant.api.metadata.MetadataStorage.SQLite;

public class CormorantConfiguration {

    private Path objectStore;

    private Path metadataStore;

    private String accountName;

    private boolean cacheMetadata = true;

    private MetadataStorage storage = DEFAULT_STORAGE;

    private int pathMaxCount = PATH_MAX_COUNT;

    public static final MetadataStorage DEFAULT_STORAGE = SQLite;

    public static final int PATH_MAX_COUNT = 10_000;

    public static class Builder {

        private CormorantConfiguration configuration = new CormorantConfiguration();

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

        public Builder cacheMetadata(boolean cacheMetadata) {
            configuration.cacheMetadata = cacheMetadata;
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

    public boolean isCacheMetadata() {
        return cacheMetadata;
    }
}
