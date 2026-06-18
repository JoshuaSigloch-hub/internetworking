package apps;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class SensorClientTest {

    @Test
    void randomInRangeReturnsValueInsideGivenRange() throws Exception {
        Method randomInRange = SensorClient.class.getDeclaredMethod("randomInRange", double.class, double.class);
        randomInRange.setAccessible(true);

        double min = 6.5;
        double max = 8.5;

        for (int i = 0; i < 100; i++) {
            double value = (double) randomInRange.invoke(null, min, max);
            assertTrue(value >= min);
            assertTrue(value <= max);
        }
    }

    @Test
    void clientPortIsConfigured() throws Exception {
        Field clientPort = SensorClient.class.getDeclaredField("CLIENT_PORT");
        clientPort.setAccessible(true);

        assertEquals(6001, clientPort.getInt(null));
    }

    @Test
    void sendIntervalIsConfigured() throws Exception {
        Field sendInterval = SensorClient.class.getDeclaredField("SEND_INTERVAL_MS");
        sendInterval.setAccessible(true);

        assertEquals(5000L, sendInterval.getLong(null));
    }

    @Test
    void mainMethodExistsAndIsStatic() throws Exception {
        Method main = SensorClient.class.getDeclaredMethod("main", String[].class);

        assertTrue(Modifier.isStatic(main.getModifiers()));
        assertEquals(void.class, main.getReturnType());
    }
}
