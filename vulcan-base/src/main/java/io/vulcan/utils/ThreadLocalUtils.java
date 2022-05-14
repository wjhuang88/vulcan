package io.vulcan.utils;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ThreadLocalUtils {

    private static final Logger log = LoggerFactory.getLogger(ThreadLocalUtils.class);

    private static final InheritableThreadLocal<Map<Object, Object>> threadBucket = new InheritableThreadLocalMap<>();

    static <T> T getValue(final InheritableThreadLocal<Map<Object, Object>> threadBucket, final Object key, final Class<T> clazz) {
        final Map<?, ?> bucket = threadBucket.get();
        if (null == bucket) {
            if (log.isWarnEnabled()) {
                log.warn("ThreadLocal bucket: $key not exists.");
            }
            return null;
        }
        final Object valueSrc = bucket.get(key);
        if (clazz.isInstance(valueSrc)) {
            return clazz.cast(valueSrc);
        } else {
            if (log.isWarnEnabled()) {
                if (null == valueSrc) {
                    log.warn("Cannot find object from ThreadLocal bucket: {}; no object found", key);
                } else {
                    log.warn("Cannot find object from ThreadLocal bucket: {}; except type is {} but got {}"
                            , key, clazz.getSimpleName(), valueSrc.getClass().getSimpleName());
                }
            }
            return null;
        }
    }

    static Object setValue(final InheritableThreadLocal<Map<Object, Object>> threadBucket, final Object key, final Object value) {
        Map<Object, Object> bucket = threadBucket.get();
        if (null == bucket) {
            final Map<Object, Object> newBucket = new HashMap<>();
            threadBucket.set(newBucket);
            bucket = newBucket;
        }
        return bucket.put(key, value);
    }

    static Object remove(final InheritableThreadLocal<Map<Object, Object>> threadBucket, final Object key) {
        final Map<Object, Object> bucket = threadBucket.get();
        if (null == bucket) {
            return null;
        }
        return bucket.remove(key);
    }

    public static <T> T getValue(final Object key, final Class<T> clazz) {
        return getValue(threadBucket, key, clazz);
    }

    public static Object setValue(final Object key, final Object value) {
        return setValue(threadBucket, key, value);
    }

    public static Object remove(final Object key) {
        return remove(threadBucket, key);
    }

    private static class InheritableThreadLocalMap<K, V> extends InheritableThreadLocal<Map<K, V>> {

        @Override
        protected Map<K, V> childValue(final Map<K, V> parentValue) {
            if (parentValue != null) {
                return new HashMap<>(parentValue);
            } else {
                return null;
            }
        }
    }
}
