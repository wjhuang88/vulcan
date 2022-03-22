package io.vulcan.api.helper.bean2bean;

public interface BeanConverter<S, D> {
    D convert(S source, D distance);
}
