package server;

import common.SocketConnection;
import server.commands.Command;
import server.commands.CommandFactory;
import server.services.MessageService;
import server.services.ServiceRegistry;
import util.SimpleLogger;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class Worker extends Thread{
    private final Socket clientSocket;
    private User user = null;
    private boolean running = true;
    private final Logger logger = SimpleLogger.getInstance().getLogger(getClass());
    private final ServiceRegistry services;
    private MessageService messageService;

    public Worker(Socket clientSocket, ServiceRegistry services){
        this.clientSocket = clientSocket;
        this.services = services;
    }

    @Override
    public void run() {
        try (SocketConnection connection = new SocketConnection(clientSocket)) {
            connection.open();
            messageService = new MessageService(connection, user);

            while(running){
                // Recibir mensaje del cliente
                String received = connection.receive();
                logger.info("Received: " + received);

                String[] tokens = received.split("\\s");
                logger.info("Tokens: " + tokens.length);

                Command command = null;
                if(tokens[0].startsWith("/")){
                    command = services.getService(CommandFactory.class).createCommand(tokens[0]);
                    logger.info("Command: " + tokens[0]);
                }

                if(command != null){
                    command.assingServices(services);
                    command.execute(tokens, this);
                } else{
                    messageService.sendUnknownCommand();
                }
            }

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
       this.user = user.isAuthenticated() ? user : null;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public void stopWorker(){
        running = false;
    }
}
