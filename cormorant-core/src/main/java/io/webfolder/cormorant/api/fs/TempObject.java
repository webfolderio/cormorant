package io.webfolder.cormorant.api.fs;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

public interface TempObject<T> {

    public WritableByteChannel getWritableByteChannel() throws IOException;

    public T toObject();
}
