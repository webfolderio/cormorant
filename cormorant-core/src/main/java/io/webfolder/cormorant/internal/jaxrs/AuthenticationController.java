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
package io.webfolder.cormorant.internal.jaxrs;

import static java.lang.Long.toHexString;
import static java.lang.String.valueOf;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.concurrent.ThreadLocalRandom.current;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import io.webfolder.cormorant.api.Json;
import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.model.Domain;
import io.webfolder.cormorant.api.model.Project;
import io.webfolder.cormorant.api.model.Role;
import io.webfolder.cormorant.api.model.User;
import io.webfolder.cormorant.api.service.KeystoneService;

@Path("/")
@RolesAllowed({ "cormorant-admin" })
@DeclareRoles({ "cormorant-admin" })
public class AuthenticationController {

    private static final String HEADER_AUTH_TOKEN_PREFIX = "AUTH_";

    private static final String HEADER_SUBJECT_TOKEN     = "X-Subject-Token";

    private static final String X_AUTH_TOKEN             = "X-Auth-Token";

    private static final String X_STORAGE_URL            = "X-Storage-Url";

    private final String infoV2;

    private final String authTemplateV2;

    private final String authTemplateV3;

    private final String domainsTemplateV3;

    private final String projectsTemplateV3;

    private final String userTemplateV3;

    private final String rolesTemplateV3;

    private final Map<String, Principal> tokens;

    private final KeystoneService keystoneService;

    private final String accountName;

    @Context
    private UriInfo uriInfo;

    public AuthenticationController(
                final Map<String, Principal> tokens,
                final KeystoneService        keysonteService,
                final String                 accountName) {
        this.tokens             = tokens;
        this.keystoneService    = keysonteService;
        this.authTemplateV2     = loadResource("/io/webfolder/cormorant/auth-v2.json");
        this.authTemplateV3     = loadResource("/io/webfolder/cormorant/auth-v3.json");
        this.domainsTemplateV3  = loadResource("/io/webfolder/cormorant/domains-v3.json");
        this.projectsTemplateV3 = loadResource("/io/webfolder/cormorant/projects-v3.json");
        this.userTemplateV3     = loadResource("/io/webfolder/cormorant/user-v3.json");
        this.rolesTemplateV3    = loadResource("/io/webfolder/cormorant/roles-v3.json");
        this.infoV2             = loadResource("/io/webfolder/cormorant/info-v2.json");
        this.accountName        = accountName;
    }

    @GET
    @PermitAll
    @Path("/auth/v1.0")
    public Response tokensV1(
                final @HeaderParam("X-Auth-User") String username,
                final @HeaderParam("X-Auth-Key")  String password) {

        if ( ! keystoneService.authenticate(username, password) ) {
            return status(BAD_REQUEST)
                            .entity("Incorrect username or password.")
                            .build();
        }

        final String token = HEADER_AUTH_TOKEN_PREFIX + valueOf(toHexString(new SecureRandom().nextLong()));

        Instant expires = now().plus(1, DAYS);

        CormorantPrincipal principal = new CormorantPrincipal(username, token, expires);
        tokens.put(token, principal);

        final String publicUrl = uriInfo.getBaseUri().toString() + "v1/" + accountName;

        return ok()
                .header(X_AUTH_TOKEN, token)
                .header(X_STORAGE_URL, publicUrl)
                .build();
    }

    @GET
    @Path("/v2.0")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response infoV2() {
        String content = infoV2;
        content = content.replace("__PROTOCOL__", uriInfo.getBaseUri().getScheme());
        content = content.replace("__HOST__", uriInfo.getBaseUri().getHost());
        content = content.replace("__PORT__", valueOf(uriInfo.getBaseUri().getPort()));
        return ok()
                .entity(content)
                .build();
    }

