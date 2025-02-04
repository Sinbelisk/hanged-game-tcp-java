package server.commands;

import server.services.ServiceRegistry;
import server.Worker;

/**
 * La interfaz Command representa un comando que recibe el servidor.
 * Cada comando tiene un comportamiento asociado cuando es ejecutado por el usuario.
 */
public interface Command {
    /**
     * Ejecuta el comando con los elementos (argumentos) proporcionados, el mensaje del cliente y el usuario.
     *
     * @param elements Los argumentos divididos de la cadena de comando.
     * @param worker El hilo de la conexión del cliente.
     */
    void execute(String[] elements, Worker worker);

    /**
     * Asigna los servicios especificados al comando.
     * @param services El registro de servicios.
     */
    void setServices(ServiceRegistry services);
}
