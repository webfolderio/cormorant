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

public class PidWatcher implements FileChangeCallback {

    private final Logger log = LoggerFactory.getLogger(PidWatcher.class);

    private final Path file;

    private final CormorantServer server;

    private String pid;

    public static String STOP = "stop";

    public PidWatcher(Path file, CormorantServer server) {
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