    @POST
    @PermitAll
    @Path("/v2.0/tokens")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response tokensV2(final String content) {
        final Json   input       = Json.read(content);
        final Json   auth        = input.at("auth");
        final Json   credentials = auth.at("passwordCredentials");        
        final String username    = credentials.at("username").asString();
        final String password    = credentials.at("password").asString();

        if ( ! keystoneService.authenticate(username, password) ) {
            return status(BAD_REQUEST)
                            .entity("Incorrect username or password.")
                            .build();
        }

        final String token = HEADER_AUTH_TOKEN_PREFIX + valueOf(toHexString(new SecureRandom().nextLong()));

        Instant expires = now().plus(1, DAYS);

        CormorantPrincipal principal = new CormorantPrincipal(username, token, expires);
        tokens.put(token, principal);

        final ResponseBuilder builder = ok();
        String response = authTemplateV2;

        final String publicUrl = uriInfo.getBaseUri().toString() + "v1/" + accountName;

        response = response.replace("__TOKEN__"      , token);
        response = response.replace("__TENANT_ID__"  , "default");
        response = response.replace("__TENANT_NAME__", "default");
        response = response.replace("__USER_NAME__"  , username);
        response = response.replace("__USER_ID__"    , username);
        response = response.replace("__ROLE_ID__"    , keystoneService.getRole(username).toString());
        response = response.replace("__ROLE_NAME__"  , keystoneService.getRole(username).toString());
        response = response.replace("__EXPIRES__"    , expires.toString());
        response = response.replace("__PUBLIC_URL__" , publicUrl);

        return builder
                    .type(APPLICATION_JSON)
                    .header(CONTENT_LENGTH, response.length())
                    .entity(response)
                .build();
    }

    @DELETE
    @Path("/v2.0/tokens")
    public Response revokeTokensV2(@HeaderParam("AUTH_TOKEN") final String authToken) {
        return status(tokens.remove(authToken) != null ?
                        NO_CONTENT : BAD_REQUEST).build();
    }

    @HEAD
    @Path("/v2.0/tokens")
    public Response checkTokensV2() {
        return status(NO_CONTENT).build();
    }

    @GET
    @Path("/v3/domains")
    @Produces(APPLICATION_JSON)
    public Response listDomains() {
        String response = domainsTemplateV3;

        final Domain domain = keystoneService.getDomain();

        response = response.replace("__ID__", domain.getId());
        response = response.replace("__NAME__", domain.getName());

        return ok().entity(domainsTemplateV3).build();
    }

    @POST
    @Path("/v3/projects")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response createProject(final String content) {

        Json json = Json.read(content);
        Map<String, Object> request = json.asMap();

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) request.get("project");
        String name = (String) map.get("name");
        String description = (String) map.get("description");
        String domainId = (String) map.get("domain_id");

        Project project = new Project(name, description, domainId);

        String projectId = keystoneService.createProject(project);

        String response = projectsTemplateV3;
        response = response.replace("__NAME__", project.getName());
        response = response.replace("__ID__", projectId);
        response = response.replace("__DESCRIPTION__", project.getDescription());
        response = response.replace("__DOMAIN_ID__", project.getDomainId());

