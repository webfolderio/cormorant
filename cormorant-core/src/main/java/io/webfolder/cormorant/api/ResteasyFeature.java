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
