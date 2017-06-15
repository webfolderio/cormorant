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
package io.webfolder.cormorant.internal.response;

public class ObjectPutResponseBody {

    private String lastModified;

    /**
     * The date and time when the object was last modified.
     *
     * The date and time stamp format is {@link <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601</a>
     *<p>
     * CCYY-MM-DDThh:mm:ss±hh:mm
     *<p>
     *
     * For example, {@literal 2015-08-27T09:49:58-05:00}.
     *
     * The {@literal ±hh:mm} value, if included, is the time zone as an offset
     * from UTC. In the previous example, the offset value is {@literal -05:00}.
     */
    public String getLastModified() {
        return lastModified;
    }

    /**
     * The date and time when the object was last modified.
     *
     * The date and time stamp format is {@link <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601</a>
     *<p>
     * CCYY-MM-DDThh:mm:ss±hh:mm
     *<p>
     *
     * For example, {@literal 2015-08-27T09:49:58-05:00}.
     *
     * The {@literal ±hh:mm} value, if included, is the time zone as an offset
     * from UTC. In the previous example, the offset value is {@literal -05:00}.
     */
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
}
