package common;

import java.io.*;
import java.net.*;

public abstract class BaseSocketHandler implements SocketHandler, AutoCloseable {
    protected Socket socket;
    protected DataInputStream input;
    protected DataOutputStream output;

    public BaseSocketHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
    }

    public String receiveMessage() throws IOException {
        return input.readUTF();
    }

    public void sendMessage(String message) throws IOException {
        output.writeUTF(message);
    }

    @Override
    public void close() throws IOException {
        input.close();
        output.close();
        socket.close();
    }
}

