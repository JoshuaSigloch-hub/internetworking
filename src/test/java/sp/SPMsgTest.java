package sp;

import exceptions.IllegalMsgException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SPMsgTest {

    @Test
    void measurementMessageCanBeParsed() throws Exception {
        MeasurementMessage original = new MeasurementMessage(1, 10, 20.0, 7.0, 8.0);

        MeasurementMessage parsed = new MeasurementMessage().parse(original.getData());

        assertEquals(1, parsed.getSensorId());
        assertEquals(10, parsed.getSequenceNumber());
    }

    @Test
    void ackMessageCanBeParsed() throws Exception {
        AckMessage original = new AckMessage(1, 10, true);

        AckMessage parsed = new AckMessage().parse(original.getData());

        assertTrue(parsed.isSuccess());
        assertEquals(10, parsed.getSequenceNumber());
    }

    @Test
    void invalidMeasurementMessageThrowsException() {
        assertThrows(IllegalMsgException.class, () -> {
            new MeasurementMessage().parse("sp DATA;1;2");
        });
    }

    @Test
    void invalidAckMessageThrowsException() {
        assertThrows(IllegalMsgException.class, () -> {
            new AckMessage().parse("sp ACK;1");
        });
    }
}
