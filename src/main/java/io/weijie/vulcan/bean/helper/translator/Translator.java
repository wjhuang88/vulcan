package io.weijie.vulcan.bean.helper.translator;

public interface Translator<T> {
    T translate(Object value);
}
