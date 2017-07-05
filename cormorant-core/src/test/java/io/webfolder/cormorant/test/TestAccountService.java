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
package io.webfolder.cormorant.test;

import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.list;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.fs.PathAccountService;

public class TestAccountService extends PathAccountService {

    private static final Long INIT = System.currentTimeMillis();

    private final Path root;

    public TestAccountService(final Path root) {
        this.root = root;
    }

    @Override
    protected List<String> getContainers(String accountName) {
        Stream<Path> list = null;
        try {
            list = list(root);
            return list
                    .filter(p -> isDirectory(p))
                    .map(p -> p.getFileName().toString())
                    .collect(toList());
        } catch (IOException e) {
            throw new CormorantException(e);
        } finally {
            if ( list != null ) {
                list.close();
            }
        }
    }

    @Override
    protected Long getAccountTimestamp(String accountName) {
        return INIT;
    }

    @Override
    protected Path getContainerPath(String accountName, String containerName) {
        return this.root.resolve(containerName);
    }
}
