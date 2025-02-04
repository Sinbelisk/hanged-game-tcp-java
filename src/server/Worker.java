package server;

import common.SocketConnection;
import server.commands.Command;
import server.commands.CommandFactory;
import server.game.GameRoom;
import server.services.GameRoomManager;
import server.services.MessageService;
import server.services.ServiceRegistry;
import util.SimpleLogger;
import util.StringUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker extends Thread {
    private final Socket clientSocket;
    private final Logger logger = SimpleLogger.getInstance().getLogger(getClass());
    private final ServiceRegistry services;
    private User user = null;

    private volatile boolean running = true;
    private MessageService messageService;
    private GameRoom currentRoom;
    private SocketConnection connection;

    public Worker(Socket clientSocket, ServiceRegistry services) {
        this.clientSocket = clientSocket;
        this.services = services;
        logger.info("[" + getName() + "] Hilo de cliente inicializado.");
    }

    @Override
    public void run() {
        try {
            initializeConnection();
            processClientRequests();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "[" + getName() + "] Error en la comunicación con el cliente.", ex);
        } finally {
            cleanup();
        }
    }

    private void initializeConnection() throws IOException {
        connection = new SocketConnection(clientSocket);
        connection.open();
        messageService = new MessageService(connection, user);
        logger.info("[" + getName() + "] Conexión establecida.");
    }

    private void processClientRequests() throws IOException {
        while (running) {
            String received = connection.receive();
            if (received == null) break;

            logger.info("[" + getName() + "] Recibido mensaje: " + received);
            processCommand(received);
        }
    }

    private void processCommand(String received) {
        String[] commandArgs = received.split("\\s");
        if (commandArgs.length == 0) return;

        Command command = parseCommand(commandArgs[0]);
        if (command != null) {
            executeCommand(command, commandArgs);
        } else {
            messageService.sendUnknownCommand();
            logger.warning("[" + getName() + "] Comando desconocido: " + commandArgs[0]);
        }
    }

    private Command parseCommand(String commandName) {
        if (!commandName.startsWith("/")) return null;
        return services.getService(CommandFactory.class).createCommand(commandName);
    }

    private void executeCommand(Command command, String[] args) {
        command.assingServices(services);
        command.execute(StringUtils.parseArguments(args), this);
        logger.info("[" + getName() + "] Comando ejecutado: " + args[0]);
    }

    private void cleanup() {
        running = false;
        try {
            if (connection != null) connection.close();
        } catch (IOException e) {
            logger.warning("[" + getName() + "] Error cerrando conexión.");
        }
        logger.info("[" + getName() + "] Hilo finalizado.");
    }

    public void stopWorker() {
        running = false;
        logger.info("[" + getName() + "] Worker detenido.");
    }

    public void setUser(User user) {
        this.user = user.isAuthenticated() ? user : null;
        logger.info("[" + getName() + "] Usuario autenticado: " + (this.user != null));
    }

    public void setCurrentRoom(GameRoom currentRoom) {
        this.currentRoom = currentRoom;
        currentRoom.addPlayer(this);
        logger.info("[" + getName() + "] Entró en sala: " + currentRoom.getName());
        services.getService(GameRoomManager.class).checkAndStartGame(currentRoom.getName());
    }

    public void exitRoom() {
        if (currentRoom.isEmpty()) {
            services.getService(GameRoomManager.class).removeRoom(currentRoom.getName());
            logger.info("[" + getName() + "] Sala eliminada: " + currentRoom.getName());
        }
        currentRoom.removePlayer(this);
        logger.info("[" + getName() + "] Salió de la sala: " + currentRoom.getName());
        currentRoom = null;
    }

    public boolean isPlaying() {
        return currentRoom != null;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public User getUser() {
        return user;
    }

    public GameRoom getCurrentRoom() {
        return currentRoom;
    }
}
