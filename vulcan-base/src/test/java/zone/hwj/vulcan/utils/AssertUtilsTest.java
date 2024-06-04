package zone.hwj.vulcan.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AssertUtilsTest {

    Object notNullObj = new Object();

    @Test
    void state() {
        assertThrows(IllegalStateException.class, () -> AssertUtils.state(false, "test"));
        assertDoesNotThrow(() -> AssertUtils.state(true, "test"));
    }

    @Test
    void isTrue() {
        assertThrows(IllegalArgumentException.class, () -> AssertUtils.isTrue(false, "test"));
        assertDoesNotThrow(() -> AssertUtils.isTrue(true, "test"));
    }

    @Test
    void isNull() {
        assertThrows(IllegalArgumentException.class, () -> AssertUtils.isNull(notNullObj, "test"));
        assertDoesNotThrow(() -> AssertUtils.isNull(null, "test"));
    }

    @Test
    void notNull() {
        assertThrows(IllegalArgumentException.class, () -> AssertUtils.notNull(null, "test"));
        assertDoesNotThrow(() -> AssertUtils.notNull(notNullObj, "test"));
    }

    @Test
    void hasLength() {
        assertThrows(IllegalArgumentException.class, () -> AssertUtils.hasLength(null, "test"));
        assertThrows(IllegalArgumentException.class, () -> AssertUtils.hasLength("", "test"));
        assertDoesNotThrow(() -> AssertUtils.hasLength("text", "test"));
    }

    @Test
    void notEmpty() {
        assertThrows(IllegalArgumentException.class, () -> AssertUtils.notEmpty((Collection<?>) null, "test"));
        assertThrows(IllegalArgumentException.class, () -> AssertUtils.notEmpty(new ArrayList<>(), "test"));
        assertDoesNotThrow(() -> AssertUtils.notEmpty(Collections.singleton("text"), "test"));

        assertThrows(IllegalArgumentException.class, () -> AssertUtils.notEmpty((Map<?, ?>) null, "test"));
        assertThrows(IllegalArgumentException.class, () -> AssertUtils.notEmpty(new HashMap<>(), "test"));
        Map<String, String> testMap = new HashMap<>();
        testMap.put("k", "v");
        assertDoesNotThrow(() -> AssertUtils.notEmpty(testMap, "test"));
    }
}