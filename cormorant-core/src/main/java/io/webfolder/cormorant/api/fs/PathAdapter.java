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
package io.webfolder.cormorant.api.fs;

import static java.lang.Boolean.*;
import static io.webfolder.cormorant.api.property.MetadataServiceFactory.MANIFEST_EXTENSION;
import static io.webfolder.cormorant.api.resource.ContentFormat.json;
import static io.webfolder.cormorant.api.resource.ContentFormat.plain;
import static io.webfolder.cormorant.api.resource.ContentFormat.xml;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.size;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.io.IOException;
import java.nio.file.Path;

import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.resource.ContentFormat;
import io.webfolder.cormorant.api.resource.ResourceAdapter;
import io.webfolder.cormorant.api.service.ChecksumService;

public class PathAdapter implements ResourceAdapter<Path> {

    private static final String BACKSLASH = "\\";

    private static final char   CHAR_BACKSLASH = BACKSLASH.charAt(0);

    private static final String FORWARD_SLASH = "/";

    private static final char   CHAR_FORWARD_SLASH = FORWARD_SLASH.charAt(0);

    private static final String NEW_LINE = "\r\n";

    private static final int    DIR_SIZE = 0;

    private static final String MD5_OF_EMPTY_STRING  = "d41d8cd98f00b204e9800998ecf8427e";

    private final Path root;

    private final ChecksumService<Path> checksumService;

    public PathAdapter(
                    final Path                   root,
                    final ChecksumService<Path>  checksumService) {
        this.root            = root.toAbsolutePath().normalize();
        this.checksumService = checksumService;
    }

    @Override
    public String convert(
                    final Path          path,
                    final ContentFormat contentFormat,
                    final Boolean       appendForwardSlash) {
        final StringBuilder builder        = new StringBuilder();
        final String        location       = root.relativize(path).toString();
        final boolean       isdir          = isDirectory(path, NOFOLLOW_LINKS);
        final String        mimeType       = checksumService.getMimeType(root, path, false);
        final String        name           = location.replace(CHAR_BACKSLASH, CHAR_FORWARD_SLASH);
        final int           start          = name.lastIndexOf(MANIFEST_EXTENSION);
        final String        normalizedName =  start > 0 ? name.substring(0, start) : name;
        Long size;
        try {
            size = isdir ? DIR_SIZE : size(path);
        } catch (IOException e) {
            throw new CormorantException(e);
        }
        String lastModified = null;
        try {
            lastModified = getLastModifiedTime(path, NOFOLLOW_LINKS).toInstant().toString();
        } catch (IOException e) {
            throw new CormorantException(e);
        }
        final String hash = isdir ? MD5_OF_EMPTY_STRING : calculateChecksum(path, lastModified, isdir);
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

    protected String calculateChecksum(
                            final Path    path        ,
                            final String  lastModified,
                            final boolean isDir       ) {
        if (isDir) {
            return checksumService.calculateChecksum(lastModified);
        } else {
            return checksumService.calculateChecksum(root, path);
        }
    }
}
