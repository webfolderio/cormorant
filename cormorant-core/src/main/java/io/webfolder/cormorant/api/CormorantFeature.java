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
package io.webfolder.cormorant.api;

import java.security.Principal;
import java.util.Map;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.webfolder.cormorant.api.exception.CormorantExceptionMapper;
import io.webfolder.cormorant.api.service.ContainerService;
import io.webfolder.cormorant.api.service.KeystoneService;
import io.webfolder.cormorant.api.service.MetadataService;
import io.webfolder.cormorant.internal.jaxrs.CormorantAuthenticationFeature;
import io.webfolder.cormorant.internal.jaxrs.ResponseWriter;

public class CormorantFeature<T> implements Feature {

    private final Map<String, Principal> tokens;

    private final KeystoneService        keystoneService;

    private final MetadataService        accountMetadataService;

    private final ContainerService<T>    containerService;

    private final Logger log = LoggerFactory.getLogger(CormorantFeature.class);

    public CormorantFeature(
                final Map<String, Principal> tokens,
                final KeystoneService        keystoneService,
                final MetadataService        accountMetadataService,
                final ContainerService<T>    containerService) {
        this.tokens                 = tokens;
        this.keystoneService        = keystoneService;
        this.accountMetadataService = accountMetadataService;
        this.containerService       = containerService;
    }

    @Override
    public boolean configure(FeatureContext context) {
        try {
            new ResteasyFeature().configure(context);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
        }
        context.register(new ResponseWriter());
        context.register(new CormorantExceptionMapper());
        context.register(new CormorantAuthenticationFeature<>(tokens,
                                                              keystoneService,
                                                              accountMetadataService,
                                                              containerService));
        return true;
    }
}
