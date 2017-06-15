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
import java.time.Instant;

class CormorantPrincipal implements Principal {

    private final String name;

    private final String token;

    private final Instant expires;

    private final String auditId;
    
    public CormorantPrincipal(
                final String name,
                final String token,
                final Instant expires) {
        this(name, token, expires, null);
    }

    public CormorantPrincipal(
                            final String  name,
                            final String  token,
                            final Instant expires,
                            final String  auditId) {
        this.name = name;
        this.token = token;
        this.expires = expires;
        this.auditId = auditId;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public Instant getExpires() {
        return expires;
    }

    public String getAuditId() {
        return auditId;
    }

    @Override
    public String toString() {
        return "CormorantPrincipal [name=" + name + ", token=" + token + ", expires=" + expires + ", auditId=" + auditId
                + "]";
    }
}
