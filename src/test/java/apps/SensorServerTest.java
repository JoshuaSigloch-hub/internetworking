package apps;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class SensorServerTest {

    @Test
    void serverPortIsConfigured() throws Exception {
        Field serverPort = SensorServer.class.getDeclaredField("SERVER_PORT");
        serverPort.setAccessible(true);

        assertEquals(6000, serverPort.getInt(null));
    }

    @Test
    void mainMethodExistsAndIsStatic() throws Exception {
        Method main = SensorServer.class.getDeclaredMethod("main", String[].class);

        assertTrue(Modifier.isStatic(main.getModifiers()));
        assertEquals(void.class, main.getReturnType());
    }
}
