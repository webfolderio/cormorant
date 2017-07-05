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

import io.webfolder.cormorant.api.model.Domain;
import io.webfolder.cormorant.api.model.Project;
import io.webfolder.cormorant.api.model.Role;
import io.webfolder.cormorant.api.model.User;

public interface KeystoneService {

    boolean authenticate(String username, String password);

    boolean hasPermission(String username, String permission, String method);

    String createProject(Project project);

    String createUser(User user);

    boolean deleteUser(String userId);

    boolean containsUser(String username);

    boolean containsProject(String projectId);

    boolean deleteProject(String projectId);

    Role getRole(String username);

    void assignRole(String userId, String role);

    Domain getDomain();
}
