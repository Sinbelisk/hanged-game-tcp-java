package server;

/**
 * Representa un usuario con su contraseña y estadísticas de juego.
 */
public class User {
    private final String username;
    private final String password;
    private int wins;
    private int losses;
    private int score;
    private boolean autentified;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.wins = 0;
        this.losses = 0;
        this.score = 0;
        autentified = false;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getScore() { return score; }

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

    public boolean isAutentified() { return autentified; }

    public void setAutentified(boolean aut) {
        autentified = aut;
    }
}

