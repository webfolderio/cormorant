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

import static io.webfolder.cormorant.api.metadata.MetadataStorage.*;
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

import io.webfolder.cormorant.api.fs.FileChecksumService;
import io.webfolder.cormorant.api.fs.PathContainerService;
import io.webfolder.cormorant.api.fs.PathObjectService;
import io.webfolder.cormorant.api.metadata.DefaultMetadataServiceFactory;
import io.webfolder.cormorant.api.metadata.MetadataServiceFactory;
import io.webfolder.cormorant.api.metadata.MetadataStorage;
import io.webfolder.cormorant.api.service.AccountService;
import io.webfolder.cormorant.api.service.AuthenticationService;
import io.webfolder.cormorant.api.service.ContainerService;
import io.webfolder.cormorant.api.service.MetadataService;
import io.webfolder.cormorant.api.service.ObjectService;
import io.webfolder.cormorant.internal.jaxrs.AccountController;
import io.webfolder.cormorant.internal.jaxrs.AuthenticationController;
import io.webfolder.cormorant.internal.jaxrs.ContainerController;
import io.webfolder.cormorant.internal.jaxrs.FaviconController;
import io.webfolder.cormorant.internal.jaxrs.HealthCheckController;
import io.webfolder.cormorant.internal.jaxrs.ObjectController;

public class CormorantApplication extends Application {

    private final int pathMaxCount     = getPathMaxCount();

    private final Path                   objectStore;

    private final Path                   metadataStore;

    private final AccountService         accountService;

    private final AuthenticationService  authenticationService;

    private final String                 host;

    private final int                    port;

    private final String                 contextPath;

    private final String                 accountName;

    private MetadataStorage              metadataStorage;

    private boolean                      enableMetadataCache;

    public CormorantApplication(
                final Path objectStore,
                final Path metadataStore,
                final AccountService accountService,
                final AuthenticationService authenticationService,
                final String host,
                final int port,
                final String contextPath,
                final String accountName) {
        this.objectStore            = objectStore;
        this.metadataStore          = metadataStore;
        this.accountService         = accountService;
        this.authenticationService  = authenticationService;
        this.host                   = host;
        this.port                   = port;
        this.contextPath            = contextPath;
        this.accountName            = accountName;
        setMetadataStorage(SQLite);
        setEnableMetadataCache(false);
    }

    @Override
    public Set<Object> getSingletons() {
        final Set<Object> singletons = new HashSet<>();

        final MetadataServiceFactory metadataServiceFactory = new DefaultMetadataServiceFactory(metadataStore, getMetadataStorage());

        final MetadataService accountMetadataService   = metadataServiceFactory.create(ACCOUNT   , isEnableMetadataCache());
        final MetadataService containerMetadataService = metadataServiceFactory.create(CONTAINER , isEnableMetadataCache());
        final MetadataService objectMetadataService    = metadataServiceFactory.create(OBJECT    , isEnableMetadataCache());
        final MetadataService systemMetadataService    = metadataServiceFactory.create(OBJECT_SYS, isEnableMetadataCache());

        final FileChecksumService    checksumService  = new FileChecksumService(systemMetadataService);
        final ContainerService<Path> containerService = new PathContainerService(objectStore, pathMaxCount, checksumService, containerMetadataService, systemMetadataService);
        final ObjectService<Path>    objectService    = new PathObjectService(containerService, systemMetadataService, checksumService);

        containerService.setObjectService(objectService);
        checksumService.setObjectService(objectService);

        final Map<String, Principal> tokens = builder()
                                                .expirationPolicy(CREATED)
                                                .expiration(1, DAYS)
                                                .maxSize(100_000)
                                            .build();

        singletons.add(new HealthCheckController());

        singletons.add(new CormorantFeature<>(tokens, authenticationService, accountMetadataService, containerService, getContextPath()));

        singletons.add(new AuthenticationController(tokens, authenticationService, host, port, getContextPath(), accountName));

        singletons.add(new AccountController(accountService,
                                                    accountMetadataService));

        singletons.add(new ContainerController<Path>(accountService,
                                                    containerService,
                                                    containerMetadataService));

        singletons.add(new ObjectController<Path>(accountService,
                                                    containerService,
                                                    objectService,
                                                    checksumService,
                                                    objectMetadataService,
                                                    systemMetadataService,
                                                    new DefaultUrlDecoder()));

        singletons.add(new FaviconController());

        return singletons;
    }

    public String getContextPath() {
        return contextPath;
    }

    protected int getPathMaxCount() {
        return 10_000;
    }

    public MetadataStorage getMetadataStorage() {
        return metadataStorage;
    }

    public void setMetadataStorage(MetadataStorage metadataStorage) {
        this.metadataStorage = metadataStorage;
    }

    public boolean isEnableMetadataCache() {
        return enableMetadataCache;
    }

    public void setEnableMetadataCache(boolean enableMetadataCache) {
        this.enableMetadataCache = enableMetadataCache;
    }
}
