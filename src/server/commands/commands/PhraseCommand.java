package server.commands.commands;

import server.Worker;
import server.commands.Command;
import server.game.GameRoom;
import server.services.ServiceRegistry;

/**
 * El comando {@code PhraseCommand} permite a un usuario intentar adivinar una frase en el juego.
 * Extiende de {@link AbstractCommand} para compartir la lógica común de los comandos y gestionar los servicios necesarios.
 */
public class PhraseCommand extends AbstractCommand {

    /**
     * Ejecuta el comando para permitir a un usuario intentar adivinar una frase en el juego.
     * Verifica si el usuario está autenticado, si el usuario está en una sala de juego y luego envía la frase al método
     * {@link GameRoom#guessPhrase(Worker, String)} para procesar la adivinanza.
     *
     * @param args Los argumentos del comando, que deben incluir la frase que el usuario desea adivinar.
     * @param worker usuario que ejecuta el comando.
     */
    @Override
    public void execute(String[] args, Worker worker) {
        // Verifica si el usuario está autenticado
        if(!isUserAuthenticated(worker)){
            messageService.sendUserMustAuth(worker);
            return;
        }

        // Une los argumentos en una sola frase
        String phrase = String.join(" ", args);

        // Verifica si el usuario está en una sala de juego
        GameRoom room = worker.getCurrentRoom();
        if(room == null){
            messageService.sendUserNotInGameRoom(worker);
            return;
        }

        room.guessPhrase(worker, phrase);
    }
}
