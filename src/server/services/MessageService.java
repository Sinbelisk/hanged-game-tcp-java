package server.services;

import common.Connection;
import server.Worker;
import server.game.User;
import util.SimpleLogger;

import java.util.logging.Logger;

public class MessageService {
    private static final Logger logger = SimpleLogger.getInstance().getLogger(MessageService.class);

    public void sendUserNotInGameRoom(Worker worker) {
        send("Error: no estas en ninguna sala de juego.", worker);
    }

    public void send(String msg, Worker worker) {
        try {
            worker.getConnection().send(msg);
        } catch (Exception e) {
            logger.severe("[" + worker.getName() + "] Error al enviar mensaje: " + e.getMessage());
        }
    }
}
