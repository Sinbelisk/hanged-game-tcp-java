package server.commands.commands;

import server.Worker;

/**
 * El comando {@code ExitCommand} permite al usuario desconectarse del servidor, cerrando su conexión.
 * Extiende de {@link AbstractCommand} para compartir la lógica común de los comandos y manejar los servicios necesarios.
 */
public class ExitCommand extends AbstractCommand {

    /**
     * Ejecuta el comando para finalizar la conexión del usuario con el servidor.
     * Detiene el trabajador (usuario) y envía un mensaje confirmando la desconexión.
     *
     * @param elements Los argumentos del comando. No se usan en este comando, ya que simplemente termina la conexión.
     * @param worker El trabajador (usuario) que ejecuta el comando.
     */
    @Override
    public void execute(String[] elements, Worker worker) {
        worker.stopWorker();
        messageService.send("Se ha terminado la conexión con el servidor.", worker);
    }
}

