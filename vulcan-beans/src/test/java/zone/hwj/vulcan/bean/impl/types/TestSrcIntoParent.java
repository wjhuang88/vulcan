package zone.hwj.vulcan.bean.impl.types;

import zone.hwj.vulcan.api.convertible.Into;

public class TestSrcIntoParent extends TestBeanDist implements Into<TestBeanParent> {

    public TestSrcIntoParent(TestBeanChild dist) {
        setFieldA(dist.getFieldA());
        setFieldB(dist.getFieldB());
        setFieldC(dist.getFieldC());
        setFieldD(dist.getFieldD());
    }

    @Override
    public TestBeanParent to(TestBeanParent dist) {
        dist.setFieldA(super.getFieldA());
        dist.setFieldB(super.getFieldB());
        return dist;
    }
}
