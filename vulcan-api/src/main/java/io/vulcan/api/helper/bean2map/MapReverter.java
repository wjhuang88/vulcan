package io.vulcan.api.helper.bean2map;

import java.util.Map;

public interface MapReverter<T> {
    <F> Map<String, F> revert(Map<String, F> map, T instance);
}
