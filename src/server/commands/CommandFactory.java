package server.commands;

import server.commands.commands.*;
import server.services.ServiceRegistry;
import server.util.SimpleLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * La clase CommandFactory es responsable de crear instancias de comandos según el nombre del comando proporcionado.
 * Los comandos están mapeados por su nombre.
 * <p>
 * Los comandos no están instanciados por defecto, se crea una instancia del comando sólamente cuando es necesario
 * para ahorrar recursos.
 * </p>
 */
public class CommandFactory {
    private static final Logger log = SimpleLogger.getInstance().getLogger(CommandFactory.class);
    private final Map<String, Class<? extends Command>> commandMap = new HashMap<>();
    private final ServiceRegistry serverServices;

    /**
     * Constructor de la clase CommandFactory.
     * Inicializa el mapeo de comandos disponibles y los servicios del servidor.
     *
     * @param serverServices El registro de servicios del servidor.
     */
    public CommandFactory(ServiceRegistry serverServices) {
        this.serverServices = serverServices;

        // Mapeo de los comandos
        commandMap.put("/login", LoginCommand.class);
        commandMap.put("/exit", ExitCommand.class);
        commandMap.put("/register", RegisterCommand.class);
        commandMap.put("/vowel", VowelCommand.class);
        commandMap.put("/consonant", ConsonantCommand.class);
        commandMap.put("/phrase", PhraseCommand.class);
        commandMap.put("/game", GameCommand.class);

        log.info("Comandos registrados correctamente.");
    }

    /**
     * Crea una instancia de un comando basado en el nombre del comando proporcionado.
     * Si el comando es desconocido, se retorna null y se registra una advertencia.
     *
     * @param commandName El nombre del comando a crear.
     * @return Una instancia del comando correspondiente o null si no se encuentra el comando.
     */
    public Command createCommand(String commandName) {
        log.info("Solicitando comando: " + commandName);

        Class<? extends Command> commandClass = commandMap.get(commandName);

        //Verifica si el comando existe.
        if (commandClass == null) {
            log.warning("Comando desconocido: " + commandName.substring(1));
            return null;
        }

        try {
            // Instancia el comando y lo devuelve.
            Command command = commandClass.getDeclaredConstructor().newInstance();
            command.setServices(serverServices);

            log.info("Comando creado: " + commandName);
            return command;

        } catch (Exception e) {
            log.severe("Error al crear el comando " + commandName + ": " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
