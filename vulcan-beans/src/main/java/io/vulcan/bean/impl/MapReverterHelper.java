package io.vulcan.bean.impl;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.vulcan.api.helper.bean2map.MapReverter;
import io.vulcan.bean.impl.helper.bean2map.BeanToMapMethodImpl;
import io.vulcan.bean.impl.helper.translator.Translators;
import io.vulcan.bean.impl.helper.translator.bytecode.TranslatorImplementation;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum MapReverterHelper {

    INSTANCE;

    private final Logger log = LoggerFactory.getLogger(MapReverterHelper.class);

    private final Cache<Class<?>, MapReverter<?>> mapReverterCache = Caffeine.newBuilder()
            .maximumSize(3000)
            .build();

    @SuppressWarnings("rawtypes")
    <T> DynamicType.Unloaded<MapReverter> makeUnloaded(final Class<T> clazz) {
        return new ByteBuddy()
                .subclass(MapReverter.class)
                .defineField("translators", Translators.class, Modifier.PRIVATE | Modifier.FINAL)
                .constructor(takesArguments(0))
                .intercept(new TranslatorImplementation())
                .method(named("revert"))
                .intercept(new BeanToMapMethodImpl(clazz))
                .make();
    }

    <T> void addConverter(final Class<T> distClass, final MapReverter<T> converter) {
        mapReverterCache.put(distClass, converter);
    }

    <T> void addConverter(final Class<T> distClass) {
        final MapReverter<?> cached = mapReverterCache.getIfPresent(distClass);
        if (cached != null) {
            return;
        }

        try {
            final MapReverter<?> converter = makeUnloaded(distClass)
                    .load(ClassLoader.getSystemClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
            mapReverterCache.put(distClass, converter);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Map converter register fail", e);
        }
    }

    <T> void saveClassFile(final Class<T> clazz, String path) {
        try {
            makeUnloaded(clazz).saveIn(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    <T> MapReverter<T> get(final T instance) {
        return get((Class<T>) instance.getClass());
    }

    @SuppressWarnings("unchecked")
    <T> MapReverter<T> get(final Class<T> clazz) {

        final MapReverter<T> cached = (MapReverter<T>) mapReverterCache.getIfPresent(clazz);

        final MapReverter<T> converter;
        if (cached == null) {
            if (log.isDebugEnabled()) {
                log.debug("Creating cached converter for {}", clazz);
            }
            try {
                converter = makeUnloaded(clazz)
                        .load(MapReverter.class.getClassLoader())
                        .getLoaded()
                        .getDeclaredConstructor()
                        .newInstance();
                mapReverterCache.put(clazz, converter);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                log.warn("Convert map to java bean instance fail, use beanutils instead.", e);
                return null;
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Use cached converter for {}", clazz);
            }
            converter = cached;
        }

        return converter;
    }
}
