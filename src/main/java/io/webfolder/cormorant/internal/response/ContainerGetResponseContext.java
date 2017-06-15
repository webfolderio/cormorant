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
package io.webfolder.cormorant.internal.response;

import io.webfolder.cormorant.api.resource.ResourceStream;

public class ContainerGetResponseContext<T> {

    private ContainerGetResponse response = new ContainerGetResponse();

    private ResourceStream<T> body;

    public ContainerGetResponseContext(final ResourceStream<T> body) {
        this.body = body;
    }

    public ContainerGetResponse getResponse() {
        return response;
    }

    public ResourceStream<T> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "ContainerGetResponseContext [response=" + response + ", body=" + body + "]";
    }
}
