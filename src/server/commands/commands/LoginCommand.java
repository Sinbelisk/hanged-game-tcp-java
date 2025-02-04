package server.commands.commands;

import server.*;
import server.game.User;
import server.services.ServiceRegistry;
import server.services.UserManager;

/**
 * El comando {@code LoginCommand} permite a un usuario iniciar sesión en el sistema utilizando un nombre de usuario y una contraseña.
 * Extiende de {@link AbstractCommand} para compartir la lógica común de los comandos y gestionar los servicios necesarios.
 */
public class LoginCommand extends AbstractCommand {
    private UserManager userManager;

    /**
     * Constructor vacío de {@code LoginCommand}. Se utiliza para la creación del comando.
     */
    public LoginCommand() {
    }

    /**
     * Ejecuta el comando para autenticar a un usuario en el sistema.
     * Verifica que se haya proporcionado un nombre de usuario y una contraseña, y valida las credenciales.
     * Si las credenciales son correctas, el usuario se marca como autenticado y se le asigna al trabajador (worker).
     *
     * @param elements Los argumentos del comando, que deben incluir el nombre de usuario y la contraseña.
     * @param worker El trabajador (usuario) que ejecuta el comando.
     */
    @Override
    public void execute(String[] elements, Worker worker) {
        //Verifica que el usuario no este ya autorizado.
        if(isUserAuthenticated(worker)){
            messageService.sendUserAlreadyAuth(worker);
        }

        // Verifica que se hayan proporcionado ambos argumentos (usuario y contraseña)
        if (elements.length < 2) {
            messageService.send("Necesitas especificar un usuario y contraseña", worker);
            return;
        }

        String username = elements[0];
        String password = elements[1];

        // Verifica si el usuario existe
        if(!userManager.userExists(username)) {
            messageService.send("ERROR: Credenciales inválidas, prueba de nuevo", worker);
            return;
        }

        // Obtiene el usuario y valida la contraseña
        User user = userManager.getUser(username);
        if(user.getPassword().equals(password)) {
            user.setAuthenticated(true);
            worker.setUser(user); // Asocia el usuario al trabajador

            // Envía un mensaje indicando que la sesión se ha iniciado correctamente
            messageService.send("Sesión iniciada correctamente, utiliza '/help' para ver los comandos disponibles", worker);
        }
    }

    /**
     * Establece los servicios necesarios para el comando.
     * En este caso, se obtiene el servicio {@code UserManager} para gestionar los usuarios.
     *
     * @param services El registro de servicios que proporciona los servicios necesarios.
     */
    @Override
    public void setServices(ServiceRegistry services) {
        super.setServices(services);
        userManager = services.getService(UserManager.class);
    }
}
