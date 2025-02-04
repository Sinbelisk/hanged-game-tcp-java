package common;

import java.io.IOException;

/**
 * La interfaz {@code Connection} define los métodos necesarios para gestionar una conexión TCP.
 * Permite abrir, cerrar, enviar y recibir mensajes a través de la conexión.
 *
 * Esta interfaz es ideal para ser implementada en clases que gestionen una comunicación basada en TCP,
 * como clientes o servidores en un juego o aplicación que requiera intercambio de mensajes en tiempo real.
 */
public interface Connection {

    /**
     * Establece una conexión con el servidor o cliente a través de TCP.
     *
     * @throws IOException Si ocurre un error al intentar abrir la conexión.
     */
    void open() throws IOException;

    /**
     * Cierra la conexión TCP actual.
     *
     * @throws IOException Si ocurre un error al intentar cerrar la conexión.
     */
    void close() throws IOException;

    /**
     * Envía un mensaje al otro extremo de la conexión.
     *
     * @param message El mensaje a enviar.
     * @throws IOException Si ocurre un error al intentar enviar el mensaje.
     */
    void send(String message) throws IOException;

    /**
     * Recibe un mensaje desde el otro extremo de la conexión.
     *
     * @return El mensaje recibido.
     * @throws IOException Si ocurre un error al intentar recibir el mensaje.
     */
    String receive() throws IOException;
}

