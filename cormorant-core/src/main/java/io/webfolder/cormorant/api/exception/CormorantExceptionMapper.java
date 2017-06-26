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
package io.webfolder.cormorant.api.exception;

import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.Response.status;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CormorantExceptionMapper implements ExceptionMapper<CormorantException> {

    private final Logger log = LoggerFactory.getLogger(CormorantExceptionMapper.class);

    @Override
    public Response toResponse(final CormorantException t) {
        Throwable cause = t.getCause();
        final boolean printDetail = ! t.getStatus().equals(CONFLICT) ? true : false;
        if (cause != null) {
            if (printDetail) {
                log.error(cause.getMessage(), cause);
            } else {
                log.error(cause.getMessage());
            }
        } else {
            if (printDetail) {
                log.error(t.getMessage(), t);
            } else {
                log.error(t.getMessage());
            }
        }
        final String error = t.getMessage();
        Response response = status(t.getStatusCode())
                                .header(CONTENT_LENGTH, error.length())
                                .entity(error)
                            .build();
        return response;
    }
}
