package server;

import common.SocketConnection;
import server.game.GameRoom;
import server.game.User;
import server.services.CommandManager;
import server.services.GameRoomManager;
import server.services.ServiceRegistry;
import server.util.SimpleLogger;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Representa un trabajador (hilo) que gestiona la conexión de un cliente en el servidor.
 * Cada {@code Worker} se ejecuta en su propio hilo y es responsable de recibir y procesar las solicitudes de un cliente,
 * así como de gestionar la autenticación del usuario y la entrada/salida de las salas de juego.
 *
 * Esta clase extiende {@link Thread} para ejecutar el procesamiento de cada cliente en un hilo independiente.
 * <p>
 * El trabajador mantiene la conexión con el cliente, procesa los comandos recibidos y gestiona el ciclo de vida de la sesión.
 * </p>
 * TODO: cambiar el formato de los logs para que tenga mas consistencia.
 */
public class Worker extends Thread {
    private final Socket clientSocket;
    private final Logger logger = SimpleLogger.getInstance().getLogger(getClass());
    private final ServiceRegistry services;
    private User user = null;

    private volatile boolean running = true;
    private GameRoom currentRoom;
    private SocketConnection connection;

    /**
     * Constructor para crear un nuevo trabajador que manejará la conexión de un cliente.
     *
     * @param clientSocket El socket de cliente asociado a esta conexión.
     * @param services El registro de servicios que proporciona acceso a las dependencias necesarias.
     */
    public Worker(Socket clientSocket, ServiceRegistry services) {
        this.clientSocket = clientSocket;
        this.services = services;
        logger.info("[" + getName() + "] Hilo de cliente inicializado.");
    }

    /**
     * Método principal que se ejecuta cuando el hilo comienza.
     * Inicializa la conexión con el cliente y procesa las solicitudes mientras el trabajador esté en ejecución.
     */
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

    /**
     * Inicializa la conexión con el cliente mediante un {@link SocketConnection}.
     * Abre los flujos de entrada y salida para la comunicación.
     *
     * @throws IOException Si ocurre un error al abrir la conexión.
     */
    private void initializeConnection() throws IOException {
        connection = new SocketConnection(clientSocket);
        connection.open();
        logger.info("[" + getName() + "] Conexión establecida.");
    }

    /**
     * Procesa las solicitudes del cliente mientras el trabajador esté en ejecución.
     * Recibe los mensajes del cliente y los pasa al {@link CommandManager} para su procesamiento.
     *
     * @throws IOException Si ocurre un error al recibir los mensajes del cliente.
     */
    private void processClientRequests() throws IOException {
        while (running) {
            String received = connection.receive();
            if (received == null) break;

            logger.info("[" + getName() + "] Recibido mensaje: " + received);
            services.getService(CommandManager.class).processCommand(received, this);
        }
    }

    /**
     * Realiza la limpieza al finalizar el procesamiento, cerrando la conexión del cliente y finalizando el hilo.
     */
    private void cleanup() {
        running = false;
        try {
            if (connection != null) connection.close();
        } catch (IOException e) {
            logger.warning("[" + getName() + "] Error cerrando conexión.");
        }
        logger.info("[" + getName() + "] Hilo finalizado.");
    }

    /**
     * Detiene el trabajador, terminando la ejecución del hilo.
     */
    public void stopWorker() {
        running = false;
        logger.info("[" + getName() + "] Worker detenido.");
    }

    /**
     * Establece el usuario autenticado para este trabajador.
     *
     * @param user El usuario que se autentica en la sesión.
     */
    public void setUser(User user) {
        this.user = user.isAuthenticated() ? user : null;
        logger.info("[" + getName() + "] Usuario autenticado: " + (this.user != null));
    }

    /**
     * Establece la sala de juego actual para este trabajador y añade al jugador a la sala.
     * Si la sala tiene suficientes jugadores, puede iniciar la partida.
     *
     * @param currentRoom La sala en la que el trabajador entra.
     */
    public void setCurrentRoom(GameRoom currentRoom) {
        this.currentRoom = currentRoom;
        currentRoom.addPlayer(this);
        logger.info("[" + getName() + "] Entró en sala: " + currentRoom.getName());
        services.getService(GameRoomManager.class).checkAndStartGame(currentRoom.getName());
    }

    /**
     * Hace que el trabajador salga de la sala actual. Si la sala está vacía, la elimina.
     */
    public void exitRoom() {
        if (currentRoom.isEmpty()) {
            services.getService(GameRoomManager.class).removeRoom(currentRoom.getName());
            logger.info("[" + getName() + "] Sala eliminada: " + currentRoom.getName());
        }
        currentRoom.removePlayer(this);
        logger.info("[" + getName() + "] Salió de la sala: " + currentRoom.getName());
        currentRoom = null;
    }

    /**
     * Verifica si el trabajador está actualmente en una sala de juego.
     *
     * @return {@code true} si el trabajador está en una sala de juego, {@code false} en caso contrario.
     */
    public boolean isPlaying() {
        return currentRoom != null;
    }

    /**
     * Obtiene el usuario autenticado para este trabajador.
     *
     * @return El usuario autenticado, o {@code null} si no está autenticado.
     */
    public User getUser() {
        return user;
    }

    /**
     * Obtiene la sala de juego actual en la que se encuentra el trabajador.
     *
     * @return La sala de juego actual, o {@code null} si no está en ninguna sala.
     */
    public GameRoom getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Obtiene la conexión del socket para este trabajador.
     *
     * @return La conexión del socket con el cliente.
     */
    public SocketConnection getConnection() {
        return connection;
    }
}
