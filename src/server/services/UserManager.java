package server.services;

import server.game.User;
import server.util.SimpleLogger;

import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

import java.util.logging.Logger;

/**
 * Gestión centralizada de los usuarios registrados en el sistema.
 * Permite registrar, consultar y verificar la existencia de usuarios.
 */
public class UserManager {
    /**
     * Ruta predeterminada del archivo donde se almacenan los usuarios.
     */
    private static final String USERS_PATH = String.valueOf(Paths.get("Files/users"));

    /**
     * Logger para registrar eventos e información de la clase.
     */
    private static final Logger logger = SimpleLogger.getInstance().getLogger(UserManager.class);

    /**
     * Estructura de almacenamiento concurrente para los usuarios registrados.
     * La clave es el nombre de usuario, y el valor es la instancia del usuario.
     */
    private final ConcurrentHashMap<String, User> users;

    /**
     * Constructor que inicializa la estructura de almacenamiento de usuarios.
     */
    public UserManager() {
        users = new ConcurrentHashMap<>();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param user Usuario a registrar.
     * @return {@code true} si el usuario fue registrado con éxito,
     *         {@code false} si el nombre de usuario ya estaba en uso.
     */
    public synchronized boolean registerUser(User user) {
        if (users.containsKey(user.getUsername())) return false;
        users.put(user.getUsername(), user);
        return true;
    }

    /**
     * Obtiene un usuario registrado por su nombre de usuario.
     *
     * @param username Nombre de usuario a buscar.
     * @return Instancia del usuario si existe, o {@code null} si no se encuentra.
     */
    public User getUser(String username) {
        return users.get(username);
    }

    /**
     * Verifica si un usuario existe en el sistema.
     *
     * @param username Nombre de usuario a verificar.
     * @return {@code true} si el usuario está registrado, {@code false} en caso contrario.
     */
    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}
