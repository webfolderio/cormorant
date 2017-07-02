package io.webfolder.cormorant.api;

import static java.util.regex.Pattern.compile;

import java.util.regex.Pattern;

public interface Util {

    public static final char    FORWARD_SLASH = '/'           ;

    public static final Pattern LEADING_SLASH = compile("^/+");

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
}
