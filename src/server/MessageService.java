package server;

import common.Connection;
import util.SimpleLogger;

import java.util.logging.Logger;

public class MessageService {
    private static final Logger logger = SimpleLogger.getInstance().getLogger(MessageService.class);

    public static void sendUnknownCommand(Connection user, String userAddress){
        send("Unknown command, try 'help' to see available commands", user, userAddress);
    }

    private static void send(String msg, Connection user, String userAddress){
        try{
            logger.info("Sending message to " + userAddress + ": " + msg);
            user.send(msg);
        } catch (Exception e){
            logger.severe("Error sending message: " + e.getMessage());
        }
    }
}
