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
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.blobstore.RegionScopedBlobStoreContext;
import org.jclouds.openstack.swift.v1.features.AccountApi;
import org.jclouds.openstack.swift.v1.features.ContainerApi;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import io.undertow.Handlers;
import io.webfolder.cormorant.api.CormorantApplication;
import io.webfolder.cormorant.api.CormorantServer;
import io.webfolder.cormorant.api.service.AccountService;
import io.webfolder.cormorant.api.service.AuthenticationService;

public class TestSwift {

    protected static SwiftApi swiftApi;

    protected static AccountApi accountApi;

    protected static ContainerApi containerApi;

    protected static Path objectStore;

    protected static Path metadataStore;

    protected static String region;

    protected static CormorantServer server;

    protected static BlobStore blobStore;

    @BeforeClass
    public static void start() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

        Locale.setDefault(ENGLISH);

        server        = new CormorantServer();
        objectStore   = Paths.get("mydir");
        metadataStore = Paths.get("mymetadata");

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
            throw new RuntimeException(e);
        }

        accountApi = swiftApi.getAccountApi(region);

        containerApi = swiftApi.getContainerApi(region);
    }

    @AfterClass
    public static void stop() {
        server.stop();
    }
}
