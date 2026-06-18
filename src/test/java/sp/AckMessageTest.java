package sp;

import exceptions.IllegalMsgException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AckMessageTest {

    @Test
    @DisplayName("AckMessage wird korrekt erstellt")
    void createAckMessageTest() {
        AckMessage ack = new AckMessage(1, 10, true);

        assertEquals(1, ack.getSensorId());
        assertEquals(10, ack.getSequenceNumber());
        assertTrue(ack.isSuccess());

        assertNotNull(ack.getData());
        assertNotNull(ack.getDataBytes());
        assertTrue(ack.getData().startsWith("sp ACK"));
    }

    @Test
    @DisplayName("AckMessage kann korrekt geparst werden")
    void parseAckMessageTest() throws Exception {
        AckMessage original = new AckMessage(2, 15, true);

        AckMessage parsed = new AckMessage().parse(original.getData());

        assertEquals(original.getSensorId(), parsed.getSensorId());
        assertEquals(original.getSequenceNumber(), parsed.getSequenceNumber());
        assertEquals(original.isSuccess(), parsed.isSuccess());
        assertEquals(original.getCrc(), parsed.getCrc());
    }

    @Test
    @DisplayName("Manipulierte AckMessage wird wegen falscher CRC abgelehnt")
    void manipulatedAckMessageThrowsExceptionTest() {
        AckMessage original = new AckMessage(3, 20, true);

        String manipulated = original.getData().replace("true", "false");

        assertThrows(IllegalMsgException.class, () -> {
            new AckMessage().parse(manipulated);
        });
    }
}