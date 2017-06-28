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

import static io.webfolder.cormorant.api.cache.CacheFactory.ACCOUNT;
import static io.webfolder.cormorant.api.cache.CacheFactory.CONTAINER;
import static io.webfolder.cormorant.api.cache.CacheFactory.OBJECT;
import static io.webfolder.cormorant.api.cache.CacheFactory.TOKENS;
import static io.webfolder.cormorant.api.metadata.MetadataServiceFactory.METADATA;
import static io.webfolder.cormorant.api.metadata.MetadataServiceFactory.SYSTEM_METADATA;
import static java.util.ServiceLoader.load;

import java.nio.file.Path;
import java.security.Principal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import javax.ws.rs.core.Application;

import io.webfolder.cormorant.api.cache.CacheFactory;
import io.webfolder.cormorant.api.cache.DefaultCacheFactory;
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

    private final AccountService         accountService;

    private final AuthenticationService  authenticationService;

    private final MetadataServiceFactory metadataServiceFactory;

    private final String                 host;

    private final int                    port;

    private final String                 contextPath;

    private final String                 accountName;

    private MetadataStorage              metadataStorage = MetadataStorage.SQLite;

    public CormorantApplication(
                final Path objectStore,
                final Path propertyStore,
                final AccountService accountService,
                final AuthenticationService authenticationService,
                final String host,
                final int port,
                final String contextPath,
                final String accountName) {
        this.objectStore            = objectStore;
        this.accountService         = accountService;
        this.authenticationService  = authenticationService;
        this.metadataServiceFactory = createPropertyServiceFactory(propertyStore);
        this.host                   = host;
        this.port                   = port;
        this.contextPath            = contextPath;
        this.accountName            = accountName;
    }

    @Override
    public Set<Object> getSingletons() {
        final Set<Object> singletons = new HashSet<>();

        final MetadataService accountMetadataService   = metadataServiceFactory.create(ACCOUNT  , METADATA       , isCacheable(ACCOUNT));
        final MetadataService containerMetadataService = metadataServiceFactory.create(CONTAINER, METADATA       , isCacheable(CONTAINER));
        final MetadataService objectMetadataService    = metadataServiceFactory.create(OBJECT   , METADATA       , isCacheable(OBJECT));
        final MetadataService systemMetadataService    = metadataServiceFactory.create(OBJECT   , SYSTEM_METADATA, isCacheable(OBJECT));

        final FileChecksumService    checksumService  = new FileChecksumService(objectMetadataService);
        final ContainerService<Path> containerService = new PathContainerService(objectStore, pathMaxCount, checksumService, containerMetadataService, systemMetadataService);
        final ObjectService<Path>    objectService    = new PathObjectService(containerService, systemMetadataService, checksumService);

        containerService.setObjectService(objectService);
        checksumService.setObjectService(objectService);

        final ServiceLoader<CacheFactory> cacheLoader   = load(CacheFactory.class, getClass().getClassLoader());
        final Iterator<CacheFactory>      cacheIterator = cacheLoader.iterator();
        final CacheFactory                cacheFactory  = cacheIterator.hasNext()        ?
                                                                cacheIterator.next()     :
                                                                new DefaultCacheFactory();

        final Map<String, Principal> tokens = cacheFactory.create(TOKENS);

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

    protected MetadataServiceFactory createPropertyServiceFactory(final Path propertyStore) {
        return new DefaultMetadataServiceFactory(propertyStore, getMetadataStorage());
    }

    protected int getPathMaxCount() {
        return 10_000;
    }

    protected boolean isCacheable(final String cacheName) {
        return true;
    }

    public MetadataStorage getMetadataStorage() {
        return metadataStorage;
    }

    public void setMetadataStorage(MetadataStorage metadataStorage) {
        this.metadataStorage = metadataStorage;
    }
}
