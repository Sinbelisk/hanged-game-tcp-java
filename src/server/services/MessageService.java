package server.services;

import server.Worker;
import util.SimpleLogger;

import java.util.logging.Logger;

public class MessageService {
    private static final Logger logger = SimpleLogger.getInstance().getLogger(MessageService.class);

    public void sendUserNotInGameRoom(Worker worker) {
        send("Error: no estas en ninguna sala de juego.", worker);
    }

    public void sendUnknownCommand(Worker worker) {
        send("Error: comando desconocido, usa '/help' para ver la lista de comandos disponibles", worker);
    }

    public void sendUserMustAuth(Worker worker) {
        send("Error: debes iniciar sesión o registrarte para realizar esa acción", worker);
    }

    public void send(String msg, Worker worker) {
        try {
            worker.getConnection().send(msg);
        } catch (Exception e) {
            logger.severe("[" + worker.getName() + "] Error al enviar mensaje: " + e.getMessage());
        }
    }
}
