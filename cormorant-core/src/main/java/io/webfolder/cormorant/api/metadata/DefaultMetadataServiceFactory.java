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
package io.webfolder.cormorant.api.metadata;

import static io.webfolder.cormorant.api.metadata.CacheNames.ACCOUNT;
import static io.webfolder.cormorant.api.metadata.CacheNames.CONTAINER;
import static io.webfolder.cormorant.api.metadata.CacheNames.OBJECT;
import static io.webfolder.cormorant.api.metadata.CacheNames.OBJECT_SYS;
import static io.webfolder.cormorant.api.metadata.MetadataStorage.File;
import static io.webfolder.cormorant.api.metadata.MetadataStorage.SQLite;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.util.ServiceLoader.load;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ServiceLoader;

import io.webfolder.cormorant.api.DataSourceFactory;
import io.webfolder.cormorant.api.SQLiteDataSourceFactory;
import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.service.MetadataService;

public class DefaultMetadataServiceFactory implements MetadataServiceFactory {

    private final Path              metadataStore;

    private final DataSourceFactory dsFactory    ;

    private final MetadataStorage   storage      ;

    public DefaultMetadataServiceFactory(final Path metadataStore, final MetadataStorage storage) {
        this.metadataStore = metadataStore;
        this.storage = storage;
        final ServiceLoader<DataSourceFactory> dsServFactory = load(DataSourceFactory.class, getClass().getClassLoader());
        final Iterator<DataSourceFactory> dsServIterator = dsServFactory.iterator();
        this.dsFactory = dsServIterator.hasNext() ? dsServIterator.next() : SQLite.equals(storage) ? new SQLiteDataSourceFactory(metadataStore) : null;
    }

    @Override
    public MetadataService create(final String  name) {
        final ServiceLoader<MetadataService> loader       = load(MetadataService.class, getClass().getClassLoader());
        final Iterator<MetadataService>      iterator     = loader.iterator();
        final Path                           absolutePath = metadataStore.toAbsolutePath().normalize().resolve(name);
        if (File.equals(storage)) {
            if ( ! exists(absolutePath, NOFOLLOW_LINKS) ) {
                try {
                    createDirectories(absolutePath);
                } catch (IOException e) {
                    throw new CormorantException("Unable to create property directory [" + absolutePath + "], namespace [" + name + "].", e);
                }
            } else if ( ! isDirectory(absolutePath, NOFOLLOW_LINKS) ) {
                throw new CormorantException("Invalid property directory [" + absolutePath + "], namespace [" + name + "].");
            }
        }
        MetadataService metadataService;
        if (iterator.hasNext()) {
            metadataService = iterator.next();
        } else {
            if (File.equals(storage)) {
                metadataService = new FileMetadataService(absolutePath, name);
            } else {
                String schema = "";
                String table  = "";
                switch (name) {
                    case ACCOUNT   : table = "ACCOUNT_META"   ; break;
                    case CONTAINER : table = "CONTAINER_META" ; break;
                    case OBJECT    : table = "OBJECT_META"    ; break;
                    case OBJECT_SYS: table = "OBJECT_META_SYS"; break;
                }
                metadataService = new JdbcMetadaService(dsFactory.get(), schema, table);
            }
        }
        try {
            metadataService.init();
        } catch (SQLException e) {
            throw new CormorantException(e);
        }
        return metadataService;
    }
}
