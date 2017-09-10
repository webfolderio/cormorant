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
import java.sql.SQLException;
import java.util.List;

import io.webfolder.cormorant.api.fs.TempObject;
import io.webfolder.cormorant.api.model.Segment;

public interface ObjectService<T> {

    WritableByteChannel getWritableChannel(T object) throws IOException, SQLException;

    ReadableByteChannel getReadableChannel(T object) throws IOException, SQLException;

    T getObject(String accountName, String containerName, String objectPath) throws IOException, SQLException;

    TempObject<T> createTempObject(String accontName, T container) throws IOException, SQLException;

    void deleteTempObject(String accountName, T container, T tempObject) throws IOException, SQLException;

    T moveObject(String accountName, T tempObject, T targetContainer, String targetObject) throws IOException, SQLException;

    long getSize(T object) throws IOException, SQLException;

    void delete(T container, T object) throws IOException, SQLException;

    String relativize(T container, T object) throws IOException, SQLException;

    String getNamespace(T container, T object) throws IOException, SQLException;

    long getLastModified(T object) throws IOException, SQLException;

    T createDirectory(String accountName, T container, String objectPath) throws IOException, SQLException;

    boolean isDirectory(T container, T object) throws IOException, SQLException;

    T getDirectory(T container, String directoryPath) throws IOException, SQLException;

    boolean isEmptyDirectory(T container, T object) throws IOException, SQLException;

    T copyObject(String destinationAccount,
                 T      destinationContainer,
                 String destinationObjectPath,
                 String sourceAccount,
                 T      sourceContainer,
                 T      sourceObject,
                 String multipartManifest) throws IOException, SQLException;

    boolean isValidPath(T container, String objectPath) throws IOException, SQLException;

    boolean isStaticLargeObject(T object) throws IOException, SQLException;

    long getCreationTime(T object) throws IOException, SQLException;

    List<T> listDynamicLargeObject(T container, T object) throws IOException, SQLException;

    List<Segment<T>> listStaticLargeObject(final String accountName, final T manifestObject) throws IOException, SQLException;

    long getDyanmicObjectSize(T container, T object) throws IOException, SQLException;

    String toPath(T container, T object) throws IOException, SQLException;

    boolean exist(T container, T object) throws IOException, SQLException;

    String getMimeType(T container, T object, boolean autoDetect) throws IOException, SQLException;

    String calculateChecksum(List<T> objects) throws IOException, SQLException;
}
