package server.commands;

import server.commands.commands.*;
import server.services.ServiceRegistry;
import util.SimpleLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CommandFactory {
    private static final Logger log = SimpleLogger.getInstance().getLogger(CommandFactory.class);
    private final Map<String, Class<? extends Command>> commandMap = new HashMap<>();
    private final ServiceRegistry serverServices;

    public CommandFactory(ServiceRegistry serverServices) {
        this.serverServices = serverServices;

        commandMap.put("/login", LoginCommand.class);
        commandMap.put("/register", RegisterCommand.class);
        commandMap.put("/vowel", VowelCommand.class);
        commandMap.put("/consonant", ConsonantCommand.class);
        commandMap.put("/phrase", PhraseCommand.class);
        commandMap.put("/game", GameCommand.class);
    }

    public Command createCommand(String commandName) {
        Class<? extends Command> commandClass = commandMap.get(commandName);

        if (commandClass == null) {
            log.warning("Unknown command: " + commandName.substring(1));
            return null;
        }

        try {
            Command command = commandClass.getDeclaredConstructor().newInstance();
            command.assingServices(serverServices);

            return commandClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}