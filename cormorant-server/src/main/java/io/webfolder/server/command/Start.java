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

import static io.webfolder.cormorant.api.metadata.MetadataStorage.SQLite;
import static io.webfolder.cormorant.api.model.Role.Admin;
import static io.webfolder.server.command.LogAppender.Console;
import static io.webfolder.server.command.LogAppender.File;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.parseInt;
import static java.lang.Long.toHexString;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.valueOf;
import static java.lang.System.exit;
import static java.lang.System.setProperty;
import static java.lang.management.ManagementFactory.getRuntimeMXBean;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isWritable;
import static java.nio.file.Files.write;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardOpenOption.SYNC;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Collections.singletonMap;
import static java.util.regex.Pattern.compile;
import static org.mindrot.jbcrypt.BCrypt.gensalt;
import static org.mindrot.jbcrypt.BCrypt.hashpw;
import static org.pmw.tinylog.Configurator.defaultConfig;
import static org.pmw.tinylog.Level.ERROR;
import static org.pmw.tinylog.Level.INFO;
import static org.pmw.tinylog.Level.WARNING;
import static org.slf4j.LoggerFactory.getLogger;
import static org.xnio.OptionMap.EMPTY;
import static org.xnio.Xnio.getInstance;

import java.io.IOException;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pmw.tinylog.Level;
import org.pmw.tinylog.labelers.TimestampLabeler;
import org.pmw.tinylog.policies.DailyPolicy;
import org.pmw.tinylog.policies.StartupPolicy;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.RollingFileWriter;
import org.pmw.tinylog.writers.Writer;
import org.slf4j.Logger;
import org.xnio.FileSystemWatcher;
import org.xnio.Xnio;

import io.webfolder.cormorant.api.CormorantApplication;
import io.webfolder.cormorant.api.CormorantConfiguration;
import io.webfolder.cormorant.api.CormorantConfiguration.Builder;
import io.webfolder.cormorant.api.CormorantServer;
import io.webfolder.cormorant.api.fs.PathAccountService;
import io.webfolder.cormorant.api.fs.PathObjectService;
import io.webfolder.cormorant.api.model.User;
import io.webfolder.cormorant.api.service.AccountService;
import io.webfolder.cormorant.api.service.DefaultKeystoneService;
import io.webfolder.cormorant.api.service.KeystoneService;
import io.webfolder.server.EmbeddedServer;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "start",
    description = "Start object storage server.",
    sortOptions = true,
    showDefaultValues = true
)
public class Start implements ExitCodes {

    private static final Path DEFAULT_PID_FILE = get("pid").resolve("pid");

    @Option(names = { "--host" }, description = "Server host.", paramLabel="<ip address>")
    private String host = "0.0.0.0";

    @Option(names = { "--port" }, description = "Server port.", paramLabel="<port number>")
    private int port = 5000;

    @Option(names = { "--password" }, description = "admin password.", paramLabel = "<password>")
    private String password;

    @Option(names = { "--log-level" }, paramLabel = "<string>", description = "Sets log level (DEBUG, INFO, WARNING, ERROR or OFF).")
    private Level logLevel = ERROR;

    @Option(names = { "--log-appender" }, paramLabel ="<string>", description = "Sets log appender. This can be either <Console> or <File>." )
    private LogAppender logAppender = Console;

    @Option(names = { "--log-file" }, arity = "1", paramLabel = "<file>", description = "Writes generated log records to file.")
    private Path logFile = get("log").resolve("cormorant.log");

    @Option(names = { "--access-log-file" }, arity = "1", paramLabel = "<file>", description = "Writes http access log records to file.")
    private Path accessLog = get("log").resolve("access.log");

    @Option(names = { "--disable-access-log" }, description = "Turn off access logs.")
    private boolean disableAccessLog;

    @Option(names = { "--data-path" }, arity = "1", paramLabel = "<directory>", description = "Sets data path.")
    private Path data = get("storage").resolve("data");

    @Option(names = { "--metadata-path" }, arity = "1", paramLabel = "<directory>", description = "Sets metadata path.")
    private Path metadata = get("storage").resolve("metadata");

    @Option(names = { "--pid-file" }, arity = "1", paramLabel = "<file>", description = "Pid file." )
    private Path pidFile = DEFAULT_PID_FILE;

    private final Logger log = getLogger(Start.class);

    public void start() {
        initLogger();
        startServer();
    }

