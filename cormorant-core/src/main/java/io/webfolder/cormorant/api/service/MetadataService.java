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
package io.webfolder.cormorant.api.service;

import java.sql.SQLException;
import java.util.Map;

public interface MetadataService {

    void init() throws SQLException;

    String get(String namespace, String key) throws SQLException;

    boolean contains(String namespace, String key) throws SQLException;

    void update(String namespace, String key, String value) throws SQLException;

    void add(String namespace, String key, String value) throws SQLException;

    Map<String, Object> getValues(String namespace) throws SQLException;

    void setValues(String namespace, Map<String, Object> values) throws SQLException;

    void delete(String namespace, String key) throws SQLException;

    void delete(String namespace) throws SQLException;
}
