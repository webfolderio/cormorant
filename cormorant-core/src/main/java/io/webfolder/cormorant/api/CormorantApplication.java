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
package io.webfolder.cormorant.api;

import static io.webfolder.cormorant.api.metadata.CacheNames.ACCOUNT;
import static io.webfolder.cormorant.api.metadata.CacheNames.CONTAINER;
import static io.webfolder.cormorant.api.metadata.CacheNames.OBJECT;
import static io.webfolder.cormorant.api.metadata.CacheNames.OBJECT_SYS;
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

    public CormorantApplication(
                final CormorantConfiguration configuration,
                final AccountService         accountService,
                final KeystoneService        keystoneService) {
        this.configuration   = configuration;
        this.accountService  = accountService;
        this.keystoneService = keystoneService;
    }

    @Override
    public Set<Object> getSingletons() {
        final Set<Object> singletons = new HashSet<>();

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

        singletons.add(new FaviconController());

        return singletons;
    }
}
