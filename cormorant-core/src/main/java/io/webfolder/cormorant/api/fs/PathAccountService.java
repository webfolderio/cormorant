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

import static java.nio.file.Files.exists;
import static java.nio.file.Files.readAttributes;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.model.Account;
import io.webfolder.cormorant.api.model.Container;
import io.webfolder.cormorant.api.service.AccountService;

public abstract class PathAccountService implements AccountService {

    protected abstract Long getAccountTimestamp(String accountName);

    protected abstract List<String> getContainers(String accountName);

    protected abstract Path getContainerPath(String accountName, String containerName);

    private Map<String, Container> containers = new ConcurrentHashMap<>();

    @Override
    public NavigableSet<Container> listContainers(final String accountName) {
        final TreeSet<Container> containers = new TreeSet<>();
        for (final String next : getContainers(accountName)) {
            final Container container = get(accountName, next);
            containers.add(container);
        }
        return containers;
    }

    public Container getContainer(
                            final String accountName,
                            final String containerName) {
        if (containsContainer(accountName, containerName)) {
            final Container container = containers.get(containerName);
            if ( container != null ) {
                return container;
            }
        } else {
            if (containers.containsKey(containerName)) {
                containers.remove(containerName);
            }
        }
        Container container = get(accountName, containerName);
        if ( container != null ) {
            this.containers.put(container.getName(), container);
        }
        return container;
    }

    @Override
    public Account getAccount(final String accountName) {
        final SortedSet<Container> containers = listContainers(accountName);
        final Account account  = new Account();
        long  objectCounter    = 0L;
        long  bytesUsedCounter = 0L;
        for (Container next : containers) {
            final Path path = getContainerPath(accountName, next.getName());
            if (path == null) {
                continue;
            }
            objectCounter    += next.getObjectCount();
            bytesUsedCounter += next.getBytesUsed();
        }
        account.setName(accountName);
        account.setTotalObjectCount(objectCounter);
        account.setTotalBytesUsed(bytesUsedCounter);
        account.setTotalContainerCount(containers.size());
        account.setTimestamp(getAccountTimestamp(accountName));
        return account;
    }

    protected Long getContainerTimestamp(
                        final String accountName,
                        final String containerName) {
        final Path path = getContainerPath(accountName, containerName);
        try {
            final BasicFileAttributes attribute = (BasicFileAttributes) readAttributes(path,
                                                                                BasicFileAttributes.class,
                                                                                NOFOLLOW_LINKS);
            return attribute.creationTime().toMillis();
        } catch (IOException e) {
            throw new CormorantException(e);
        }
    }

    protected Long getContainerLastModified(
                        final String accountName,
                        final String containerName) {
        final Path path = getContainerPath(accountName, containerName);
        if (path == null) {
            return null;
        }
        try {
            final BasicFileAttributes attribute = (BasicFileAttributes) readAttributes(path,
                                                                                BasicFileAttributes.class,
                                                                                NOFOLLOW_LINKS);
            return attribute.lastModifiedTime().toMillis();
        } catch (IOException e) {
            throw new CormorantException(e);
        }
    }

    protected Container get(final String accountName, final String containerName) {
        final Path path = getContainerPath(accountName, containerName);
        if ( ! exists(path, NOFOLLOW_LINKS) ) {
            return null;
        }
        final Long            timestamp    = getContainerTimestamp(accountName, containerName);
        final Long            lastModified = getContainerLastModified(accountName, containerName);
        final FileSizeVisitor counter      = new FileSizeVisitor();
        try {
            walkFileTree(path, counter);
        } catch (IOException e) {
            throw new CormorantException(e);
        }
        final Container container = new Container(containerName,
                                            timestamp,
                                            lastModified,
                                            counter.getObjectCount(),
                                            counter.getBytesUsed());
        return container;
    }
}
