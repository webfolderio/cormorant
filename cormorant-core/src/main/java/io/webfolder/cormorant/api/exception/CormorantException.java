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
