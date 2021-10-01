package io.weijie.vulcan.bean.helper.bean2bean;

public interface BeanConverter<S, D> {
    D convert(S source, D distance);
}
