package io.vulcan.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void join() {
        List<String> list = new ArrayList<>();
        list.add("test_a");
        list.add("test_a1");
        list.add("test_a2");

        assertEquals("test, test1, test2", StringUtils.joinOn(", ").join("test", "test1", "test2"));
        assertEquals("test_a, test_a1, test_a2", StringUtils.joinOn(", ").join(list));

        assertEquals("test,testb,testc", StringUtils.join("test", "testb", "testc"));
        assertEquals("test_a,test_a1,test_a2", StringUtils.join(list));
        assertEquals("test_a|test_a1|test_a2", StringUtils.join("|", list));

        assertEquals("", StringUtils.join((String[]) null));
        assertEquals("", StringUtils.join((List<String>) null));
        assertEquals("", StringUtils.join("|", null));

        assertEquals("", StringUtils.join());
        assertEquals("", StringUtils.join(new ArrayList<>()));
        assertEquals("", StringUtils.join("|", new ArrayList<>()));

        assertEquals("test_single", StringUtils.join("test_single"));
        assertEquals("test_single_l", StringUtils.join(Collections.singleton("test_single_l")));
        assertEquals("test_single_l", StringUtils.join("|", Collections.singleton("test_single_l")));
    }

    @Test
    void hasLength() {
        assertFalse(StringUtils.hasLength(null));
        assertFalse(StringUtils.hasLength(""));
        assertTrue(StringUtils.hasLength("1"));
    }

    @Test
    void isNullOrEmpty() {
        assertTrue(StringUtils.isNullOrEmpty(null));
        assertTrue(StringUtils.isNullOrEmpty(""));
        assertFalse(StringUtils.isNullOrEmpty("1"));
    }
}