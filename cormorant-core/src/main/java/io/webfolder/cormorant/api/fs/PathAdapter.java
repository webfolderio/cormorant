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

import static io.webfolder.cormorant.api.metadata.MetadataServiceFactory.MANIFEST_EXTENSION;
import static io.webfolder.cormorant.api.resource.ContentFormat.json;
import static io.webfolder.cormorant.api.resource.ContentFormat.plain;
import static io.webfolder.cormorant.api.resource.ContentFormat.xml;
import static java.lang.Boolean.TRUE;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.resource.ContentFormat;
import io.webfolder.cormorant.api.resource.ResourceAdapter;
import io.webfolder.cormorant.api.service.ChecksumService;
import io.webfolder.cormorant.api.service.MetadataService;
import io.webfolder.cormorant.api.service.ObjectService;

public class PathAdapter implements ResourceAdapter<Path> {

    private static final String BACKSLASH = "\\";

    private static final char   CHAR_BACKSLASH = BACKSLASH.charAt(0);

    private static final String FORWARD_SLASH = "/";

    private static final char   CHAR_FORWARD_SLASH = FORWARD_SLASH.charAt(0);

    private static final String NEW_LINE = "\r\n";

    private static final int    DIR_SIZE = 0;

    private static final String MD5_OF_EMPTY_STRING  = "d41d8cd98f00b204e9800998ecf8427e";

    private final Path container;

    private final ChecksumService<Path> checksumService;

    private final ObjectService<Path> objectService;

    private final MetadataService systemMetadataService;

    public PathAdapter(
                    final Path                  container,
                    final ChecksumService<Path> checksumService,
                    final ObjectService<Path>   objectService,
                    final MetadataService       systemMetadataService) {
        this.container        = container.toAbsolutePath().normalize();
        this.checksumService = checksumService;
        this.objectService   = objectService;
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
        long size = isdir ? DIR_SIZE : objectService.getSize(path);
        String lastModified = null;
        try {
            lastModified = getLastModifiedTime(path, NOFOLLOW_LINKS).toInstant().toString();
        } catch (IOException e) {
            throw new CormorantException(e);
        }
        final String hash = isdir ? MD5_OF_EMPTY_STRING : checksumService.calculateChecksum(path);
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
