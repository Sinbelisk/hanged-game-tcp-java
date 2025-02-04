package server.services;

import server.Worker;
import server.commands.Command;
import server.commands.CommandFactory;
import server.util.SimpleLogger;

import java.util.logging.Logger;

/**
 * Gestor de comandos del servidor.
 * <p>
 * Esta clase se encarga de procesar los comandos recibidos de los clientes,
 * validarlos y ejecutarlos a través del {@code CommandFactory}.
 * </p>
 */
public class CommandManager {
    private static final Logger logger = SimpleLogger.getInstance().getLogger(CommandManager.class);
    private final CommandFactory commandFactory;
    private final ServiceRegistry services;
    private final MessageService messageService;

    /**
     * Constructor de {@code CommandManager}.
     *
     * @param services Registro de servicios del servidor para gestionar dependencias.
     */
    public CommandManager(ServiceRegistry services) {
        this.services = services;
        this.commandFactory = new CommandFactory(services);
        this.messageService = services.getService(MessageService.class);
    }

    /**
     * Procesa un comando recibido desde un cliente.
     *
     * @param received Cadena de texto con el comando recibido.
     * @param worker   Instancia del {@code Worker} que envió el comando.
     */
    public void processCommand(String received, Worker worker) {
        String[] commandArgs = received.split("\\s");
        if (commandArgs.length == 0) return;

        Command command = parseCommand(commandArgs[0]);
        if (command != null) {
            executeCommand(command, commandArgs, worker);
        } else {
            messageService.sendUnknownCommand(worker);
            logger.warning("[" + worker.getName() + "] Comando desconocido: " + commandArgs[0]);
        }
    }

    /**
     * Analiza el nombre del comando y devuelve una instancia del comando correspondiente.
     *
     * @param commandName Nombre del comando recibido.
     * @return Instancia del comando correspondiente o {@code null} si no existe.
     */
    private Command parseCommand(String commandName) {
        if (!commandName.startsWith("/")) return null;
        return commandFactory.createCommand(commandName);
    }

    /**
     * Extrae los argumentos de un comando recibido.
     *
     * @param strings Array de cadenas que representan el comando y sus argumentos.
     * @return Un nuevo array que contiene solo los argumentos del comando.
     */
    public String[] parseArguments(String[] strings) {
        int size = strings.length - 1;
        String[] parsedStrings = new String[size];
        System.arraycopy(strings, 1, parsedStrings, 0, size);
        return parsedStrings;
    }

    /**
     * Ejecuta el comando especificado con los argumentos dados.
     *
     * @param command Instancia del comando a ejecutar.
     * @param args    Argumentos del comando.
     * @param worker  Instancia del {@code Worker} que ejecuta el comando.
     */
    private void executeCommand(Command command, String[] args, Worker worker) {
        command.setServices(services);
        command.execute(parseArguments(args), worker);
        logger.info("[" + Thread.currentThread().getName() + "] Comando ejecutado: " + args[0]);
    }
}