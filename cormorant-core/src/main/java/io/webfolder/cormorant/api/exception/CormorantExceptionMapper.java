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
package io.webfolder.cormorant.api.exception;

import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.REQUEST_ENTITY_TOO_LARGE;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CormorantExceptionMapper implements ExceptionMapper<CormorantException> {

    private final Logger log = LoggerFactory.getLogger(CormorantExceptionMapper.class);

    private static final int UNPROCESSABLE_ENTITY = 422;

    @Override
    public Response toResponse(final CormorantException t) {
        final boolean silent = t.getStatusCode() == UNPROCESSABLE_ENTITY               ||
                                        BAD_REQUEST.equals(t.getStatus())              ||
                                        REQUEST_ENTITY_TOO_LARGE.equals(t.getStatus()) ||
                                        NOT_FOUND.equals(t.getStatus())                ||
                                        CONFLICT.equals(t.getStatus()) ? true : false;
        if (silent) {
            log.error(t.getMessage());
        } else {
            log.error(t.getMessage(), t);
        }
        final String error = t.getMessage();
        return status(t.getStatusCode())
                    .header(CONTENT_LENGTH, error.length())
                    .entity(error)
                .build();
    }
}
