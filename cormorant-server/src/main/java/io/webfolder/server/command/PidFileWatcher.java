/**
 * The MIT License
 * Copyright © 2017 WebFolder OÜ
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
package io.webfolder.server.command;

import static java.nio.file.Files.delete;
import static java.nio.file.Files.getAttribute;
import static java.nio.file.Files.readAllBytes;

import static java.nio.file.Files.isRegularFile;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.FileChangeCallback;
import org.xnio.FileChangeEvent;

import io.webfolder.cormorant.api.CormorantServer;
import static java.nio.file.Files.exists;

public class PidFileWatcher implements FileChangeCallback {

    private final Logger log = LoggerFactory.getLogger(PidFileWatcher.class);

    private final Path file;

    private final CormorantServer server;

    private String pid;

    public static String STOP = "stop";

    public PidFileWatcher(Path file, CormorantServer server) {
        this.file = file;
        this.server = server;
        try {
            pid = new String(readAllBytes(file));
        } catch (IOException e) {
            pid = null;
            log.warn(e.getMessage());
        }
    }

    @Override
    public void handleChanges(Collection<FileChangeEvent> changes) {
        Iterator<FileChangeEvent> iterator = changes.iterator();
        while (iterator.hasNext()) {
            FileChangeEvent next = iterator.next();
            Path path = next.getFile().toPath().toAbsolutePath();
            if (file.equals(path)) {
                try {
                    Object oCommand = getAttribute(path, "user:command");
                    Object oPid     = getAttribute(path, "user:pid");
                    if ( oCommand != null && byte[].class.isAssignableFrom(oCommand.getClass()) ) {
                        byte[] attributeCommand = (byte[]) getAttribute(path, "user:command");
                        if ( attributeCommand != null && attributeCommand.length > 0 ) {
                            String command = new String(attributeCommand);
                            if (STOP.equals(command)) {
                                if ( oCommand != null && byte[].class.isAssignableFrom(oPid.getClass()) ) {
                                    byte[] attributePid = (byte[]) getAttribute(path, "user:pid");
                                    if ( attributePid != null && attributePid.length > 0 ) {
                                        String commandPid = new String(attributePid);
                                        boolean samePid = commandPid.equals(pid);
                                        if (samePid) {
                                            server.stop();
                                            if (exists(file) && isRegularFile(file)) {
                                                delete(file);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    if ( ! e.getClass().isAssignableFrom(NoSuchFileException.class) ) {
                        log.warn(e.getMessage());
                    }
                }
            }
        }
    }
}