    protected void startServer() {
        if ( ! exists(data) ) {
            try {
                createDirectories(data);
            } catch (IOException e) {
                log.error("Unable to data create directory [" + data.toString() + "].", e);
                exit(CAN_NOT_CREATE_DATA_FOLDER);
            }
        }

        if ( ! exists(metadata) ) {
            try {
                createDirectories(metadata);
            } catch (IOException e) {
                log.error("Unable to metadata create directory [" + metadata.toString() + "].", e);
                exit(CAN_NOT_CREATE_METADATA_FOLDER);
            }
        }

        if ( ! disableAccessLog && ! exists(accessLog.getParent()) ) {
            try {
                createDirectories(accessLog.getParent());
            } catch (IOException e) {
                log.error("Unable to create access log directory [" + accessLog.toString() + "].", e);
                exit(CAN_NOT_CREATE_ACCESS_LOG_FOLDER);
            }
        }

        String rndPwd = password == null || password.trim().isEmpty() ?
                                                    toHexString(new SecureRandom().nextLong()) :
                                                    null;

        String hashPwd = rndPwd != null ? hashpw(rndPwd, gensalt(12)) : hashpw(password, gensalt(12));

        User admin = new User("admin",
                              hashPwd,
                              "admin@example.com",
                              "default",
                              Admin,
                              true);

        AccountService accountService   = new PathAccountService(data);
        KeystoneService keystoneService = new DefaultKeystoneService(singletonMap(admin.getUsername(), admin));

        CormorantConfiguration configuration = new Builder()
                                                    .accountName("default")
                                                    .cacheMetadata(true)
                                                    .storage(SQLite)
                                                    .pathMaxCount(10_000)
                                                    .objectStore(data)
                                                    .metadataStore(metadata)
                                                .build();

        CormorantApplication application = new CormorantApplication(configuration,
                                                    accountService,
                                                    keystoneService);

        CormorantServer server = new CormorantServer();
        server.setPort(port);
        server.setHost(host);
        if ( ! disableAccessLog ) {
            server.setAccessLogPath(accessLog);
        }
        server.deploy(application);

        server.start();

        Thread thread = new Thread(server::stop);
        thread.setDaemon(true);

        getRuntime().addShutdownHook(thread);

        Path file = createPidFile();
        watchPidFile(file, server);

        String version = EmbeddedServer.class.getPackage().getImplementationVersion();

        log.info("==========================================================");
        log.info("Cormorant {} is ready to use.", new Object[] { version });
        log.info("==========================================================");
        if ( rndPwd != null && Console.equals(logAppender) ) {
            log.info("Username : {}", admin.getUsername());
            log.info("Password : {}", rndPwd);
            log.info("----------------------------------------------------------");
        }
        log.info("Auth V1  : http://{}:{}/auth/v1.0", new Object[] { server.getHost(), server.getPort() });
        log.info("Auth V2  : http://{}:{}/v2.0", new Object[] { server.getHost(), server.getPort() });
        log.info("Auth V3  : http://{}:{}/v3", new Object[] { server.getHost(), server.getPort() });
        log.info("----------------------------------------------------------");
        log.info("pid file : {}", pidFile.toAbsolutePath());
        log.info("==========================================================");
    }

    protected void watchPidFile(Path file, CormorantServer server) {
        if ( file == null   ||
             ! exists(file) ||
             ! isReadable(file) ) {
            return;
        }
        Path path = file.getParent();
        if (isDirectory(path)) {
            FileSystemWatcher watcher = getInstance().createFileSystemWatcher("cormorant-pid-watcher", EMPTY);
            watcher.watchPath(path.toFile(), new PidFileWatcher(file, server));
        }
    }

    protected Path createPidFile() {
        Path path = null;
        Path file = null;
        if (DEFAULT_PID_FILE.equals(this.pidFile)) {
            URL location = Stop.class.getProtectionDomain().getCodeSource().getLocation();
            Path pwd = null;
            try {
                pwd = get(location.toURI());
            } catch (URISyntaxException e) {
                log.warn(e.getMessage());
            }
            path = pwd.getParent().resolve("pid");
            file = path.resolve("pid");
            if ( path != null && ! exists(path) ) {
                try {
                    createDirectories(path);
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
            }
        } else {
            path = this.pidFile.getParent();
            file = this.pidFile;
        }
        if ( exists(path) && isDirectory(path) ) {
            if ( file != null && exists(file) ) {
                try {
                    delete(file);
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
            }
            if ( ! exists(file) ) {
                try {
                    file = createFile(file);
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
            }
        } else {
            log.error("Unable to create pid file. Directory does not exist: " + path);
            exit(PID_FILE_NOT_FOUND);
        }
        if ( file != null && exists(file) && isWritable(file) ) {
            RuntimeMXBean rtb = getRuntimeMXBean();
            String processName = rtb.getName();
            Integer pid = parsePid(processName);
            if (pid.intValue() > 0) {
                try {
                    write(file, valueOf(pid).getBytes(), TRUNCATE_EXISTING, WRITE, SYNC);
                    return file;
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
            }
        } else {
            log.error("Permission denied. Unable to create pid file: " + file);
            exit(UNABLE_TO_WRITE_PID_FILE);
        }
        return null;
    }

    protected static Integer parsePid(String processName) {
        Integer result = null;
        Pattern pattern = compile("^([0-9]+)@.+$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(processName);
        if (matcher.matches()) {
            result = Integer.valueOf(parseInt(matcher.group(1)));
        }
        return result;
     }

    protected void initLogger() {
        setProperty("org.jboss.logging.provider", "slf4j");
        final Writer writer;
        if (File.equals(logAppender)) {
            writer = new RollingFileWriter(
                        logFile.toString(),
                        MAX_VALUE,
                        new TimestampLabeler(),
                        new StartupPolicy(),
                        new DailyPolicy()
                    );
        } else {
            writer = new ConsoleWriter();
        }
        defaultConfig()
            .writer(writer)
            .level(logLevel)
            .level(Start.class, INFO)
            .level(CormorantServer.class, INFO)
            .level(PathObjectService.class, INFO)
            .level(EmbeddedServer.class, WARNING)
            .level(Start.class.getPackage(), WARNING)
            .level(Xnio.class.getPackage(), WARNING)
            .level(PathObjectService.class, WARNING)
            .formatPattern("{{level}|min-size=8} {date} {message}")
        .activate();
    }
}
