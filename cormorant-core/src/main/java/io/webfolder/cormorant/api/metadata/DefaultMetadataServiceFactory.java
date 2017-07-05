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
package io.webfolder.cormorant.api.metadata;

import static io.webfolder.cormorant.api.metadata.CacheNames.ACCOUNT;
import static io.webfolder.cormorant.api.metadata.CacheNames.CONTAINER;
import static io.webfolder.cormorant.api.metadata.CacheNames.OBJECT;
import static io.webfolder.cormorant.api.metadata.CacheNames.OBJECT_SYS;
import static io.webfolder.cormorant.api.metadata.MetadataStorage.SQLite;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.util.ServiceLoader.load;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.ServiceLoader;

import io.webfolder.cormorant.api.DataSourceFactory;
import io.webfolder.cormorant.api.SQLiteDataSourceFactory;
import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.service.MetadataService;

public class DefaultMetadataServiceFactory implements MetadataServiceFactory {

    private final Path            root       ;

    private final DataSourceFactory dsFactory;

    private final MetadataStorage storage    ;

    public DefaultMetadataServiceFactory(final Path root, final MetadataStorage storage) {
        this.root = root;
        this.storage = storage;
        final ServiceLoader<DataSourceFactory> dsServFactory = load(DataSourceFactory.class, getClass().getClassLoader());
        final Iterator<DataSourceFactory> dsServIterator = dsServFactory.iterator();
        this.dsFactory = dsServIterator.hasNext() ? dsServIterator.next() : SQLite.equals(storage) ? new SQLiteDataSourceFactory() : null;
    }

    @Override
    public MetadataService create(
                                final String  cacheName,
                                final boolean cacheable) {
        final ServiceLoader<MetadataService> loader       = load(MetadataService.class, getClass().getClassLoader());
        final Iterator<MetadataService>      iterator     = loader.iterator();
        final Path                           absolutePath = root.toAbsolutePath().normalize().resolve(cacheName);
        if ( ! exists(absolutePath, NOFOLLOW_LINKS) ) {
            try {
                createDirectories(absolutePath);
            } catch (IOException e) {
                throw new CormorantException("Unable to create property directory [" + absolutePath + "], namespace [" + cacheName + "].", e);
            }
        } else if ( ! isDirectory(absolutePath, NOFOLLOW_LINKS) ) {
            throw new CormorantException("Invalid property directory [" + absolutePath + "], namespace [" + cacheName + "].");
        }
        MetadataService metadataService;
        if (iterator.hasNext()) {
            metadataService = iterator.next();
        } else {
            if (MetadataStorage.File.equals(storage)) {
                metadataService = new FileMetadataService(absolutePath, cacheName);
            } else {
                String schema = "";
                String table  = "";
                switch (cacheName) {
                    case ACCOUNT   : table = "ACCOUNT_META"   ; break;
                    case CONTAINER : table = "CONTAINER_META" ; break;
                    case OBJECT    : table = "OBJECT_META"    ; break;
                    case OBJECT_SYS: table = "OBJECT_META_SYS"; break;
                }
                metadataService = new JdbcMetadaService(dsFactory.get(), schema, table);
            }
        }
        if (cacheable) {
            return new CacheMetadataService(metadataService);
        } else {
            return metadataService;
        }
    }
}
