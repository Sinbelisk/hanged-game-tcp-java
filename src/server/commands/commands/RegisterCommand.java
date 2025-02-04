package server.commands.commands;

import server.*;
import server.commands.Command;
import server.game.User;
import server.services.ServiceRegistry;
import server.services.UserManager;

public class RegisterCommand extends AbstractCommand{
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

        User newUser = new User(name, password);

        boolean result = userManager.registerUser(newUser);
        if (result) {
            messageService.send("Registro completado correctamente, utiliza '/login <usuario> <contraseña>' para iniciar sesión", worker);
        }
        else {
            messageService.send("Ya existe un usuario con ese nombre, utiliza otro", worker);
        }
    }

    @Override
    public void assingServices(ServiceRegistry services) {
        super.assingServices(services);
        userManager = services.getService(UserManager.class);
    }
}
