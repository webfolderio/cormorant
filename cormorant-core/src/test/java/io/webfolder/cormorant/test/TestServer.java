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
package io.webfolder.cormorant.test;

import static io.undertow.Handlers.predicate;
import static io.undertow.Handlers.requestDump;
import static io.undertow.predicate.Predicates.path;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.mindrot.jbcrypt.BCrypt;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.ConsoleWriter;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.webfolder.cormorant.api.CormorantApplication;
import io.webfolder.cormorant.api.CormorantConfiguration;
import io.webfolder.cormorant.api.CormorantServer;
import io.webfolder.cormorant.api.fs.PathAccountService;
import io.webfolder.cormorant.api.metadata.MetadataStorage;
import io.webfolder.cormorant.api.model.Role;
import io.webfolder.cormorant.api.model.User;
import io.webfolder.cormorant.api.service.AccountService;
import io.webfolder.cormorant.api.service.KeystoneService;
import io.webfolder.cormorant.api.service.DefaultKeystoneService;

public class TestServer {

    @Test
    public void startServer() {
        CormorantServer server = new CormorantServer();

        try {
            if (Files.exists(Paths.get("cormorant.db"))) {
                Files.delete(Paths.get("cormorant.db"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.setProperty("org.jboss.logging.provider", "slf4j");

        Configurator
                .defaultConfig()
                .writer(new ConsoleWriter())
                .level(Level.ERROR)
                .formatPattern("{{level}|min-size=8} {date} {message}")
                .activate();

        Path objectStore = Paths.get("mydir");
        Path metadataStore = Paths.get("mymetadata");

        if ( ! Files.exists(objectStore) ) {
            try {
                Files.createDirectory(objectStore);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if ( ! Files.exists(metadataStore) ) {
            try {
                Files.createDirectory(metadataStore);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        Map<String, User> users = new HashMap<>();

        User user = new User("myaccount",
                            BCrypt.hashpw("mypassword", BCrypt.gensalt(12)),
                            "test@example.com",
                            "default",
                            Role.Admin,
                            true);
        users.put("myaccount", user);

        AccountService accountService   = new PathAccountService(objectStore);
        KeystoneService keystoneService = new DefaultKeystoneService(users);

        CormorantConfiguration configuration = new CormorantConfiguration.Builder()
                                                        .accountName("myaccount")
                                                        .cacheMetadata(true)
                                                        .storage(MetadataStorage.File)
                                                        .pathMaxCount(10_000)
                                                        .objectStore(objectStore)
                                                        .metadataStore(metadataStore)
                                                        .build();

        CormorantApplication application = new CormorantApplication(configuration,
                                                    accountService,
                                                    keystoneService);

        server.deploy(application);

        CountDownLatch latch = new CountDownLatch(1);

        HttpHandler shutdownHandler = new HttpHandler() {

            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                new Thread() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("bye");
                        latch.countDown();
                    }
                }.start();

                exchange.setStatusCode(200);
                exchange.getResponseSender().send("OK");
                exchange.getResponseSender().close();
            }
        };

        boolean dumpRequest = "true".equals(System.getProperty("dump.request", "false"));

        server.start((root) -> { return
            predicate(path("/shutdown"), shutdownHandler, dumpRequest ? requestDump(root) : root);
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-2);
        }

        server.stop();

        try {
            JUnitCore core = new JUnitCore();
            core.run(TestCormorant.class);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
