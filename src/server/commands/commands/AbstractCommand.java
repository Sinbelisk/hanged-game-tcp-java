package server.commands.commands;

import server.Worker;
import server.commands.Command;
import server.services.MessageService;
import server.services.ServiceRegistry;

public abstract class AbstractCommand implements Command {
    protected MessageService messageService;

    @Override
    public void assingServices(ServiceRegistry services) {
        messageService = services.getService(MessageService.class);
    }
}
