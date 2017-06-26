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

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import io.webfolder.cormorant.api.model.Segment;

public interface ObjectService<T> {

    WritableByteChannel getWritableChannel(T object);

    ReadableByteChannel getReadableChannel(T object);

    T getObject(String accountName, String containerName, String objectPath);

    T createTempObject(String accontName, T container);

    void deleteTempObject(String accountName, T container, T tempObject);

    T moveTempObject(String accountName, T tempObject, T targetContainer, String targetObjectPath);

    long getSize(T object);

    void delete(T container, T object);

    String relativize(T container, T object);

    String getNamespace(T container, T object);

    long getLastModified(T object);

    T createDirectory(String accountName, T container, String objectPath);

    boolean isDirectory(T container, T object);

    T getDirectory(T container, String directoryPath);

    boolean isEmptyDirectory(T container, T object);

    T copyObject(String destinationAccount,
                 T      destinationContainer,
                 String destinationObjectPath,
                 String sourceAccount,
                 T      sourceContainer,
                 T      sourceObject,
                 String multipartManifest);

    boolean isValidPath(T container, String objectPath);

    boolean isMultipartManifest(T object);

    long getCreationTime(T object);

    List<T> listDynamicLargeObject(T container, T object);

    List<Segment<T>> listStaticLargeObject(final String accountName, final T manifestObject);

    long getDyanmicObjectSize(T container, T object);

    String toPath(T container, T object);

    boolean exist(T container, T object);
}
