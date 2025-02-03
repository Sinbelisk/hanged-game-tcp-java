package server.commands.commands;

import server.Worker;
import server.commands.Command;
import server.game.GameRoom;
import server.services.GameRoomManager;
import server.services.ServiceRegistry;
import server.services.UserManager;

public class GameCommand implements Command {
    private UserManager userManager;
    private GameRoomManager gameRoomManager;

    private static final String CREATE_COMMAND = "create";
    private static final String ENTER_COMMAND = "enter";
    private static final String SOLO_COMMAND = "solo";
    private static final int MIN_PLAYERS = 3;

    @Override
    public void execute(String[] args, Worker worker) {
        if (args == null || args.length < 1) {
            worker.getMessageService().send(getCommandUsage());
            return;
        }

        String type = args[0];

        if (worker.isPlaying()) {
            worker.getMessageService().sendPlayerCurrentlyPlaying();
            return;
        }

        switch (type.toLowerCase()) {
            case CREATE_COMMAND:
                createGame(args, worker);
                break;
            case ENTER_COMMAND:
                enterGame(args, worker);
                break;
            case SOLO_COMMAND:
                startSoloGame(worker);
                break;
            default:
                worker.getMessageService().send(getCommandUsage());
        }
    }

    private void createGame(String[] args, Worker worker) {
        if (args.length < 2) {
            worker.getMessageService().send("La sala necesita un nombre.");
            return;
        }

        String roomName = args[1];

        GameRoom room = new GameRoom(roomName, true);
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
        }
        else {
            enterRoom(worker, room);
        }
    }

    private void startSoloGame(Worker worker) {
        worker.setCurrentRoom(new GameRoom("Solo", false));
        worker.getMessageService().send("Partida de un solo jugador iniciada.");
    }

    private void enterRoom(Worker worker, GameRoom room) {
        worker.setCurrentRoom(room);
        worker.getMessageService().send("Has entrado en la sala " + room.getName());
    }

    @Override
    public void assingServices(ServiceRegistry services) {
        userManager = services.getService(UserManager.class);
        gameRoomManager = services.getService(GameRoomManager.class);
    }

    private String getCommandUsage() {
        return """
                /game <args>
                
                Argumentos válidos:
                create <name>: crea una nueva sala, son necesarios 3 jugadores para comenzar la partida.
                enter <name>: entra en una sala ya existente.
                solo: inicia una partida de un solo jugador.
                """;
    }
}
