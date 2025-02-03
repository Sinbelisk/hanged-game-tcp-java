package server.services;

import common.Connection;
import server.User;
import util.SimpleLogger;

import java.util.logging.Logger;

public class MessageService {
    private static final Logger logger = SimpleLogger.getInstance().getLogger(MessageService.class);
    private final Connection connection;
    private final User user;

    public MessageService(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    public void sendUnknownCommand(){
        send("Unknown command, try 'help' to see available commands");
    }

    public void sendUserHasLogedIn(){
        send("You have logged in");
    }

    public void sendUnknownCredentials(){
        send("Invalid credentials, try again");
    }

    public void sendUserMustRegister(){
        send("Please register to access the server");
    }

    public void sendUserHasRegistered(){
        send("User registered, use 'login <user> <password>' to log in.");
    }

    public void sedUserCouldntRegister(){
        send("That username is not available, try another");
    }

    private void send(String msg){
        try{
            String userId = user == null ? "" : user.getId();
            logger.info("Sending message to " + userId + ": " + msg);
            connection.send(msg);
        } catch (Exception e){
            logger.severe("Error sending message: " + e.getMessage());
        }
    }
}
