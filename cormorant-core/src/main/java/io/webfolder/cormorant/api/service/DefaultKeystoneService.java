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
package io.webfolder.cormorant.api.service;

import static io.webfolder.cormorant.api.model.Role.Admin;
import static io.webfolder.cormorant.api.model.Role.None;
import static io.webfolder.cormorant.api.model.Role.valueOf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;
import at.favre.lib.crypto.bcrypt.BCrypt.Verifyer;
import io.webfolder.cormorant.api.model.Domain;
import io.webfolder.cormorant.api.model.Project;
import io.webfolder.cormorant.api.model.Role;
import io.webfolder.cormorant.api.model.User;

public class DefaultKeystoneService implements KeystoneService {

    private final Map<String, User>    users    = new ConcurrentHashMap<>();

    private final Map<String, Project> projects = new ConcurrentHashMap<>();

    private final Verifyer verifyer = BCrypt.verifyer();

    private final Domain domain;

    public DefaultKeystoneService(Map<String, User> users) {
        this(users, new Domain("default", "default"));
    }

    public DefaultKeystoneService(Map<String, User> users, Domain domain) {
        this.domain = domain;
        this.users.putAll(users);
    }

    @Override
    public boolean authenticate(String username, String password) {
        if ( ! users.containsKey(username) ) {
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        if ( ! users.get(username).isEnable() ) {
            return false;
        }
        try {
            String realPassword = users.get(username).getPassword();
            Result result = verifyer.verify(password.toCharArray(), realPassword.toCharArray());
            return result.verified;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean hasPermission(String username, String permission, String method) {
        final User user = users.get(username);
        if (user == null) {
            return false;
        }
        if (None.equals(user.getRole())) {
            return false;
        }
        if ( "cormorant-admin".equals(permission) && ! Admin.equals(user.getRole()) ) {
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
}
