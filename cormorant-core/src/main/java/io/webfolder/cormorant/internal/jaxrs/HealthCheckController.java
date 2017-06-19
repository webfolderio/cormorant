package io.webfolder.cormorant.internal.jaxrs;

import static java.lang.Long.toHexString;
import static java.lang.String.valueOf;
import static java.time.ZoneId.of;
import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Locale.ENGLISH;
import static java.util.concurrent.ThreadLocalRandom.current;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.DATE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.ok;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class HealthCheckController {

    private static final String X_TRANS_ID = "X-Trans-Id";

    private static final ZoneId GMT = of("GMT");

    private static final DateTimeFormatter FORMATTER = ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'")
                                                    .withLocale(ENGLISH)
                                                    .withZone(GMT);

    @GET
    @PermitAll
    @Path("/healthcheck")
    public Response healthcheck() {
        final String part1 = leadingZeros(valueOf(toHexString(current().nextLong())), 21);
        final String part2 = leadingZeros(valueOf(toHexString(current().nextLong())), 10);
        return ok()
                .header(CONTENT_LENGTH, 2)
                .header(CONTENT_TYPE, TEXT_PLAIN)
                .header(DATE, FORMATTER.format(now()))
                .header(X_TRANS_ID, "tx" + part1 + "-" + (part2.length() > 10 ? part2.substring(0, 10) : part2))
                .entity("OK")
                .build();
    }

    protected String leadingZeros(final String str, final int length) {
        if (str.length() >= length) {
            return str;
        }
        else {
            return String.format("%0" + (length-str.length()) + "d%s", 0, str);
        }
    }
}
