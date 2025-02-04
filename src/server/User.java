package server;

import java.io.Serializable;

/**
 * Representa un usuario con su contraseña y estadísticas de juego.
 */
public class User implements Serializable {
    private final String username;
    private final String password;
    private final String id;
    private int tries;
    private boolean authenticated;

    public User(String username, String password, String id) {
        this.username = username;
        this.password = password;
        this.tries = 0;
        authenticated = false;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getTries() {
        return tries;
    }

    public int getScore() {
        int score = 0;

        if(tries < 5) score = 150;
        else if (tries <= 8) score = 100;
        else if (tries <= 11) score = 70;
        else if (tries <= 15) score = 50;

        return score;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void addTry() {
        tries++;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean aut) {
        authenticated = aut;
    }
}

