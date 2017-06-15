/**
 * cormorant - Object Storage Server
 * Copyright © 2017 WebFolder OÜ (cormorant@webfolder.io)
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.webfolder.cormorant.api.model.Domain;
import io.webfolder.cormorant.api.model.Project;
import io.webfolder.cormorant.api.model.User;
import io.webfolder.cormorant.api.service.AuthenticationService;

public class TestAuthenticationService implements AuthenticationService {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    private final Map<String, Project> projects = new ConcurrentHashMap<>();

    public TestAuthenticationService() {
        User user = new User("myaccount", "mypassword", "", "", true);
        users.put("myaccount", user);
    }

    @Override
    public boolean authenticate(String username, String password) {
        return users.containsKey(username) &&
                        password != null   &&
                        password.equals(users.get(username).getPassword());
    }

    @Override
    public boolean isUserInRole(String username, String role) {
        return true;
    }

    @Override
    public String createProject(Project project) {
        projects.put(project.getName(), project);
        return project.getName();
    }

    @Override
    public String createUser(User user) {
        users.put(user.getUsername(), user);
        return user.getUsername();
    }

    @Override
    public Domain getDomain() {
        Domain domain = new Domain("default", "default");
        return domain;
    }

    @Override
    public void deleteUser(String username) {
        users.remove(username);
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
    public void deleteProject(String projectId) {
        projects.remove(projectId);
    }

    @Override
    public String getRole(String username) {
        return "admin";
    }
}
