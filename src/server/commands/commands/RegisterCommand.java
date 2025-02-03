package server.commands.commands;

import server.*;
import server.commands.Command;
import server.services.ServiceRegistry;
import server.services.UserManager;
import util.StringUtils;

public class RegisterCommand implements Command {
    private UserManager userManager;

    public RegisterCommand() {
    }

    @Override
    public void execute(String[] elements, Worker worker) {
        if (elements.length < 2) {
            return;
        }

        String name = elements[0];
        String password = elements[1];

        User newUser = new User(name, password, StringUtils.getUserId(worker.getClientSocket(), name));

        boolean result = userManager.registerUser(newUser);
        if (result) {
            worker.getMessageService().sendUserHasRegistered();
        }
        else {
            worker.getMessageService().sedUserCouldntRegister();
        }
    }

    @Override
    public void assingServices(ServiceRegistry services) {
        userManager = services.getService(UserManager.class);
    }
}
