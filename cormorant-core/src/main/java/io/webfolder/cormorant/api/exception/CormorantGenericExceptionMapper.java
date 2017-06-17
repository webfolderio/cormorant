package io.webfolder.cormorant.api.exception;

import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CormorantGenericExceptionMapper implements ExceptionMapper<Throwable> {

    private final Logger log = LoggerFactory.getLogger(CormorantExceptionMapper.class);

    @Override
    public Response toResponse(Throwable t) {
        Throwable cause = t.getCause();
        if (cause != null) {
            log.error(cause.getMessage(), cause);
        } else {
            log.error(t.getMessage(), t);
        }
        final String error = t.getMessage();
        Response response = status(BAD_REQUEST)
                                .header(CONTENT_LENGTH, error.length())
                                .entity(error)
                            .build();
        return response;
    }
}
