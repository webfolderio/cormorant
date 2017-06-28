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
package io.webfolder.cormorant.api.service;

import java.io.IOException;
import java.util.NavigableSet;

import io.webfolder.cormorant.api.model.Account;
import io.webfolder.cormorant.api.model.Container;

public interface AccountService {

    NavigableSet<Container> listContainers(String accountName) throws IOException;

    Container getContainer(String accountName, String containerName) throws IOException;

    boolean containsContainer(String account, String container) throws IOException;

    Account getAccount(String accountName) throws IOException;
}
