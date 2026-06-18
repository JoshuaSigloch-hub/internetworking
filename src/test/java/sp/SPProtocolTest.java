package sp;

import core.Msg;
import core.Protocol;
import org.junit.jupiter.api.Test;
import phy.PhyConfiguration;
import phy.PhyProtocol;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SPProtocolTest {

    @Test
    void sendMeasurementMessageForwardsDataToPhyProtocol() throws Exception {
        PhyProtocol phy = mock(PhyProtocol.class);
        SPProtocol sp = new SPProtocol(phy);

        SPConfiguration config = new SPConfiguration(
                InetAddress.getByName("localhost"),
                9999
        );

        MeasurementMessage message = new MeasurementMessage(1, 10, 20.5, 7.2, 8.9);

        sp.send(message, config);

        verify(phy).send(eq(message.getData()), any(PhyConfiguration.class));
    }

    @Test
    void receiveReturnsMeasurementMessage() throws Exception {
        PhyProtocol phy = mock(PhyProtocol.class);
        SPProtocol sp = new SPProtocol(phy);

        MeasurementMessage incoming = new MeasurementMessage(1, 10, 20.5, 7.2, 8.9);
        incoming.setConfiguration(new PhyConfiguration(
                InetAddress.getByName("localhost"),
                12345,
                Protocol.proto_id.SP
        ));

        when(phy.receive()).thenReturn(incoming);

        Msg result = sp.receive();

        assertInstanceOf(MeasurementMessage.class, result);

        MeasurementMessage received = (MeasurementMessage) result;
        assertEquals(1, received.getSensorId());
        assertEquals(10, received.getSequenceNumber());
        assertEquals(20.5, received.getTemperature());
        assertEquals(7.2, received.getPhValue());
        assertEquals(8.9, received.getOxygenValue());

        assertNotNull(sp.getSenderConfig());
        assertEquals(12345, sp.getSenderConfig().getRemotePort());
    }

    @Test
    void receiveReturnsAckMessage() throws Exception {
        PhyProtocol phy = mock(PhyProtocol.class);
        SPProtocol sp = new SPProtocol(phy);

        AckMessage incoming = new AckMessage(1, 10, true);
        incoming.setConfiguration(new PhyConfiguration(
                InetAddress.getByName("localhost"),
                12345,
                Protocol.proto_id.SP
        ));

        when(phy.receive()).thenReturn(incoming);

        Msg result = sp.receive();

        assertInstanceOf(AckMessage.class, result);

        AckMessage received = (AckMessage) result;
        assertEquals(1, received.getSensorId());
        assertEquals(10, received.getSequenceNumber());
        assertTrue(received.isSuccess());
    }

    @Test
    void receiveReturnsNullForWrongProtocolId() throws Exception {
        PhyProtocol phy = mock(PhyProtocol.class);
        SPProtocol sp = new SPProtocol(phy);

        MeasurementMessage incoming = new MeasurementMessage(1, 10, 20.5, 7.2, 8.9);
        incoming.setConfiguration(new PhyConfiguration(
                InetAddress.getByName("localhost"),
                12345,
                Protocol.proto_id.APP
        ));

        when(phy.receive()).thenReturn(incoming);

        Msg result = sp.receive();

        assertNull(result);
    }
}