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
package io.webfolder.cormorant.api.fs;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.sql.SQLException;

import io.webfolder.cormorant.api.service.ObjectService;

public class DefaultTempObject<T> implements TempObject<T> {

    private final T path;

    private ObjectService<T> objectService;

    public DefaultTempObject(T path, ObjectService<T> objectService) {
        this.path = path;
        this.objectService = objectService;
    }

    @Override
    public WritableByteChannel getWritableByteChannel() throws IOException, SQLException {
        return objectService.getWritableChannel(path);
    }

    @Override
    public T toObject() {
        return path;
    }
}
