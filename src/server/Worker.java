package server;

import common.SocketConnection;
import server.commands.Command;
import server.commands.CommandFactory;
import util.SimpleLogger;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class Worker extends Thread{
    private final Socket clientSocket;
    private User user = null;
    private final Logger logger = SimpleLogger.getInstance().getLogger(getClass());

    public Worker(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (SocketConnection connection = new SocketConnection(clientSocket)) {
            connection.open();

            // Recibir mensaje del cliente
            String received = connection.receive();
            logger.info("Received: " + received);

            String[] tokens = received.split("\\s");
            logger.info("Tokens: " + tokens.length);

            Command command = null;
            if(tokens[0].startsWith("/")){
                command = CommandFactory.createCommand(tokens[0]);
                logger.info("Command: " + tokens[0]);
            }

            if(command != null){
                command.execute(tokens, this);
            } else{
                connection.send("Unknown command");
            }


            // Aquí se podría procesar el mensaje; en este ejemplo se envía un eco.
            String response = "Eco: " + received;
            connection.send(response);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
       this.user = user.isAutentified() ? user : null;
    }
}
