package server.commands;

import util.SimpleLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The CommandHandler class is responsible for managing and executing commands issued by users in a chat room.
 * It handles the parsing of the command, selects the appropriate Command implementation, and executes it.
 */
public class CommandFactory {
    private static final Logger log = SimpleLogger.getInstance().getLogger(CommandFactory.class);
    private final Map<String, Command> commandMap;

    public CommandFactory() {
        // Registrar comandos
        commandMap = new HashMap<>();
    }

    public Command createCommand(String commandName) {
        Class<? extends Command> commandClass = commandMap.get(commandName).getClass();
        try {
            return commandClass.getDeclaredConstructor().newInstance(); // Crear la instancia del comando
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}