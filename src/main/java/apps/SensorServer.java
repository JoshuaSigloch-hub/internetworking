package apps;

import java.io.IOException;

import core.Msg;
import phy.PhyProtocol;
import sp.SPProtocol;
import sp.SPConfiguration;
import sp.MeasurementMessage;
import sp.AckMessage;
import exceptions.IWProtocolException;

public class SensorServer {

    public static final int SERVER_PORT = 6000;

    public static void main(String[] args) {

        // 1. PHY initialisieren
        PhyProtocol phy = new PhyProtocol(SERVER_PORT);

        // 2. SP Protokoll initialisieren
        SPProtocol sp = new SPProtocol(phy);

        System.out.println("[SensorServer] Lauscht auf Port " + SERVER_PORT + " ...");

        // 3. Empfangs-Loop
        while (true) {
            try {
                // a) Auf Nachricht warten
                Msg incoming = sp.receive();

                if (incoming instanceof MeasurementMessage measurement) {

                    // b) Messdaten ausgeben
                    printMeasurement(measurement);

                    // c) ACK an den richtigen Sender zurückschicken
                    SPConfiguration senderConfig = sp.getSenderConfig();
                    AckMessage ack = new AckMessage(
                        measurement.getSensorId(),
                        measurement.getSequenceNumber(),
                        true
                    );
                    sp.send(ack, senderConfig);

                    System.out.println("[SensorServer] ACK gesendet an " + senderConfig.getRemoteAddress()
                        + ":" + senderConfig.getRemotePort());

                } else {
                    System.err.println("[SensorServer] Unbekannter Nachrichtentyp - ignoriert.");
                }

            } catch (IWProtocolException e) {
                System.err.println("[SensorServer] Protokollfehler: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("[SensorServer] IO-Fehler: " + e.getMessage());
            }
        }
    }

    private static void printMeasurement(MeasurementMessage m) {
        System.out.println("========================================");
        System.out.println("[SensorServer] Messung empfangen:");
        System.out.println("  Sensor ID   : " + m.getSensorId());
        System.out.println("  Sequenz Nr  : " + m.getSequenceNumber());
        System.out.println("  pH          : " + m.getPhValue());
        System.out.println("  Temperatur  : " + m.getTemperature() + " °C");
        System.out.println("  Sauerstoff  : " + m.getOxygenValue() + " mg/L");
        System.out.println("========================================");
    }
}