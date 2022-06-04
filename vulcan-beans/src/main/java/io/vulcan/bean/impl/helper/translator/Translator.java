package io.vulcan.bean.impl.helper.translator;

public interface Translator<T> {
    T translate(Object value);
}
