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

import static io.webfolder.cormorant.api.Json.object;
import static io.webfolder.cormorant.api.metadata.MetadataServiceFactory.METADATA_EXTENSION;
import static java.lang.String.valueOf;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.move;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import io.webfolder.cormorant.api.Json;
import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.service.MetadataService;

public class FileMetadataService implements MetadataService {

    private final String METADATA            = "metadata"       ;

    private final String SYSTEM_METADATA     = "system-metadata";

    private final Path root;

    private final String groupName;

    public FileMetadataService(
                final Path                root     ,
                final String              cacheName) {
        this.root      = root;
        this.groupName = cacheName.endsWith("-sys") ? SYSTEM_METADATA : METADATA;
    }

    @Override
    public String get(final String namespace, final String propertyName) {
        final Json json = read(namespace);
        if (json == null) {
            return null;
        }
        final Json properties = json.at(groupName);
        final Map<String, Object> map = properties.asMap();
        final Object value = map.get(propertyName);
        return value == null ? null : valueOf(value);
    }

    @Override
    public boolean contains(final String namespace, final String propertyName) {
        return get(namespace, propertyName) != null;
    }

    @Override
    public void delete(final String namespace, final String propertyName) {
        final Json json = read(namespace);
        final Json properties = json.at(groupName);
        if (properties.has(propertyName)) {
            properties.delAt(propertyName);
            write(namespace, json);
        }
    }

    @Override
    public void update(final String namespace, String propertyName, final String value) {
        add(namespace, propertyName, value);
    }

    @Override
    public void add(final String namespace, final String propertyName, final String value) {
        Json json = read(namespace);
        if (json == null) {
            json = object();
        }
        Json properties = json != null ? json.at(groupName) : null;
        if (properties == null) {
            properties = json.set(groupName, object());
        }
        if ( ! properties.isObject() ) {
            return;
        }
        if ( value != null ) {
            json.at(groupName)
                .set(propertyName, valueOf(value));
        } else if (json.at(groupName).has(propertyName)) {
            json.at(groupName)
                .delAt(propertyName);
        }
        write(namespace, json);
    }

    @Override
    public Map<String, Object> getValues(final String namespace) {
        final Json json = read(namespace);
        if ( json == null ) {
            return new HashMap<>();
        }
        final Json properties = json.at(groupName);
        final Map<String, Object> map = properties.asMap();
        return map;
    }

    @Override
    public void setValues(
                        final String namespace,
                        final Map<String, Object> map) {
        Json json = read(namespace);
        if (json == null) {
            json = object();
        }
        Json properties = json != null ? json.at(groupName) : null;
        if ( properties == null ) {
            properties = json.set(groupName, object());
        }
        for (Map.Entry<String, Object> next : map.entrySet()) {
            final String key = next.getKey();
            final Object value = next.getValue();
            if ( value != null ) {
                json.at(groupName)
                    .set(key, valueOf(value));
            }
        }
        write(namespace, json);
    }

    protected Json read(final String namespace) {
        final Path path = root.resolve(namespace).toAbsolutePath().normalize();
        if ( ! path.startsWith(root) ) {
            throw new CormorantException("Unable to read property. Invalid path ["+ path.toString() + "].");
        }
        return read(path);
    }

    protected void write(final String namespace, final Json json) {
        final Path path = root.resolve(namespace).toAbsolutePath().normalize();
        if ( ! path.startsWith(root) ) {
            throw new CormorantException("Unable to write property. Invalid path ["+ path.toString() + "].");
        }
        final String str = json.toString();
        try {
            final Path temp = createTempFile("cormorant", ".tmp");
            Files.write(temp, str.getBytes(UTF_8));
            final Path dataFile = path.getParent().resolve(path.getFileName() + METADATA_EXTENSION);
            if ( ! exists(dataFile.getParent(), NOFOLLOW_LINKS) ) {
                createDirectories(path.getParent());
            }
            move(temp, dataFile, ATOMIC_MOVE, NOFOLLOW_LINKS);
        } catch (IOException e) {
            throw new CormorantException(e);
        }
    }

    protected Json read(final Path path) {
        byte[] content = null;
        try {
            final Path dataFile = path.getParent().resolve(path.getFileName() + METADATA_EXTENSION);
            if ( ! exists(dataFile, NOFOLLOW_LINKS) ) {
                return null;
            }
            content = readAllBytes(dataFile);
        } catch (IOException e) {
            throw new CormorantException(e);
        }
        final String str = new String(content, UTF_8);
        final Json json = Json.read(str);
        if ( ! json.isObject() ) {
            return null;
        }
        return json;
    }

    @Override
    public void delete(String namespace) {
        final Path path     = root.resolve(namespace).toAbsolutePath().normalize();
        final Path dataFile = path.getParent().resolve(path.getFileName() + METADATA_EXTENSION);
        if ( path.startsWith(root)               &&
                exists(dataFile, NOFOLLOW_LINKS) &&
                isRegularFile(dataFile, NOFOLLOW_LINKS) ) {
            try {
                Files.delete(dataFile);
            } catch (IOException e) {
                throw new CormorantException("Unable to delete metadata file.", e);
            }
        }
    }
}
