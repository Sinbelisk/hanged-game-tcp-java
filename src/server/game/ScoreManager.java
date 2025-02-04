package server.game;

/**
 * Gestiona la puntuación de un jugador en función del número de intentos realizados.
 */
public class ScoreManager {
    private int tries;

    /**
     * Crea un nuevo ScoreManager con el contador de intentos inicializado en 0.
     */
    public ScoreManager() {
        tries = 0;
    }

    /**
     * Calcula y devuelve la puntuación de la ronda actual según el número de intentos.
     *
     * <p>Las puntuaciones se asignan de la siguiente manera:
     * <ul>
     *     <li>Menos de 5 intentos: 150 puntos</li>
     *     <li>Entre 5 y 8 intentos: 100 puntos</li>
     *     <li>Entre 9 y 11 intentos: 70 puntos</li>
     *     <li>Entre 12 y 15 intentos: 50 puntos</li>
     *     <li>Más de 15 intentos: 0 puntos</li>
     * </ul>
     * </p>
     *
     * @return La puntuación correspondiente a la cantidad de intentos.
     */
    public int getRoundScore() {
        int score = 0;

        if (tries < 5) score = 150;
        else if (tries <= 8) score = 100;
        else if (tries <= 11) score = 70;
        else if (tries <= 15) score = 50;

        return score;
    }

    /**
     * Incrementa en 1 el número de intentos realizados.
     */
    public void addTry() {
        tries++;
    }
}

