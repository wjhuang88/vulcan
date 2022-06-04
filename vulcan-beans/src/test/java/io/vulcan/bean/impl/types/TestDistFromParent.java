package io.vulcan.bean.impl.types;

import io.vulcan.api.convertible.From;

public class TestDistFromParent extends TestBeanDist implements From<TestBeanParent> {

    @Override
    public void from(TestBeanParent src) {
        setFieldA(src.getFieldA());
        setFieldB(src.getFieldB());
        setFieldC(null);
        setFieldD(null);
    }
}
