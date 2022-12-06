package io.vulcan.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
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

    @Test
    void testPatternReplace() {
        String template = "您的“${v: ${1}}”,车牌${2}, ${test-value}, ${no-value}";
        Map<String, Object> contentParamMap = new HashMap<>();
        contentParamMap.put("1", "奥迪");
        contentParamMap.put("2", "沪A0001");
        contentParamMap.put("test-value", "here is a value");

        String result = StringUtils.patternReplace(template, Pattern.compile("\\$\\{([^$^{}]*?)}"), contentParamMap::get);

        assertEquals("您的“${v: 奥迪}”,车牌沪A0001, here is a value, no-value", result);

        assertThrows(IllegalArgumentException.class, () -> StringUtils.patternReplace(template, Pattern.compile("\\$\\{[^$^{}]*?}"), contentParamMap::get));
    }
}