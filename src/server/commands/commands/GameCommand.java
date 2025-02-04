package server.commands.commands;

import server.Worker;
import server.commands.Command;
import server.game.GameRoom;
import server.services.GameRoomManager;
import server.services.ServiceRegistry;

public class GameCommand implements Command {
    private GameRoomManager gameRoomManager;

    private static final String CREATE_COMMAND = "create";
    private static final String ENTER_COMMAND = "enter";
    private static final String SOLO_COMMAND = "solo";
    private static final String EXIT_COMMAND = "exit";
    private static final int MIN_PLAYERS = 3;

    @Override
    public void execute(String[] args, Worker worker) {
        if ((args == null || args.length < 1) && !worker.isPlaying()) {
            worker.getMessageService().send(getCommandUsage());
            return;
        }

        if(!worker.getUser().isAuthenticated()){
            worker.getMessageService().send("No estas autenticado, registrate o inicia sesión.");
        }

        String type = args[0];

        if (worker.isPlaying() && !type.equalsIgnoreCase(EXIT_COMMAND)) {
            worker.getMessageService().sendPlayerCurrentlyPlaying();
            return;
        }

        switch (type.toLowerCase()) {
            case CREATE_COMMAND -> createGame(args, worker);
            case ENTER_COMMAND -> enterGame(args, worker);
            case SOLO_COMMAND -> startSoloGame(worker);
            case EXIT_COMMAND -> exitGame(worker);
            default -> worker.getMessageService().send(getCommandUsage());
        }
    }

    private void exitGame(Worker worker) {
        if (worker.isPlaying()){
            worker.exitRoom();

            worker.getMessageService().sendUserExittedRoom();
        }
    }

    private void createGame(String[] args, Worker worker) {
        if (args.length < 2) {
            worker.getMessageService().send("La sala necesita un nombre.");
            return;
        }

        String roomName = args[1];

        GameRoom room = new GameRoom(roomName, false);
        gameRoomManager.addRoom(room);


        worker.setCurrentRoom(room);
        worker.getMessageService().send("Sala creada: " + roomName);
    }

    private void enterGame(String[] args, Worker worker) {
        if (args.length < 2) {
            worker.getMessageService().send("Necesitas introducir el nombre de la sala.");
            return;
        }

        String roomName = args[1];

        GameRoom room = gameRoomManager.getRoom(roomName);

        if(room == null) {
            worker.getMessageService().send("La sala " + roomName + " no existe");
            return;
        }

        enterRoom(worker, room);
    }

    private void startSoloGame(Worker worker) {
        worker.setCurrentRoom(new GameRoom("Solo", true));
        worker.getMessageService().send("Partida de un solo jugador iniciada.");
    }

    private void enterRoom(Worker worker, GameRoom room) {
        worker.setCurrentRoom(room);
        worker.getMessageService().send("Has entrado en la sala " + room.getName());
    }

    @Override
    public void assingServices(ServiceRegistry services) {
        gameRoomManager = services.getService(GameRoomManager.class);
    }

    private String getCommandUsage() {
        return """
                /game <args>
                
                Argumentos válidos:
                create <name>: crea una nueva sala, son necesarios 3 jugadores para comenzar la partida.
                enter <name>: entra en una sala ya existente.
                exit: sales de la sala actual, si lo usas en mitad de una partida se cancelará el juego.
                solo: inicia una partida de un solo jugador.
                """;
    }
}
