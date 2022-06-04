package io.vulcan.bean;

import io.vulcan.api.helper.bean2bean.BeanConverter;
import io.vulcan.api.helper.map2bean.MapConverter;
import io.vulcan.bean.impl.BeanImpl;
import java.util.List;
import java.util.Map;

public interface Bean {

    class Holder {
        private final static BeanImpl INSTANCE = new BeanImpl();
    }

    static Bean getDefault() {
        return Holder.INSTANCE;
    }

    <T> void register(Class<T> distClass, MapConverter<T> converter);

    <T> void speedup(Class<T> distClass);

    <S, D> void register(Class<S> srcClass, Class<D> distClass, BeanConverter<S, D> converter);

    <S, D> void speedup(Class<S> srcClass, Class<D> distClass);

    /**
     * Convert map to java bean instance, a new instance will be returned. <b>This method is friendly with the
     * serializing process but has a bad performance</b>
     *
     * @param map   map to be converted
     * @param clazz class of the instance
     * @param <T>   type parameter
     * @return the result instance with type T
     */
    <T> T mapToBean(final Map<String, Object> map, final Class<T> clazz);

    /**
     * Convert map to java bean instance with a instance but not a class.
     *
     * @param map      map to be converted
     * @param instance an instance of the class to be converted
     * @param <T>      type parameter
     * @return the result instance with type T
     */
    <T> T mapToBean(final Map<String, Object> map, final T instance);

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
    <D, S> D beanToBean(final S src, final Class<D> distClass);

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
    <D, S> D beanToBean(final S src, final D dist);

    /**
     * Convert a java bean to a map.
     *
     * @param bean bean to be converted
     * @param <T>  bean type
     * @return result map
     */
    <T> Map<String, Object> beanToMap(final T bean);

    <T> List<T> mapToBeanInList(final List<Map<String, Object>> mapList, final Class<T> clazz);

    <T> List<Map<String, Object>> beanToMapInList(final List<T> beanList);

    <D, S> List<D> beanToBeanInList(final List<S> srcList, final Class<D> distClass);
}
