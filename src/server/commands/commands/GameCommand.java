package server.commands.commands;

import server.Worker;
import server.game.GameRoom;
import server.services.GameRoomManager;
import server.services.ServiceRegistry;

/**
 * El comando {@code GameCommand} permite a los usuarios gestionar las salas de juego en el sistema, incluyendo la creación, entrada, salida y la opción de iniciar una partida en solitario.
 * Este comando extiende {@link AbstractCommand} para compartir la lógica común de los comandos y gestionar los servicios necesarios.
 */
public class GameCommand extends AbstractCommand {
    private GameRoomManager gameRoomManager;

    private static final String CREATE_COMMAND = "create";
    private static final String ENTER_COMMAND = "enter";
    private static final String SOLO_COMMAND = "solo";
    private static final String EXIT_COMMAND = "exit";

    /**
     * Ejecuta el comando según el tipo de acción que el usuario desee realizar en relación con las salas de juego.
     * Las acciones disponibles incluyen crear una sala, entrar en una sala existente, iniciar una partida de un solo jugador, o salir de una sala.
     * Si el usuario no está autenticado, se le pedirá que lo haga antes de continuar.
     *
     * @param args Los argumentos del comando, que especifican la acción deseada y los parámetros correspondientes.
     * @param worker El trabajador (usuario) que ejecuta el comando.
     */
    @Override
    public void execute(String[] args, Worker worker) {
        // Verifica si el usuario está autenticado
        if (!isUserAuthenticated(worker)) {
            messageService.sendUserMustAuth(worker);
            return;
        }

        // Verifica que se haya proporcionado un tipo de acción, y si el usuario no está jugando, le proporciona la sintaxis del comando
        if ((args == null || args.length < 1) && !worker.isPlaying()) {
            messageService.send(getCommandUsage(), worker);
            return;
        }

        String type = args[0];

        // Si el usuario ya está jugando y no está pidiendo salir, le notifica que ya está en una sala
        if (worker.isPlaying() && !type.equalsIgnoreCase(EXIT_COMMAND)) {
            messageService.send("Ya estas en una sala de juego.", worker);
            return;
        }

        // Ejecuta la acción correspondiente según el tipo
        switch (type.toLowerCase()) {
            case CREATE_COMMAND -> createGame(args, worker);
            case ENTER_COMMAND -> enterGame(args, worker);
            case SOLO_COMMAND -> startSoloGame(worker);
            case EXIT_COMMAND -> exitGame(worker);
            default -> messageService.send(getCommandUsage(), worker);
        }
    }

    /**
     * Sale de la sala de juego actual si el usuario está en una.
     * Si el usuario está en medio de una partida, el juego se cancela.
     *
     * @param worker El trabajador (usuario) que ejecuta el comando de salida.
     */
    private void exitGame(Worker worker) {
        if (worker.isPlaying()) {
            worker.exitRoom();
            messageService.send("Has abandonado la sala de juego.", worker);
        }
    }

    /**
     * Crea una nueva sala de juego con el nombre especificado.
     * La sala requiere que haya 3 jugadores para comenzar la partida.
     *
     * @param args Los argumentos del comando, que incluyen el nombre de la sala.
     * @param worker El trabajador (usuario) que ejecuta el comando de creación de la sala.
     */
    private void createGame(String[] args, Worker worker) {
        if (args.length < 2) {
            messageService.send("La sala necesita un nombre.", worker);
            return;
        }

        String roomName = args[1];

        GameRoom room = new GameRoom(roomName, false, messageService);
        gameRoomManager.addRoom(room);

        worker.setCurrentRoom(room);
        messageService.send("Sala creada: " + roomName, worker);
    }

    /**
     * Permite a un usuario unirse a una sala existente por su nombre.
     *
     * @param args Los argumentos del comando, que incluyen el nombre de la sala a la que unirse.
     * @param worker El trabajador (usuario) que ejecuta el comando de entrada.
     */
    private void enterGame(String[] args, Worker worker) {
        if (args.length < 2) {
            messageService.send("Necesitas introducir el nombre de la sala.", worker);
            return;
        }

        String roomName = args[1];

        GameRoom room = gameRoomManager.getRoom(roomName);

        if (room == null) {
            messageService.send("La sala " + roomName + " no existe", worker);
            return;
        }

        enterRoom(worker, room);
    }

    /**
     * Inicia una partida de un solo jugador.
     * Crea automáticamente una sala de un jugador y la asigna al usuario.
     *
     * @param worker El trabajador (usuario) que ejecuta el comando de iniciar una partida en solitario.
     */
    private void startSoloGame(Worker worker) {
        messageService.send("Partida de un solo jugador iniciada.", worker);
        worker.setCurrentRoom(new GameRoom("Solo", true, messageService));
    }

    /**
     * Permite al usuario entrar en una sala específica.
     *
     * @param worker El trabajador (usuario) que ejecuta el comando de entrar en la sala.
     * @param room La sala a la que el usuario desea unirse.
     */
    private void enterRoom(Worker worker, GameRoom room) {
        messageService.send("Has entrado en la sala " + room.getName(), worker);
        worker.setCurrentRoom(room);
    }

    /**
     * Asocia los servicios necesarios con el comando, incluyendo el {@link GameRoomManager} para gestionar las salas de juego.
     *
     * @param services El registro de servicios que contiene todos los servicios disponibles.
     */
    @Override
    public void setServices(ServiceRegistry services) {
        super.setServices(services);
        gameRoomManager = services.getService(GameRoomManager.class);
    }

    /**
     * Devuelve la sintaxis y descripción de los argumentos válidos para el comando {@code /game}.
     *
     * @return Una cadena de texto con la descripción de los argumentos válidos.
     */
    private String getCommandUsage() {
        return """
                /game <args>
                ----------------------------------------------------------------------------------------
                Argumentos válidos:
                create <name>: crea una nueva sala, son necesarios 3 jugadores para comenzar la partida.
                enter <name>: entra en una sala ya existente.
                exit: sales de la sala actual, si lo usas en mitad de una partida se cancelará el juego.
                solo: inicia una partida de un solo jugador.
                """;
    }
}
