package server.services;

import server.Worker;
import server.commands.Command;
import server.commands.CommandFactory;
import util.SimpleLogger;
import util.StringUtils;

import java.util.logging.Logger;

public class CommandManager {
    private static final Logger logger = SimpleLogger.getInstance().getLogger(CommandManager.class);
    private final CommandFactory commandFactory;

    private final ServiceRegistry services;

    public CommandManager(ServiceRegistry services) {
        this.services = services;
        this.commandFactory = new CommandFactory(services);
    }

    public void processCommand(String received, Worker worker) {
        String[] commandArgs = received.split("\\s");
        if (commandArgs.length == 0) return;

        Command command = parseCommand(commandArgs[0]);
        if (command != null) {
            executeCommand(command, commandArgs, worker);
        } else {
            worker.getMessageService().sendUnknownCommand();
            logger.warning("[" + worker.getName() + "] Comando desconocido: " + commandArgs[0]);
        }
    }

    private Command parseCommand(String commandName) {
        if (!commandName.startsWith("/")) return null;
        return commandFactory.createCommand(commandName);
    }

    private void executeCommand(Command command, String[] args, Worker worker) {
        command.assingServices(services);
        command.execute(StringUtils.parseArguments(args), worker);
        logger.info("[" + Thread.currentThread().getName() + "] Comando ejecutado: " + args[0]);
    }

    public String getAllRegisteredCommands(){

    }
}
