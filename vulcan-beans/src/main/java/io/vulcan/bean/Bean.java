package io.vulcan.bean;

import io.vulcan.api.convertible.FromMap;
import io.vulcan.api.convertible.IntoMap;
import io.vulcan.api.helper.bean2bean.BeanConverter;
import io.vulcan.api.helper.map2bean.MapConverter;
import io.vulcan.bean.helper.DateConverter;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Bean {

    private static final Logger log = LoggerFactory.getLogger(Bean.class);

    private final BeanUtilsBean beanUtilsBean;
    private final MapConverterHelper mapConverterHelper = MapConverterHelper.INSTANCE;
    private final BeanConverterHelper beanConverterHelper = BeanConverterHelper.INSTANCE;

    private Bean() {
        final DateConverter dtConverter = DateConverter.getInstance();
        final ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
        convertUtilsBean.deregister(Date.class);
        convertUtilsBean.register(dtConverter, Date.class);
        beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());
    }

    private static class BeanHolder {
        private final static Bean INSTANCE = new Bean();
    }

    public static Bean getInstance() {
        return BeanHolder.INSTANCE;
    }

    public <T> void register(Class<T> distClass, MapConverter<T> converter) {
        mapConverterHelper.addConverter(distClass, converter);
    }

    public <T> void speedup(Class<T> distClass) {
        mapConverterHelper.addConverter(distClass);
    }

    public <S, D> void register(Class<S> srcClass, Class<D> distClass, BeanConverter<S, D> converter) {
        beanConverterHelper.addConverter(srcClass, distClass, converter);
    }

    public <S, D> void speedup(Class<S> srcClass, Class<D> distClass) {
        beanConverterHelper.addConverter(srcClass, distClass);
    }

    /**
     * Convert map to java bean instance, a new instance will be returned. <b>This method is friendly with the
     * serializing process but has a bad performance</b>
     *
     * @param map   map to be converted
     * @param clazz class of the instance
     * @param <T>   type parameter
     * @return the result instance with type T
     */
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

    /**
     * Convert map to java bean instance with a instance but not a class.
     *
     * @param map      map to be converted
     * @param instance an instance of the class to be converted
     * @param <T>      type parameter
     * @return the result instance with type T
     */
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
//            throw new RuntimeException("err!!!", e);
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

    /**
     * Copy properties from `src` instance to a new `distClass`'s instance <b>This method is friendly with the
     * serializing process but has a bad performance</b>
     *
     * @param src       instance that copy from
     * @param distClass instance's class that copy to
     * @param <D>       dist instance type
     * @param <S>       src instance type
     * @return the result instance with type D
     */
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

    /**
     * Copy properties from `src` instance to a new `distClass`'s instance <b>instance will not be changed, a new
     * instance will be returned</b>
     *
     * @param src  instance that copy from
     * @param dist an instance of the class to be converted
     * @param <D>  dist instance type
     * @param <S>  src instance type
     * @return the result instance with type D
     */
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

    /**
     * Convert a java bean to a map.
     *
     * @param bean bean to be converted
     * @param <T>  bean type
     * @return result map
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, Object> beanToMap(final T bean) {
        if (bean instanceof IntoMap) {
            final Map<String, ?> map = ((IntoMap) bean).to(new HashMap<>());
            return (Map<String, Object>) map;
        }

        final BeanMap beanMap = new BeanMap();
        beanMap.setBean(bean);
        Map<String, Object> copy = new HashMap<>();

        for (Object key : beanMap.keySet()) {
            copy.put(key.toString(), beanMap.get(key));
        }
        return copy;
    }

    public <T> List<T> mapToBeanInList(final List<Map<String, Object>> mapList, final Class<T> clazz) {
        if (mapList == null || mapList.isEmpty()) {
            return Collections.emptyList();
        }

        final MapConverter<T> converter = mapConverterHelper.get(clazz);
        if (converter == null) {
            return mapList.stream().map(input -> mapToBeanOld(input, clazz)).collect(Collectors.toList());
        }

        return mapList.stream().map(input -> mapToBean(converter, input, clazz)).collect(Collectors.toList());
    }

    public <T> List<Map<String, Object>> beanToMapInList(final List<T> beanList) {
        if (beanList == null || beanList.isEmpty()) {
            return Collections.emptyList();
        }
        return beanList.stream().map(this::beanToMap).collect(Collectors.toList());
    }

    public <D, S> List<D> beanToBeanInList(final List<S> srcList, final Class<D> distClass) {
        if (srcList == null || srcList.isEmpty()) {
            return Collections.emptyList();
        }

        Optional<Class<S>> listType = beanConverterHelper.getListType(srcList);
        if (!listType.isPresent()) {
            return srcList.stream().map(input -> beanToBeanOld(input, distClass)).collect(Collectors.toList());
        }

        final BeanConverter<S, D> converter = beanConverterHelper.get(listType.get(), distClass);
        if (converter == null) {
            return srcList.stream().map(input -> beanToBeanOld(input, distClass)).collect(Collectors.toList());
        }

        return srcList.stream().map(input -> beanToBean(converter, input, distClass)).collect(Collectors.toList());
    }
}
