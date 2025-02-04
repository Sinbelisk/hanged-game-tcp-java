package server.commands.commands;

import server.Worker;
import server.commands.Command;
import server.game.GameRoom;
import server.services.ServiceRegistry;

/**
 * El comando {@code VowelCommand} permite a un jugador adivinar una vocal en el juego.
 * Extiende de {@link AbstractCommand} para compartir la lógica común de los comandos y manejar los servicios necesarios.
 */
public class VowelCommand extends AbstractCommand {

    /**
     * Ejecuta el comando para que el usuario adivine una vocal.
     * Primero verifica si el usuario está autenticado y si está en una sala de juego.
     * Luego, pasa la vocal al método correspondiente en {@link GameRoom} para procesar el intento.
     *
     * @param args Los argumentos del comando, donde {@code args[0]} debe contener la vocal que el usuario quiere adivinar.
     * @param worker El trabajador (usuario) que ejecuta el comando.
     */
    @Override
    public void execute(String[] args, Worker worker) {
        // Verifica si el usuario está autenticado
        if (!isUserAuthenticated(worker)) {
            messageService.sendUserMustAuth(worker);
            return;
        }

        // Extrae la vocal del primer argumento
        char vowel = args[0].charAt(0);

        // Obtiene la sala de juego del trabajador
        GameRoom room = worker.getCurrentRoom();
        if (room == null) {
            messageService.sendUserNotInGameRoom(worker);
            return;
        }

        // Llama al método para procesar el intento de la vocal en la sala de juego
        room.guessLetter(worker, vowel, true);
    }
}
