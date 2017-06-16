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

public class AccountGetResponseBody {

    private Long count;

    private Long bytes;

    private String name;

    private Long lastModified;

    /**
     * The number of objects in the container.
     */
    public Long getCount() {
        return count;
    }

    /**
     * The number of objects in the container.
     */
    public void setCount(Long count) {
        this.count = count;
    }

    /**
     * The total number of bytes that are stored in
     * Object Storage for the account.
     */
    public Long getBytes() {
        return bytes;
    }

    /**
     * The total number of bytes that are stored in
     * Object Storage for the account.
     */
    public void setBytes(Long bytes) {
        this.bytes = bytes;
    }

    /**
     * The name of the container.
     */
    public String getName() {
        return name;
    }

    /**
     * The name of the container.
     */
    public void setName(String name) {
        this.name = name;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }
}
