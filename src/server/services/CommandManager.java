package server.services;

import server.Worker;
import server.commands.Command;
import server.commands.CommandFactory;
import util.SimpleLogger;

import java.util.logging.Logger;

public class CommandManager {
    private static final Logger logger = SimpleLogger.getInstance().getLogger(CommandManager.class);
    private final CommandFactory commandFactory;

    private final ServiceRegistry services;
    private final MessageService messageService;

    public CommandManager(ServiceRegistry services) {
        this.services = services;
        this.commandFactory = new CommandFactory(services);
        this.messageService = services.getService(MessageService.class);
    }

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

    private Command parseCommand(String commandName) {
        if (!commandName.startsWith("/")) return null;
        return commandFactory.createCommand(commandName);
    }

    public String[] parseArguments(String[] strings){
        int size = strings.length - 1;
        String[] parsedStrings = new String[size];
        System.arraycopy(strings, 1, parsedStrings, 0, size);

        return parsedStrings;
    }

    private void executeCommand(Command command, String[] args, Worker worker) {
        command.setServices(services);
        command.execute(parseArguments(args), worker);
        logger.info("[" + Thread.currentThread().getName() + "] Comando ejecutado: " + args[0]);
    }
}
