package server.commands.commands;

import server.*;
import server.commands.Command;
import server.services.ServiceRegistry;
import server.services.UserManager;

public class LoginCommand implements Command {
    private UserManager userManager;

    public LoginCommand() {
    }

    @Override
    public void execute(String[] elements, Worker worker) {
        if (elements.length < 2) {
            worker.getMessageService().send("Necesitas especificar un usuario y contraseña");
        }

        String username = elements[0];
        String password = elements[1];

        if(!userManager.userExists(username)) {
            worker.getMessageService().sendUnknownCredentials();
            return;
        }

        User user = userManager.getUser(username);

        if(user.getPassword().equals(password)) {
            user.setAuthenticated(true);
            worker.setUser(user);

            worker.getMessageService().sendUserHasLogedIn();
        }
    }

    @Override
    public void assingServices(ServiceRegistry services) {
        userManager = services.getService(UserManager.class);
    }
}
