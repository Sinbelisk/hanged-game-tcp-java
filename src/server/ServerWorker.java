package server;

import common.BaseSocketHandler;

import java.io.IOException;
import java.net.Socket;

public class ServerWorker extends BaseSocketHandler implements Runnable {

    public ServerWorker(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void run() {

    }

    @Override
    public void handle() {

    }
}
