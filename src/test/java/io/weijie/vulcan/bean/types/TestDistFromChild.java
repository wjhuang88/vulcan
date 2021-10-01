package io.weijie.vulcan.bean.types;


import io.weijie.vulcan.api.convertible.From;

public class TestDistFromChild extends TestBeanDist implements From<TestBeanChild> {

    @Override
    public void from(TestBeanChild src) {
        setFieldA(src.getFieldA());
        setFieldB(src.getFieldB());
        setFieldC(src.getFieldC());
        setFieldD(src.getFieldD());
    }
}
