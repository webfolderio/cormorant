/**
 * cormorant - Object Storage Server
 * Copyright © 2017 WebFolder OÜ (cormorant@webfolder.io)
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

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import javax.ws.rs.core.Response.Status;

public class CormorantException extends RuntimeException {

    private static final long serialVersionUID = -6484994502244271330L;

    private final Status status;

    private final int statusCode;

    public CormorantException(final String message) {
        this(message, BAD_REQUEST);
    }

    public CormorantException(final String message, final Status status) {
        super(message);
        this.status = status;
        this.statusCode = status.getStatusCode();
    }

    public CormorantException(final String message, final int statusCode) {
        super(message);
        this.status = null;
        this.statusCode = statusCode;
    }

    public CormorantException(Throwable e) {
        super(e);
        this.status = BAD_REQUEST;
        this.statusCode = status.getStatusCode();
    }

    public CormorantException(String message, Throwable e) {
        super(message, e);
        this.status = BAD_REQUEST;
        this.statusCode = status.getStatusCode();
    }

    public Status getStatus() {
        return status;
    }

    public int getStatusCode() {
        return status != null ? status.getStatusCode() : statusCode;
    }
}
