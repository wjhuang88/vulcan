package zone.hwj.vulcan.bean.impl;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import zone.hwj.vulcan.api.convertible.From;
import zone.hwj.vulcan.api.convertible.Into;
import zone.hwj.vulcan.api.helper.bean2bean.BeanConverter;
import zone.hwj.vulcan.bean.impl.helper.bean2bean.BeanToBeanMethodImpl;
import zone.hwj.vulcan.bean.impl.helper.translator.Translators;
import zone.hwj.vulcan.bean.impl.helper.translator.bytecode.TranslatorImplementation;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum BeanConverterHelper {

    INSTANCE;

    private final Logger log = LoggerFactory.getLogger(BeanConverterHelper.class);

    private final Cache<String, BeanConverter<?, ?>> beanConverterCache = Caffeine.newBuilder()
            .maximumSize(3000)
            .build();

    private String getCacheKey(final Class<?> srcClass, final Class<?> distClass) {
        return srcClass.getName() + "_to_" + distClass.getName();
    }

    @SuppressWarnings("rawtypes")
    <S, D> Unloaded<BeanConverter> makeUnloaded(final Class<S> srcClass, final Class<D> distClass) {
        return new ByteBuddy()
                .subclass(BeanConverter.class)
                .defineField("translators", Translators.class, Modifier.PRIVATE | Modifier.FINAL)
                .constructor(takesArguments(0))
                .intercept(new TranslatorImplementation())
                .method(named("convert"))
                .intercept(new BeanToBeanMethodImpl(srcClass, distClass))
                .make();
    }

    <S, D> void addConverter(final Class<S> srcClass, final Class<D> distClass, final BeanConverter<S, D> converter) {
        beanConverterCache.put(getCacheKey(srcClass, distClass), converter);
    }

    <S, D> void addConverter(final Class<S> srcClass, final Class<D> distClass) {
        final BeanConverter<?, ?> cached = beanConverterCache.getIfPresent(getCacheKey(srcClass, distClass));
        if (cached != null) {
            return;
        }

//        saveClassFile(srcClass, distClass, "temp");

        try(@SuppressWarnings("rawtypes") Unloaded<BeanConverter> unloaded = makeUnloaded(srcClass, distClass)) {
            final BeanConverter<?, ?> converter = unloaded
                    .load(ClassLoader.getSystemClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
            beanConverterCache.put(getCacheKey(srcClass, distClass), converter);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Map converter register fail", e);
        }
    }

    // for test
    @SuppressWarnings("unused")
    <S, D> void saveClassFile(final Class<S> srcClass, final Class<D> distClass, String path) {
        try(@SuppressWarnings("rawtypes") Unloaded<BeanConverter> unloaded = makeUnloaded(srcClass, distClass)) {
            unloaded.saveIn(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    <S, D> BeanConverter<S, D> get(final S src, final D dist) {
        return get((Class<S>) src.getClass(), (Class<D>) dist.getClass());
    }

    @SuppressWarnings("unchecked")
    <S, D> BeanConverter<S, D> get(final Class<S> srcClass, final Class<D> distClass) {

        final String cacheKey = getCacheKey(srcClass, distClass);
        final BeanConverter<S, D> cached = (BeanConverter<S, D>) beanConverterCache.getIfPresent(cacheKey);

        final BeanConverter<S, D> converter;
        if (cached == null) {
            if (log.isDebugEnabled()) {
                log.debug("Creating cached converter for {} to {} conversion", srcClass, distClass);
            }
            try(@SuppressWarnings("rawtypes") Unloaded<BeanConverter> unloaded = makeUnloaded(srcClass, distClass)) {
                converter = unloaded
                        .load(BeanConverter.class.getClassLoader())
                        .getLoaded()
                        .getDeclaredConstructor()
                        .newInstance();
                beanConverterCache.put(cacheKey, converter);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                log.warn("Convert bean to bean fail, use beanutils instead.", e);
                return null;
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Use cached converter for {} to {} conversion", srcClass, distClass);
            }
            converter = cached;
        }

        return converter;
    }

    @SuppressWarnings("unchecked")
    <S, D> Optional<D> handleConvertible(final S src, final D dist) {
        if (src == null || dist == null) {
            return Optional.empty();
        }

        if (dist instanceof From) {
            final Optional<ParameterizedType> fromInterface = Arrays.stream(dist.getClass().getGenericInterfaces())
                    .filter(type -> type instanceof ParameterizedType)
                    .map(type -> (ParameterizedType) type)
                    .filter(type -> type.getRawType() == From.class)
                    .findFirst();
            if (fromInterface.isPresent()) {
                final ParameterizedType from = fromInterface.get();
                final Type[] typeParameters = from.getActualTypeArguments();
                assert typeParameters.length == 1;
                if (typeParameters[0] instanceof Class && ((Class<?>) typeParameters[0]).isAssignableFrom(src.getClass())) {
                    ((From<? super S>) dist).from(src);
                    return Optional.of(dist);
                }
            }
        }

        if (src instanceof Into) {
            final Optional<ParameterizedType> intoInterface = Arrays.stream(src.getClass().getGenericInterfaces())
                    .filter(type -> type instanceof ParameterizedType)
                    .map(type -> (ParameterizedType) type)
                    .filter(type -> type.getRawType() == Into.class)
                    .findFirst();
            if (intoInterface.isPresent()) {
                final ParameterizedType into = intoInterface.get();
                final Type[] typeParameters = into.getActualTypeArguments();
                assert typeParameters.length == 1;
                if (typeParameters[0] instanceof Class && ((Class<?>) typeParameters[0]).isAssignableFrom(dist.getClass())) {
                    ((Into<? super D>) src).to(dist);
                    return Optional.of(dist);
                }
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    <T> Optional<Class<T>> getListType(List<T> list) {
        int size = list.size();
        T item = null;
        int i = 0;
        while(i < size && (item = list.get(i)) == null) {
            i++;
        }
        if (item == null) {
            return Optional.empty();
        }
        return Optional.of((Class<T>) item.getClass());
    }
}
