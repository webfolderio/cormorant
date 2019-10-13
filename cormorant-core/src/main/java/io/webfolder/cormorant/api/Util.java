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

    public static final String  MD5_OF_EMPTY_STRING      = "d41d8cd98f00b204e9800998ecf8427e";

    public default String removeLeadingSlash(String uri) {
        if (uri == null) {
            return null;
        }
        if (uri.isEmpty()) {
            return uri;
        }
        String normalizedPath = uri;
        if (normalizedPath.charAt(0) == FORWARD_SLASH) {
            normalizedPath = uri.substring(1, uri.length());
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
