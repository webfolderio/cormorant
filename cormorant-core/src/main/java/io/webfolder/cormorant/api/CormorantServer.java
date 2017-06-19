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
package io.webfolder.cormorant.api;

import static io.undertow.Handlers.gracefulShutdown;
import static io.undertow.security.api.AuthenticationMode.CONSTRAINT_DRIVEN;
import static io.undertow.servlet.Servlets.filter;
import static io.undertow.servlet.api.ServletStackTraces.NONE;
import static io.undertow.util.Methods.COPY;
import static io.undertow.util.Methods.DELETE;
import static io.undertow.util.Methods.GET;
import static io.undertow.util.Methods.HEAD;
import static io.undertow.util.Methods.POST;
import static io.undertow.util.Methods.PUT;
import static java.util.Collections.singletonMap;
import static java.util.concurrent.TimeUnit.MINUTES;
import static javax.servlet.DispatcherType.REQUEST;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import javax.servlet.ServletException;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.servlet.Filter30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.AllowedMethodsHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.HttpContinueAcceptingHandler;
import io.undertow.server.handlers.HttpContinueReadHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.ServletContainer;
import io.webfolder.cormorant.api.exception.CormorantException;

public class CormorantServer {

    public static final String  DEFAULT_HOST     = "localhost";

    public static final int     DEFAULT_PORT     = 5000;

    private static final long   SHUTDOWN_TIMEOUT = MINUTES.toSeconds(1);

    private static final Logger LOG              = getLogger(CormorantServer.class);

    private final PathHandler root = new PathHandler();

    private final ServletContainer container = ServletContainer.Factory.newInstance();

    private volatile DeploymentManager manager;

    private volatile Undertow server;

    private volatile ResteasyDeployment deployment;

    private volatile GracefulShutdownHandler gracefulHandler;

    private volatile ReentrantLock lock = new ReentrantLock(true);

    private String host = DEFAULT_HOST;

    private int port = DEFAULT_PORT;

    public CormorantServer deploy(
                            final Application application,
                            final HttpHandler ...httpHandlers) {
        return deploy(application, di -> di);
    }

    public CormorantServer deploy(
                            final Application application                          ,
                            final Function<DeploymentInfo, DeploymentInfo> function,
                            final HttpHandler ...httpHandlers) {

        deployment = new ResteasyDeployment();
        deployment.setApplication(application);

        final DeploymentInfo deploymentInfo = initDeployment(application);

        manager = container.addDeployment(function.apply(deploymentInfo));
        manager.deploy();

        try {
            final HttpHandler handler = manager.start();
            root.addPrefixPath(deploymentInfo.getContextPath(), handler);
        } catch (ServletException e) {
            LOG.error(e.getMessage(), e);
            throw new CormorantException(e);
        }

        return this;
    }

    protected DeploymentInfo initDeployment(final Application application) {
        final FilterInfo filterInfo = filter("RestEasyFilter", Filter30Dispatcher.class).setAsyncSupported(true);
        return new DeploymentInfo()
                        .addServletContextAttribute(ResteasyDeployment.class.getName(), deployment)
                        .addFilter(filterInfo)
                        .addFilterUrlMapping("RestEasyFilter", "/*", REQUEST)
                        .setContextPath("/")
                        .setDeploymentName("cormorant")
                        .setDisplayName("cormorant")
                        .setAuthenticationMode(CONSTRAINT_DRIVEN)
                        .setServletStackTraces(NONE)
                        .addInitialHandlerChainWrapper(new AccessLogHandler.Builder().build(singletonMap("format", "combined")))
                        .setClassLoader(deployment.getApplication().getClass().getClassLoader());
    }

    public CormorantServer start() {
        return start();
    }

    public CormorantServer start(final String host) {
        return start(root -> root);
    }

    public CormorantServer start(
                                final Function<HttpHandler, HttpHandler> function) {
        final HttpContinueReadHandler      readHandler      = new HttpContinueReadHandler(root);
        final HttpContinueAcceptingHandler acceptingHandler = new HttpContinueAcceptingHandler(readHandler);
        final HttpHandler                  handler          = function.apply(acceptingHandler);
                                           gracefulHandler  = gracefulShutdown(handler);
        final AllowedMethodsHandler        allowedHandler   = new AllowedMethodsHandler(gracefulHandler,
                                                                       HEAD  , GET,
                                                                       POST  , PUT,
                                                                       DELETE, COPY);

        server = Undertow
                    .builder()
                    .addHttpListener(port, host)
                    .setHandler(allowedHandler)
                    .build();
        server.start();

        deployment.start();

        return this;
    }

    public void stop() {
        if (lock == null) {
            return;
        }
        boolean locked = lock.tryLock();
        if ( ! locked ) {
            return;
        }
        try {
            if ( gracefulHandler != null ) {
                try {
                    gracefulHandler.shutdown();
                    gracefulHandler.awaitShutdown(SHUTDOWN_TIMEOUT);
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage(), e);
                } finally {
                    gracefulHandler = null;
                }
            }
            if ( deployment != null ) {
                deployment.stop();
                deployment = null;
            }
            if ( manager != null ) {
                manager.undeploy();
                manager = null;
            }
            if ( server != null ) {
                server.stop();
                server = null;
            }
        } finally {
            lock.unlock();
            lock = null;
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
