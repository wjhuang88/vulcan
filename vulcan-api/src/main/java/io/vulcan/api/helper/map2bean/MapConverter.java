package io.vulcan.api.helper.map2bean;

import java.util.Map;

public interface MapConverter<T> {
    T convert(Map<String, ?> map, T instance);
}
