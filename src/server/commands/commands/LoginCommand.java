package server.commands.commands;

import server.*;
import server.commands.Command;
import server.game.User;
import server.services.ServiceRegistry;
import server.services.UserManager;

public class LoginCommand extends AbstractCommand {
    private UserManager userManager;

    public LoginCommand() {
    }

    @Override
    public void execute(String[] elements, Worker worker) {
        if (elements.length < 2) {
            messageService.send("Necesitas especificar un usuario y contraseña", worker);
            return;
        }

        String username = elements[0];
        String password = elements[1];

        if(!userManager.userExists(username)) {
            messageService.send("ERROR: Credenciales inválidas, prueba de nuevo", worker);
            return;
        }

        User user = userManager.getUser(username);

        if(user.getPassword().equals(password)) {
            user.setAuthenticated(true);
            worker.setUser(user);

            messageService.send("Sesión iniciada correctamente, utiliza '/help' para ver los comandos disponibles", worker);
        }
    }

    @Override
    public void assingServices(ServiceRegistry services) {
        super.assingServices(services);
        userManager = services.getService(UserManager.class);
    }
}
