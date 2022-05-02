package io.vulcan.api.base;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TripleTest {
    @Test
    public void createAndGet() {

        assertThrows(IllegalArgumentException.class, () -> new Triple<>(null, "test", "test"));
        assertThrows(IllegalArgumentException.class, () -> new Triple<>(null, "test", null));
        assertThrows(IllegalArgumentException.class, () -> new Triple<>("test", null, "test"));
        assertThrows(IllegalArgumentException.class, () -> new Triple<>("test", "test", null));

        Triple<String, Long, String> sl = new Triple<>("test", 2020L, "test2");
        assertEquals(sl.getFirst(), "test");
        assertEquals(sl.getSecond(), 2020L);
        assertEquals(sl.getThird(), "test2");
        assertEquals(sl.toString(), "(test, 2020, test2)");
        assertThrows(IllegalStateException.class, sl::toList);

        Triple<String, String, String> ss = new Triple<>("test s", "test s2", "test s3");
        assertEquals(ss.getFirst(), "test s");
        assertEquals(ss.getSecond(), "test s2");
        assertEquals(ss.getThird(), "test s3");
        assertEquals(ss.toString(), "(test s, test s2, test s3)");
        assertArrayEquals(ss.toList().toArray(new String[0]), new String[] {"test s", "test s2", "test s3"});
    }
}