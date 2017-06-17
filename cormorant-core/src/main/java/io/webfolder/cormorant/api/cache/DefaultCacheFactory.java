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
package io.webfolder.cormorant.api.cache;

import static java.util.ServiceLoader.load;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.jodah.expiringmap.ExpirationPolicy.CREATED;
import static net.jodah.expiringmap.ExpiringMap.builder;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

public class DefaultCacheFactory implements CacheFactory {

    public static final int CACHE_MAX_SIZE = 100_000     ;

    public static final int CACHE_DURATION = 60 * 60 * 24;

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <K, V> Map<K, V> create(String name) {
        final Map<K, V> cache = builder()
                                    .expirationPolicy(CREATED)
                                    .expiration(CACHE_DURATION, SECONDS)
                                    .maxSize(CACHE_MAX_SIZE)
                                .build();
        final ServiceLoader<CacheLoader> loader   = load(CacheLoader.class, getClass().getClassLoader());
        final Iterator<CacheLoader>      iterator = loader.iterator();
        if (iterator.hasNext()) {
            final CacheLoader cacheLoader = iterator.next();
            cacheLoader.load(name, cache);
        }
        return (Map<K, V>) cache;
    }
}
