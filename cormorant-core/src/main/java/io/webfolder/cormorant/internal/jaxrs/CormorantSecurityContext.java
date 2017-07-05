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

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import io.webfolder.cormorant.api.service.KeystoneService;

class CormorantSecurityContext implements SecurityContext {

    private final SecurityContext securityContext;

    private final Principal principal;

    private final KeystoneService keystoneService;

    private final String method;
    
    public CormorantSecurityContext(
                final SecurityContext securityContext,
                final Principal       principal,
                final KeystoneService keystoneService,
                final String          method) {
        this.securityContext = securityContext;
        this.principal       = principal;
        this.keystoneService = keystoneService;
        this.method          = method;
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return keystoneService.hasPermission(principal.getName(), role, method);
    }

    @Override
    public boolean isSecure() {
        return securityContext.isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return "TOKEN_AUTH";
    }
}
