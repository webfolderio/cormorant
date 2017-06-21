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

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.WeakHashMap;

public class DefaultCacheFactory implements CacheFactory {

    public static final int CACHE_MAX_SIZE = 100_000     ;

    public static final int CACHE_DURATION = 60 * 60 * 24;

    @SuppressWarnings("rawtypes")
    private final Map<String, WeakReference<Map>> cacheMappings = new WeakHashMap<>();

    @SuppressWarnings({ "rawtypes" })
    private final CacheLoader cacheLoader;

    @SuppressWarnings("rawtypes")
    public DefaultCacheFactory() {
        final ServiceLoader<CacheLoader> loader   = load(CacheLoader.class, getClass().getClassLoader());
        final Iterator<CacheLoader>      iterator = loader.iterator();
        if (iterator.hasNext()) {
            cacheLoader = iterator.next();
        } else {
            cacheLoader = null;
        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <K, V> Map<K, V> create(final String name) {
        if (cacheMappings.containsKey(name)) {
            return cacheMappings.get(name).get();
        }
        final Map<K, V> cache = builder()
                                    .expirationPolicy(CREATED)
                                    .expiration(CACHE_DURATION, SECONDS)
                                    .maxSize(CACHE_MAX_SIZE)
                                .build();
        if ( cacheLoader != null ) {
            cacheLoader.load(name, cache);
        }
        cacheMappings.put(name, new WeakReference<Map>(cache));
        return (Map<K, V>) cache;
    }
}
