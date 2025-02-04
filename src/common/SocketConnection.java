package common;

import java.io.*;
import java.net.Socket;

import java.io.*;

/**
 * Implementación de la interfaz {@code Connection} para conexiones basadas en {@code Socket}.
 * Esta clase gestiona una conexión TCP, permitiendo enviar y recibir mensajes utilizando
 * flujos de entrada y salida a través de un {@code Socket}.
 * <p>
 * La clase también implementa {@code AutoCloseable}, lo que permite utilizarla en un bloque {@code try-with-resources}
 * para asegurar que los recursos se cierren correctamente.
 * </p>
 */
public class SocketConnection implements Connection, AutoCloseable {
    private final Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    /**
     * Crea una nueva instancia de {@code SocketConnection} utilizando un {@code Socket} proporcionado.
     *
     * @param socket El {@code Socket} que representa la conexión TCP.
     */
    public SocketConnection(Socket socket) {
        this.socket = socket;
    }

    /**
     * Abre la conexión TCP, inicializando los flujos de entrada y salida.
     *
     * @throws IOException Si ocurre un error al intentar abrir los flujos de entrada o salida.
     */
    @Override
    public void open() throws IOException {
        // Inicializa los streams para lectura y escritura
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Cierra la conexión TCP y libera los recursos asociados, cerrando los flujos y el socket.
     *
     * @throws IOException Si ocurre un error al intentar cerrar los flujos o el socket.
     */
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

    /**
     * Envía un mensaje a través de la conexión TCP.
     * El mensaje es escrito en el flujo de salida, seguido de un salto de línea para marcar el final.
     *
     * @param message El mensaje a enviar.
     * @throws IOException Si ocurre un error al intentar escribir el mensaje en el flujo de salida.
     */
    @Override
    public void send(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    /**
     * Recibe un mensaje desde la conexión TCP.
     * Lee una línea del flujo de entrada y la devuelve como un {@code String}.
     *
     * @return El mensaje recibido desde la conexión.
     * @throws IOException Si ocurre un error al intentar leer desde el flujo de entrada.
     */
    @Override
    public String receive() throws IOException {
        return reader.readLine();
    }
}
