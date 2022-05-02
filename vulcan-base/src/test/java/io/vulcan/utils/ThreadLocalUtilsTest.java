package io.vulcan.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;
import org.junit.jupiter.api.Test;

public class ThreadLocalUtilsTest {

    @Test
    public void getValue() {
        Object oldValue = ThreadLocalUtils.setValue("test", "Test tl");
        assertNull(oldValue);

        String test = ThreadLocalUtils.getValue("test", String.class);
        assertEquals(test, "Test tl");

        Object oldValue2 = ThreadLocalUtils.setValue("test", 999L);
        assertEquals(oldValue2, "Test tl");

        Long test2 = ThreadLocalUtils.getValue("test", Long.class);
        assertEquals(test2, 999L);

        Object oldValueRemove = ThreadLocalUtils.remove("test");
        assertEquals(oldValueRemove, 999L);
        assertNull(ThreadLocalUtils.getValue("test", String.class));
    }

    @Test
    public void getValueCustomMap() {
        InheritableThreadLocal<Map<Object, Object>> threadBucket = new InheritableThreadLocal<>();

        Object oldValue = ThreadLocalUtils.setValue(threadBucket, "test", "Test tl");
        assertNull(oldValue);

        String test = ThreadLocalUtils.getValue(threadBucket, "test", String.class);
        assertEquals(test, "Test tl");

        Object oldValue2 = ThreadLocalUtils.setValue(threadBucket, "test", 999L);
        assertEquals(oldValue2, "Test tl");

        Long test2 = ThreadLocalUtils.getValue(threadBucket, "test", Long.class);
        assertEquals(test2, 999L);

        Object oldValueRemove = ThreadLocalUtils.remove(threadBucket, "test");
        assertEquals(oldValueRemove, 999L);
        assertNull(ThreadLocalUtils.getValue(threadBucket, "test", String.class));
    }
}