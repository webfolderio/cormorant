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
package io.webfolder.server.command;

import static io.webfolder.server.command.PidFileWatcher.STOP;
import static java.io.File.pathSeparator;
import static java.lang.Integer.parseInt;
import static java.lang.System.exit;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static java.nio.charset.Charset.defaultCharset;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isWritable;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.setAttribute;
import static java.nio.file.Files.size;
import static java.nio.file.Paths.get;
import static java.util.Locale.ENGLISH;
import static org.pmw.tinylog.Configurator.defaultConfig;
import static org.pmw.tinylog.Level.ERROR;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;

import org.jvnet.winp.WinProcess;
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

    private static final String  OS      = getProperty("os.name").toLowerCase(ENGLISH);

    private static final boolean WINDOWS = ";".equals(pathSeparator);

    private static final boolean LINUX   = "linux".contains(OS);

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
                if (WINDOWS) {
                    boolean found = false;
                    Iterator<WinProcess> iterator = WinProcess.all().iterator();
                    while (iterator.hasNext()) {
                        WinProcess winProcess = iterator.next();
                        if (winProcess.getPid() == processId) {
                            found = true;
                            break;
                        }
                    }
                    if ( ! found ) {
                        System.err.println("Process is not running.");
                        exit(PROCESS_NOT_FOUND);
                    }
                } else if (LINUX) {
                    Path pidDir = Paths.get("/proc/" + pid);
                    boolean found = exists(pidDir) && isDirectory(pidDir);
                    if ( ! found ) {
                        System.err.println("Process is not running.");
                        exit(PROCESS_NOT_FOUND);
                    }
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
