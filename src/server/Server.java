package server;

import server.services.ServiceRegistry;
import server.util.SimpleLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Representa un servidor que acepta conexiones TCP de clientes y gestiona las solicitudes mediante un pool de hilos.
 * El servidor puede manejar múltiples conexiones simultáneamente, ejecutando cada solicitud en un hilo separado.
 * Utiliza un {@code ExecutorService} para gestionar un pool de hilos que procesan a los trabajadores (clientes).
 * <p>
 * El servidor también mantiene un registro de servicios mediante {@link ServiceRegistry}, que pueden ser utilizados por los trabajadores.
 * </p>
 */
public class Server {
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final Logger logger = SimpleLogger.getInstance().getLogger(Server.class);
    private final ExecutorService pool;
    private final ServiceRegistry services;

    private final int port;
    private boolean running = true;

    /**
     * Crea una instancia del servidor en el puerto especificado con un tamaño de pool de hilos dado.
     * Si el tamaño del pool es menor o igual a cero, se utilizará el valor predeterminado de 10.
     *
     * @param port El puerto en el que el servidor escuchará las conexiones de los clientes.
     * @param poolSize El tamaño del pool de hilos para gestionar las conexiones entrantes.
     */
    public Server(int port, int poolSize) {
        this.port = port;
        this.pool = Executors.newFixedThreadPool(poolSize > 0 ? poolSize : DEFAULT_POOL_SIZE);
        services = new ServiceRegistry();
    }

    /**
     * Inicia el servidor, creando un socket de servidor y aceptando conexiones entrantes.
     * Por cada conexión aceptada, se crea un nuevo {@link Worker} para manejarla, que se ejecutará en un hilo del pool.
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Servidor iniciado en el puerto: " + port);

            while (running) {
                try {
                    // Acepta una nueva conexión de cliente
                    Socket clientSocket = serverSocket.accept();

                    if (clientSocket != null && !clientSocket.isClosed()) {
                        logger.info("Conexión aceptada de: " + clientSocket.getInetAddress());

                        // Crea un trabajador (hilo) para gestionar la conexión del cliente
                        Worker thread = new Worker(clientSocket, services);
                        thread.setName(generateThreadId(clientSocket));
                        pool.execute(thread);
                    }

                } catch (IOException e) {
                    logger.severe("Error al aceptar la conexión de cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.severe("Error al iniciar servidor en el puerto: " + port + ": " + e.getMessage());
        }
    }

    /**
     * Genera un identificador único para el hilo basado en la dirección IP y el puerto del cliente.
     *
     * @param socket El socket de cliente del que se extraen la dirección y el puerto.
     * @return Un identificador de hilo en formato {@code "<dirección_ip>:<puerto>"}.
     */
    private String generateThreadId(Socket socket) {
        String address = socket.getInetAddress().getHostAddress();
        int port = socket.getPort();
        return address + ":" + port;
    }

    /**
     * Método principal que inicia el servidor en el puerto 2050 con un pool de 10 hilos.
     *
     * @param args Argumentos de línea de comandos (no utilizados en esta implementación).
     */
    public static void main(String[] args) {
        Server server = new Server(2050, 10);
        server.start();
    }
}
