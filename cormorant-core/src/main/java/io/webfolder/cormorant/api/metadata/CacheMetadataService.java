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

    public CacheMetadataService(final MetadataService delegate) {
        this.delegate = delegate;
    }

    @Override
    public synchronized String get(String namespace, String key) throws SQLException {
        String value = cache.get(namespace, key);
        if ( value != null ) {
            return value;
        }
        return delegate.get(namespace, key);
    }

    @Override
    public synchronized boolean contains(String namespace, String key) throws SQLException {
        return get(namespace, key) != null;
    }

    @Override
    public synchronized void update(String namespace, String key, String value) throws SQLException {
        delegate.update(namespace, key, value);
        cache.removeMultiKey(namespace, key);
        cache.put(namespace, key, value);
    }

    @Override
    public synchronized void add(String namespace, String key, String value) throws SQLException {
        delegate.add(namespace, key, value);
        cache.removeMultiKey(namespace, key);
        cache.put(namespace, key, value);
    }

    @Override
    @SuppressWarnings({ "rawtypes" })
    public synchronized Map<String, Object> getValues(String namespace) throws SQLException {
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

    @Override
    public synchronized void setValues(String namespace, Map<String, Object> values) throws SQLException {
        delegate.setValues(namespace, values);
        cache.removeAll(namespace);
        for (String key : values.keySet()) {
            final Object value = values.get(key);
            cache.put(namespace, key, value != null ? valueOf(value) : null);
        }
    }

    @Override
    public synchronized void delete(String namespace, String key) throws SQLException {
        delegate.delete(namespace, key);
        cache.removeMultiKey(namespace, key);
    }

    @Override
    public synchronized void delete(String namespace) throws SQLException {
        delegate.delete(namespace);
        cache.removeAll(namespace);
    }
}
