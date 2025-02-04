package server.commands.commands;

import server.Worker;

/**
 * El comando {@code HelpCommand} proporciona al usuario una lista de los comandos disponibles y su uso.
 * Extiende de {@link AbstractCommand} para compartir la lógica común de los comandos y manejar los servicios necesarios.
 */
public class HelpCommand extends AbstractCommand {

    /**
     * Ejecuta el comando para mostrar al usuario la lista de comandos disponibles y su descripción.
     * Si el usuario está autenticado, se le envía la información. Si no, se le solicita autenticarse primero.
     *
     * @param args Los argumentos del comando (no se usan en este caso).
     * @param worker El trabajador (usuario) que ejecuta el comando.
     */
    @Override
    public void execute(String[] args, Worker worker) {
        if(!isUserAuthenticated(worker)) {
            messageService.send(getLimitedHelpMessage(), worker);
        }

        // Envía la información sobre los comandos disponibles al usuario
        messageService.send(getHelpMessage(), worker);
    }

    /**
     * Devuelve un mensaje con el uso de los comandos disponibles.
     *
     * @return Un string con la lista de comandos y su descripción.
     */
    private String getHelpMessage() {
        return """
                Comandos disponibles:
                ----------------------------------------
                /login <usuario> <contraseña>: Inicia sesión con el usuario y la contraseña.
                /register <usuario> <contraseña>: Registra un nuevo usuario con el nombre y la contraseña.
                /exit: Finaliza la conexión con el servidor.
                
                Comandos disponibles en el juego:
                ----------------------------------------
                /vowel <vocal>: Adivina una vocal en el juego.
                /consonant <consonante>: Adivina una consonante en el juego.
                /phrase <frase>: Adivina una frase completa en el juego.
                
                Comando para empezar el juego:
                ----------------------------------------
                /game <comando>: Administra las salas de juego.
                    - create <nombre>: Crea una nueva sala.
                    - enter <nombre>: Entra a una sala existente.
                    - exit: Sale de la sala de juego.
                    - solo: Inicia una partida en solitario.
                """;
    }
    /**
     * Devuelve un mensaje con el uso de los comandos de registro y inicio de sesión.
     *
     * @return Un string con la lista de comandos y su descripción.
     */
    private String getLimitedHelpMessage() {
        return """
                Comandos disponibles:
                ----------------------------------------
                /login <usuario> <contraseña>: Inicia sesión con el usuario y la contraseña.
                /register <usuario> <contraseña>: Registra un nuevo usuario con el nombre y la contraseña.
                /exit: Finaliza la conexión con el servidor""";
    }
}

