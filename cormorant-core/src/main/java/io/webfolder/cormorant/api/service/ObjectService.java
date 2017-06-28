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

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import io.webfolder.cormorant.api.model.Segment;

public interface ObjectService<T> {

    WritableByteChannel getWritableChannel(T object) throws IOException;

    ReadableByteChannel getReadableChannel(T object) throws IOException;

    T getObject(String accountName, String containerName, String objectPath) throws IOException;

    T createTempObject(String accontName, T container) throws IOException;

    void deleteTempObject(String accountName, T container, T tempObject) throws IOException;

    T moveTempObject(String accountName, T tempObject, T targetContainer, String targetObjectPath) throws IOException;

    long getSize(T object) throws IOException;

    void delete(T container, T object) throws IOException;

    String relativize(T container, T object) throws IOException;

    String getNamespace(T container, T object) throws IOException;

    long getLastModified(T object) throws IOException;

    T createDirectory(String accountName, T container, String objectPath) throws IOException;

    boolean isDirectory(T container, T object) throws IOException;

    T getDirectory(T container, String directoryPath) throws IOException;

    boolean isEmptyDirectory(T container, T object) throws IOException;

    T copyObject(String destinationAccount,
                 T      destinationContainer,
                 String destinationObjectPath,
                 String sourceAccount,
                 T      sourceContainer,
                 T      sourceObject,
                 String multipartManifest) throws IOException;

    boolean isValidPath(T container, String objectPath) throws IOException;

    boolean isStaticLargeObject(T object) throws IOException;

    long getCreationTime(T object) throws IOException;

    List<T> listDynamicLargeObject(T container, T object) throws IOException;

    List<Segment<T>> listStaticLargeObject(final String accountName, final T manifestObject) throws IOException;

    long getDyanmicObjectSize(T container, T object) throws IOException;

    String toPath(T container, T object) throws IOException;

    boolean exist(T container, T object) throws IOException;
}
