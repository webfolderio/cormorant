/**
 * cormorant - Object Storage Server
 * Copyright © 2017 WebFolder OÜ (cormorant@webfolder.io)
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
package io.webfolder.cormorant.api.cache;

import java.util.Map;

public interface CacheFactory {

    String ACCOUNT   = "metadata/account"     ;

    String CONTAINER = "metadata/container"   ;

    String OBJECT    = "metadata/object"      ;

    String TOKENS    = "authentication/tokens";

    String USERS     = "authentication/users" ;

    <K, V> Map<K, V> create(String name);
}
