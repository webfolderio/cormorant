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
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.ConsoleWriter;

import at.favre.lib.crypto.bcrypt.BCrypt;
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
import io.webfolder.cormorant.api.service.DefaultKeystoneService;
import io.webfolder.cormorant.api.service.KeystoneService;

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
                            BCrypt.withDefaults().hashToString(12, "mypassword".toCharArray()),
                            "test@example.com",
                            "default",
                            Role.Admin,
                            true);
        users.put("myaccount", user);

        AccountService accountService   = new PathAccountService(objectStore);
        KeystoneService keystoneService = new DefaultKeystoneService(users);

        CormorantConfiguration configuration = new CormorantConfiguration.Builder()
                                                        .accountName("myaccount")
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
