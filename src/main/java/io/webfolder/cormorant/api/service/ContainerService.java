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
package io.webfolder.cormorant.api.service;

import io.webfolder.cormorant.api.model.ListContainerOptions;
import io.webfolder.cormorant.api.resource.ResourceStream;

public interface ContainerService<T> {

    ResourceStream<T> listObjects(String accountName,
                                    String containerName,
                                    ListContainerOptions options);

    T getContainer(String accountName, String containerName);

    boolean contains(String accountName, String containerName);

    void create(String accountName, String containerName);

    boolean delete(String accountName, String containerName);

    long getMaxQuotaBytes(String accountName, String containerName);

    long getMaxQuotaCount(String accountName, String containerName);
}
