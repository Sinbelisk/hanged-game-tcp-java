package server;

import server.services.ServiceRegistry;
import util.SimpleLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final Logger logger = SimpleLogger.getInstance().getLogger(Server.class);
    private final ExecutorService pool;
    private final ServiceRegistry services;

    private final int port;
    private boolean running = true;

    public Server(int port, int poolSize) {
        this.port = port;
        this.pool = Executors.newFixedThreadPool(poolSize > 0 ? poolSize : DEFAULT_POOL_SIZE);
        services = new ServiceRegistry();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Servidor iniciado en el puerto: " + port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    if (clientSocket != null && !clientSocket.isClosed()) {
                        logger.info("Conexión aceptada de: " + clientSocket.getInetAddress());

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

    private String generateThreadId(Socket socket) {
        String address = socket.getInetAddress().getHostAddress();
        int port = socket.getPort();
        return address + ":" + port;
    }


    public static void main(String[] args) {
        Server server = new Server(2050, 10);
        server.start();
    }
}


