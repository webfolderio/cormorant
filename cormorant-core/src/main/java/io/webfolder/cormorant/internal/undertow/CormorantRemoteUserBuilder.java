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

import static io.webfolder.cormorant.internal.undertow.CormorantRemoteUser.INSTANCE;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeBuilder;

public class CormorantRemoteUserBuilder implements ExchangeAttributeBuilder {

    public static final String REMOTE_USER_SHORT = "%u";

    public static final String REMOTE_USER = "%{REMOTE_USER}";

    @Override
    public String name() {
        return "Cormorant Remote User";
    }

    @Override
    public ExchangeAttribute build(String token) {
        if (token.equals(REMOTE_USER) || token.equals(REMOTE_USER_SHORT)) {
            return INSTANCE;
        }
        return null;
    }

    @Override
    public int priority() {
        return 100;
    }
}
