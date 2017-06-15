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

import static java.util.Locale.ENGLISH;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import io.undertow.Handlers;
import io.webfolder.cormorant.api.CormorantApplication;
import io.webfolder.cormorant.api.CormorantServer;
import io.webfolder.cormorant.api.service.AccountService;
import io.webfolder.cormorant.api.service.AuthenticationService;

public class TestServer {

    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

        Locale.setDefault(ENGLISH);

        CormorantServer server = new CormorantServer();
        Path objectStore = Paths.get("mydir");
        Path metadataStore = Paths.get("mymetadata");

        server.setHost("localhost");

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
        server.start((root) -> { return Handlers.requestDump(root); });
    }
}
