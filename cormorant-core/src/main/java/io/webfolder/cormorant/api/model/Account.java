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
package io.webfolder.cormorant.api.model;

public class Account {

    private String name;

    private Long timestamp;

    private Long totalBytesUsed;

    private Long totalObjectCount;

    private Integer totalContainerCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTotalBytesUsed() {
        return totalBytesUsed;
    }

    public void setTotalBytesUsed(Long totalBytesUsed) {
        this.totalBytesUsed = totalBytesUsed;
    }

    public Long getTotalObjectCount() {
        return totalObjectCount;
    }

    public void setTotalObjectCount(Long totalObjectCount) {
        this.totalObjectCount = totalObjectCount;
    }

    public Integer getTotalContainerCount() {
        return totalContainerCount;
    }

    public void setTotalContainerCount(Integer totalContainerCount) {
        this.totalContainerCount = totalContainerCount;
    }
}
