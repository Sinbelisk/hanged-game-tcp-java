package server.game;

import java.io.Serializable;

/**
 * Representa un usuario con su información de autenticación y estadísticas de juego.
 */
public class User implements Serializable {
    private final String username;
    private final String password;
    private boolean authenticated;

    private int totalScore = 0;
    private int wins = 0;
    private int loses = 0;

    /**
     * Crea un nuevo usuario con el nombre de usuario y la contraseña especificados.
     *
     * @param username Nombre de usuario del jugador.
     * @param password Contraseña del jugador.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        authenticated = false;
    }

    /**
     * Obtiene el nombre de usuario.
     *
     * @return Nombre de usuario del jugador.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Obtiene la contraseña del usuario.
     *
     * @return Contraseña del jugador.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Obtiene una representación de las estadísticas del usuario.
     *
     * @return Cadena con las estadísticas del jugador.
     */
    public String getStats() {
        return String.format("[%s] Tus estadísticas son: <Victorias: %d> <Derrotas: %d> <Puntuación total: %d> ",
                username, wins, loses, totalScore);
    }

    /**
     * Incrementa en 1 el número de victorias del usuario.
     */
    public void addWin() {
        wins++;
    }

    /**
     * Incrementa en 1 el número de derrotas del usuario.
     */
    public void addLoss() {
        loses++;
    }

    /**
     * Verifica si el usuario está autenticado.
     *
     * @return {@code true} si el usuario ha iniciado sesión, de lo contrario {@code false}.
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Establece el estado de autenticación del usuario.
     *
     * @param aut {@code true} para marcar al usuario como autenticado, {@code false} en caso contrario.
     */
    public void setAuthenticated(boolean aut) {
        authenticated = aut;
    }

    /**
     * Suma una cantidad de puntos a la puntuación total del usuario.
     *
     * @param score Cantidad de puntos a agregar.
     */
    public void sumScore(int score) {
        totalScore += score;
    }
}

