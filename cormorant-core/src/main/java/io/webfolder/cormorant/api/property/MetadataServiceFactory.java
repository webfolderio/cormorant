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

import io.webfolder.cormorant.api.service.MetadataService;

public interface MetadataServiceFactory {

    String METADATA            = "metadata"       ;

    String SYSTEM_METADATA     = "system-metadata";

    String MANIFEST_EXTENSION  = ".manifest"      ;

    String METADATA_EXTENSION  = ".metadata"      ;

    MetadataService create(
                        final String  cacheName,
                        final String  groupName,
                        final boolean cacheable);
}
