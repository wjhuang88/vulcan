package io.vulcan.bean.impl;

import io.vulcan.api.convertible.FromMap;
import io.vulcan.api.convertible.IntoMap;
import io.vulcan.api.helper.bean2bean.BeanConverter;
import io.vulcan.api.helper.bean2map.MapReverter;
import io.vulcan.api.helper.map2bean.MapConverter;
import io.vulcan.bean.Bean;
import io.vulcan.bean.impl.helper.DateConverter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BeanImpl implements Bean {

    private static final Logger log = LoggerFactory.getLogger(BeanImpl.class);

    private final BeanUtilsBean beanUtilsBean;
    private final MapConverterHelper mapConverterHelper = MapConverterHelper.INSTANCE;
    private final BeanConverterHelper beanConverterHelper = BeanConverterHelper.INSTANCE;
    private final MapReverterHelper mapReverterHelper = MapReverterHelper.INSTANCE;

    public BeanImpl() {
        final DateConverter dtConverter = DateConverter.getInstance();
        final ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
        convertUtilsBean.deregister(Date.class);
        convertUtilsBean.register(dtConverter, Date.class);
        beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());
    }

    @Override
    public <T> void register(Class<T> distClass, MapConverter<T> converter) {
        mapConverterHelper.addConverter(distClass, converter);
    }

    @Override
    public <T> void register(Class<T> distClass, MapReverter<T> converter) {
        mapReverterHelper.addConverter(distClass, converter);
    }

    @Override
    public <T> void speedup(Class<T> distClass) {
        mapConverterHelper.addConverter(distClass);
        mapReverterHelper.addConverter(distClass);
    }

    @Override
    public <S, D> void register(Class<S> srcClass, Class<D> distClass, BeanConverter<S, D> converter) {
        beanConverterHelper.addConverter(srcClass, distClass, converter);
    }

    @Override
    public <S, D> void speedup(Class<S> srcClass, Class<D> distClass) {
        beanConverterHelper.addConverter(srcClass, distClass);
    }

    @Override
    public <T> T mapToBean(final Map<String, Object> map, final Class<T> clazz) {

        final T instance;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
            return mapToBean(map, instance);
        } catch (NoSuchMethodException e) {
            log.error("Class " + clazz.getName() + " must have a no arguments constructor.", e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.error("Convert map to java bean instance fail", e);
        }
        return null;
    }

    @Override
    public <T> T mapToBean(final Map<String, Object> map, final T instance) {

        if (instance instanceof FromMap) {
            ((FromMap) instance).from(map);
            return instance;
        }

        final MapConverter<T> converter = mapConverterHelper.get(instance);
        if (converter == null) {
            return mapToBeanOld(map, instance);
        }

        try {
            return converter.convert(map, instance);
        } catch (Throwable e) {
            log.warn("Convert map to java bean instance fail, use beanutils instead.", e);
            return mapToBeanOld(map, instance);
        }
    }

    private <T> T mapToBean(MapConverter<T> converter, final Map<String, Object> map, final T instance) {
        if (instance instanceof FromMap) {
            ((FromMap) instance).from(map);
            return instance;
        }

        try {
            return converter.convert(map, instance);
        } catch (Throwable e) {
            log.warn("Convert map to java bean instance fail, use beanutils instead.", e);
            return mapToBeanOld(map, instance);
        }
    }

    private <T> T mapToBean(MapConverter<T> converter, final Map<String, Object> map, final Class<T> clazz) {
        final T instance;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
            return mapToBean(converter, map, instance);
        } catch (NoSuchMethodException e) {
            log.error("Class " + clazz.getName() + " must have a no arguments constructor.", e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.error("Convert map to java bean instance fail", e);
        }
        return null;
    }

    <T> T mapToBeanOld(final Map<String, Object> map, final T instance) {
        if (instance instanceof FromMap) {
            ((FromMap) instance).from(map);
            return instance;
        }

        try {
            beanUtilsBean.populate(instance, map);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Convert map to java bean instance fail", e);
        }
        return instance;
    }

    <T> T mapToBeanOld(final Map<String, Object> map, final Class<T> clazz) {
        final T instance;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
            return mapToBeanOld(map, instance);
        } catch (NoSuchMethodException e) {
            log.error("Class " + clazz.getName() + " must have a no arguments constructor.", e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.error("Convert map to java bean instance fail", e);
        }
        return null;
    }

    @Override
    public <D, S> D beanToBean(final S src, final Class<D> distClass) {
        final D dist;
        try {
            dist = distClass.getDeclaredConstructor().newInstance();
            return beanToBean(src, dist);
        } catch (NoSuchMethodException e) {
            log.error("Class " + distClass.getName() + " must have a no arguments constructor.", e);
        }  catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("Copy properties fail", e);
        }
        return null;
    }

    @Override
    public <D, S> D beanToBean(final S src, final D dist) {
        if (src instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> mapSrc = (Map<String, Object>) src;
            return mapToBean(mapSrc, dist);
        }

        final Optional<D> result = beanConverterHelper.handleConvertible(src, dist);
        if (result.isPresent()) {
            return result.get();
        }

        final BeanConverter<S, D> converter = beanConverterHelper.get(src, dist);
        if (converter == null) {
            return beanToBeanOld(src, dist);
        }

        try {
            return converter.convert(src, dist);
        } catch (Throwable e) {
            log.warn("Convert bean to bean fail, use beanutils instead.", e);
            return beanToBeanOld(src, dist);
        }
    }

    <D, S> D beanToBeanOld(final S src, final D dist) {
        final Optional<D> result = beanConverterHelper.handleConvertible(src, dist);
        if (result.isPresent()) {
            return result.get();
        }

        try {
            beanUtilsBean.copyProperties(dist, src);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Copy properties fail", e);
        }
        return dist;
    }

    <D, S> D beanToBeanOld(final S src, final Class<D> distClass) {

        final D dist;
        try {
            dist = distClass.getDeclaredConstructor().newInstance();
            return beanToBeanOld(src, dist);
        } catch (NoSuchMethodException e) {
            log.error("Class " + distClass.getName() + " must have a no arguments constructor.", e);
        }  catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("Copy properties fail", e);
        }
        return null;
    }

    private <D, S> D beanToBean(BeanConverter<S, D> converter, final S src, final D dist) {
        final Optional<D> result = beanConverterHelper.handleConvertible(src, dist);
        if (result.isPresent()) {
            return result.get();
        }

        try {
            return converter.convert(src, dist);
        } catch (Throwable e) {
            log.warn("Convert bean to bean fail, use beanutils instead.", e);
            return beanToBeanOld(src, dist);
        }
    }

    private <D, S> D beanToBean(BeanConverter<S, D> converter, final S src, final Class<D> distClass) {

        final D dist;
        try {
            dist = distClass.getDeclaredConstructor().newInstance();
            return beanToBean(converter, src, dist);
        } catch (NoSuchMethodException e) {
            log.error("Class " + distClass.getName() + " must have a no arguments constructor.", e);
        }  catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("Copy properties fail", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, Object> beanToMap(final T bean) {
        if (bean instanceof IntoMap) {
            final Map<String, ?> map = ((IntoMap) bean).to(new HashMap<>());
            return (Map<String, Object>) map;
        }
        final MapReverter<T> reverter = mapReverterHelper.get(bean);
        if (reverter == null) {
            return beanToMapOld(bean);
        }

        final Map<String, Object> map = new HashMap<>();
        try {
            return reverter.revert(map, bean);
        } catch (Throwable e) {
            log.warn("Convert bean to map fail, use beanmap instead.", e);
            return beanToMapOld(bean);
        }
    }

    <T> Map<String, Object> beanToMapOld(final T bean) {
        final BeanMap beanMap = new BeanMap();
        beanMap.setBean(bean);
        Map<String, Object> copy = new HashMap<>();

        for (Object key : beanMap.keySet()) {
            copy.put(key.toString(), beanMap.get(key));
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    private <T> Map<String, Object> beanToMap(MapReverter<T> reverter, final T bean) {
        if (bean instanceof IntoMap) {
            final Map<String, ?> map = ((IntoMap) bean).to(new HashMap<>());
            return (Map<String, Object>) map;
        }

        final Map<String, Object> map = new HashMap<>();
        try {
            return reverter.revert(map, bean);
        } catch (Throwable e) {
            log.warn("Convert bean to map fail, use beanmap instead.", e);
            return beanToMapOld(bean);
        }
    }

    @Override
    public <T> List<T> mapToBeanInList(final List<Map<String, Object>> mapList, final Class<T> clazz) {
        return mapToBeanInList(mapList, clazz, false);
    }

    @Override
    public <T> List<T> mapToBeanInList(final List<Map<String, Object>> mapList, final Class<T> clazz, boolean ignoreNull) {
        if (mapList == null || mapList.isEmpty()) {
            return Collections.emptyList();
        }

        final MapConverter<T> converter = mapConverterHelper.get(clazz);
        if (converter == null) {
            return convertList(mapList, ignoreNull, input -> mapToBeanOld(input, clazz));
        }

        return convertList(mapList, ignoreNull, input -> mapToBean(converter, input, clazz));
    }

    @Override
    public <T> List<Map<String, Object>> beanToMapInList(final List<T> beanList) {
        return beanToMapInList(beanList, false);
    }

    @Override
    public <T> List<Map<String, Object>> beanToMapInList(final List<T> beanList, boolean ignoreNull) {
        if (beanList == null || beanList.isEmpty()) {
            return Collections.emptyList();
        }

        final Optional<ParameterizedType> listInterface = Arrays.stream(beanList.getClass().getGenericInterfaces())
                .filter(type -> type instanceof ParameterizedType)
                .map(type -> (ParameterizedType) type)
                .filter(type -> type.getRawType() == List.class)
                .findFirst();
        if (!listInterface.isPresent()) {
            return convertList(beanList, ignoreNull, this::beanToMapOld);
        }

        final ParameterizedType listType = listInterface.get();
        final Type[] typeParameters = listType.getActualTypeArguments();
        assert typeParameters.length == 1;

        if (!(typeParameters[0] instanceof Class)) {
            return convertList(beanList, ignoreNull, this::beanToMapOld);
        }

        @SuppressWarnings("unchecked")
        final MapReverter<T> reverter = mapReverterHelper.get((Class<T>) typeParameters[0]);
        if (reverter == null) {
            return convertList(beanList, ignoreNull, this::beanToMapOld);
        }

        return convertList(beanList, ignoreNull, input -> beanToMap(reverter, input));
    }

    @Override
    public <D, S> List<D> beanToBeanInList(final List<S> srcList, final Class<D> distClass) {
        return beanToBeanInList(srcList, distClass, false);
    }

    @Override
    public <D, S> List<D> beanToBeanInList(final List<S> srcList, final Class<D> distClass, boolean ignoreNull) {
        if (srcList == null || srcList.isEmpty()) {
            return Collections.emptyList();
        }

        Optional<Class<S>> listType = beanConverterHelper.getListType(srcList);
        if (!listType.isPresent()) {
            return convertList(srcList, ignoreNull, input -> beanToBeanOld(input, distClass));
        }

        final BeanConverter<S, D> converter = beanConverterHelper.get(listType.get(), distClass);
        if (converter == null) {
            return convertList(srcList, ignoreNull, input -> beanToBeanOld(input, distClass));
        }

        return convertList(srcList, ignoreNull, input -> beanToBean(converter, input, distClass));
    }

    private <F,T> List<T> convertList(List<F> inList, boolean ignoreNull, Function<F, T> handler) {
        if (inList.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>(inList.size());
        for (F input : inList) {
            if (input != null) {
                result.add(handler.apply(input));
                continue;
            }
            if (!ignoreNull) {
                result.add(null);
            }
        }
        return result;
    }
}
