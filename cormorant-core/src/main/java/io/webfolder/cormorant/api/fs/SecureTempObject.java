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
