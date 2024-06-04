package zone.hwj.vulcan.api.base;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PairTest {

    @Test
    public void createAndGet() {

        assertThrows(IllegalArgumentException.class, () -> new Pair<>(null, "test"));
        assertThrows(IllegalArgumentException.class, () -> new Pair<>("test", null));

        Pair<String, Long> sl = new Pair<>("test", 2020L);
        assertEquals(sl.getFirst(), "test");
        assertEquals(sl.getSecond(), 2020L);
        assertEquals(sl.toString(), "(test, 2020)");
        assertThrows(IllegalStateException.class, sl::toList);

        Pair<String, String> ss = new Pair<>("test s", "test s2");
        assertEquals(ss.getFirst(), "test s");
        assertEquals(ss.getSecond(), "test s2");
        assertEquals(ss.toString(), "(test s, test s2)");
        assertArrayEquals(ss.toList().toArray(new String[0]), new String[] {"test s", "test s2"});
    }
}