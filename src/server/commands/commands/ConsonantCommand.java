package server.commands.commands;

import server.Worker;
import server.game.GameRoom;

/**
 * El comando {@code ConsonantCommand} permite a un jugador adivinar una consonante en el juego.
 * Extiende de {@link AbstractCommand} para compartir la lógica común de los comandos y manejar los servicios necesarios.
 */
public class ConsonantCommand extends AbstractCommand {

    /**
     * Ejecuta el comando para que el usuario adivine una consonante.
     * Primero verifica si el usuario está autenticado y si está en una sala de juego.
     * Luego, pasa la consonante al método correspondiente en {@link GameRoom} para procesar el intento.
     *
     * @param args Los argumentos del comando, donde {@code args[0]} debe contener la consonante que el usuario quiere adivinar.
     * @param worker El trabajador (usuario) que ejecuta el comando.
     */
    @Override
    public void execute(String[] args, Worker worker) {
        // Verifica si el usuario está autenticado
        if (!isUserAuthenticated(worker)) {
            messageService.sendUserMustAuth(worker);
            return;
        }

        // Extrae la consonante del primer argumento
        char consonant = args[0].charAt(0);

        // Obtiene la sala de juego del trabajador
        GameRoom room = worker.getCurrentRoom();
        if (room == null) {
            messageService.sendUserNotInGameRoom(worker);
            return;
        }

        // Llama al método para procesar el intento de la consonante en la sala de juego
        room.guessLetter(worker, consonant, false);
    }
}
