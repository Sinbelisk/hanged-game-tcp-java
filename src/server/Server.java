package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int port;
    private boolean running = true;
    public Server(int port) {
        this.port = port;
    }

    public void start(){
        try(ServerSocket serverSocket = new ServerSocket(port)){

            while(running){
                Socket clientSocket = serverSocket.accept();
                Worker worker = new Worker(clientSocket);
                worker.start();
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
