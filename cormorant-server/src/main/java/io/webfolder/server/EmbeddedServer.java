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
package io.webfolder.server;

import static io.webfolder.cormorant.api.metadata.MetadataStorage.SQLite;
import static io.webfolder.cormorant.api.model.Role.Admin;
import static java.lang.Long.toHexString;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.exit;
import static java.lang.System.setProperty;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;
import static java.util.Collections.singletonMap;
import static org.pmw.tinylog.Configurator.defaultConfig;
import static org.pmw.tinylog.Level.ERROR;
import static org.pmw.tinylog.Level.INFO;

import java.io.IOException;
import java.nio.file.Path;
import java.security.SecureRandom;

import org.pmw.tinylog.writers.ConsoleWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.webfolder.cormorant.api.CormorantApplication;
import io.webfolder.cormorant.api.CormorantConfiguration;
import io.webfolder.cormorant.api.CormorantConfiguration.Builder;
import io.webfolder.cormorant.api.CormorantServer;
import io.webfolder.cormorant.api.fs.PathAccountService;
import io.webfolder.cormorant.api.model.User;
import io.webfolder.cormorant.api.service.AccountService;
import io.webfolder.cormorant.api.service.DefaultKeystoneService;
import io.webfolder.cormorant.api.service.KeystoneService;

public class EmbeddedServer {

    private static final int CAN_NOT_CREATE_DATA_DIR     = -1;

    private static final int CAN_NOT_CREATE_METADATA_DIR = -2;

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedServer.class);

    public static void main(String[] args) {
        setProperty("org.jboss.logging.provider", "slf4j");

        defaultConfig()
                .writer(new ConsoleWriter())
                .level(ERROR)
                .level(EmbeddedServer.class, INFO)
                .formatPattern("{{level}|min-size=8} {date} {message}")
                .activate();

        final Path objectStore = get("data");

        if ( ! exists(objectStore) ) {
            try {
                createDirectory(objectStore);
            } catch (IOException e) {
                LOG.error(e.getMessage());
                exit(CAN_NOT_CREATE_DATA_DIR);
            }
        }

        final Path metadataStore = get("metadata");

        if ( ! exists(metadataStore) ) {
            try {
                createDirectory(metadataStore);
            } catch (IOException e) {
                LOG.error(e.getMessage());
                exit(CAN_NOT_CREATE_METADATA_DIR);
            }
        }


        User admin = new User("admin",
                              toHexString(new SecureRandom().nextLong()),
                              "admin@example.com",
                              "default",
                              Admin,
                              true);

        AccountService accountService   = new PathAccountService(objectStore);
        KeystoneService keystoneService = new DefaultKeystoneService(singletonMap(admin.getUsername(), admin));

        CormorantConfiguration configuration = new Builder()
                                                    .accountName("myaccount")
                                                    .cacheMetadata(true)
                                                    .storage(SQLite)
                                                    .pathMaxCount(10_000)
                                                    .objectStore(objectStore)
                                                    .metadataStore(metadataStore)
                                                    .build();

        CormorantApplication application = new CormorantApplication(configuration,
                                                    accountService,
                                                    keystoneService);

        CormorantServer server = new CormorantServer();
        server.deploy(application);

        server.start();

        Thread thread = new Thread(server::stop);
        thread.setDaemon(true);

        getRuntime().addShutdownHook(thread);

        LOG.info("Cormorant started.");

        LOG.info("Host     : {}", server.getHost());
        LOG.info("Port     : {}", server.getPort());
        LOG.info("Username : {}", admin.getUsername());
        LOG.info("Password : {}", admin.getPassword());
    }
}
