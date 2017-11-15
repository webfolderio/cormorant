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

import static io.webfolder.server.command.PidFileWatcher.STOP;
import static java.lang.Integer.parseInt;
import static java.lang.System.exit;
import static java.lang.System.setProperty;
import static java.nio.charset.Charset.defaultCharset;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isWritable;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.setAttribute;
import static java.nio.file.Files.size;
import static java.nio.file.Paths.get;
import static org.pmw.tinylog.Configurator.defaultConfig;
import static org.pmw.tinylog.Level.ERROR;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;

import org.pmw.tinylog.writers.ConsoleWriter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "stop",
    description = "Stop object storage server.",
    sortOptions = true,
    showDefaultValues = true
)
public class Stop implements ExitCodes {

    private static final int MAX_PID_FILE_SIZE = 32;

    private static final Path DEFAULT_PID_FILE = get("pid").resolve("pid");

    @Option(names = { "-i", "--pid-file" }, arity = "1", paramLabel = "<file>", description = "Pid file." )
    private Path pidFile = DEFAULT_PID_FILE;

    public void stop() {
        setProperty("org.jboss.logging.provider", "slf4j");

        defaultConfig()
            .writer(new ConsoleWriter())
            .level(ERROR)
        .activate();

        Path path = null;
        Path file = null;

        if (DEFAULT_PID_FILE.equals(this.pidFile)) {
            URL location = Stop.class.getProtectionDomain().getCodeSource().getLocation();
            Path pwd = null;
            try {
                pwd = get(location.toURI());
            } catch (URISyntaxException e) {
                System.err.println(e.getMessage());
                exit(INVALID_PID_FILE);
                return;
            }
            path = pwd.getParent().resolve("pid");
            file = path.resolve("pid");
        } else {
            path = pidFile.getParent();
            file = pidFile;
        }

        if ( ! exists(path) ) {
            System.err.println("Unable to stop server. Directory does not exist: " + file);
            exit(PID_FILE_NOT_FOUND);
            return;
        }

        if ( ! exists(file) ) {
            System.err.println("Unable to stop server. pid file does not exist: " + file);
            exit(PID_FILE_NOT_FOUND);
            return;
        }

        if ( ! isWritable(file) ) {
            System.err.println("Unable to write pid file: " + file);
            exit(UNABLE_TO_WRITE_PID_FILE);
            return;
        }

        String pid = null;
        try {
            long size = size(file);
            if (size > MAX_PID_FILE_SIZE) {
                System.err.println("Unable to stop server. Invalid pid file: " + file);
                exit(PID_FILE_NOT_FOUND);
                return;
            }
            byte[] content = readAllBytes(file);
            if (content.length > 0) {
                pid = new String(content);
                int processId = 0;
                try {
                    processId = parseInt(pid);
                } catch (NumberFormatException e) {
                }
                if (processId <= 0) {
                    System.err.println("Unable to stop server. Invalid process id: " + processId);
                    exit(INVALID_PID_FILE);
                    return;
                }
                setAttribute(file, "user:command", defaultCharset().encode(STOP));
                setAttribute(file, "user:pid", defaultCharset().encode(pid));
            }
        } catch (IOException e) {
            System.err.println("Unable to stop server. " + e.getMessage());
            exit(IO_ERROR);
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        
        if ( pid != null ) {
            Timer timer = new Timer("pid-stop-watcher", true);
            timer.scheduleAtFixedRate(new ShutdownTask(pid, file, latch), 0, 500);
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
