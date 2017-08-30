package io.webfolder.cormorant.api.fs;

import static java.nio.channels.FileChannel.open;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

public class PathTempObject implements TempObject<Path> {

    private final Path path;

    public PathTempObject(Path path) {
        this.path = path;
    }

    @Override
    public WritableByteChannel getWritableByteChannel() throws IOException {
        return open(path, WRITE, CREATE, NOFOLLOW_LINKS);
    }

    @Override
    public Path toObject() {
        return path;
    }
}
