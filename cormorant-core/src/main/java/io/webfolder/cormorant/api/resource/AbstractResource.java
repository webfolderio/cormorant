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
package io.webfolder.cormorant.api.resource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

public abstract class AbstractResource<T> implements Resource<T> {

    private final Iterable<T> stream;

    private final ResourceAdapter<T> resourceAdapter;

    public AbstractResource(
                            final Iterable<T> stream,
                            final ResourceAdapter<T> resourceAdapter) {
        this.stream = stream;
        this.resourceAdapter = resourceAdapter;
    }

    @Override
    public Iterator<T> iterator() {
        return stream.iterator();
    }

    @Override
    public String convert(final T o, final ContentFormat contentFormat, final Boolean appendForwardSlash) throws IOException, SQLException {
        return resourceAdapter.convert(o, contentFormat, appendForwardSlash);
    }
}
