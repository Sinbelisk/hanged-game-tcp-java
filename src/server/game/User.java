package server.game;

import java.io.Serializable;

/**
 * Representa un usuario con su contraseña y estadísticas de juego.
 */
public class User implements Serializable {
    private final String username;
    private final String password;
    private int tries;
    private boolean authenticated;

    private int totalScore = 0;
    private int wins = 0;
    private int loses = 0;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.tries = 0;
        authenticated = false;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getRoundScore() {
        int score = -1;

        if(tries < 5 && tries > 0) score = 150;
        else if (tries <= 8) score = 100;
        else if (tries <= 11) score = 70;
        else if (tries <= 15) score = 50;

        return score == -1 ? 0 : score;
    }

    public void addScore(int score) {
        totalScore += score;
    }

    public void addTry() {
        tries++;
    }

    public String getStats(){
        return String.format("[%s] Tus estadísticas son: <Victorias: %d> <Derrotas: %d> <Puntuación total: %d> ", username, wins, loses, totalScore);
    }

    public void addWin(){
        wins++;
    }

    public void addLoss(){
        loses++;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean aut) {
        authenticated = aut;
    }

    public void resetTries(){
        tries = 0;
    }
}

