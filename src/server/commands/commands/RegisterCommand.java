package server.commands.commands;

import server.*;
import server.game.User;
import server.services.ServiceRegistry;
import server.services.UserManager;

/**
 * El comando {@code RegisterCommand} permite a un usuario registrarse en el sistema, creando una nueva cuenta con un nombre de usuario y una contraseña.
 * Extiende de {@link AbstractCommand} para compartir la lógica común de los comandos y gestionar los servicios necesarios.
 */
public class RegisterCommand extends AbstractCommand {
    private UserManager userManager;

    /**
     * Constructor de {@code RegisterCommand}.
     * Inicializa la clase sin parámetros adicionales.
     */
    public RegisterCommand() {
    }

    /**
     * Ejecuta el comando para registrar a un nuevo usuario.
     * Verifica que se haya proporcionado un nombre de usuario y una contraseña.
     * Si el registro es exitoso, el usuario recibirá un mensaje de confirmación.
     * Si ya existe un usuario con el mismo nombre, se notificará al usuario que debe elegir otro nombre.
     *
     * @param elements Los elementos del comando, que deben incluir el nombre de usuario y la contraseña.
     * @param worker El trabajador (usuario) que ejecuta el comando.
     */
    @Override
    public void execute(String[] elements, Worker worker) {
        //Verifica que el usuario no este ya autorizado.
        if(isUserAuthenticated(worker)){
            messageService.sendUserAlreadyAuth(worker);
        }

        // Verifica que se hayan proporcionado suficientes argumentos (nombre y contraseña)
        if (elements.length < 2) {
            return;
        }

        String name = elements[0];
        String password = elements[1];

        // Crea un nuevo usuario con el nombre y la contraseña proporcionados
        User newUser = new User(name, password);

        // Intenta registrar al nuevo usuario
        boolean result = userManager.registerUser(newUser);
        if (result) {
            // Registro exitoso
            messageService.send("Registro completado correctamente, utiliza '/login <usuario> <contraseña>' para iniciar sesión", worker);
        }
        else {
            // El nombre de usuario ya existe
            messageService.send("Ya existe un usuario con ese nombre, utiliza otro", worker);
        }
    }

    /**
     * Asocia los servicios necesarios con el comando, incluyendo el {@link UserManager} para gestionar el registro de usuarios.
     *
     * @param services El registro de servicios que contiene todos los servicios disponibles.
     */
    @Override
    public void setServices(ServiceRegistry services) {
        super.setServices(services);
        userManager = services.getService(UserManager.class);
    }
}
