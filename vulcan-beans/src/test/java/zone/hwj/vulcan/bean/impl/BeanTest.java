package zone.hwj.vulcan.bean.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import zone.hwj.vulcan.bean.Bean;
import zone.hwj.vulcan.bean.impl.types.TestDistFromParent;
import zone.hwj.vulcan.bean.impl.types.TestBeanChild;
import zone.hwj.vulcan.bean.impl.types.TestBeanDist;
import zone.hwj.vulcan.bean.impl.types.TestBeanParent;
import zone.hwj.vulcan.bean.impl.types.TestDistFromChild;
import zone.hwj.vulcan.bean.impl.types.TestLongProp;
import zone.hwj.vulcan.bean.impl.types.TestLongPropDist;
import zone.hwj.vulcan.bean.impl.types.TestSrcIntoChild;
import zone.hwj.vulcan.bean.impl.types.TestSrcIntoParent;
import zone.hwj.vulcan.bean.impl.types.TestTypes;
import zone.hwj.vulcan.bean.impl.types.TestTypesConv;
import zone.hwj.vulcan.bean.impl.types.TestTypesDist;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BeanTest {

    private static final Logger log = LoggerFactory.getLogger(BeanTest.class);

    private final Bean beanManager = Bean.getDefault();

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
    static void setUp() {
        bean.setFieldA("field-a");
        bean.setFieldB("field-b");
        bean.setFieldC("field-c");
        bean.setFieldD(new TestTypes(dateForTest));

    }

    @Test
    public void beanToBeanTest() {

        TestBeanDist dist = beanManager.beanToBean(bean, TestBeanDist.class);
        assertNotNull(dist);
        assertEquals(bean.getFieldA(), dist.getFieldA());
        assertEquals(bean.getFieldB(), dist.getFieldB());
        assertEquals(bean.getFieldC(), dist.getFieldC());
        assertEquals(bean.getFieldD().getFieldDate().getTime(), dist.getFieldD().getFieldDate().getTime());

        TestBeanDist testSameDest = new TestBeanDist();
        TestBeanDist testSameDestResult = beanManager.beanToBean(bean, testSameDest);
        assertSame(testSameDest, testSameDestResult);
        assertEquals(bean.getFieldA(), testSameDest.getFieldA());
        assertEquals(bean.getFieldB(), testSameDest.getFieldB());
        assertEquals(bean.getFieldC(), testSameDest.getFieldC());
        assertEquals(bean.getFieldD().getFieldDate().getTime(), testSameDest.getFieldD().getFieldDate().getTime());

        TestBeanParent parentBean = new TestBeanParent();
        parentBean.setFieldA("field-a-p");
        parentBean.setFieldB("field-b-p");

        TestDistFromParent fromParent = new TestDistFromParent();
        beanManager.beanToBean(bean, fromParent);
        assertEquals(bean.getFieldA(), fromParent.getFieldA());
        assertEquals(bean.getFieldB(), fromParent.getFieldB());
        assertNull(fromParent.getFieldC());
        assertNull(fromParent.getFieldD());

        beanManager.beanToBean(parentBean, fromParent);
        assertEquals(parentBean.getFieldA(), fromParent.getFieldA());
        assertEquals(parentBean.getFieldB(), fromParent.getFieldB());
        assertNull(fromParent.getFieldC());
        assertNull(fromParent.getFieldD());

        TestDistFromChild fromChild = new TestDistFromChild();
        beanManager.beanToBean(bean, fromChild);
        assertEquals(bean.getFieldA(), fromChild.getFieldA());
        assertEquals(bean.getFieldB(), fromChild.getFieldB());
        assertEquals(bean.getFieldC(), fromChild.getFieldC());
        assertEquals(bean.getFieldD().getFieldDate().getTime(), fromChild.getFieldD().getFieldDate().getTime());

        beanManager.beanToBean(parentBean, fromChild);
        assertEquals(parentBean.getFieldA(), fromChild.getFieldA());
        assertEquals(parentBean.getFieldB(), fromChild.getFieldB());
        assertEquals(bean.getFieldC(), fromChild.getFieldC());
        assertEquals(bean.getFieldD().getFieldDate().getTime(), fromChild.getFieldD().getFieldDate().getTime());

        TestBeanParent parent;
        TestBeanChild child;
        TestSrcIntoParent intoParent = new TestSrcIntoParent(bean);
        child = new TestBeanChild();
        beanManager.beanToBean(intoParent, child);
        assertEquals(bean.getFieldA(), child.getFieldA());
        assertEquals(bean.getFieldB(), child.getFieldB());
        assertNull(child.getFieldC());
        assertNull(child.getFieldD());

        parent = new TestBeanParent();
        beanManager.beanToBean(intoParent, parent);
        assertEquals(bean.getFieldA(), parent.getFieldA());
        assertEquals(bean.getFieldB(), parent.getFieldB());

        TestSrcIntoChild intoChild = new TestSrcIntoChild(bean);
        child = new TestBeanChild();
        beanManager.beanToBean(intoChild, child);
        assertEquals(bean.getFieldA(), child.getFieldA());
        assertEquals(bean.getFieldB(), child.getFieldB());
        assertEquals(bean.getFieldC(), child.getFieldC());
        assertEquals(bean.getFieldD().getFieldDate().getTime(), child.getFieldD().getFieldDate().getTime());

        parent = new TestBeanParent();
        beanManager.beanToBean(intoChild, parent);
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

        TestTypesDist typeDist = beanManager.beanToBean(typeSrc, TestTypesDist.class);
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
    public void beanToBeanTypesConv() {
        Date now = new Date();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        Date fieldDateValue = Date.from(Instant.from(formatter
                .parse("2023-06-01T00:00:00", LocalDateTime::from).atZone(ZoneId.systemDefault())));
        String dateString = formatter.format(
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(now.getTime()), ZoneId.systemDefault()));

        TestTypesConv typeSrc = new TestTypesConv();
        typeSrc.setFieldDate("2023-06-01T00:00:00");
        typeSrc.setFieldString(now);
        typeSrc.setFieldLong("20");
        typeSrc.setFieldInt(10);
        typeSrc.setFieldLongBox(30L);
        typeSrc.setFieldStringArr(new String[]{"test_str_string_0", "test_str_string_1"});
        typeSrc.setFieldIntArr(new int[]{0, 1});

//        BeanConverterHelper.INSTANCE.saveClassFile(TestTypesConv.class, TestTypesDist.class, "temp");
        TestTypesDist typeDist = beanManager.beanToBean(typeSrc, TestTypesDist.class);
        assertNotNull(typeDist);
        assertEquals(fieldDateValue.getTime(), typeDist.getFieldDate().getTime());
        assertEquals(dateString, typeDist.getFieldString());
        assertEquals(20L, typeDist.getFieldLong());
        assertEquals(10, typeDist.getFieldInt());
        assertEquals(30L, typeDist.getFieldLongBox());
        assertEquals("test_str_string_0", typeDist.getFieldStringArr()[0]);
        assertEquals("test_str_string_1", typeDist.getFieldStringArr()[1]);
        assertEquals(0, typeDist.getFieldIntArr()[0]);
        assertEquals(1, typeDist.getFieldIntArr()[1]);
    }

    @Test
    public void mapToBeanTest() {
        Map<String, Object> testMap = new HashMap<>();
        Date now = new Date();
        testMap.put("fieldDate", now);
        testMap.put("fieldString", "test_string_filed");
        testMap.put("fieldLong", 20L);
        testMap.put("fieldInt", 10);
        testMap.put("fieldLongBox", 30L);
        testMap.put("fieldStringArr", new String[]{"test_str_string_0", "test_str_string_1"});
        testMap.put("fieldIntArr", new int[]{0, 1});
        TestTypes testTypes = beanManager.mapToBean(testMap, TestTypes.class);

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
        TestTypes testSameInstResult = beanManager.mapToBean(testMap, testSameInst);
        assertSame(testSameInst, testSameInstResult);
        assertEquals(now.getTime(), testSameInst.getFieldDate().getTime());

        Map<String, Object> testConvertMap = new HashMap<>();
        testConvertMap.put("fieldDate", LocalDateTime.ofInstant(Instant.ofEpochMilli(now.getTime()), ZoneId.systemDefault()));
        TestTypes testConvertDate = beanManager.mapToBean(testConvertMap, TestTypes.class);
        assertNotNull(testConvertDate);
        assertEquals(now.getTime(), testConvertDate.getFieldDate().getTime());

        Map<String, Object> testConvertLongMap = new HashMap<>();
        testConvertLongMap.put("fieldDate", now.getTime());
        TestTypes testConvertLongDate = beanManager.mapToBean(testConvertLongMap, TestTypes.class);
        assertNotNull(testConvertLongDate);
        assertEquals(now.getTime(), testConvertLongDate.getFieldDate().getTime());
    }

    @Test
    public void serializeTest() throws Exception {
        TestBeanChild bean = beanManager.mapToBean(testMap, TestBeanChild.class);
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
        beanManager.speedup(TestTypes.class, TestTypesDist.class);
        long warmDelta = System.nanoTime() - warmStart;
        System.out.println("bean预热耗时: " + TimeUnit.NANOSECONDS.toMillis(warmDelta));
        TestTypesDist typeDist = null;
        long newStart = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            typeDist = beanManager.beanToBean(typeSrc, TestTypesDist.class);
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
            typeDistOld = ((BeanImpl)beanManager).beanToBeanOld(typeSrc, TestTypesDist.class);
        }
        long oldDelta = System.nanoTime() - oldStart;
        System.out.println("bean老方法耗时: " + TimeUnit.NANOSECONDS.toMillis(oldDelta));

        assertNotNull(typeDistOld);
        assertEquals(now.getTime(), typeDistOld.getFieldDate().getTime());
        assertEquals("test_string_filed", typeDistOld.getFieldString());
//        assertEquals(20L, typeDistOld.getFieldLong());
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
        beanManager.speedup(TestTypes.class);
        long warmDelta = System.nanoTime() - warmStart;
        System.out.println("map预热耗时: " + TimeUnit.NANOSECONDS.toMillis(warmDelta));
        TestTypes testTypes = null;
        long newStart = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            testTypes = beanManager.mapToBean(testMap, TestTypes.class);
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
            testTypesOld = ((BeanImpl)beanManager).mapToBeanOld(testMap, TestTypes.class);
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

        TestLongPropDist mapDist = beanManager.mapToBean(map, TestLongPropDist.class);
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

        TestLongPropDist beanDist = beanManager.beanToBean(longProp, TestLongPropDist.class);
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

    @Test
    public void beanToMapTest() {
        Map<String, Object> map = beanManager.beanToMap(bean);
        log.info(map.toString());
        assertEquals(bean.getFieldA(), map.get("fieldA"));
        assertEquals(bean.getFieldB(), map.get("fieldB"));
        assertEquals(bean.getFieldC(), map.get("fieldC"));
        assertEquals(bean.getFieldD().getFieldDate().getTime(), ((TestTypes)map.get("fieldD")).getFieldDate().getTime());

        TestLongProp longProp = new TestLongProp();
        Map<String, Object> longMap = beanManager.beanToMap(longProp);
        log.info(longMap.toString());

        assertEquals(4L, longMap.get("long4"));
        assertEquals(5L, longMap.get("long5"));
        assertEquals(6L, longMap.get("long6"));
        assertEquals(7L, longMap.get("long7"));
        assertEquals(8L, longMap.get("long8"));
        assertEquals(9L, longMap.get("long9"));
        assertEquals(1, longMap.get("int1"));
        assertEquals((byte)2, longMap.get("byte1"));
        assertEquals('2', longMap.get("char1"));
        assertEquals(2.0f, longMap.get("float1"));
        assertEquals(4.1d, longMap.get("double1"));
        assertEquals(4.2d, longMap.get("double2"));
        assertEquals(4.3d, longMap.get("double3"));
        assertEquals(4.4d, longMap.get("double4"));
        assertEquals(4.5d, longMap.get("double5"));
        assertEquals(4.6d, longMap.get("double6"));
    }
}