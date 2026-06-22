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

public class SensorClient {

    private static final String SERVER_HOST      = "localhost";
    private static final int    SERVER_PORT      = 6000;
    private static final int    CLIENT_PORT      = 6001;
    private static final long   SEND_INTERVAL_MS = 5000;

    private static final double PH_MIN   = 6.5,  PH_MAX   = 8.5;
    private static final double TEMP_MIN = 5.0,  TEMP_MAX = 30.0;
    private static final double O2_MIN   = 6.0,  O2_MAX   = 12.0;

    private static int sequenceNumber = 0;

    public static void main(String[] args) {

        PhyProtocol phy = new PhyProtocol(CLIENT_PORT);

        SPProtocol sp = new SPProtocol(phy);

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

        System.out.println("[SensorClient] Gestartet. Sende an " + SERVER_HOST + ":" + SERVER_PORT);

        while (true) {
            try {
                MeasurementMessage measurement = new MeasurementMessage(
                    CLIENT_PORT,
                    sequenceNumber++,
                    randomInRange(TEMP_MIN, TEMP_MAX),
                    randomInRange(PH_MIN,   PH_MAX),
                    randomInRange(O2_MIN,   O2_MAX)
                );

                System.out.println("[SensorClient] Sende Messung #" + measurement.getSequenceNumber());

                sp.send(measurement, serverConfig);

                Msg response = sp.receive();
                if (response instanceof AckMessage ack) {
                    System.out.println("[SensorClient] ACK erhalten (seq="
                        + ack.getSequenceNumber() + ", success=" + ack.isSuccess() + ")");
                } else {
                    System.err.println("[SensorClient] Unerwarteter Nachrichtentyp empfangen.");
                }

                Thread.sleep(SEND_INTERVAL_MS);

            } catch (IWProtocolException e) {
                System.err.println("[SensorClient] Protokollfehler: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("[SensorClient] IO-Fehler: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("[SensorClient] Unterbrochen, beende.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static double randomInRange(double min, double max) {
        return Math.round((min + Math.random() * (max - min)) * 100.0) / 100.0;
    }
}