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
    @Test
    @DisplayName("Ungültiger Nachrichtentyp löst Exception aus")
    void wrongTypeThrowsExceptionTest() {
        assertThrows(IllegalMsgException.class, () -> {
            new AckMessage().parse("sp DATA;1;2;20.0;7.0;8.0;123");
        });
    }

    @Test
    @DisplayName("Unvollständige ACK löst Exception aus")
    void incompleteAckThrowsExceptionTest() {
        assertThrows(IllegalMsgException.class, () -> {
            new AckMessage().parse("sp ACK;1;2");
        });
    }

    @Test
    @DisplayName("ACK mit success false wird korrekt gespeichert")
    void ackFalseStoredCorrectlyTest() {
        AckMessage ack = new AckMessage(5, 42, false);

        assertEquals(5, ack.getSensorId());
        assertEquals(42, ack.getSequenceNumber());
        assertFalse(ack.isSuccess());
    }

    @Test
    @DisplayName("CRC bleibt bei identischer ACK gleich")
    void crcIsDeterministicTest() {
        AckMessage ack1 = new AckMessage(1, 10, true);
        AckMessage ack2 = new AckMessage(1, 10, true);

        assertEquals(ack1.getCrc(), ack2.getCrc());
    }

    @Test
    @DisplayName("Geparste ACK enthält dieselben Werte")
    void parsedAckContainsCorrectValuesTest() throws Exception {
        AckMessage original = new AckMessage(7, 99, false);

        AckMessage parsed = new AckMessage().parse(original.getData());

        assertEquals(7, parsed.getSensorId());
        assertEquals(99, parsed.getSequenceNumber());
        assertFalse(parsed.isSuccess());
    }
}