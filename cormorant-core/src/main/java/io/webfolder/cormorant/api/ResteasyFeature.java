package io.webfolder.cormorant.api;

import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import io.webfolder.cormorant.internal.jaxrs.CormorantMediaTypeHeaderDelegate;

class ResteasyFeature {

    void configure(FeatureContext context) {
        ResteasyProviderFactory providerFactory = (ResteasyProviderFactory) context.getConfiguration();
        providerFactory.addHeaderDelegate(MediaType.class, new CormorantMediaTypeHeaderDelegate());
    }
}
