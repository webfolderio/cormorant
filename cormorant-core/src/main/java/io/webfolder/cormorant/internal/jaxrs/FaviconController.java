package io.webfolder.cormorant.internal.jaxrs;

import static javax.ws.rs.core.Response.ok;
import static org.apache.commons.io.IOUtils.toByteArray;

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
