package io.vulcan.bean;

import static org.junit.jupiter.api.Assertions.*;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import io.vulcan.bean.types.TestDistFromParent;
import io.vulcan.bean.types.TestBeanChild;
import io.vulcan.bean.types.TestBeanDist;
import io.vulcan.bean.types.TestBeanParent;
import io.vulcan.bean.types.TestDistFromChild;
import io.vulcan.bean.types.TestLongProp;
import io.vulcan.bean.types.TestLongPropDist;
import io.vulcan.bean.types.TestSrcIntoChild;
import io.vulcan.bean.types.TestSrcIntoParent;
import io.vulcan.bean.types.TestTypes;
import io.vulcan.bean.types.TestTypesDist;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BeanUtilsTest {

    private final static Date dateForTest;

    static {
        try {
            dateForTest = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .parse("2020-11-23 13:34:54");
        } catch (ParseException e) {
            throw new RuntimeException("Date init fail.");
        }
    }

    private final Map<String, Object> testMap = new HashMap<String, Object>() {{
        put("fieldA", "field-a");
        put("fieldB", "field-b");
        put("fieldC", "field-c");
        put("filedD", new TestTypes(dateForTest));
    }};

    private static final TestBeanChild bean = new TestBeanChild();

    @BeforeAll
    static void setUp() throws Exception {
        bean.setFieldA("field-a");
        bean.setFieldB("field-b");
        bean.setFieldC("field-c");
        bean.setFieldD(new TestTypes(dateForTest));

    }

    @Test
    public void beanToBeanTest() throws Exception {

        TestBeanDist dist = BeanUtils.beanToBean(bean, TestBeanDist.class);
        assertNotNull(dist);
        assertEquals(bean.getFieldA(), dist.getFieldA());
        assertEquals(bean.getFieldB(), dist.getFieldB());
        assertEquals(bean.getFieldC(), dist.getFieldC());
        assertEquals(bean.getFieldD().getFieldDate().getTime(), dist.getFieldD().getFieldDate().getTime());

        TestBeanDist testSameDest = new TestBeanDist();
        TestBeanDist testSameDestResult = BeanUtils.beanToBean(bean, testSameDest);
        assertSame(testSameDest, testSameDestResult);
        assertEquals(bean.getFieldA(), testSameDest.getFieldA());
        assertEquals(bean.getFieldB(), testSameDest.getFieldB());
        assertEquals(bean.getFieldC(), testSameDest.getFieldC());
        assertEquals(bean.getFieldD().getFieldDate().getTime(), testSameDest.getFieldD().getFieldDate().getTime());

        TestBeanParent parentBean = new TestBeanParent();
        parentBean.setFieldA("field-a-p");
        parentBean.setFieldB("field-b-p");

        TestDistFromParent fromParent = new TestDistFromParent();
        BeanUtils.beanToBean(bean, fromParent);
        assertEquals(bean.getFieldA(), fromParent.getFieldA());
        assertEquals(bean.getFieldB(), fromParent.getFieldB());
        assertNull(fromParent.getFieldC());
        assertNull(fromParent.getFieldD());

        BeanUtils.beanToBean(parentBean, fromParent);
        assertEquals(parentBean.getFieldA(), fromParent.getFieldA());
        assertEquals(parentBean.getFieldB(), fromParent.getFieldB());
        assertNull(fromParent.getFieldC());
        assertNull(fromParent.getFieldD());

        TestDistFromChild fromChild = new TestDistFromChild();
        BeanUtils.beanToBean(bean, fromChild);
        assertEquals(bean.getFieldA(), fromChild.getFieldA());
        assertEquals(bean.getFieldB(), fromChild.getFieldB());
        assertEquals(bean.getFieldC(), fromChild.getFieldC());
        assertEquals(bean.getFieldD().getFieldDate().getTime(), fromChild.getFieldD().getFieldDate().getTime());

        BeanUtils.beanToBean(parentBean, fromChild);
        assertEquals(parentBean.getFieldA(), fromChild.getFieldA());
        assertEquals(parentBean.getFieldB(), fromChild.getFieldB());
        assertEquals(bean.getFieldC(), fromChild.getFieldC());
        assertEquals(bean.getFieldD().getFieldDate().getTime(), fromChild.getFieldD().getFieldDate().getTime());

        TestBeanParent parent;
        TestBeanChild child;
        TestSrcIntoParent intoParent = new TestSrcIntoParent(bean);
        child = new TestBeanChild();
        BeanUtils.beanToBean(intoParent, child);
        assertEquals(bean.getFieldA(), child.getFieldA());
        assertEquals(bean.getFieldB(), child.getFieldB());
        assertNull(child.getFieldC());
        assertNull(child.getFieldD());

        parent = new TestBeanParent();
        BeanUtils.beanToBean(intoParent, parent);
        assertEquals(bean.getFieldA(), parent.getFieldA());
        assertEquals(bean.getFieldB(), parent.getFieldB());

        TestSrcIntoChild intoChild = new TestSrcIntoChild(bean);
        child = new TestBeanChild();
        BeanUtils.beanToBean(intoChild, child);
        assertEquals(bean.getFieldA(), child.getFieldA());
        assertEquals(bean.getFieldB(), child.getFieldB());
        assertEquals(bean.getFieldC(), child.getFieldC());
        assertEquals(bean.getFieldD().getFieldDate().getTime(), child.getFieldD().getFieldDate().getTime());

        parent = new TestBeanParent();
        BeanUtils.beanToBean(intoChild, parent);
        assertEquals(bean.getFieldA(), parent.getFieldA());
        assertEquals(bean.getFieldB(), parent.getFieldB());
    }

    @Test
    public void beanToBeanTypes() {
        Date now = new Date();
        TestTypes typeSrc = new TestTypes();
        typeSrc.setFieldDate(now);
        typeSrc.setFieldString("test_string_filed");
        typeSrc.setFieldLong(20L);
        typeSrc.setFieldInt(10);
        typeSrc.setFieldLongBox(30L);
        typeSrc.setFieldStringArr(new String[]{"test_str_string_0", "test_str_string_1"});
        typeSrc.setFieldIntArr(new int[]{0, 1});

        TestTypesDist typeDist = BeanUtils.beanToBean(typeSrc, TestTypesDist.class);
        assertNotNull(typeDist);
        assertEquals(now.getTime(), typeDist.getFieldDate().getTime());
        assertEquals("test_string_filed", typeDist.getFieldString());
        assertEquals(20L, typeDist.getFieldLong());
        assertEquals(10, typeDist.getFieldInt());
        assertEquals(30L, typeDist.getFieldLongBox());
        assertEquals("test_str_string_0", typeDist.getFieldStringArr()[0]);
        assertEquals("test_str_string_1", typeDist.getFieldStringArr()[1]);
        assertEquals(0, typeDist.getFieldIntArr()[0]);
        assertEquals(1, typeDist.getFieldIntArr()[1]);
    }

    @Test
    public void mapToBeanTest() throws Exception {
        Map<String, Object> testMap = new HashMap<>();
        Date now = new Date();
        testMap.put("fieldDate", now);
        testMap.put("fieldString", "test_string_filed");
        testMap.put("fieldLong", 20L);
        testMap.put("fieldInt", 10);
        testMap.put("fieldLongBox", 30L);
        testMap.put("fieldStringArr", new String[]{"test_str_string_0", "test_str_string_1"});
        testMap.put("fieldIntArr", new int[]{0, 1});
        TestTypes testTypes = BeanUtils.mapToBean(testMap, TestTypes.class);

        assertNotNull(testTypes);
        assertEquals(now.getTime(), testTypes.getFieldDate().getTime());
        assertEquals("test_string_filed", testTypes.getFieldString());
        assertEquals(20L, testTypes.getFieldLong());
        assertEquals(10, testTypes.getFieldInt());
        assertEquals(30L, testTypes.getFieldLongBox());
        assertEquals("test_str_string_0", testTypes.getFieldStringArr()[0]);
        assertEquals("test_str_string_1", testTypes.getFieldStringArr()[1]);
        assertEquals(0, testTypes.getFieldIntArr()[0]);
        assertEquals(1, testTypes.getFieldIntArr()[1]);

        TestTypes testSameInst = new TestTypes();
        TestTypes testSameInstResult = BeanUtils.mapToBean(testMap, testSameInst);
        assertSame(testSameInst, testSameInstResult);
        assertEquals(now.getTime(), testSameInst.getFieldDate().getTime());

        Map<String, Object> testConvertMap = new HashMap<>();
        testConvertMap.put("fieldDate", LocalDateTime.ofInstant(Instant.ofEpochMilli(now.getTime()), ZoneId.systemDefault()));
        TestTypes testConvertDate = BeanUtils.mapToBean(testConvertMap, TestTypes.class);
        assertNotNull(testConvertDate);
        assertEquals(now.getTime(), testConvertDate.getFieldDate().getTime());

        Map<String, Object> testConvertLongMap = new HashMap<>();
        testConvertLongMap.put("fieldDate", now.getTime());
        TestTypes testConvertLongDate = BeanUtils.mapToBean(testConvertLongMap, TestTypes.class);
        assertNotNull(testConvertLongDate);
        assertEquals(now.getTime(), testConvertLongDate.getFieldDate().getTime());
    }

    @Test
    public void serializeTest() throws Exception {
        TestBeanChild bean = BeanUtils.mapToBean(testMap, TestBeanChild.class);
        assertNotNull(bean);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(os);
        ho.writeObject(bean);
        byte[] dist = os.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(dist);
        HessianInput hi = new HessianInput(is);
        TestBeanChild hessianBean = (TestBeanChild) hi.readObject();
        assertEquals(testMap.get("fieldA"), hessianBean.getFieldA());
        assertEquals(testMap.get("fieldA"), bean.getFieldA());
        assertEquals(bean.getFieldA(), hessianBean.getFieldA());
    }

    @Test
    public void beanBenchmark() {
        Date now = new Date();
        TestTypes typeSrc = new TestTypes();
        typeSrc.setFieldDate(now);
        typeSrc.setFieldString("test_string_filed");
        typeSrc.setFieldLong(20L);
        typeSrc.setFieldInt(10);
        typeSrc.setFieldLongBox(30L);
        typeSrc.setFieldStringArr(new String[]{"test_str_string_0", "test_str_string_1"});
        typeSrc.setFieldIntArr(new int[]{0, 1});

        long warmStart = System.nanoTime();
        BeanUtils.speedup(TestTypes.class, TestTypesDist.class);
        long warmDelta = System.nanoTime() - warmStart;
        System.out.println("bean预热耗时: " + TimeUnit.NANOSECONDS.toMillis(warmDelta));
        TestTypesDist typeDist = null;
        long newStart = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            typeDist = BeanUtils.beanToBean(typeSrc, TestTypesDist.class);
        }
        long newDelta = System.nanoTime() - newStart;
        System.out.println("bean新方法耗时: " + TimeUnit.NANOSECONDS.toMillis(newDelta));

        assertNotNull(typeDist);
        assertEquals(now.getTime(), typeDist.getFieldDate().getTime());
        assertEquals("test_string_filed", typeDist.getFieldString());
        assertEquals(20L, typeDist.getFieldLong());
        assertEquals(10, typeDist.getFieldInt());
        assertEquals(30L, typeDist.getFieldLongBox());
        assertEquals("test_str_string_0", typeDist.getFieldStringArr()[0]);
        assertEquals("test_str_string_1", typeDist.getFieldStringArr()[1]);
        assertEquals(0, typeDist.getFieldIntArr()[0]);
        assertEquals(1, typeDist.getFieldIntArr()[1]);

        TestTypesDist typeDistOld = null;
        long oldStart = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            typeDistOld = BeanUtils.beanToBeanOld(typeSrc, TestTypesDist.class);
        }
        long oldDelta = System.nanoTime() - oldStart;
        System.out.println("bean老方法耗时: " + TimeUnit.NANOSECONDS.toMillis(oldDelta));

        assertNotNull(typeDistOld);
        assertEquals(now.getTime(), typeDistOld.getFieldDate().getTime());
        assertEquals("test_string_filed", typeDistOld.getFieldString());
        assertEquals(20L, typeDistOld.getFieldLong());
        assertEquals(10, typeDistOld.getFieldInt());
        assertEquals(30L, typeDistOld.getFieldLongBox());
        assertEquals("test_str_string_0", typeDistOld.getFieldStringArr()[0]);
        assertEquals("test_str_string_1", typeDistOld.getFieldStringArr()[1]);
        assertEquals(0, typeDistOld.getFieldIntArr()[0]);
        assertEquals(1, typeDistOld.getFieldIntArr()[1]);

        assertTrue(newDelta < oldDelta);
    }

    @Test
    public void mapBenchmark() {
        Map<String, Object> testMap = new HashMap<>();
        Date now = new Date();
        testMap.put("fieldDate", now);
        testMap.put("fieldString", "test_string_filed");
        testMap.put("fieldLong", 20L);
        testMap.put("fieldInt", 10);
        testMap.put("fieldLongBox", 30L);
        testMap.put("fieldStringArr", new String[]{"test_str_string_0", "test_str_string_1"});
        testMap.put("fieldIntArr", new int[]{0, 1});

        long warmStart = System.nanoTime();
        BeanUtils.speedup(TestTypes.class);
        long warmDelta = System.nanoTime() - warmStart;
        System.out.println("map预热耗时: " + TimeUnit.NANOSECONDS.toMillis(warmDelta));
        TestTypes testTypes = null;
        long newStart = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            testTypes = BeanUtils.mapToBean(testMap, TestTypes.class);
        }
        long newDelta = System.nanoTime() - newStart;
        System.out.println("map新方法耗时: " + TimeUnit.NANOSECONDS.toMillis(newDelta));

        assertNotNull(testTypes);
        assertEquals(now.getTime(), testTypes.getFieldDate().getTime());
        assertEquals("test_string_filed", testTypes.getFieldString());
        assertEquals(20L, testTypes.getFieldLong());
        assertEquals(10, testTypes.getFieldInt());
        assertEquals(30L, testTypes.getFieldLongBox());
        assertEquals("test_str_string_0", testTypes.getFieldStringArr()[0]);
        assertEquals("test_str_string_1", testTypes.getFieldStringArr()[1]);
        assertEquals(0, testTypes.getFieldIntArr()[0]);
        assertEquals(1, testTypes.getFieldIntArr()[1]);

        TestTypes testTypesOld = null;
        long oldStart = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            testTypesOld = BeanUtils.mapToBeanOld(testMap, TestTypes.class);
        }
        long oldDelta = System.nanoTime() - oldStart;
        System.out.println("map老方法耗时: " + TimeUnit.NANOSECONDS.toMillis(oldDelta));

        assertNotNull(testTypesOld);
        assertEquals(now.getTime(), testTypesOld.getFieldDate().getTime());
        assertEquals("test_string_filed", testTypesOld.getFieldString());
        assertEquals(20L, testTypesOld.getFieldLong());
        assertEquals(10, testTypesOld.getFieldInt());
        assertEquals(30L, testTypesOld.getFieldLongBox());
        assertEquals("test_str_string_0", testTypesOld.getFieldStringArr()[0]);
        assertEquals("test_str_string_1", testTypesOld.getFieldStringArr()[1]);
        assertEquals(0, testTypesOld.getFieldIntArr()[0]);
        assertEquals(1, testTypesOld.getFieldIntArr()[1]);

        assertTrue(newDelta < oldDelta);
    }

    @Test
    public void testLongProp() {
        TestLongProp longProp = new TestLongProp();
        Map<String, Object> map = TestLongProp.makeMap();

        TestLongPropDist mapDist = BeanUtils.mapToBean(map, TestLongPropDist.class);
        assertNotNull(mapDist);
        assertEquals(0L, mapDist.getLong1());
        assertEquals(2L, mapDist.getLong2());
        assertEquals(3L, mapDist.getLong3());
        assertEquals(4L, mapDist.getLong4());
        assertEquals(5L, mapDist.getLong5());
        assertEquals(6L, mapDist.getLong6());
        assertEquals(7L, mapDist.getLong7());
        assertEquals(8L, mapDist.getLong8());
        assertEquals(9L, mapDist.getLong9());
        assertEquals(1, mapDist.getInt1());
        assertEquals(2, mapDist.getByte1());
        assertEquals('2', mapDist.getChar1());
        assertEquals(2.0, mapDist.getFloat1());
        assertEquals(4.1, mapDist.getDouble1());
        assertEquals(4.2, mapDist.getDouble2());
        assertEquals(4.3, mapDist.getDouble3());
        assertEquals(4.4, mapDist.getDouble4());
        assertEquals(4.5, mapDist.getDouble5());
        assertEquals(4.6, mapDist.getDouble6());

        TestLongPropDist beanDist = BeanUtils.beanToBean(longProp, TestLongPropDist.class);
        assertNotNull(beanDist);
        assertEquals(0L, beanDist.getLong1());
        assertEquals(2L, beanDist.getLong2());
        assertEquals(3L, beanDist.getLong3());
        assertEquals(4L, beanDist.getLong4());
        assertEquals(5L, beanDist.getLong5());
        assertEquals(6L, beanDist.getLong6());
        assertEquals(7L, beanDist.getLong7());
        assertEquals(8L, beanDist.getLong8());
        assertEquals(9L, beanDist.getLong9());
        assertEquals(1, beanDist.getInt1());
        assertEquals(2, beanDist.getByte1());
        assertEquals('2', beanDist.getChar1());
        assertEquals(2.0, beanDist.getFloat1());
        assertEquals(4.1, beanDist.getDouble1());
        assertEquals(4.2, beanDist.getDouble2());
        assertEquals(4.3, beanDist.getDouble3());
        assertEquals(4.4, beanDist.getDouble4());
        assertEquals(4.5, beanDist.getDouble5());
        assertEquals(4.6, beanDist.getDouble6());
    }
}