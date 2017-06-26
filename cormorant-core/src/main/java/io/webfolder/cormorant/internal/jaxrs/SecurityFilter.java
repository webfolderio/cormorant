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

import static io.webfolder.cormorant.api.model.Permission.ADMIN;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.time.Instant.now;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;

import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.service.AuthenticationService;
import io.webfolder.cormorant.api.service.ContainerService;
import io.webfolder.cormorant.api.service.MetadataService;

class SecurityFilter<T> implements ContainerRequestFilter {

    private final Map<String, Principal> tokens;

    private final AuthenticationService  authenticationService;

    private final String                 permission;

    private final String                 AUTH_TOKEN          = "X-Auth-Token";

    private final String                 TEMP_URL_SIG        = "temp_url_sig";

    private final String                 TEMP_URL_EXPIRES    = "temp_url_expires";
 
    private final String                 HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private final char[]                 HEX_ARRAY           = "0123456789abcdef".toCharArray();

    private final MetadataService accountMetadataService;

    private final ContainerService<T> containerService;

    private final String contextPath;

    public SecurityFilter(
                final Map<String, Principal> tokens,
                final String                 permission,
                final AuthenticationService  authenticationService,
                final MetadataService        accountMetadataService,
                final ContainerService<T>    containerService,
                final String                 contextPath) {
        this.tokens                 = tokens;
        this.permission             = permission;
        this.authenticationService  = authenticationService;
        this.accountMetadataService = accountMetadataService;
        this.containerService       = containerService;
        this.contextPath            = contextPath;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final String authToken = requestContext.getHeaderString(AUTH_TOKEN);
        if (authToken == null) {
            final String tus = requestContext.getUriInfo().getQueryParameters().getFirst(TEMP_URL_SIG);
            final String tue = requestContext.getUriInfo().getQueryParameters().getFirst(TEMP_URL_EXPIRES);

            if  ( tus != null && tue != null ) {
                long expires;
                try {
                    expires = parseLong(tue);
                } catch (NumberFormatException e) {
                    expires = 0L;
                }
                final long unixTime = expires * 1000L;
                if (unixTime > currentTimeMillis()) {
                    final String hmacBody = format("%s\n%s\n%s", requestContext.getMethod(), tue, contextPath + requestContext.getUriInfo().getPath());
                    final String account = requestContext.getUriInfo().getPathParameters().getFirst("account");
                    String tempUrlKey = accountMetadataService.getProperty(account, "temp-url-key");
                    if ( tempUrlKey != null && ! tempUrlKey.isEmpty() ) {
                        String hash = calculateHash(hmacBody, tempUrlKey);
                        if (  tus.equalsIgnoreCase(hash) ) {
                            return;
                        }
                    }
                }
            } else {
                requestContext.abortWith(status(UNAUTHORIZED).entity("401 Unauthorized").build());
                return;
            }
        }

        final CormorantPrincipal principal = (CormorantPrincipal) tokens.get(authToken);
        if (principal == null) {
            requestContext.abortWith(status(UNAUTHORIZED).entity("401 Unauthorized").build());
            return;
        }

        if (now().isAfter(principal.getExpires())) {
            final String error = "Auth token has expired.";
            tokens.remove(authToken);
            requestContext
                .abortWith(status(UNAUTHORIZED)
                .header(CONTENT_LENGTH, error.length())
                .entity(error)
                .build());
        }

        // Let the authenticated user delete its own account
        // This logic is required to pass tempest (TokensV3Test.test_create_token)
        boolean deleteSelf = ADMIN.equals(permission) &&
                                "DELETE".equalsIgnoreCase(requestContext.getMethod()) &&
                                principal.getName().equals(requestContext.getUriInfo().getPathParameters().getFirst("userId"));

        final boolean authorized = authenticationService.hasPermission(principal.getName(), permission, requestContext.getMethod());
        if ( ! deleteSelf && ! authorized ) {
            final String error = "Insufficient permission.";
            requestContext
                    .abortWith(status(FORBIDDEN)
                    .header(CONTENT_LENGTH, error.length())
                    .entity(error)
                    .build());
            return;
        }

        if ("cormorant-object".equals(permission)) {
            final String accountName = requestContext.getUriInfo().getPathParameters().getFirst("account");
            final String containerName = requestContext.getUriInfo().getPathParameters().getFirst("container");
            if ( ! containerService.contains(accountName, containerName) ) {
                final String error = "Container [" + containerName + "] not found.";
                requestContext
                        .abortWith(status(FORBIDDEN)
                        .header(CONTENT_LENGTH, error.length())
                        .entity(error)
                        .build());
                return;
            }
        }

        SecurityContext securityContext = requestContext.getSecurityContext();
        requestContext.setSecurityContext(new CormorantSecurityContext(securityContext,
                                                    principal,
                                                    authenticationService,
                                                    requestContext.getMethod()));
    }

    protected String bytesToHex(final byte[] bytes) {
        final char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String calculateHash(final String data, final String privateKey) {
        final SecretKeySpec signingKey = new SecretKeySpec(privateKey.getBytes(), HMAC_SHA1_ALGORITHM);
        final Mac mac;
        try {
            mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            return bytesToHex(mac.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CormorantException(e);
        }
    }
}
