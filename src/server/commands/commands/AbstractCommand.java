package server.commands.commands;

import server.Worker;
import server.commands.Command;
import server.game.User;
import server.services.MessageService;
import server.services.ServiceRegistry;

public abstract class AbstractCommand implements Command {
    protected MessageService messageService;

    @Override
    public void setServices(ServiceRegistry services) {
        messageService = services.getService(MessageService.class);
    }

    protected boolean isUserAuthenticated(Worker worker) {
        User user = worker.getUser();

        if (user == null) {
            return false;
        }
        else return user.isAuthenticated();
    }

}
