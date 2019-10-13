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
package io.webfolder.cormorant.api.fs;

import static io.webfolder.cormorant.api.metadata.MetadataServiceFactory.MANIFEST_EXTENSION;
import static io.webfolder.cormorant.api.resource.ContentFormat.json;
import static io.webfolder.cormorant.api.resource.ContentFormat.plain;
import static io.webfolder.cormorant.api.resource.ContentFormat.xml;
import static java.lang.Boolean.TRUE;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import io.webfolder.cormorant.api.Util;
import io.webfolder.cormorant.api.resource.ContentFormat;
import io.webfolder.cormorant.api.resource.ResourceAdapter;
import io.webfolder.cormorant.api.service.MetadataService;
import io.webfolder.cormorant.api.service.ObjectService;

public class PathAdapter implements ResourceAdapter<Path>, Util {

    private static final char    CHAR_BACKSLASH = '\\';

    private static final char    CHAR_FORWARD_SLASH = '/';

    private static final int     DIR_SIZE = 0;

    private static final String  MD5_OF_EMPTY_STRING = "d41d8cd98f00b204e9800998ecf8427e";

    private static final String  NEW_LINE = "\r\n";

    private final Path container;

    private final ObjectService<Path> objectService;

    private final MetadataService systemMetadataService;

    public PathAdapter(
                    final Path                container,
                    final ObjectService<Path> objectService,
                    final MetadataService     systemMetadataService) {
        this.container             = container.toAbsolutePath().normalize();
        this.objectService         = objectService;
        this.systemMetadataService = systemMetadataService;
    }

    @Override
    public String convert(
                    final Path          path,
                    final ContentFormat contentFormat,
                    final Boolean       appendForwardSlash) throws IOException, SQLException {
        final StringBuilder builder        = new StringBuilder();
        final String        location       = container.relativize(path).toString();
        final boolean       isdir          = isDirectory(path, NOFOLLOW_LINKS);
        final String        mimeType       = objectService.getMimeType(container, path, false);
        final String        name           = location.replace(CHAR_BACKSLASH, CHAR_FORWARD_SLASH);
        final int           start          = name.lastIndexOf(MANIFEST_EXTENSION);
        final String        normalizedName =  start > 0 ? name.substring(0, start) : name;
        if (isdir) {
            final String namespace = objectService.getNamespace(container, path);
            final boolean deleted = "true".equals(systemMetadataService.get(namespace, "X-Cormorant-Deleted"));
            if (deleted) {
                return null;
            }
        }
        final long size = isdir ? DIR_SIZE : objectService.getSize(path);
        final String lastModified = getLastModifiedTime(path, NOFOLLOW_LINKS).toInstant().toString();
        final String hash = isdir ? MD5_OF_EMPTY_STRING : objectService.calculateChecksum(asList(path));
        if (json.equals(contentFormat)) {
            builder.append("{")
                   .append("\"name\":\"").append(isdir && TRUE.equals(appendForwardSlash) ? normalizedName + FORWARD_SLASH : normalizedName).append("\"").append(",")
                   .append("\"hash\":\"").append(hash).append("\"").append(",")
                   .append("\"content_type\":\"").append(mimeType).append("\",")
                   .append("\"bytes\":").append(size).append(",")
                   .append("\"last_modified\": \"").append(lastModified).append("\"")
                   .append("}");
        } else if (xml.equals(contentFormat)) {
            builder.append("<object>")
                   .append("<name>").append(isdir && TRUE.equals(appendForwardSlash) ? normalizedName + FORWARD_SLASH : normalizedName).append("</name>")
                   .append("<hash>").append(hash).append("</hash>")
                   .append("<content_type>").append(mimeType).append("</content_type>")
                   .append("<bytes>").append(size).append("</bytes>")
                   .append("<last_modified>").append(lastModified).append("</last_modified>")
                   .append("</object>");
        } else if (plain.equals(contentFormat)) {
            builder.append(isdir ? normalizedName + FORWARD_SLASH : normalizedName)
                   .append(NEW_LINE);
        }
        return builder.toString();
    }
}
