package zone.hwj.vulcan.bean.impl.types;

import java.util.Date;

public class TestTypes {

    private Date fieldDate;
    private String fieldString;
    private long fieldLong;
    private int fieldInt;
    private Long fieldLongBox;
    private String[] fieldStringArr;
    private int[] fieldIntArr;

    public TestTypes() {
        fieldDate = new Date();
    }

    public TestTypes(Date date) {
        fieldDate = date;
    }

    public Date getFieldDate() {
        return fieldDate;
    }

    public void setFieldDate(Date fieldDate) {
        this.fieldDate = fieldDate;
    }

    public String getFieldString() {
        return fieldString;
    }

    public void setFieldString(String fieldString) {
        this.fieldString = fieldString;
    }

    public long getFieldLong() {
        return fieldLong;
    }

    public void setFieldLong(long fieldLong) {
        this.fieldLong = fieldLong;
    }

    public int getFieldInt() {
        return fieldInt;
    }

    public void setFieldInt(int fieldInt) {
        this.fieldInt = fieldInt;
    }

    public Long getFieldLongBox() {
        return fieldLongBox;
    }

    public void setFieldLongBox(Long fieldLongBox) {
        this.fieldLongBox = fieldLongBox;
    }

    public String[] getFieldStringArr() {
        return fieldStringArr;
    }

    public void setFieldStringArr(String[] fieldStringArr) {
        this.fieldStringArr = fieldStringArr;
    }

    public int[] getFieldIntArr() {
        return fieldIntArr;
    }

    public void setFieldIntArr(int[] fieldIntArr) {
        this.fieldIntArr = fieldIntArr;
    }
}
