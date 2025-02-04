package server.commands.commands;

import server.Worker;
import server.commands.Command;
import server.game.GameRoom;
import server.services.GameRoomManager;
import server.services.ServiceRegistry;

public class GameCommand extends AbstractCommand{
    private GameRoomManager gameRoomManager;

    private static final String CREATE_COMMAND = "create";
    private static final String ENTER_COMMAND = "enter";
    private static final String SOLO_COMMAND = "solo";
    private static final String EXIT_COMMAND = "exit";

    @Override
    public void execute(String[] args, Worker worker) {
        if(!isUserAuthenticated(worker)){
            messageService.sendUserMustAuth(worker);
            return;
        }

        if ((args == null || args.length < 1) && !worker.isPlaying()) {
            messageService.send(getCommandUsage(), worker);
            return;
        }

        String type = args[0];

        if (worker.isPlaying() && !type.equalsIgnoreCase(EXIT_COMMAND)) {
            messageService.send("Ya estas en una sala de juego.", worker);
            return;
        }

        switch (type.toLowerCase()) {
            case CREATE_COMMAND -> createGame(args, worker);
            case ENTER_COMMAND -> enterGame(args, worker);
            case SOLO_COMMAND -> startSoloGame(worker);
            case EXIT_COMMAND -> exitGame(worker);
            default -> messageService.send(getCommandUsage(), worker);
        }
    }

    private void exitGame(Worker worker) {
        if (worker.isPlaying()){
            worker.exitRoom();

            messageService.send("Has abandonado la sala de juego.", worker);
        }
    }

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

    private void enterGame(String[] args, Worker worker) {
        if (args.length < 2) {
            messageService.send("Necesitas introducir el nombre de la sala.", worker);
            return;
        }

        String roomName = args[1];

        GameRoom room = gameRoomManager.getRoom(roomName);

        if(room == null) {
            messageService.send("La sala " + roomName + " no existe", worker);
            return;
        }

        enterRoom(worker, room);
    }

    private void startSoloGame(Worker worker) {
        worker.setCurrentRoom(new GameRoom("Solo", true, messageService));
        messageService.send("Partida de un solo jugador iniciada.", worker);
    }

    private void enterRoom(Worker worker, GameRoom room) {
        worker.setCurrentRoom(room);
        messageService.send("Has entrado en la sala " + room.getName(), worker);
    }

    @Override
    public void assingServices(ServiceRegistry services) {
        super.assingServices(services);
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
