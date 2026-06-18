package sp;

import exceptions.IllegalMsgException;

public class AckMessage extends SPMsg {

    public static final String TYPE = "sp ACK";

    private boolean success;

    public AckMessage() {
    }

    public AckMessage(int sensorId, int sequenceNumber, boolean success) {
        this.sensorId = sensorId;
        this.sequenceNumber = sequenceNumber;
        this.success = success;
        buildMessage();
    }

    private void buildMessage() {
        String dataWithoutCrc = TYPE
                + SEPARATOR + sensorId
                + SEPARATOR + sequenceNumber
                + SEPARATOR + success;

        this.crc = calculateCrc(dataWithoutCrc);

        String fullMessage = dataWithoutCrc
                + SEPARATOR + crc;

        create(fullMessage);
    }

    @Override
    protected AckMessage parse(String sentence) throws IllegalMsgException {
        String[] parts = sentence.split(SEPARATOR);

        if (parts.length != 5) {
            throw new IllegalMsgException();
        }

        if (!parts[0].equals(TYPE)) {
            throw new IllegalMsgException();
        }

        String dataWithoutCrc = parts[0]
                + SEPARATOR + parts[1]
                + SEPARATOR + parts[2]
                + SEPARATOR + parts[3];

        long receivedCrc;

        try {
            this.sensorId = Integer.parseInt(parts[1]);
            this.sequenceNumber = Integer.parseInt(parts[2]);
            this.success = Boolean.parseBoolean(parts[3]);
            receivedCrc = Long.parseLong(parts[4]);
        } catch (NumberFormatException e) {
            throw new IllegalMsgException();
        }

        if (!isCrcValid(dataWithoutCrc, receivedCrc)) {
            throw new IllegalMsgException();
        }

        this.crc = receivedCrc;
        create(sentence);

        return this;
    }

    public boolean isSuccess() {
        return success;
    }
}
