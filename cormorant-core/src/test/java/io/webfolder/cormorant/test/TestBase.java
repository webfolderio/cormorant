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
package io.webfolder.cormorant.test;

import static java.util.Locale.ENGLISH;
import static org.jclouds.ContextBuilder.newBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.model.Account;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.blobstore.RegionScopedBlobStoreContext;
import org.jclouds.openstack.swift.v1.features.AccountApi;
import org.jclouds.openstack.swift.v1.features.ContainerApi;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mindrot.jbcrypt.BCrypt;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.ConsoleWriter;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import io.undertow.Handlers;
import io.webfolder.cormorant.api.CormorantApplication;
import io.webfolder.cormorant.api.CormorantConfiguration;
import io.webfolder.cormorant.api.CormorantServer;
import io.webfolder.cormorant.api.exception.CormorantException;
import io.webfolder.cormorant.api.fs.PathAccountService;
import io.webfolder.cormorant.api.metadata.MetadataStorage;
import io.webfolder.cormorant.api.model.Role;
import io.webfolder.cormorant.api.model.User;
import io.webfolder.cormorant.api.service.AccountService;
import io.webfolder.cormorant.api.service.DefaultKeystoneService;
import io.webfolder.cormorant.api.service.KeystoneService;
import io.webfolder.otmpfile.SecureTempFile;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

public class TestBase {

    protected static SwiftApi swiftApi;

    protected static AccountApi accountApi;

    protected static ContainerApi containerApi;

    protected static Path objectStore;

    protected static Path metadataStore;

    protected static String region;

    protected static CormorantServer server;

    protected static BlobStore blobStore;

    protected static AccountConfig jossConfig;

    protected static Account jossAccount;

    protected static OkHttpClient client;

    protected static String contextPath = "/cormorant";

    @BeforeClass
    public static void start() {
        System.out.println("Supports O_TMPFILE: " + SecureTempFile.SUPPORT_O_TMPFILE);

        Locale.setDefault(ENGLISH);

        System.setProperty("org.jboss.logging.provider", "slf4j");

        Configurator
                .defaultConfig()
                .writer(new ConsoleWriter())
                .level("error".equals(System.getProperty("log.level", "info")) ? Level.ERROR : Level.INFO)
                .formatPattern("{{level}|min-size=8} {date} {message}")
                .activate();

        server        = new CormorantServer();
        objectStore   = Paths.get("mydir");
        metadataStore = Paths.get("mymetadata");

        server.setContextPath(contextPath);

        Map<String, User> users = new HashMap<>();

        User user = new User("myaccount",
                             BCrypt.hashpw("mypassword", BCrypt.gensalt(12)),
                             "test@example.com",
                             "default",
                             Role.Admin,
                             true);
        users.put("myaccount", user);

        AccountService accountService   = new PathAccountService(objectStore);
        KeystoneService keystoneService = new DefaultKeystoneService(users);

        CormorantConfiguration configuration = new CormorantConfiguration.Builder()
                                                .accountName("myaccount")
                                                .storage(MetadataStorage.File)
                                                .pathMaxCount(10_000)
                                                .objectStore(objectStore)
                                                .metadataStore(metadataStore)
                                                .build();

        CormorantApplication application = new CormorantApplication(configuration,
                                                    accountService,
                                                    keystoneService);

        server.deploy(application);

        server.start((root) -> { return Handlers.requestDump(root); });

        Iterable<Module> modules = ImmutableSet.<Module>of(
                                            new SLF4JLoggingModule());

        swiftApi = newBuilder("openstack-swift")
                    .endpoint("http://" + server.getHost() + ":" + server.getPort() + contextPath + "/v2.0")
                    .credentials("myaccount", "mypassword")
                    .modules(modules)
                    .buildApi(SwiftApi.class);

        Set<String> regions = swiftApi.getConfiguredRegions();

        region = regions.iterator().next();

        Properties overrides = new Properties();
        overrides.setProperty("jclouds.mpu.parallel.degree", "1");

        RegionScopedBlobStoreContext buildView = newBuilder("openstack-swift")
                                                    .endpoint("http://" + server.getHost() + ":" + server.getPort() + contextPath + "/v2.0")
                                                    .credentials("myaccount", "mypassword")
                                                    .overrides(overrides)
                                                    .modules(modules)
                                                    .buildView(RegionScopedBlobStoreContext.class);

        blobStore = buildView.getBlobStore(region);

        try {
            Files.walkFileTree(metadataStore, new RecursiveDeleteVisitor());
            Files.walkFileTree(objectStore, new RecursiveDeleteVisitor());
            Files.createDirectory(objectStore);
            Files.createDirectory(metadataStore);
        } catch (IOException e) {
            throw new CormorantException(e);
        }

        accountApi = swiftApi.getAccountApi(region);

        containerApi = swiftApi.getContainerApi(region);

        jossConfig = new AccountConfig();

        jossConfig.setUsername("myaccount");
        jossConfig.setPassword("mypassword");
        jossConfig.setAuthUrl("http://localhost:" + server.getPort() + contextPath + "/auth/v1.0");
        jossConfig.setAuthenticationMethod(AuthenticationMethod.BASIC);

        jossAccount = new AccountFactory(jossConfig).createAccount();

        client = new OkHttpClient();
        try {
            client.newCall(new Request.Builder().get().url(getUrl() + contextPath + "/v2.0").build()).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Response response;
        try {
            response = client.newCall(new Request.Builder().get().url(getUrl() + contextPath + "/auth/v1.0").header("X-Auth-User", "myaccount").header("X-Auth-Key", "mypassword").build()).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String token = response.header("X-Auth-Token");
        client = new OkHttpClient().newBuilder().addNetworkInterceptor(new Interceptor() {
            
            @Override
            public Response intercept(Chain chain) throws IOException {
                Builder builder = chain.request().newBuilder();
                builder.header("X-Auth-Token", token);
                return chain.proceed(builder.build());
            }
        }).readTimeout(10, TimeUnit.MINUTES).build();
    }

    protected static String getUrl() {
        return "http://" + server.getHost() + ":" + server.getPort();
    }

    @AfterClass
    public static void stop() {
        boolean startServer = "true".equals(System.getProperty("start.server", "true"));
        if (startServer) {
            server.stop();
        }
    }
}
