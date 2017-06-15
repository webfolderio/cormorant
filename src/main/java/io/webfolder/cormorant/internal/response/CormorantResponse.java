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
package io.webfolder.cormorant.internal.response;

public interface CormorantResponse {

    /**
     * <p>The date and time the system responded to the request, using the preferred format of {@link <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">RFC 7231</a>} as shown in this example {@literal Thu, 16 Jun 2016 15:10:38 GMT}. The time is always in UTC.</p>
     */
    String getDate();

    /**
     * <p>The date and time the system responded to the request, using the preferred format of {@link <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">RFC 7231</a>} as shown in this example {@literal Thu, 16 Jun 2016 15:10:38 GMT}. The time is always in UTC.</p>
     */
    void setDate(String date);

    /**
     * <p>A unique transaction ID for this request. Your service provider might need this value if you report a problem.</p>
     */
    String getTransId();

    /**
     * <p>A unique transaction ID for this request. Your service provider might need this value if you report a problem.</p>
     */
    void setTransId(String transId);

    /**
     * <p>The date and time in {@link <a href="https://en.wikipedia.org/wiki/Unix_time">UNIX Epoch time stamp format</a>} when the account, container, or object was initially created as a current version.  For example, {@literal 1440619048} is equivalent to {@literal Mon, Wed, 26 Aug 2015 19:57:28 GMT}.</p>
     */
    public Long getTimestamp();

    /**
     * <p>The date and time in {@link <a href="https://en.wikipedia.org/wiki/Unix_time">UNIX Epoch time stamp format</a>} when the account, container, or object was initially created as a current version.  For example, {@literal 1440619048} is equivalent to {@literal Mon, Wed, 26 Aug 2015 19:57:28 GMT}.</p>
     */
    public void setTimestamp(Long timestamp);
}
