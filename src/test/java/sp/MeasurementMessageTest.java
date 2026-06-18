package sp;

import exceptions.IllegalMsgException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeasurementMessageTest {

    @Test
    @DisplayName("MeasurementMessage wird korrekt erstellt")
    void createMeasurementMessageTest() {
        MeasurementMessage message = new MeasurementMessage(1, 10, 18.5, 7.2, 8.9);

        assertEquals(1, message.getSensorId());
        assertEquals(10, message.getSequenceNumber());
        assertEquals(18.5, message.getTemperature());
        assertEquals(7.2, message.getPhValue());
        assertEquals(8.9, message.getOxygenValue());

        assertNotNull(message.getData());
        assertNotNull(message.getDataBytes());
        assertTrue(message.getData().startsWith("sp DATA"));
    }

    @Test
    @DisplayName("MeasurementMessage kann korrekt geparst werden")
    void parseMeasurementMessageTest() throws Exception {
        MeasurementMessage original = new MeasurementMessage(2, 15, 20.1, 6.8, 9.3);

        MeasurementMessage parsed = new MeasurementMessage().parse(original.getData());

        assertEquals(original.getSensorId(), parsed.getSensorId());
        assertEquals(original.getSequenceNumber(), parsed.getSequenceNumber());
        assertEquals(original.getTemperature(), parsed.getTemperature());
        assertEquals(original.getPhValue(), parsed.getPhValue());
        assertEquals(original.getOxygenValue(), parsed.getOxygenValue());
        assertEquals(original.getCrc(), parsed.getCrc());
    }

    @Test
    @DisplayName("Manipulierte MeasurementMessage wird wegen falscher CRC abgelehnt")
    void manipulatedMeasurementMessageThrowsExceptionTest() {
        MeasurementMessage original = new MeasurementMessage(3, 20, 19.0, 7.0, 8.0);

        String manipulated = original.getData().replace("19.0", "99.0");

        assertThrows(IllegalMsgException.class, () -> {
            new MeasurementMessage().parse(manipulated);
        });
    }
}