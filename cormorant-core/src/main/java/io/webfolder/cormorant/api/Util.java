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
package io.webfolder.cormorant.api;

import static java.lang.Long.toHexString;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.time.ZoneId.of;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Locale.ENGLISH;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.regex.Pattern.compile;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public interface Util {

    public static final char              FORWARD_SLASH  = '/';

    public static final Pattern           LEADING_SLASH  = compile("^/+");

    public static final ZoneId            GMT            = of("GMT");

    public static final DateTimeFormatter DATE_FORMATTER = ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'")
                                                             .withLocale(ENGLISH)
                                                             .withZone(GMT);

    public default String removeLeadingSlash(String path) {
        if (path == null) {
            return null;
        }
        String normalizedPath = path;
        if (normalizedPath.charAt(0) == FORWARD_SLASH) {
            normalizedPath = path.substring(1, path.length());
        }
        if (normalizedPath.charAt(0) == FORWARD_SLASH) {
            normalizedPath = LEADING_SLASH.matcher(normalizedPath).replaceAll("");
        }
        return normalizedPath;
    }

    public default String generateTxId() {
        final String part1 = leadingZeros(valueOf(toHexString(current().nextLong())), 21);
        final String part2 = leadingZeros(valueOf(toHexString(current().nextLong())), 10);
        return "tx" + part1 + "-" + (part2.length() > 10 ? part2.substring(0, 10) : part2);
    }

    public default String leadingZeros(final String str, final int length) {
        if (str.length() >= length) {
            return str;
        } else {
            return format("%0" + (length - str.length()) + "d%s", 0, str);
        }
    }
}
