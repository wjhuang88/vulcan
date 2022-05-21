package io.vulcan.eventbus;

import static org.junit.jupiter.api.Assertions.*;

import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EventBusTest {

    EventBus eventBus = new EventBus();

    static Map<String, String> testMap = new HashMap<>();
    static List<String> testList = new ArrayList<>();
    static TestData testObject = new TestData();

    @BeforeAll
    static void beforeAll() {
        testMap.put("k1", "v1");
        testMap.put("k2", "v2");
        testMap.put("k3", "v3");

        testList.add("l1");
        testList.add("l2");
        testList.add("l3");

        testObject.setLongValue(99L);
        testObject.setStringValue("o1");
    }

    @Test
    void basicTest() throws Throwable {
        assertTrue(eventBus.hasSchema("local"));
        assertThrows(RuntimeException.class, () -> {
            Future<String> publish = eventBus.publish("test", String.class, "test");
            assertNull(publish.get());
        });

        VertxTestContext testContext = new VertxTestContext();
        eventBus.listen("test-string", String.class, value -> {
            System.out.println("router: test-string");
            assertEquals("test", value);
            testContext.failNow("Should not run here");
        });

        eventBus.close();
        eventBus.send("test-string", String.class, "test");

        Assertions.assertFalse(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    @Test
    void testString() throws Throwable {
        VertxTestContext testContext = new VertxTestContext();
        eventBus.listen("test-string", String.class, value -> {
            System.out.println("router: test-string");
            assertEquals("test", value);
            testContext.completeNow();
        });

        eventBus.send("test-string", String.class, "test");

        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    @Test
    void listenAndSend() throws Throwable {
        VertxTestContext testContext = new VertxTestContext();
        eventBus.listen("local://test-local", List.class, list -> {
            System.out.println("router: local://test-local");
            assertEquals("l1", list.get(0));
            assertEquals("l2", list.get(1));
            assertEquals("l3", list.get(2));
            testContext.completeNow();
        });

        eventBus.send("local://test-local", List.class, testList);
        @SuppressWarnings("rawtypes")
        Future<List> send = eventBus.send("local://test-local_no_listener", List.class, testList);
        assertEquals("l1", send.get().get(0));

        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    @Test
    void listenAndSendNoSchema() throws Throwable {
        VertxTestContext testContext = new VertxTestContext();
        eventBus.listen("test-local-no-schema", Map.class, map -> {
            System.out.println("router: test-local-no-schema");
            assertEquals("v1", map.get("k1"));
            assertEquals("v2", map.get("k2"));
            assertEquals("v3", map.get("k3"));
            testContext.completeNow();
        });

        eventBus.send("test-local-no-schema", Map.class, testMap);
        eventBus.send("test-local-no-schema_no_listener", Map.class, testMap);

        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    @Test
    void listenAndSendObject() throws Throwable {
        VertxTestContext testContext = new VertxTestContext();
        eventBus.listen("test-local-object", TestData.class, data -> {
            System.out.println("router: test-local-object");
            assertEquals(99, data.getLongValue());
            assertEquals("o1", data.getStringValue());
            testContext.completeNow();
        });

        eventBus.send("test-local-object", TestData.class, testObject);
        eventBus.send("test-local-object_no_listener", TestData.class, testObject);

        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    static class TestData {
        long longValue;
        String stringValue;

        public long getLongValue() {
            return longValue;
        }

        public void setLongValue(long longValue) {
            this.longValue = longValue;
        }

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }
    }
}