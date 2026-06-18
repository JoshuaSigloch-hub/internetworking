package apps;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import core.Msg;
import phy.PhyProtocol;
import sp.SPProtocol;
import sp.SPConfiguration;
import sp.MeasurementMessage;
import sp.AckMessage;
import exceptions.IWProtocolException;

/**
 * SensorClient (Sensor System)
 *
 * Periodically creates a MeasurementMessage with random sensor values,
 * sends it to the server via SPProtocol, and waits for an ACK.
 *
 * Lifecycle:
 *   1. Initialize PhyProtocol + SPProtocol
 *   2. Loop:
 *        a. Create MeasurementMessage with random values
 *        b. Send via sp.send()
 *        c. Wait for AckMessage via sp.receive()
 *        d. Sleep SEND_INTERVAL_MS before next measurement
 */
public class SensorClient {

    // ---------------------------------------------------------------
    // Configuration — adjust to match your deployment
    // ---------------------------------------------------------------
    private static final String SERVER_HOST    = "localhost";
    private static final int    SERVER_PORT    = 6000;   // SPServer listens here
    private static final int    CLIENT_PORT    = 6001;   // this sensor's PHY port
    private static final long   SEND_INTERVAL_MS = 5000; // send every 5 seconds

    // ---------------------------------------------------------------
    // Sensor value ranges (realistic water quality values)
    // ---------------------------------------------------------------
    private static final double PH_MIN   = 6.5,  PH_MAX   = 8.5;
    private static final double TEMP_MIN = 5.0,  TEMP_MAX = 30.0;
    private static final double O2_MIN   = 6.0,  O2_MAX   = 12.0;

    public static void main(String[] args) {

        // --- 1. Setup PHY layer ---
        PhyProtocol phy = new PhyProtocol(CLIENT_PORT);

        // --- 2. Setup SP protocol ---
        SPProtocol sp = new SPProtocol(phy);

        // --- 3. Build destination config ---
        SPConfiguration serverConfig;
        try {
            serverConfig = new SPConfiguration(
                InetAddress.getByName(SERVER_HOST),
                SERVER_PORT
            );
        } catch (UnknownHostException e) {
            System.err.println("[SensorClient] Unknown host: " + SERVER_HOST);
            return;
        }

        System.out.println("[SensorClient] Started. Sending to " + SERVER_HOST + ":" + SERVER_PORT);

        // --- 4. Main send loop ---
        while (true) {
            try {
                // a) Create measurement with random values in realistic ranges
                MeasurementMessage measurement = createMeasurement();
                System.out.println("[SensorClient] Sending: " + measurement);

                // b) Send to server
                sp.send(measurement.getData(), serverConfig);

                // c) Wait for ACK
                Msg response = sp.receive();
                if (response instanceof AckMessage) {
                    AckMessage ack = (AckMessage) response;
                    System.out.println("[SensorClient] ACK received (seq=" + ack.getSequenceNumber() + ")");
                } else {
                    System.err.println("[SensorClient] Unexpected message type: " + response.getClass().getSimpleName());
                }

                // d) Wait before next measurement
                Thread.sleep(SEND_INTERVAL_MS);

            } catch (IWProtocolException e) {
                System.err.println("[SensorClient] Protocol error: " + e.getMessage());
                // continue trying — sensor should be robust
            } catch (IOException e) {
                System.err.println("[SensorClient] IO error: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("[SensorClient] Interrupted, shutting down.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // Sequenznummer pro Client hochzählen
    private static int sequenceNumber = 0;
    private static final int SENSOR_ID = CLIENT_PORT; // Port als eindeutige Sensor-ID

    /**
     * Creates a MeasurementMessage with random values in realistic ranges.
     * pH:          6.5 – 8.5
     * Temperature: 5.0 – 30.0 °C
     * Oxygen:      6.0 – 12.0 mg/L
     */
    private static MeasurementMessage createMeasurement() {
        double ph          = randomInRange(PH_MIN,   PH_MAX);
        double temperature = randomInRange(TEMP_MIN, TEMP_MAX);
        double oxygen      = randomInRange(O2_MIN,   O2_MAX);
        // Konstruktor: (sensorId, sequenceNumber, temperature, phValue, oxygenValue)
        return new MeasurementMessage(SENSOR_ID, sequenceNumber++, temperature, ph, oxygen);
    }

    private static double randomInRange(double min, double max) {
        return Math.round((min + Math.random() * (max - min)) * 100.0) / 100.0;
    }
}