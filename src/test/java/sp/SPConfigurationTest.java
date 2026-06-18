package sp;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;

class SPConfigurationTest {

    @Test
    void constructorSetsRemoteAddressAndPort() throws Exception {
        InetAddress address = InetAddress.getByName("localhost");

        SPConfiguration config = new SPConfiguration(address, 6000);

        assertEquals(address, config.getRemoteAddress());
        assertEquals(6000, config.getRemotePort());
    }

    @Test
    void setRemoteAddressChangesRemoteAddress() throws Exception {
        InetAddress firstAddress = InetAddress.getByName("localhost");
        InetAddress secondAddress = InetAddress.getByName("127.0.0.1");
        SPConfiguration config = new SPConfiguration(firstAddress, 6000);

        config.setRemoteAddress(secondAddress);

        assertEquals(secondAddress, config.getRemoteAddress());
    }

    @Test
    void setRemotePortChangesRemotePort() throws Exception {
        InetAddress address = InetAddress.getByName("localhost");
        SPConfiguration config = new SPConfiguration(address, 6000);

        config.setRemotePort(7000);

        assertEquals(7000, config.getRemotePort());
    }
}
