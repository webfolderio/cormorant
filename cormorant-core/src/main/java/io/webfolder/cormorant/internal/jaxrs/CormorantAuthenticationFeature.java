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
package io.webfolder.cormorant.internal.jaxrs;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import io.webfolder.cormorant.api.service.KeystoneService;
import io.webfolder.cormorant.api.service.ContainerService;
import io.webfolder.cormorant.api.service.MetadataService;

public class CormorantAuthenticationFeature<T> implements DynamicFeature {

    private final Map<String, Principal> tokens;

    private final KeystoneService        keystoneService;

    private final MetadataService        accountMetadataService;

    private final ContainerService<T>    containerService;

    public CormorantAuthenticationFeature(
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
    public void configure(
                    final ResourceInfo   resourceInfo,
                    final FeatureContext context) {

        final Method method = resourceInfo.getResourceMethod();

        if (method.isAnnotationPresent(PermitAll.class)) {
            return;
        }

        final Class<?> klass = resourceInfo.getResourceClass();

        if (klass.getPackage()
                .equals(getClass().getPackage()) &&
                klass.isAnnotationPresent(RolesAllowed.class)) {
            final RolesAllowed rolesAllowed = klass.getAnnotation(RolesAllowed.class);
            final String       role         = rolesAllowed.value()[0];
            context.register(new SecurityFilter<>(
                                        tokens,
                                        role,
                                        keystoneService,
                                        accountMetadataService,
                                        containerService));
        }
    }
}
