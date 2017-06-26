package io.webfolder.cormorant.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.regex.Pattern.compile;

import java.util.regex.Pattern;

import io.undertow.util.URLUtils;
import io.webfolder.cormorant.api.service.UrlDecoder;

public class DefaultUrlDecoder implements UrlDecoder {

    private static final char    FORWARD_SLASH = '/';

    private static final Pattern LEADING_SLASH = compile("^/+");

    @Override
    public String decode(String uri) {
        return URLUtils.decode(removeLeadingSlash(uri), UTF_8.name(), false, new StringBuilder());
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
