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
