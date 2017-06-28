package io.webfolder.cormorant.api.metadata;

import static java.lang.String.valueOf;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import io.webfolder.cormorant.api.service.MetadataService;

public class CacheMetadataService implements MetadataService {

    private MultiKeyMap<String, String> cache = new MultiKeyMap<>();

    private final MetadataService delegate;

    private final Object mutex = new Object();

    public CacheMetadataService(final MetadataService delegate) {
        this.delegate = delegate;
    }

    @Override
    public String get(String namespace, String key) throws SQLException {
        synchronized (mutex) {
            String value = cache.get(namespace, key);
            if ( value != null ) {
                return value;
            }
            return delegate.get(namespace, key);
        }
    }

    @Override
    public boolean contains(String namespace, String key) throws SQLException {
        synchronized (mutex) {
            return get(namespace, key) != null;
        }
    }

    @Override
    public void update(String namespace, String key, String value) throws SQLException {
        synchronized (mutex) {
            delegate.update(namespace, key, value);
            cache.removeMultiKey(namespace, key);
            cache.put(namespace, key, value);
        }
    }

    @Override
    public void add(String namespace, String key, String value) throws SQLException {
        synchronized (mutex) {
            delegate.add(namespace, key, value);
            cache.removeMultiKey(namespace, key);
            cache.put(namespace, key, value);
        }
    }

    @Override
    @SuppressWarnings({ "rawtypes" })
    public Map<String, Object> getValues(String namespace) throws SQLException {
        synchronized (mutex) {
            final MapIterator<MultiKey<? extends String>, String> iter = cache.mapIterator();
            final Map<String, Object> map = new HashMap<>();
            if (iter.hasNext()) {
                while (iter.hasNext()) {
                    final MultiKey multiKey = iter.next();
                    final Object[] keys = multiKey.getKeys();
                    if (keys.length == 2 && namespace.equals(keys[0])) {
                        String key = (String) keys[1];
                        String value = cache.get(namespace, key);
                        map.put(key, value);
                    }
                }
            }
            if ( ! map.isEmpty() ) {
                return map;
            }
            return delegate.getValues(namespace);
        }
    }

    @Override
    public void setValues(String namespace, Map<String, Object> values) throws SQLException {
        synchronized (mutex) {
            delegate.setValues(namespace, values);
            cache.removeAll(namespace);
            for (String key : values.keySet()) {
                final Object value = values.get(key);
                cache.put(namespace, key, value != null ? valueOf(value) : null);
            }
        }
    }

    @Override
    public void delete(String namespace, String key) throws SQLException {
        synchronized (mutex) {
            delegate.delete(namespace, key);
            cache.removeMultiKey(namespace, key);
        }
    }

    @Override
    public void delete(String namespace) throws SQLException {
        synchronized (mutex) {
            delegate.delete(namespace);
            cache.removeAll(namespace);
        }
    }
}
