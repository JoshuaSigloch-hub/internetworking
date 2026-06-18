package sp;

import exceptions.IllegalMsgException;

public class MeasurementMessage extends SPMsg {

    public static final String TYPE = "sp DATA";

    private double temperature;
    private double phValue;
    private double oxygenValue;

    public MeasurementMessage() {
    }

    public MeasurementMessage(int sensorId, int sequenceNumber,
                              double temperature, double phValue, double oxygenValue) {
        this.sensorId = sensorId;
        this.sequenceNumber = sequenceNumber;
        this.temperature = temperature;
        this.phValue = phValue;
        this.oxygenValue = oxygenValue;
        buildMessage();
    }

    private void buildMessage() {
        String dataWithoutCrc = TYPE
                + SEPARATOR + sensorId
                + SEPARATOR + sequenceNumber
                + SEPARATOR + temperature
                + SEPARATOR + phValue
                + SEPARATOR + oxygenValue;

        this.crc = calculateCrc(dataWithoutCrc);

        String fullMessage = dataWithoutCrc
                + SEPARATOR + crc;

        create(fullMessage);
    }

    @Override
    protected MeasurementMessage parse(String sentence) throws IllegalMsgException {
        String[] parts = sentence.split(SEPARATOR);

        if (parts.length != 7) {
            throw new IllegalMsgException();
        }

        if (!parts[0].equals(TYPE)) {
            throw new IllegalMsgException();
        }

        String dataWithoutCrc = parts[0]
                + SEPARATOR + parts[1]
                + SEPARATOR + parts[2]
                + SEPARATOR + parts[3]
                + SEPARATOR + parts[4]
                + SEPARATOR + parts[5];

        long receivedCrc;

        try {
            this.sensorId = Integer.parseInt(parts[1]);
            this.sequenceNumber = Integer.parseInt(parts[2]);
            this.temperature = Double.parseDouble(parts[3]);
            this.phValue = Double.parseDouble(parts[4]);
            this.oxygenValue = Double.parseDouble(parts[5]);
            receivedCrc = Long.parseLong(parts[6]);
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

    public double getTemperature() {
        return temperature;
    }

    public double getPhValue() {
        return phValue;
    }

    public double getOxygenValue() {
        return oxygenValue;
    }
}
