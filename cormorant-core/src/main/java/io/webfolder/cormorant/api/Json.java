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
package io.webfolder.cormorant.api;

import java.util.List;
import java.util.Map;

public class Json {

    private mjson.Json delegate;

    private Json(mjson.Json delegate) {
        this.delegate = delegate;
    }

    public Json at(String property) {
        final mjson.Json json = delegate.at(property);
        if (json == null) {
            return null;
        }
        return new Json(json);
    }

    public String asString() {
        return delegate.asString();
    }

    public boolean isObject() {
        return delegate.isObject();
    }

    public boolean isString() {
        return delegate.isString();
    }

    public Map<String, Object> asMap() {
        return delegate.asMap();
    }

    public boolean has(String property) {
        return delegate.has(property);
    }

    public Json delAt(String property) {
        delegate.delAt(property);
        return this;
    }

    public Json set(String property, String value) {
        delegate.set(property, value);
        return this;
    }

    public Json set(String property, Json value) {
        delegate.set(property, value.delegate);
        return this;
    }

    public static Json object() {
        return new Json(mjson.Json.object());
    }

    public static Json read(String content) {
        return new Json(mjson.Json.read(content));
    }

    public boolean isArray() {
        return delegate.isArray();
    }

    public List<Object> asList() {
        return delegate.asList();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
