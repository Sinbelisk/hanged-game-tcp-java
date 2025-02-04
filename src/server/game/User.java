package server.game;

import java.io.Serializable;

/**
 * Representa un usuario con su contraseña y estadísticas de juego.
 */
public class User implements Serializable {
    private final String username;
    private final String password;
    private boolean authenticated;

    private int totalScore = 0;
    private int wins = 0;
    private int loses = 0;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        authenticated = false;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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

    public void sumScore(int score){
        totalScore += score;
    }
}

