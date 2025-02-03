package server.services;

import server.User;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestión centralizada de los usuarios registrados.
 */
public class UserManager{
    private static UserManager instance;
    private final ConcurrentHashMap<String, User> users;


    public UserManager(){
        users = new ConcurrentHashMap<>();
    }

    public synchronized boolean registerUser(User user) {
        if(users.containsKey(user.getUsername())) return false;
        users.put(user.getUsername(), user);
        return true;
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}