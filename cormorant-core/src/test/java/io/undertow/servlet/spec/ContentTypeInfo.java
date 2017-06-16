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
package io.undertow.servlet.spec;

/**
 * @author Stuart Douglas
 */
class ContentTypeInfo {
    private final String header;
    private final String charset;
    private final String contentType;

    ContentTypeInfo(String header, String charset, String contentType) {
        if (header.contains(";charset=")) {
            header = header.replaceFirst(";charset=", "; charset=");
        }
        this.header = header;
        this.charset = charset;
        this.contentType = contentType;
    }

    public String getHeader() {
        return header;
    }

    public String getCharset() {
        return charset;
    }

    public String getContentType() {
        return contentType;
    }
}
