package server;

import common.BaseSocketHandler;
import common.SocketHandler;

import java.io.IOException;
import java.net.Socket;

public class ServerSocketHandler extends BaseSocketHandler {

    public ServerSocketHandler(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void handle() {

    }
}
