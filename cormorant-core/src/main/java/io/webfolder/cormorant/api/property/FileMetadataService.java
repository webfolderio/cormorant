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
package io.webfolder.cormorant.api.property;

import static io.webfolder.cormorant.api.Json.object;
import static io.webfolder.cormorant.api.property.MetadataServiceFactory.METADATA_EXTENSION;
import static java.lang.Long.parseLong;
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

    private final Path root;

    private final String groupName;

    private final boolean cacheable;

    private final Map<String, Object> cache;

    public FileMetadataService(
                final Path                root     ,
                final String              groupName,
                final boolean             cacheable,
                final Map<String, Object> cache) {
        this.root      = root;
        this.groupName = groupName;
        this.cacheable = cacheable;
        this.cache     = cache;
    }

    @Override
    public String getProperty(final String namespace, final String propertyName) {
        final Json json = read(namespace);
        if (json == null) {
            return null;
        }
        final Json properties = json.at(groupName);
        if ( properties == null || ! properties.isObject() ) {
            return null;
        }
        final Map<String, Object> map = properties.asMap();
        final Object value = map.get(propertyName);
        return value == null ? null : valueOf(value);
    }

    @Override
    public Long getPropertyLong(final String namespace, final String propertyName) {
        final Json json = read(namespace);
        if (json == null) {
            return null;
        }
        final Json properties = json.at(groupName);
        if ( ! properties.isObject() ) {
            return null;
        }
        final Json value = properties.at(propertyName);
        if (value == null) {
            return null;
        }
        if (value.isNumber()) {
            return value.asLong();
        } else if (value.isString()) {
            return parseLong(value.asString());
        } else {
            return null;
        }
    }

    @Override
    public boolean containsProperty(final String namespace, final String propertyName) {
        final Json json = read(namespace);
        if (json == null) {
            return false;
        }
        final Json properties = json.at(groupName);
        if ( properties == null || ! properties.isObject() ) {
            return false;
        }
        return properties.has(propertyName);
    }

    @Override
    public void removeProperty(final String namespace, final String propertyName) {
        final Json json = read(namespace);
        if ( json == null ) {
            return;
        }
        final Json properties = json.at(groupName);
        if ( ! properties.isObject() ) {
            return;
        }
        if (properties.has(propertyName)) {
            properties.delAt(propertyName);
            write(namespace, json);
        }
    }

    @Override
    public void updateProperty(final String namespace, String propertyName, final String value) {
        addProperty(namespace, propertyName, value);
    }

    @Override
    public void addProperty(final String namespace, final String propertyName, final String value) {
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
    public Map<String, Object> getProperties(final String namespace) {
        final Json json = read(namespace);
        if ( json == null ) {
            return new HashMap<>();
        }
        final Json properties = json.at(groupName);
        if ( properties == null || ! properties.isObject() ) {
            return new HashMap<>();
        }
        final Map<String, Object> map = properties.asMap();
        return map;
    }

    @Override
    public void setProperties(
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
        Json group = json.at(groupName);
        if (group == null) {
            group = object();
        }
        for (Map.Entry<String, Object> next : map.entrySet()) {
            final String key = next.getKey();
            final Object value = next.getValue();
            if ( value != null ) {
                json.at(groupName)
                    .set(key, valueOf(value));
            } else if (json.at(groupName).has(key)) {
                json.at(groupName)
                    .delAt(key);
            }
        }
        write(namespace, json);
    }

    protected Json read(final String namespace) {
        final Path path = root.resolve(namespace).toAbsolutePath().normalize();
        if ( ! path.startsWith(root) ) {
            throw new CormorantException("Unable to read property. Invalid path ["+ path.toString() + "].");
        }
        if (cacheable) {
            return (Json) cache.computeIfAbsent(namespace, key -> read(path));
        } else {
            return read(path);
        }
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
        } finally {
            if (cacheable) {
                cache.remove(namespace);
            }
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
        if (str.isEmpty()) {
            return null;
        }
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
                cache.remove(namespace);
                Files.delete(dataFile);
            } catch (IOException e) {
                throw new CormorantException("Unable to delete metadata file.", e);
            }
        }
    }
}
