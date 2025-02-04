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

public class Client {
    private static final Logger logger = SimpleLogger.getInstance().getLogger(Client.class);
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        try (Socket socket = new Socket(host, port)) {
            logger.info("Connected to server at " + host + ":" + port);
            Connection connection = new SocketConnection(socket);
            connection.open();
            logger.info("Connection opened.");

            // Thread to receive messages from the server
            Thread readerThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = connection.receive()) != null) {
                        System.out.println("Server: " + response);
                    }
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error receiving message: " + e.getMessage(), e);
                }
            });
            readerThread.start();

            Scanner scanner = new Scanner(System.in);
            logger.info("Enter your commands:");
            while (socket.isConnected() && !socket.isClosed()) {
                String line = scanner.nextLine();
                if (line != null) {
                    connection.send(line);
                    logger.info("Sent to server: " + line);
                    if (line.equalsIgnoreCase("/exit")) {
                        logger.info("EXIT command received, closing connection.");
                        break;
                    }
                }
            }

            connection.close();
            logger.info("Connection closed.");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error connecting to server at " + host + ":" + port, e);
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 2050);
        client.connect();
    }
}

