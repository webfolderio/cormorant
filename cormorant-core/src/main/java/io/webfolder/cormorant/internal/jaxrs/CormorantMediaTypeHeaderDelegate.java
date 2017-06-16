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

import static javax.ws.rs.core.MediaType.WILDCARD_TYPE;

import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;

public class CormorantMediaTypeHeaderDelegate extends MediaTypeHeaderDelegate {

    public Object fromString(String type)  {
        // if Content-Type header value is null or empty string, 
        // default implementation throws IllegalArgumentException.
        if (type == null || type.isEmpty()) {
            return WILDCARD_TYPE;
        }
        return super.fromString(type);
    }
}
