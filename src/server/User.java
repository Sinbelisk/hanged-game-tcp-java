package server;

import java.io.Serializable;

/**
 * Representa un usuario con su contraseña y estadísticas de juego.
 */
public class User implements Serializable {
    private final String username;
    private final String password;
    private final String id;
    private int wins;
    private int losses;
    private int score;
    private boolean authenticated;

    public User(String username, String password, String id) {
        this.username = username;
        this.password = password;
        this.wins = 0;
        this.losses = 0;
        this.score = 0;
        authenticated = false;
        this.id = id;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getScore() { return score; }
    public String getId() {
        return id == null ? "" : id;
    }

    public void addWin(int points) {
        wins++;
        score += points;
    }

    public void addLoss() {
        losses++;
    }

    public String getStats() {
        return "Victorias: " + wins + ", Derrotas: " + losses + ", Puntuación: " + score;
    }

    public boolean isAuthenticated() { return authenticated; }

    public void setAuthenticated(boolean aut) {
        authenticated = aut;
    }
}

