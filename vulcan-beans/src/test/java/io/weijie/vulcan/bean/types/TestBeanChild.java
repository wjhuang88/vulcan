package io.weijie.vulcan.bean.types;

public class TestBeanChild extends TestBeanParent {

    private String fieldC;
    private TestTypes fieldD;

    public String getFieldC() {
        return fieldC;
    }

    public void setFieldC(String fieldC) {
        this.fieldC = fieldC;
    }

    public TestTypes getFieldD() {
        return fieldD;
    }

    public void setFieldD(TestTypes fieldD) {
        this.fieldD = fieldD;
    }
}
