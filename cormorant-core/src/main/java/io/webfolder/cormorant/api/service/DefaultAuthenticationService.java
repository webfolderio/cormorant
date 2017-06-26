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

import static io.webfolder.cormorant.api.model.Permission.ADMIN;
import static io.webfolder.cormorant.api.model.Role.Admin;
import static io.webfolder.cormorant.api.model.Role.None;
import static io.webfolder.cormorant.api.model.Role.valueOf;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.webfolder.cormorant.api.model.Domain;
import io.webfolder.cormorant.api.model.Project;
import io.webfolder.cormorant.api.model.Role;
import io.webfolder.cormorant.api.model.User;

public class DefaultAuthenticationService implements AuthenticationService {

    private final Map<String, User>    users    = new ConcurrentHashMap<>();

    private final Map<String, Project> projects = new ConcurrentHashMap<>();

    private final Domain domain;

    public DefaultAuthenticationService(Map<String, User> users) {
        this(users, new Domain("default", "default"));
    }

    public DefaultAuthenticationService(Map<String, User> users, Domain domain) {
        this.domain = domain;
        this.users.putAll(users);
    }

    @Override
    public boolean authenticate(String username, String password) {
        return users.containsKey(username)                             &&
                    password != null                                   &&
                    password.equals(users.get(username).getPassword()) &&
                    users.get(username).isEnable();
    }

    @Override
    public boolean hasPermission(String username, String permission, String method) {
        final User user = users.get(username);
        if ( user == null ) {
            return false;
        }
        if (None.equals(user.getRole())) {
            return false;
        }
        if ( ADMIN.equals(permission) && ! Admin.equals(user.getRole()) ) {
            return false;
        }
        return true;
    }

    @Override
    public String createProject(Project project) {
        if ( ! projects.containsKey(project.getName()) ) {
            projects.put(project.getName(), project);
            return project.getName();
        }
        return null;
    }

    @Override
    public String createUser(User user) {
        if ( ! users.containsKey(user.getUsername()) ) {
            users.put(user.getUsername(), user);
            return user.getUsername();
        }
        return null;
    }

    @Override
    public Domain getDomain() {
        return domain;
    }

    @Override
    public boolean deleteUser(String userId) {
        return users.remove(userId) != null ? true : false;
    }

    @Override
    public boolean containsUser(String username) {
        return users.containsKey(username);
    }

    @Override
    public boolean containsProject(String projectId) {
        return projects.containsKey(projectId);
    }

    @Override
    public boolean deleteProject(String projectId) {
        return projects.remove(projectId) != null ? true : false;
    }

    @Override
    public Role getRole(String username) {
        final User user = users.get(username);
        if (user == null) {
            return None;
        }
        return user.getRole();
    }

    @Override
    public void assignRole(String userId, String role) {
        final User user = users.get(userId);
        if (user == null) {
            return;
        }
        final Role newRole = valueOf(role);
        final User newUser = new User(user.getUsername(),
                                    user.getPassword(),
                                    user.getEmail(),
                                    user.getProjectId(),
                                    newRole,
                                    true);
        users.put(userId, newUser);
    }

    protected Map<String, Project> getProjects() {
        return Collections.unmodifiableMap(projects);
    }

    protected Map<String, User> getUsers() {
        return Collections.unmodifiableMap(users);
    }
}
