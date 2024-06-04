package zone.hwj.vulcan.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.alibaba.fastjson2.TypeReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonUtilsTest {

    private final TestBean testBean = TestBean.create();

    @Test
    public void jsonTest() {
        String s = JsonUtils.encode(testBean);
        TestBean re = JsonUtils.decode(s, TestBean.class);
        assert re != null;
        assertEquals(re.getField(), testBean.getField());
        assertEquals(re.getPrimField(), testBean.getPrimField());
        assertEquals(re.getListField().get(0), testBean.getListField().get(0));
        assertEquals(re.getMapField().get("testKey1"), testBean.getMapField().get("testKey1"));
    }

    @Test
    public void jsonListTest() {
        List<TestBean> beans = new ArrayList<>();
        beans.add(testBean);
        beans.add(testBean);
        beans.add(testBean);
        String s = JsonUtils.encode(beans);
        List<TestBean> re = JsonUtils.decodeToList(s, TestBean.class);
        assert re != null;
        assertEquals(re.get(0).getField(), testBean.getField());
        assertEquals(re.get(0).getPrimField(), testBean.getPrimField());
        assertEquals(re.get(0).getListField().get(0), testBean.getListField().get(0));
        assertEquals(re.get(0).getMapField().get("testKey1"), testBean.getMapField().get("testKey1"));

        assertEquals(re.get(1).getField(), testBean.getField());
        assertEquals(re.get(1).getPrimField(), testBean.getPrimField());
        assertEquals(re.get(1).getListField().get(0), testBean.getListField().get(0));
        assertEquals(re.get(1).getMapField().get("testKey1"), testBean.getMapField().get("testKey1"));

        assertEquals(re.get(2).getField(), testBean.getField());
        assertEquals(re.get(2).getPrimField(), testBean.getPrimField());
        assertEquals(re.get(2).getListField().get(0), testBean.getListField().get(0));
        assertEquals(re.get(2).getMapField().get("testKey1"), testBean.getMapField().get("testKey1"));

        List<TestBean> rel = JsonUtils.decodeToList(s.getBytes(StandardCharsets.UTF_8), TestBean.class);
        assert rel != null;
        assertEquals(rel.get(0).getField(), testBean.getField());
        assertEquals(rel.get(0).getPrimField(), testBean.getPrimField());
        assertEquals(rel.get(0).getListField().get(0), testBean.getListField().get(0));
        assertEquals(rel.get(0).getMapField().get("testKey1"), testBean.getMapField().get("testKey1"));

        List<TestBean> re2 = JsonUtils.decode(s, new TypeReference<List<TestBean>>() {});
        assert re2 != null;
        assertEquals(re2.get(0).getField(), testBean.getField());
        assertEquals(re2.get(0).getPrimField(), testBean.getPrimField());
        assertEquals(re2.get(0).getListField().get(0), testBean.getListField().get(0));
        assertEquals(re2.get(0).getMapField().get("testKey1"), testBean.getMapField().get("testKey1"));

        List<TestBean> rel2 = JsonUtils.decode(s.getBytes(StandardCharsets.UTF_8), new TypeReference<List<TestBean>>() {});
        assert rel2 != null;
        assertEquals(rel2.get(0).getField(), testBean.getField());
        assertEquals(rel2.get(0).getPrimField(), testBean.getPrimField());
        assertEquals(rel2.get(0).getListField().get(0), testBean.getListField().get(0));
        assertEquals(rel2.get(0).getMapField().get("testKey1"), testBean.getMapField().get("testKey1"));
    }

    public static class TestBean {

        String field;
        int primField;
        Map<String, Object> mapField;
        List<String> listField;
        List<String> anotherList;

        public static TestBean create() {
            TestBean testBean = new TestBean();
            testBean.setField("test string field");
            List<String> list = new ArrayList<>();
            list.add("test list 1");
            list.add("test list 2");
            testBean.setListField(list);
            Map<String, Object> map = new HashMap<String, Object>() {{
                put("testKey1", "test map 1");
                put("testKey2", "test map 2");
            }};
            List<String> anotherList = new ArrayList<>();
            list.add("another 1");
            list.add("another 2");
            testBean.setAnotherList(anotherList);
            testBean.setMapField(map);
            testBean.setPrimField(4321);
            return testBean;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public int getPrimField() {
            return primField;
        }

        public void setPrimField(int primField) {
            this.primField = primField;
        }

        public Map<String, Object> getMapField() {
            return mapField;
        }

        public void setMapField(Map<String, Object> mapField) {
            this.mapField = mapField;
        }

        public List<String> getListField() {
            return listField;
        }

        public void setListField(List<String> listField) {
            this.listField = listField;
        }

        public List<String> getAnotherList() {
            return anotherList;
        }

        public void setAnotherList(List<String> anotherList) {
            this.anotherList = anotherList;
        }
    }
}