package client;

import common.Connection;
import common.SocketConnection;
import util.SimpleLogger;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private static final Logger logger = SimpleLogger.getInstance().getLogger(Client.class);
    private final String host;
    private final int port;
    private volatile boolean running = true; // Controla la ejecución del hilo de lectura

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        try (Socket socket = new Socket(host, port);
             Scanner scanner = new Scanner(System.in)) {
            logger.info("Conexión al servidor " + host + ":" + port);
            Connection connection = new SocketConnection(socket);
            connection.open();
            logger.info("Conexión abierta, esperando mensajes");

            // Iniciar el hilo de escucha
            Thread readerThread = new Thread(() -> listenToServer(connection));
            readerThread.start();

            logger.info("Ya se pueden introducir comandos:");
            while (running && socket.isConnected() && !socket.isClosed()) {
                String line = scanner.nextLine();
                if (line != null) {
                    connection.send(line);

                    if (line.equalsIgnoreCase("/exit")) {
                        logger.info("Terminando conexión...");
                        running = false;
                        break;
                    }
                }
            }

            connection.close();
            logger.info("Conexión con el servidor terminada.");
            readerThread.join();

        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Error al conectarse al servidor " + host + ":" + port, e);
            Thread.currentThread().interrupt();
        }
    }

    private void listenToServer(Connection connection) {
        try {
            String response;
            while (running && (response = connection.receive()) != null) {
                System.out.println("Server: " + response);
            }
        } catch (IOException e) {
            if (running) {
                logger.log(Level.WARNING, "Error al recibir mensaje: " + e.getMessage(), e);
            }
        } finally {
            running = false;
        }
    }
}


