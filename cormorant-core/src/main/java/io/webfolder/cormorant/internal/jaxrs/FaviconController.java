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

import static javax.ws.rs.core.Response.ok;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

@Path("/favicon.ico")
public class FaviconController {

    private final byte[] icon;

    private final CacheControl cache;

    private final String mimeType = "image/x-icon";

    public FaviconController() {
        try (InputStream is = getClass().getResourceAsStream("/io/webfolder/cormorant/favicon.ico")) {
            icon = toByteArray(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cache = new CacheControl();
        cache.setMaxAge(60 * 60 * 24 * 7);
        cache.setMustRevalidate(false);
    }

    public byte[] toByteArray(InputStream is) throws IOException {
        try (ByteArrayOutputStream r = new ByteArrayOutputStream(2048)) {
            byte[] read = new byte[512];
            for (int i; -1 != (i = is.read(read)); r.write(read, 0, i));
            is.close();
            return r.toByteArray();
        }
    }

    @GET
    @PermitAll
    public Response icon() {
        return ok()
                .entity(icon)
                .type(mimeType)
                .cacheControl(cache)
            .build();
    }
}
