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

public class ListContainerOptions {

    private final String path     ;

    private final String delimiter;

    private final String prefix   ;

    private final Integer limit   ;

    private final String marker   ;

    private final String endMarker;

    private final Boolean reverse ;

    public ListContainerOptions(
                    final String path     ,
                    final String delimiter,
                    final String prefix   ,
                    final Integer limit   ,
                    final String marker   ,
                    final String endMarker,
                    final Boolean reverse ) {
        this.path      = path     ;
        this.delimiter = delimiter;
        this.prefix    = prefix   ;
        this.limit     = limit    ;
        this.marker    = marker   ;
        this.endMarker = endMarker;
        this.reverse   = reverse  ;
    }

    public String getPath() {
        return path;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getPrefix() {
        return prefix;
    }

    public Integer getLimit() {
        return limit;
    }

    public String getMarker() {
        return marker;
    }

    public String getEndMarker() {
        return endMarker;
    }

    public Boolean getReverse() {
        return reverse;
    }
}
