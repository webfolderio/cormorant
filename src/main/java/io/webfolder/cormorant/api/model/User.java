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
package io.webfolder.cormorant.api.model;

public class User {

    private final String username;

    private final String password;

    private final String email;

    private final String projectId;

    private final boolean enable;

    public User(final String username,
                final String password,
                final String email,
                final String projectId,
                final boolean enable) {
        this.username  = username;
        this.password  = password;
        this.email     = email;
        this.projectId = projectId;
        this.enable    = enable;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getProjectId() {
        return projectId;
    }

    public boolean isEnable() {
        return enable;
    }

    @Override
    public String toString() {
        return "User [username=" + username + ", password=" + password + ", email=" + email + ", projectId=" + projectId
                + ", enable=" + enable + "]";
    }
}
