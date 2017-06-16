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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.webfolder.cormorant.api.CormorantApplication;
import io.webfolder.cormorant.api.CormorantServer;
import io.webfolder.cormorant.api.service.AccountService;
import io.webfolder.cormorant.api.service.AuthenticationService;

public class TestServer {

    @Test
    public void startServer() {

        if ( ! "true".equals(System.getProperty("start.server")) ) {
            return;
        }

        CormorantServer server = new CormorantServer();

        Path objectStore = Paths.get("mydir");

        Path metadataStore = Paths.get("mymetadata");
        
        AccountService accountService = new TestAccountService(objectStore);
        AuthenticationService authenticationService = new TestAuthenticationService();

        server.deploy(
                new CormorantApplication(objectStore,
                                         metadataStore,
                                         accountService,
                                         authenticationService,
                                         server.getHost(),
                                         server.getPort(),
                                         "",
                                         "myaccount"));

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

        boolean dumpRequest = "true".equals(System.getProperty("dump.request"));

        server.start((root) -> { return
            predicate(path("/shutdown"), shutdownHandler, dumpRequest ? requestDump(root) : root);
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-2);
        }
    }
}