        return status(CREATED).entity(response).build();
    }

    @POST
    @Path("/v3/users")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response createUser(final String content) {
        Json json = Json.read(content);
        Map<String, Object> request = json.asMap();

        @SuppressWarnings("unchecked")
        final Map<String, Object> map       = (Map<String, Object>) request.get("user");
        final String              password  = (String) map.get("password");
        final String              projectId = (String) map.get("project_id");
        final String              username  = (String) map.get("name");
        final String              email     = (String) map.get("email");

        final User   user   = new User(username, password, email, projectId, Role.None, true);
        final String id     = keystoneService.createUser(user);
        final Domain domain = keystoneService.getDomain();

        String response = userTemplateV3;
        response = response.replace("__NAME__", user.getUsername());
        response = response.replace("__ID__", id);
        response = response.replace("__ENABLE__", valueOf(user.isEnable()));
        response = response.replace("__DOMAIN_ID__", domain.getId());

        return status(CREATED).entity(response).build();
    }

    @POST
    @PermitAll
    @Path("/v3/auth/tokens")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response tokensV3(final String content) {
        final Json input;
        try {
            input = Json.read(content);
        } catch (Throwable t) {
            throw new CormorantException("Failed to parse malformed json request.", t);
        }
        final Json auth = input.at("auth");
        if ( auth == null || ! auth.isObject() ) {
            throw new CormorantException("Failed to authenticate the request. Missing or invalid json object [auth].");
        }
        final Json identity = auth.at("identity");
        if ( identity == null || ! identity.isObject() ) {
            throw new CormorantException("Failed to authenticate the request. Missing or invalid json object [auth.identity].");
        }
        final Json methods = identity.at("methods");
        if ( methods == null || ! methods.isArray() ) {
            throw new CormorantException("Failed to authenticate the request. Missing or invalid json array [auth.methods].");
        }
        final List<Object> list = methods.asList();
        if ( list == null || list.isEmpty() || ! list.contains("password") ) {
            throw new CormorantException("Failed to authenticate the request. Invalid authentication method." +
                            " For password authentication, specify password.");
        }
        final Json password = identity.at("password");
        if ( password == null || ! password.isObject() ) {
            throw new CormorantException("Failed to authenticate the request. Missing or invalid json object [auth.identity.password].");
        }
        final Json user = password.at("user");
        if ( user == null || ! user.isObject() ) {
            throw new CormorantException("Failed to authenticate the request. Missing or invalid json object [auth.identity.password.user].");
        }
        final Json passwd = user.at("password");
        if ( passwd == null || ! passwd.isString() ) {
            throw new CormorantException("Failed to authenticate the request. Missing or invalid json value [auth.identity.password.user.password].");
        }

        final Json username = user.at("name");
        final Json userId   = user.at("id");
        final String authUsername = username != null ? username.asString() : userId != null ? userId.asString() : null;
        final String authPassword = passwd.asString();

        if ( authUsername == null || authUsername.trim().isEmpty() ) {
            throw new CormorantException("Failed to authenticate the request. Missing user name or user id.");
        }

        if ( ! keystoneService.authenticate(authUsername, authPassword) ) {
            return status(BAD_REQUEST)
                        .entity("Incorrect username or password.")
                        .build();
        }

        final String token = HEADER_AUTH_TOKEN_PREFIX + valueOf(toHexString(new SecureRandom().nextLong()));

        Instant expires = now().plus(1, DAYS);

        final String auditId = valueOf(toHexString(current().nextLong()));

        CormorantPrincipal principal = new CormorantPrincipal(authUsername, token, expires, auditId);
        tokens.put(token, principal);

        final ResponseBuilder builder = status(CREATED);
        String response = authTemplateV3;

        response = response.replace("__KEYSTONE_URL__"     , uriInfo.getBaseUri().toString());
        response = response.replace("__OBJECT_STORE_URL__" , uriInfo.getBaseUri().toString() + "v1/" + accountName);
        response = response.replace("__EXPIRES_AT__"       , expires.toString());
        response = response.replace("__USER_ID__"          , authUsername);
        response = response.replace("__USER__"             , authUsername);
        response = response.replace("__ROLE__"             , keystoneService.getRole(authUsername).toString());
        response = response.replace("__ROLE_ID__"          , keystoneService.getRole(authUsername).toString());
        response = response.replace("__AUDIT_ID__"         , auditId);
        response = response.replace("__ISSUED_AT__"        , now().toString());

        return builder
                    .type(APPLICATION_JSON)
                    .header(HEADER_SUBJECT_TOKEN, token)
                    .header(CONTENT_LENGTH, response.length())
                    .entity(response)
                .build();
    }

    @DELETE
    @Path("/v3/auth/tokens")
    public Response revokeTokensV3(@HeaderParam("AUTH_TOKEN") final String authToken) {
        return revokeTokensV2(authToken);
    }

    @HEAD
    @Path("/v3/auth/tokens")
    public Response checkTokensV3() {
        return status(NO_CONTENT).build();
    }

    @GET
    @Path("/v3/roles")
    @Produces(APPLICATION_JSON)
    public Response listRoles() {
        String roles = rolesTemplateV3;
        return ok().entity(roles).build();
    }

    @PUT
    @Path("/v3/projects/{projectId}/users/{userId}/roles/{roleId}")
    public Response assignRoleToUser(
                                    @PathParam("projectId") final String projectId,
                                    @PathParam("userId")    final String userId,
                                    @PathParam("roleId")    final String roleId) {
        keystoneService.assignRole(userId, roleId);
        return status(NO_CONTENT).build();
    }

    @DELETE
    @Path("/v3/users/{userId}")
    public Response deleteUser(@PathParam("userId")  final String userId) {
        if ( ! keystoneService.containsUser(userId) ) {
            return status(NOT_FOUND).entity("User [" + userId + "] not found").build();
        }
        boolean deleted = keystoneService.deleteUser(userId);
        return status(deleted ? NO_CONTENT : BAD_REQUEST).build();
    }

    @DELETE
    @Path("/v3/projects/{projectId}")
    public Response deleteProject(@PathParam("projectId") final String projectId) {
        if ( ! keystoneService.containsProject(projectId) ) {
            return status(NOT_FOUND).entity("Project [" + projectId + "] not found").build();   
        }
        boolean deleted = keystoneService.deleteProject(projectId);
        return status(deleted ? NO_CONTENT : BAD_REQUEST).build();
    }

    protected String loadResource(final String name) {
        final StringBuilder builder = new StringBuilder();
        try (InputStream is = getClass().getResourceAsStream(name)) {
            try (Scanner scanner = new Scanner(is)) {
                while (scanner.hasNext()) {
                    String next = scanner.next();
                    builder.append(next);
                }
            }
            return builder.toString();
        } catch (IOException e) {
            throw new CormorantException(e);
        }
    }
}
