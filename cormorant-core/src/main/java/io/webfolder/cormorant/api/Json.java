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
package io.webfolder.cormorant.api;

import java.util.List;
import java.util.Map;

public class Json {

    private mjson.Json delegate;

    private Json(mjson.Json delegate) {
        this.delegate = delegate;
    }

    public Json at(String property) {
        final mjson.Json json = delegate.at(property);
        if (json == null) {
            return null;
        }
        return new Json(json);
    }

    public String asString() {
        return delegate.asString();
    }

    public boolean isObject() {
        return delegate.isObject();
    }

    public boolean isString() {
        return delegate.isString();
    }

    public Map<String, Object> asMap() {
        return delegate.asMap();
    }

    public boolean has(String property) {
        return delegate.has(property);
    }

    public Json delAt(String property) {
        delegate.delAt(property);
        return this;
    }

    public Json set(String property, String value) {
        delegate.set(property, value);
        return this;
    }

    public Json set(String property, Json value) {
        delegate.set(property, value.delegate);
        return this;
    }

    public static Json object() {
        return new Json(mjson.Json.object());
    }

    public static Json read(String content) {
        return new Json(mjson.Json.read(content));
    }

    public boolean isArray() {
        return delegate.isArray();
    }

    public List<Object> asList() {
        return delegate.asList();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
