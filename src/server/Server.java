package server;

import server.services.ServiceRegistry;
import server.services.WorkerManager;
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

    public Server(int port) {
        this(port, DEFAULT_POOL_SIZE);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server started on port: " + port);  // Log en lugar de println

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    if (clientSocket != null && !clientSocket.isClosed()) {
                        logger.info("Accepted connection from " + clientSocket.getInetAddress());
                        Worker thread = new Worker(clientSocket, services);
                        pool.execute(thread);
                        services.getService(WorkerManager.class).addWorker(thread);
                    }

                } catch (IOException e) {
                    logger.severe("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.severe("Error starting server on port " + port + ": " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        Server server = new Server(2050, 10);
        server.start();
    }
}


