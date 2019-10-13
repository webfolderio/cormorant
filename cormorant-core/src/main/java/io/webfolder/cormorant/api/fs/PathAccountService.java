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

import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.list;
import static java.nio.file.Files.readAttributes;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.model.Account;
import io.webfolder.cormorant.api.model.Container;
import io.webfolder.cormorant.api.service.AccountService;

public class PathAccountService implements AccountService {

    private Map<String, Container> containers = new ConcurrentHashMap<>();

    private Path root;

    public PathAccountService(Path root) {
        this.root = root;
    }
    
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

    protected Path getContainerPath(String accountName, String containerName) {
        return root.resolve(containerName);
    }

    protected Long getAccountTimestamp(String accountName) {
        BasicFileAttributeView view = Files.getFileAttributeView(root, BasicFileAttributeView.class);
        BasicFileAttributes attributes;
        try {
            attributes = view.readAttributes();
        } catch (IOException e) {
            throw new CormorantException(e);
        }
        return Long.valueOf(attributes.creationTime().toMillis());
    }

    @Override
    public NavigableSet<Container> listContainers(final String accountName) throws IOException {
        final TreeSet<Container> containers = new TreeSet<>();
        for (final String next : getContainers(accountName)) {
            final Container container = get(accountName, next);
            containers.add(container);
        }
        return containers;
    }

    public Container getContainer(
                            final String accountName,
                            final String containerName) throws IOException {
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
    public Account getAccount(final String accountName) throws IOException {
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

    @Override
    public boolean containsContainer(String account, String container) {
        return getContainers(account).contains(container);
    }

    protected Long getContainerTimestamp(
                        final String accountName,
                        final String containerName) throws IOException {
        final Path path = getContainerPath(accountName, containerName);
        final BasicFileAttributes attribute = (BasicFileAttributes) readAttributes(path,
                                                                            BasicFileAttributes.class,
                                                                            NOFOLLOW_LINKS);
        return attribute.creationTime().toMillis();
    }

    protected Long getContainerLastModified(
                        final String accountName,
                        final String containerName) throws IOException {
        final Path path = getContainerPath(accountName, containerName);
        if (path == null) {
            return null;
        }
        final BasicFileAttributes attribute = (BasicFileAttributes) readAttributes(path,
                                                                            BasicFileAttributes.class,
                                                                            NOFOLLOW_LINKS);
        return attribute.lastModifiedTime().toMillis();
    }

    protected Container get(final String accountName, final String containerName) throws IOException {
        final Path path = getContainerPath(accountName, containerName);
        if ( ! exists(path, NOFOLLOW_LINKS) ) {
            return null;
        }
        final Long            timestamp    = getContainerTimestamp(accountName, containerName);
        final Long            lastModified = getContainerLastModified(accountName, containerName);
        final FileSizeVisitor counter      = new FileSizeVisitor();
        walkFileTree(path, counter);
        final Container container = new Container(containerName,
                                            timestamp,
                                            lastModified,
                                            counter.getObjectCount(),
                                            counter.getBytesUsed());
        return container;
    }
}
