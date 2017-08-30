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

import static java.lang.Long.toHexString;
import static java.lang.System.getProperty;
import static java.nio.channels.Channels.newChannel;
import static java.nio.file.Paths.get;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.security.SecureRandom;

import io.webfolder.otmpfile.SecureTempFile;

public class SecureTempObject implements TempObject<Path> {

    private static final SecureRandom RANDOM = new SecureRandom();

    private SecureTempFile secureTempFile;

    private Path path;

    private static final String TEMP_DIR = getProperty("java.io.tmpdir");

    public SecureTempObject(SecureTempFile secureTempFile) {
        this.secureTempFile = secureTempFile;
    }

    @Override
    public WritableByteChannel getWritableByteChannel() throws IOException {
        return newChannel(new FileOutputStream(secureTempFile.getFileDescriptor()));
    }

    @Override
    public Path toObject() {
        if (path == null) {
            String name = toHexString(RANDOM.nextLong()) + toHexString(RANDOM.nextLong());
            path = get(TEMP_DIR).resolve(name).toAbsolutePath();
            secureTempFile.setName(path.toString());
        }
        return path;
    }
}
