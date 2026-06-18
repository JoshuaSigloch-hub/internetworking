package sp;

import core.Configuration;
import core.Msg;
import core.Protocol;
import exceptions.IWProtocolException;
import phy.PhyConfiguration;
import phy.PhyProtocol;

import java.io.IOException;

public class SPProtocol implements Protocol {

    private final PhyProtocol phy;
    private SPConfiguration senderConfig;

    public SPProtocol(PhyProtocol phy) {
        this.phy = phy;
    }

    public SPConfiguration getSenderConfig() {
        return senderConfig;
    }

    @Override
    public void send(String s, Configuration config)
            throws IOException, IWProtocolException {

        SPConfiguration spConfig = (SPConfiguration) config;

        phy.send(s, new PhyConfiguration(
                spConfig.getRemoteAddress(),
                spConfig.getRemotePort(),
                Protocol.proto_id.SP
        ));
    }

    @Override
    public Msg receive()
            throws IOException, IWProtocolException {

        Msg in = phy.receive();

        if (in.getConfiguration() instanceof PhyConfiguration phyConfig) {
            this.senderConfig = new SPConfiguration(
                    phyConfig.getRemoteIPAddress(),
                    phyConfig.getRemotePort()
            );
        }

        if (((PhyConfiguration) in.getConfiguration()).getPid()
                != Protocol.proto_id.SP) {
            return null;
        }

        SPMsg msg = new MeasurementMessage();
        return msg.parse(in.getData());
    }
}