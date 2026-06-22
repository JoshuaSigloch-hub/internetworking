package sp;

import core.Msg;
import exceptions.IWProtocolException;
import exceptions.IllegalMsgException;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public abstract class SPMsg extends Msg {

    protected static final String SP_HEADER = "sp";
    protected static final String SEPARATOR = ";";

    protected int sensorId;
    protected int sequenceNumber;
    protected long crc;

    public int getSensorId() {
        return sensorId;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public long getCrc() {
        return crc;
    }

    protected long calculateCrc(String dataWithoutCrc) {
        CRC32 crc32 = new CRC32();
        crc32.update(dataWithoutCrc.getBytes(StandardCharsets.UTF_8));
        return crc32.getValue();
    }

    protected boolean isCrcValid(String dataWithoutCrc, long receivedCrc) {
        return calculateCrc(dataWithoutCrc) == receivedCrc;
    }

    @Override
    protected void create(String data) {
        this.data = data;
        this.dataBytes = data.getBytes(StandardCharsets.UTF_8);
    }

    public static SPMsg parseMessage(String sentence) throws IWProtocolException {
        if (sentence == null || !sentence.startsWith(SP_HEADER)) {
            throw new IllegalMsgException();
        }

        if (sentence.startsWith(MeasurementMessage.TYPE)) {
            return (SPMsg) new MeasurementMessage().parse(sentence);
        }

        if (sentence.startsWith(AckMessage.TYPE)) {
            return (SPMsg) new AckMessage().parse(sentence);
        }

        throw new IllegalMsgException();
    }

    @Override
    protected Msg parse(String sentence) throws IWProtocolException {
        return parseMessage(sentence);
    }
}