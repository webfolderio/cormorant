/**
 * The MIT License
 * Copyright © 2017, 2019 WebFolder OÜ
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
