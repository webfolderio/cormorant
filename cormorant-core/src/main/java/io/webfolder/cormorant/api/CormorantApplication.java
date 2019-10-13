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
package io.webfolder.cormorant.api;

import static io.webfolder.cormorant.api.metadata.CacheNames.ACCOUNT;
import static io.webfolder.cormorant.api.metadata.CacheNames.CONTAINER;
import static io.webfolder.cormorant.api.metadata.CacheNames.OBJECT;
import static io.webfolder.cormorant.api.metadata.CacheNames.OBJECT_SYS;
import static java.util.Collections.unmodifiableSet;
import static java.util.concurrent.TimeUnit.DAYS;
import static net.jodah.expiringmap.ExpirationPolicy.CREATED;
import static net.jodah.expiringmap.ExpiringMap.builder;

import java.nio.file.Path;
import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

import io.webfolder.cormorant.api.fs.PathContainerService;
import io.webfolder.cormorant.api.fs.PathObjectService;
import io.webfolder.cormorant.api.metadata.DefaultMetadataServiceFactory;
import io.webfolder.cormorant.api.metadata.MetadataServiceFactory;
import io.webfolder.cormorant.api.service.AccountService;
import io.webfolder.cormorant.api.service.ContainerService;
import io.webfolder.cormorant.api.service.KeystoneService;
import io.webfolder.cormorant.api.service.MetadataService;
import io.webfolder.cormorant.api.service.ObjectService;
import io.webfolder.cormorant.internal.jaxrs.AccountController;
import io.webfolder.cormorant.internal.jaxrs.AuthenticationController;
import io.webfolder.cormorant.internal.jaxrs.ContainerController;
import io.webfolder.cormorant.internal.jaxrs.FaviconController;
import io.webfolder.cormorant.internal.jaxrs.HealthCheckController;
import io.webfolder.cormorant.internal.jaxrs.ObjectController;
import io.webfolder.cormorant.internal.undertow.CormorantRemoteUser;

public class CormorantApplication extends Application {

    private final AccountService         accountService;

    private final KeystoneService        keystoneService;

    private final CormorantConfiguration configuration;

    private Set<Object> singletons;

    private Set<Class<?>> classes;

    public CormorantApplication(
                final CormorantConfiguration configuration,
                final AccountService         accountService,
                final KeystoneService        keystoneService) {
        this.configuration   = configuration;
        this.accountService  = accountService;
        this.keystoneService = keystoneService;
        init();
    }

    protected void init() {
        singletons = new HashSet<>();

        final MetadataServiceFactory metadataServiceFactory = new DefaultMetadataServiceFactory(configuration.getMetadataStore(), configuration.getStorage());

        final MetadataService accountMetadataService   = metadataServiceFactory.create(ACCOUNT   , configuration.isCacheMetadata());
        final MetadataService containerMetadataService = metadataServiceFactory.create(CONTAINER , configuration.isCacheMetadata());
        final MetadataService objectMetadataService    = metadataServiceFactory.create(OBJECT    , configuration.isCacheMetadata());
        final MetadataService systemMetadataService    = metadataServiceFactory.create(OBJECT_SYS, configuration.isCacheMetadata());

        final ContainerService<Path> containerService  = new PathContainerService(configuration.getObjectStore(),
                                                                configuration.getPathMaxCount(), containerMetadataService, systemMetadataService);
        final ObjectService<Path>    objectService     = new PathObjectService(containerService, systemMetadataService);

        containerService.setObjectService(objectService);

        final Map<String, Principal> tokens = builder()
                                                .expirationPolicy(CREATED)
                                                .expiration(1, DAYS)
                                                .maxSize(100_000)
                                            .build();

        CormorantRemoteUser.principals = tokens;

        singletons.add(new HealthCheckController());

        singletons.add(new CormorantFeature<>(tokens, keystoneService, accountMetadataService, containerService));

        singletons.add(new AuthenticationController(tokens, keystoneService, configuration.getAccountName()));

        singletons.add(new AccountController(accountService,
                                                    accountMetadataService));

        singletons.add(new ContainerController<Path>(accountService,
                                                    containerService,
                                                    containerMetadataService));

        singletons.add(new ObjectController<Path>(accountService,
                                                    containerService,
                                                    objectService,
                                                    objectMetadataService,
                                                    systemMetadataService));

        singletons = unmodifiableSet(singletons);

        classes = new HashSet<>();
        classes.add(FaviconController.class);

        classes = unmodifiableSet(classes);
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

    public Set<Class<?>> getClasses() {
        return classes;
    }
}
