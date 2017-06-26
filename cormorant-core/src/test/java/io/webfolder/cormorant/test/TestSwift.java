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
import static org.jclouds.ContextBuilder.newBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.model.Account;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.blobstore.RegionScopedBlobStoreContext;
import org.jclouds.openstack.swift.v1.features.AccountApi;
import org.jclouds.openstack.swift.v1.features.ContainerApi;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.ConsoleWriter;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import io.undertow.Handlers;
import io.webfolder.cormorant.api.CormorantApplication;
import io.webfolder.cormorant.api.CormorantServer;
import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.model.Role;
import io.webfolder.cormorant.api.model.User;
import io.webfolder.cormorant.api.service.AccountService;
import io.webfolder.cormorant.api.service.AuthenticationService;
import io.webfolder.cormorant.api.service.DefaultAuthenticationService;

public class TestSwift {

    protected static SwiftApi swiftApi;

    protected static AccountApi accountApi;

    protected static ContainerApi containerApi;

    protected static Path objectStore;

    protected static Path metadataStore;

    protected static String region;

    protected static CormorantServer server;

    protected static BlobStore blobStore;

    protected static AccountConfig jossConfig;

    protected static Account jossAccount;

    @BeforeClass
    public static void start() {
        Locale.setDefault(ENGLISH);

        System.setProperty("org.jboss.logging.provider", "slf4j");

        Configurator
                .defaultConfig()
                .writer(new ConsoleWriter())
                .level(Level.INFO)
                .formatPattern("{{level}|min-size=8} {date} {message}")
                .activate();

        server        = new CormorantServer();
        objectStore   = Paths.get("mydir");
        metadataStore = Paths.get("mymetadata");

        Map<String, User> users = new HashMap<>();

        User user = new User("myaccount",
                             "mypassword",
                             "test@example.com",
                             "default",
                             Role.Admin,
                             true);
        users.put("myaccount", user);

        AccountService accountService = new TestAccountService(objectStore);
        AuthenticationService authenticationService = new DefaultAuthenticationService(users);

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

        Iterable<Module> modules = ImmutableSet.<Module>of(
                                            new SLF4JLoggingModule());

        swiftApi = newBuilder("openstack-swift")
                    .endpoint("http://" + server.getHost() + ":5000" + "/v2.0")
                    .credentials("myaccount", "mypassword")
                    .modules(modules)
                    .buildApi(SwiftApi.class);

        Set<String> regions = swiftApi.getConfiguredRegions();

        region = regions.iterator().next();

        Properties overrides = new Properties();
        overrides.setProperty("jclouds.mpu.parallel.degree", "1");

        RegionScopedBlobStoreContext buildView = newBuilder("openstack-swift")
                                                    .endpoint("http://" + server.getHost() + ":5000" + "/v2.0")
                                                    .credentials("myaccount", "mypassword")
                                                    .overrides(overrides)
                                                    .modules(modules)
                                                    .buildView(RegionScopedBlobStoreContext.class);

        blobStore = buildView.getBlobStore(region);

        try {
            Files.walkFileTree(metadataStore, new RecursiveDeleteVisitor());
            Files.walkFileTree(objectStore, new RecursiveDeleteVisitor());
            Files.createDirectory(objectStore);
            Files.createDirectory(metadataStore);
        } catch (IOException e) {
            throw new CormorantException(e);
        }

        accountApi = swiftApi.getAccountApi(region);

        containerApi = swiftApi.getContainerApi(region);

        jossConfig = new AccountConfig();

        jossConfig.setUsername("myaccount");
        jossConfig.setPassword("mypassword");
        jossConfig.setAuthUrl("http://localhost:5000/auth/v1.0");
        jossConfig.setAuthenticationMethod(AuthenticationMethod.BASIC);

        jossAccount = new AccountFactory(jossConfig).createAccount();
    }

    @AfterClass
    public static void stop() {
        server.stop();
    }
}
