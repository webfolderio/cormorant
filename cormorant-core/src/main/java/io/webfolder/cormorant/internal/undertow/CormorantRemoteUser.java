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
package io.webfolder.cormorant.internal.undertow;

import static io.undertow.util.HttpString.tryFromString;

import java.security.Principal;
import java.util.Map;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import static java.util.Collections.emptyMap;

public class CormorantRemoteUser implements ExchangeAttribute {

    protected static final ExchangeAttribute INSTANCE = new CormorantRemoteUser();

    private final String AUTH_TOKEN = "X-Auth-Token";

    private final HttpString H_AUTH_TOKEN = tryFromString(AUTH_TOKEN);

    public static Map<String, Principal> principals = emptyMap();

    @Override
    public String readAttribute(HttpServerExchange exchange) {
        String token = exchange.getRequestHeaders().getFirst(H_AUTH_TOKEN);
        if ( token != null ) {
            Principal principal = principals.get(token);
            if ( principal != null ) {
                return principal.getName();
            }
        }
        return null;
    }

    @Override
    public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
        throw new ReadOnlyAttributeException("Cormorant Remote User", newValue);
    }
}
