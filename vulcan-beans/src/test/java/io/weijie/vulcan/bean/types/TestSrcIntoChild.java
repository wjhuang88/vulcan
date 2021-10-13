package io.weijie.vulcan.bean.types;

import io.weijie.vulcan.api.convertible.Into;

public class TestSrcIntoChild extends TestBeanDist implements Into<TestBeanChild> {

    public TestSrcIntoChild(TestBeanChild dist) {
        setFieldA(dist.getFieldA());
        setFieldB(dist.getFieldB());
        setFieldC(dist.getFieldC());
        setFieldD(dist.getFieldD());
    }

    @Override
    public TestBeanChild to(TestBeanChild dist) {
        dist.setFieldA(super.getFieldA());
        dist.setFieldB(super.getFieldB());
        dist.setFieldC(super.getFieldC());
        dist.setFieldD(super.getFieldD());
        return dist;
    }
}
