package server;

import common.SocketConnection;

import java.io.IOException;
import java.net.Socket;

public class Worker extends Thread{
    private final Socket clientSocket;

    public Worker(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (SocketConnection connection = new SocketConnection(clientSocket)) {
            connection.open();

            // Recibir mensaje del cliente
            String received = connection.receive();
            System.out.println("Mensaje recibido del cliente: " + received);

            // Aquí se podría procesar el mensaje; en este ejemplo se envía un eco.
            String response = "Eco: " + received;
            connection.send(response);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
