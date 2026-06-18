package sp;

import core.Configuration;
import java.net.InetAddress;

/**
 * Configuration for the Sensor Protocol.
 * Stores the destination address and port for sending SP messages.
 */
public class SPConfiguration extends Configuration {
    private InetAddress remoteAddress;
    private int remotePort;

    public SPConfiguration(InetAddress remoteAddress, int remotePort) {
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
    }

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
}
