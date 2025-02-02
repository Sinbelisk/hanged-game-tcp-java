package common;

import java.io.*;
import java.net.Socket;

/**
 * Implementación de la interfaz Connection para conexiones basadas en Socket.
 */
public class SocketConnection implements Connection, AutoCloseable {
    private final Socket socket;
    private  BufferedReader reader;
    private BufferedWriter writer;

    public SocketConnection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void open() throws IOException {
        // Inicializa los streams para lectura y escritura
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void close() throws IOException {
        // Cierre de recursos en orden: reader, writer y socket.
        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    @Override
    public void send(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    @Override
    public String receive() throws IOException {
        return reader.readLine();
    }
}