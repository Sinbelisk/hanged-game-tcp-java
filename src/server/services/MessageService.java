package server.services;

import server.Worker;
import server.util.SimpleLogger;

import java.util.logging.Logger;

/**
 * Servicio de mensajería encargado de enviar mensajes servidor -> cliente.
 * <p>
 * Esta clase proporciona métodos para enviar mensajes
 * a los usuarios a través de su conexión establecida.
 * </p>
 */
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

    public void sendUserAlreadyAuth(Worker worker) {
        send("Error: ya has iniciado sesión", worker);
    }

    /**
     * Envía un mensaje al usuario asociado con el {@link Worker} proporcionado.
     * <p>
     * Si ocurre un error al enviar el mensaje, se registra en el log.
     * </p>
     *
     * @param msg    El mensaje que se enviará al usuario.
     * @param worker El trabajador que recibirá el mensaje.
     */
    public synchronized void send(String msg, Worker worker) {
        try {
            worker.getConnection().send(msg);
        } catch (Exception e) {
            logger.severe("[" + worker.getName() + "] Error al enviar mensaje: " + e.getMessage());
        }
    }

}
