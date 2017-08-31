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

import static io.webfolder.cormorant.api.metadata.MetadataStorage.SQLite;
import static io.webfolder.cormorant.api.model.Role.Admin;
import static io.webfolder.server.command.LogAppender.Console;
import static io.webfolder.server.command.LogAppender.File;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Long.toHexString;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.exit;
import static java.lang.System.setProperty;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;
import static java.util.Collections.singletonMap;
import static org.mindrot.jbcrypt.BCrypt.gensalt;
import static org.mindrot.jbcrypt.BCrypt.hashpw;
import static org.pmw.tinylog.Configurator.defaultConfig;
import static org.pmw.tinylog.Level.ERROR;
import static org.pmw.tinylog.Level.INFO;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.nio.file.Path;
import java.security.SecureRandom;

import org.pmw.tinylog.Level;
import org.pmw.tinylog.labelers.TimestampLabeler;
import org.pmw.tinylog.policies.DailyPolicy;
import org.pmw.tinylog.policies.StartupPolicy;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.RollingFileWriter;
import org.pmw.tinylog.writers.Writer;
import org.slf4j.Logger;

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
import io.webfolder.server.CommandLine.Command;
import io.webfolder.server.CommandLine.Option;

@Command(
    name = "start",
    description = "Start object storage server.",
    sortOptions = true,
    showDefaultValues = true
)
public class Start implements ExitCodes {

    @Option(names = { "-h", "--host" }, description = "Server host.", paramLabel="<string>")
    private String host = "0.0.0.0";

    @Option(names = { "-p", "--port" }, description = "Server port.", paramLabel="<int>")
    private int port = 5000;

    @Option(names = { "-s", "--password" }, description = "admin password.", paramLabel = "<string>")
    private String password;

    @Option(names = { "-l", "--log-level" }, paramLabel = "<string>", description = "Sets log level (DEBUG, INFO, WARNING, ERROR).")
    private Level logLevel = ERROR;

    @Option(names = { "-r", "--log-appender" }, paramLabel ="<string>", description = "Sets log appender. This can be either <Console> or <File>." )
    private LogAppender logAppender = Console;

    @Option(names = { "-f", "--log-file" }, arity ="1", paramLabel = "<file>", description = "Writes generated log records to file.")
    private Path logFile = get("log").resolve("cormorant.log");

    @Option(names = { "-a", "--access-log-file" }, arity ="1", paramLabel = "<file>", description = "Writes http access log records to file.")
    private Path accessLog = get("log").resolve("access.log");

    @Option(names = { "-q", "--disable-access-log-file" }, description = "Turn off access logs.")
    private boolean disableAccessLog;

    @Option(names = { "-d", "--data-path" }, arity ="1", paramLabel = "<path>", description = "Sets data path.")
    private Path data = get("storage").resolve("data");

    @Option(names = { "-m", "--metadata-path" }, arity ="1", paramLabel = "<path>", description = "Sets metadata path.")
    private Path metadata = get("storage").resolve("metadata");

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

        String hashPwd = rndPwd != null ? hashpw(rndPwd, gensalt(12)) : password;

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
            .formatPattern("{{level}|min-size=8} {date} {message}")
        .activate();
    }
}
