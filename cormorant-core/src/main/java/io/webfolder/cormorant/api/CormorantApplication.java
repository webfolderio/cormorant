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
import static io.webfolder.cormorant.api.property.MetadataServiceFactory.METADATA;
import static io.webfolder.cormorant.api.property.MetadataServiceFactory.SYSTEM_METADATA;
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
import io.webfolder.cormorant.api.property.DefaultMetadataServiceFactory;
import io.webfolder.cormorant.api.property.MetadataServiceFactory;
import io.webfolder.cormorant.api.service.AccountService;
import io.webfolder.cormorant.api.service.AuthenticationService;
import io.webfolder.cormorant.api.service.ContainerService;
import io.webfolder.cormorant.api.service.MetadataService;
import io.webfolder.cormorant.api.service.ObjectService;
import io.webfolder.cormorant.internal.jaxrs.AccountController;
import io.webfolder.cormorant.internal.jaxrs.AuthenticationController;
import io.webfolder.cormorant.internal.jaxrs.ContainerController;
import io.webfolder.cormorant.internal.jaxrs.HealthCheckController;
import io.webfolder.cormorant.internal.jaxrs.ObjectController;

public class CormorantApplication extends Application {

    private final int pathMaxCount = getPathMaxCount();

    private final Path objectStore;

    private final AccountService accountService;

    private final AuthenticationService authenticationService;

    private final MetadataServiceFactory propertyServiceFactory;

    private final String host;

    private final int port;

    private final String contextPath;

    private final String accountName;

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
        this.propertyServiceFactory = createPropertyServiceFactory(propertyStore);
        this.host                   = host;
        this.port                   = port;
        this.contextPath            = contextPath;
        this.accountName            = accountName;
    }

    @Override
    public Set<Object> getSingletons() {
        final Set<Object> singletons = new HashSet<>();

        final boolean cacheable = true;

        final MetadataService accountMetadataService   = propertyServiceFactory.create(ACCOUNT  , METADATA       , cacheable);
        final MetadataService containerMetadataService = propertyServiceFactory.create(CONTAINER, METADATA       , cacheable);
        final MetadataService objectMetadataService    = propertyServiceFactory.create(OBJECT   , SYSTEM_METADATA, cacheable);

        final FileChecksumService     checksumService  = new FileChecksumService(objectMetadataService);
        final ContainerService<Path>  containerService = new PathContainerService(objectStore, pathMaxCount, checksumService, containerMetadataService);
        final ObjectService<Path>     objectService    = new PathObjectService(containerService);

        checksumService.setObjectService(objectService);

        final ServiceLoader<CacheFactory> cacheLoader   = load(CacheFactory.class, getClass().getClassLoader());
        final Iterator<CacheFactory>      cacheIterator = cacheLoader.iterator();
        final CacheFactory                cacheFactory  = cacheIterator.hasNext()        ?
                                                                cacheIterator.next()     :
                                                                new DefaultCacheFactory();

        final Map<String, Principal> tokens = cacheFactory.create(TOKENS);

        singletons.add(new HealthCheckController());

        singletons.add(new CormorantFeature<>(tokens, authenticationService, accountMetadataService, containerService, contextPath));

        singletons.add(new AuthenticationController(tokens, authenticationService, host, port, contextPath, accountName));

        singletons.add(new AccountController(accountService,
                                                    accountMetadataService));

        singletons.add(new ContainerController<Path>(accountService,
                                                    containerService,
                                                    containerMetadataService));

        final MetadataService systemMetadataService = propertyServiceFactory.create(OBJECT, METADATA, cacheable);
        singletons.add(new ObjectController<Path>(accountService,
                                                    containerService,
                                                    objectService,
                                                    checksumService,
                                                    objectMetadataService,
                                                    systemMetadataService));

        return singletons;
    }

    protected MetadataServiceFactory createPropertyServiceFactory(final Path propertyStore) {
        return new DefaultMetadataServiceFactory(propertyStore);
    }

    protected int getPathMaxCount() {
        return 10_000;
    }

    public String getContextPath() {
        return contextPath;
    }
}
