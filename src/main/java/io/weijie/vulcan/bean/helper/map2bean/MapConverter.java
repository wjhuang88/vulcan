package io.weijie.vulcan.bean.helper.map2bean;

import java.util.Map;

public interface MapConverter<T> {
    T convert(Map<String, ?> map, T instance);
}
