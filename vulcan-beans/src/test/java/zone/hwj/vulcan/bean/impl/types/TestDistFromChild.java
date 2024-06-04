package zone.hwj.vulcan.bean.impl.types;


import zone.hwj.vulcan.api.convertible.From;

public class TestDistFromChild extends TestBeanDist implements From<TestBeanChild> {

    @Override
    public void from(TestBeanChild src) {
        setFieldA(src.getFieldA());
        setFieldB(src.getFieldB());
        setFieldC(src.getFieldC());
        setFieldD(src.getFieldD());
    }
}
