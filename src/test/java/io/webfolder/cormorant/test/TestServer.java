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
