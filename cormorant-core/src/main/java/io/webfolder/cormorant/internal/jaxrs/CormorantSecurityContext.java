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
