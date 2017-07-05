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

import java.security.Principal;
import java.util.Map;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.webfolder.cormorant.api.exception.CormorantExceptionMapper;
import io.webfolder.cormorant.api.service.ContainerService;
import io.webfolder.cormorant.api.service.KeystoneService;
import io.webfolder.cormorant.api.service.MetadataService;
import io.webfolder.cormorant.internal.jaxrs.CormorantAuthenticationFeature;
import io.webfolder.cormorant.internal.jaxrs.ResponseWriter;

public class CormorantFeature<T> implements Feature {

    private final Map<String, Principal> tokens;

    private final KeystoneService        keystoneService;

    private final MetadataService        accountMetadataService;

    private final ContainerService<T>    containerService;

    private final Logger log = LoggerFactory.getLogger(CormorantFeature.class);

    public CormorantFeature(
                final Map<String, Principal> tokens,
                final KeystoneService        keystoneService,
                final MetadataService        accountMetadataService,
                final ContainerService<T>    containerService) {
        this.tokens                 = tokens;
        this.keystoneService        = keystoneService;
        this.accountMetadataService = accountMetadataService;
        this.containerService       = containerService;
    }

    @Override
    public boolean configure(FeatureContext context) {
        try {
            new ResteasyFeature().configure(context);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
        }
        context.register(new ResponseWriter());
        context.register(new CormorantExceptionMapper());
        context.register(new CormorantAuthenticationFeature<>(tokens,
                                                              keystoneService,
                                                              accountMetadataService,
                                                              containerService));
        return true;
    }
}
