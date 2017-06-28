package io.webfolder.cormorant.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.regex.Pattern.compile;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.service.UrlDecoder;

public class DefaultUrlDecoder implements UrlDecoder {

    private static final char    FORWARD_SLASH = '/';

    private static final Pattern LEADING_SLASH = compile("^/+");

    @Override
    public String decode(String uri) {
        return decode(removeLeadingSlash(uri), UTF_8.name(), false, new StringBuilder());
    }

    /*
     * @see io.undertow.util.URLUtils.decode()
     * 
     * JBoss, Home of Professional Open Source.
     * Copyright 2014 Red Hat, Inc., and individual contributors
     * as indicated by the @author tags.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *     http://www.apache.org/licenses/LICENSE-2.0
     *
     *  Unless required by applicable law or agreed to in writing, software
     *  distributed under the License is distributed on an "AS IS" BASIS,
     *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     *  See the License for the specific language governing permissions and
     *  limitations under the License.
     */
    public String decode(String s, String enc, boolean decodeSlash, StringBuilder buffer) {
        buffer.setLength(0);
        boolean needToChange = false;
        int numChars = s.length();
        int i = 0;

        char c;
        byte[] bytes = null;
        while (i < numChars) {
            c = s.charAt(i);
            switch (c) {
                case '+':
                    buffer.append(' ');
                    i++;
                    needToChange = true;
                    break;
                case '%':
                /*
                 * Starting with this instance of %, process all
                 * consecutive substrings of the form %xy. Each
                 * substring %xy will yield a byte. Convert all
                 * consecutive  bytes obtained this way to whatever
                 * character(s) they represent in the provided
                 * encoding.
                 *
                 * Note that we need to decode the whole rest of the value, we can't just decode
                 * three characters. For multi code point characters there if the code point can be
                 * represented as an alphanumeric
                 */
                    try {
                        // (numChars-i) is an upper bound for the number
                        // of remaining bytes
                        if (bytes == null) {
                            bytes = new byte[numChars - i + 1];
                        }
                        int pos = 0;

                        while ((i< numChars)) {
                            if (c == '%') {
                                char p1 = Character.toLowerCase(s.charAt(i + 1));
                                char p2 = Character.toLowerCase(s.charAt(i + 2));
                                if (!decodeSlash && ((p1 == '2' && p2 == 'f') || (p1 == '5' && p2 == 'c'))) {
                                    bytes[pos++] = (byte) c;
                                    // should be copied with preserved upper/lower case
                                    bytes[pos++] = (byte) s.charAt(i + 1);
                                    bytes[pos++] = (byte) s.charAt(i + 2);
                                    i += 3;

                                    if (i < numChars) {
                                        c = s.charAt(i);
                                    }
                                    continue;
                                }
                                int v = 0;
                                if (p1 >= '0' && p1 <= '9') {
                                    v = (p1 - '0') << 4;
                                } else if (p1 >= 'a' && p1 <= 'f') {
                                    v = (p1 - 'a' + 10) << 4;
                                } else {
                                    throw new CormorantException("Failed to decode [" + s + "]");
                                }
                                if (p2 >= '0' && p2 <= '9') {
                                    v += (p2 - '0');
                                } else if (p2 >= 'a' && p2 <= 'f') {
                                    v += (p2 - 'a' + 10);
                                } else {
                                    throw new CormorantException("Failed to decode [" + s + "]");
                                }
                                if (v < 0) {
                                    throw new CormorantException("Failed to decode [" + s + "]");
                                }

                                bytes[pos++] = (byte) v;
                                i += 3;
                                if (i < numChars) {
                                    c = s.charAt(i);
                                }
                            }else if(c == '+') {
                                bytes[pos++] = (byte) ' ';
                                ++i;
                                if (i < numChars) {
                                    c = s.charAt(i);
                                }
                            } else {
                                bytes[pos++] = (byte) c;
                                ++i;
                                if (i < numChars) {
                                    c = s.charAt(i);
                                }
                            }
                        }

                        String decoded = new String(bytes, 0, pos, enc);
                        buffer.append(decoded);
                    } catch (NumberFormatException e) {
                        throw new CormorantException("Failed to decode [" + s + "]", e);
                    } catch (UnsupportedEncodingException e) {
                        throw new CormorantException("Failed to decode [" + s + "]", e);
                    }
                    needToChange = true;
                    break;
                default:
                    buffer.append(c);
                    i++;
                    if(c > 127 && !needToChange) {
                        //we have non-ascii data in our URL, which sucks
                        //its hard to know exactly what to do with this, but we assume that because this data
                        //has not been properly encoded none of the other data is either
                        try {
                            char[] carray = s.toCharArray();
                            byte[] buf = new byte[carray.length];
                            for(int l = 0;l < buf.length; ++l) {
                                buf[l] = (byte) carray[l];
                            }
                            return new String(buf, enc);
                        } catch (UnsupportedEncodingException e) {
                            throw new CormorantException("Failed to decode [" + s + "]", e);
                        }
                    }
                    break;
            }
        }

        return (needToChange ? buffer.toString() : s);
    }

    protected String removeLeadingSlash(String path) {
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
}
