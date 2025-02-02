package server;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestión centralizada de los usuarios registrados.
 */
public class UserManager{
    private static UserManager instance;
    private UserManager(){}
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    public synchronized boolean registerUser(String username, String password) {
        if(users.containsKey(username)) return false;
        users.put(username, new User(username, password));
        return true;
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public static UserManager getInstance() {
        if(instance == null) instance = new UserManager();
        return instance;
    }
}