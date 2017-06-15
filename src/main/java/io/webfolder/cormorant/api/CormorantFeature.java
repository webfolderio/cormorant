/**
 * cormorant - Object Storage Server
 * Copyright © 2017 WebFolder OÜ (cormorant@webfolder.io)
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

import java.security.Principal;
import java.util.Map;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import io.webfolder.cormorant.api.exception.CormorantExceptionMapper;
import io.webfolder.cormorant.api.service.AuthenticationService;
import io.webfolder.cormorant.api.service.ContainerService;
import io.webfolder.cormorant.api.service.MetadataService;
import io.webfolder.cormorant.internal.jaxrs.CormorantAuthenticationFeature;
import io.webfolder.cormorant.internal.jaxrs.CormorantMediaTypeHeaderDelegate;
import io.webfolder.cormorant.internal.jaxrs.ResponseWriter;

public class CormorantFeature<T> implements Feature {

    private final Map<String, Principal> tokens;

    private final AuthenticationService authenticationService;

    private final MetadataService accountMetadataService;

    private final ContainerService<T> containerService;

    private final String contextPath;

    public CormorantFeature(
                final Map<String, Principal> tokens,
                final AuthenticationService authenticationService,
                final MetadataService accountMetadataService,
                final ContainerService<T> containerService,
                final String contextPath) {
        this.tokens = tokens;
        this.authenticationService = authenticationService;
        this.accountMetadataService = accountMetadataService;
        this.containerService = containerService;
        this.contextPath = contextPath;
    }

    @Override
    public boolean configure(FeatureContext context) {
        ResteasyProviderFactory providerFactory = (ResteasyProviderFactory) context.getConfiguration();
        providerFactory.addHeaderDelegate(MediaType.class, new CormorantMediaTypeHeaderDelegate());
        context.register(new ResponseWriter());
        context.register(new CormorantExceptionMapper());
        context.register(new CormorantAuthenticationFeature<>(tokens,
                                                              authenticationService,
                                                              accountMetadataService,
                                                              containerService,
                                                              contextPath));
        return true;
    }
}
