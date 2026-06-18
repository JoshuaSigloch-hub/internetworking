package apps;

import java.io.IOException;

import core.Msg;
import phy.PhyProtocol;
import sp.SPProtocol;
import sp.SPConfiguration;
import sp.MeasurementMessage;
import sp.AckMessage;
import exceptions.IWProtocolException;

/**
 * SensorServer (Data Processing Station)
 *
 * Waits for incoming MeasurementMessages from arbitrary many sensors,
 * prints the data to screen, and sends an ACK back to the correct sender.
 *
 * Lifecycle:
 *   1. Initialize PhyProtocol + SPProtocol
 *   2. Loop forever:
 *        a. Wait for incoming message
 *        b. If MeasurementMessage: print data, send ACK back
 *        c. If unknown type: log warning, ignore
 */
public class SensorServer {

    // ---------------------------------------------------------------
    // Configuration
    // ---------------------------------------------------------------
    public static final int SERVER_PORT = 6000; // must match SensorClient.SERVER_PORT

    public static void main(String[] args) {

        // --- 1. Setup PHY layer ---
        PhyProtocol phy = new PhyProtocol(SERVER_PORT);

        // --- 2. Setup SP protocol ---
        SPProtocol sp;
        try {
            sp = new SPProtocol(SERVER_PORT, phy);
        } catch (IWProtocolException e) {
            System.err.println("[SensorServer] Failed to initialize SPProtocol: " + e.getMessage());
            return;
        }

        System.out.println("[SensorServer] Listening on port " + SERVER_PORT + " ...");

        // --- 3. Main receive loop ---
        while (true) {
            try {
                // a) Block until a message arrives
                Msg incoming = sp.receive();

                if (incoming instanceof MeasurementMessage) {
                    MeasurementMessage measurement = (MeasurementMessage) incoming;

                    // b) Print measurement data to screen (required by task)
                    printMeasurement(measurement);

                    // c) ACK zurück zum richtigen Sender
                    SPConfiguration senderConfig = sp.getSenderConfig();
                    // Konstruktor: (sensorId, sequenceNumber, success)
                    AckMessage ack = new AckMessage(
                        measurement.getSensorId(),
                        measurement.getSequenceNumber(),
                        true
                    );
                    sp.send(ack, senderConfig);

                    System.out.println("[SensorServer] ACK sent to " + senderConfig);

                } else {
                    // Unknown message type — log and continue
                    System.err.println("[SensorServer] Unknown message type received: "
                        + incoming.getClass().getSimpleName() + " — ignoring.");
                }

            } catch (IWProtocolException e) {
                System.err.println("[SensorServer] Protocol error: " + e.getMessage());
                // keep running — server must stay up for all sensors
            } catch (IOException e) {
                System.err.println("[SensorServer] IO error: " + e.getMessage());
            }
        }
    }

    /**
     * Prints measurement data to screen in a readable format.
     * Called after each successfully received MeasurementMessage.
     */
    private static void printMeasurement(MeasurementMessage m) {
        System.out.println("========================================");
        System.out.println("[SensorServer] Measurement received:");
        System.out.println("  Sensor ID   : " + m.getSensorId());
        System.out.println("  Sequence Nr : " + m.getSequenceNumber());
        System.out.println("  pH          : " + m.getPhValue());
        System.out.println("  Temperature : " + m.getTemperature() + " °C");
        System.out.println("  Oxygen      : " + m.getOxygenValue() + " mg/L");
        System.out.println("========================================");
    }
}
