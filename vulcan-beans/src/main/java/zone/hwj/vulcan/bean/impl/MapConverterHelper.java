package zone.hwj.vulcan.bean.impl;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import zone.hwj.vulcan.api.helper.map2bean.MapConverter;
import zone.hwj.vulcan.bean.impl.helper.map2bean.MapToBeanMethodImpl;
import zone.hwj.vulcan.bean.impl.helper.translator.Translators;
import zone.hwj.vulcan.bean.impl.helper.translator.bytecode.TranslatorImplementation;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum MapConverterHelper {

    INSTANCE;

    private final Logger log = LoggerFactory.getLogger(MapConverterHelper.class);

    private final Cache<Class<?>, MapConverter<?>> mapConverterCache = Caffeine.newBuilder()
            .maximumSize(3000)
            .build();

    @SuppressWarnings("rawtypes")
    <T> Unloaded<MapConverter> makeUnloaded(final Class<T> clazz) {
        return new ByteBuddy()
                .subclass(MapConverter.class)
                .defineField("translators", Translators.class, Modifier.PRIVATE | Modifier.FINAL)
                .constructor(takesArguments(0))
                .intercept(new TranslatorImplementation())
                .method(named("convert"))
                .intercept(new MapToBeanMethodImpl(clazz))
                .make();
    }

    <T> void addConverter(final Class<T> distClass, final MapConverter<T> converter) {
        mapConverterCache.put(distClass, converter);
    }

    <T> void addConverter(final Class<T> distClass) {
        final MapConverter<?> cached = mapConverterCache.getIfPresent(distClass);
        if (cached != null) {
            return;
        }

        try(@SuppressWarnings("rawtypes") Unloaded<MapConverter> unloaded = makeUnloaded(distClass)) {
            final MapConverter<?> converter = unloaded
                    .load(ClassLoader.getSystemClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
            mapConverterCache.put(distClass, converter);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Map converter register fail", e);
        }
    }

    // for test
    @SuppressWarnings("unused")
    <T> void saveClassFile(final Class<T> clazz, String path) {
        try(@SuppressWarnings("rawtypes") Unloaded<MapConverter> unloaded = makeUnloaded(clazz)) {
            unloaded.saveIn(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    <T> MapConverter<T> get(final T instance) {
        return get((Class<T>) instance.getClass());
    }

    @SuppressWarnings("unchecked")
    <T> MapConverter<T> get(final Class<T> clazz) {

        final MapConverter<T> cached = (MapConverter<T>) mapConverterCache.getIfPresent(clazz);

        final MapConverter<T> converter;
        if (cached == null) {
            if (log.isDebugEnabled()) {
                log.debug("Creating cached converter for {}", clazz);
            }
            try(@SuppressWarnings("rawtypes") Unloaded<MapConverter> unloaded = makeUnloaded(clazz)) {
                converter = unloaded
                        .load(MapConverter.class.getClassLoader())
                        .getLoaded()
                        .getDeclaredConstructor()
                        .newInstance();
                mapConverterCache.put(clazz, converter);
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
