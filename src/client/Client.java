package client;

import common.Connection;
import common.SocketConnection;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private final String host;
    private final int port;

    public Client(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void connect(){
        try(Socket socket = new Socket(host, port)){
            Connection connection = new SocketConnection(socket);
            connection.open();

            while(socket.isConnected() && !socket.isClosed()){

            }

        } catch (IOException e){
            // shhh.
        }
    }
}
