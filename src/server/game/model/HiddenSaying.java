package server.game.model;

import java.util.List;

/**
 * Clase que representa un refrán oculto en el juego del ahorcado.
 * Esta clase gestiona tanto el refrán original como su versión oculta,
 * y proporciona métodos para revelar las letras adivinadas.
 */
public class HiddenSaying {
    /**
     * El refrán original que el jugador está intentando adivinar.
     */
    private String saying;

    /**
     * El refrán con las letras ocultas (por defecto todas las letras están reemplazadas por "_").
     */
    private char[] hiddenSaying;

    /**
     * Constructor que inicializa el refrán y su versión oculta.
     *
     * @param saying el refrán original que se va a ocultar.
     */
    public HiddenSaying(String saying) {
        this.saying = saying;
        this.hiddenSaying = buildHiddenSaying(saying);
    }

    /**
     * Construye la versión oculta del refrán reemplazando todas las letras y números por "_".
     *
     * @param saying el refrán original que se va a ocultar.
     * @return un array de caracteres representando el refrán oculto.
     */
    private char[] buildHiddenSaying(String saying) {
        return saying.replaceAll("[\\p{L}\\p{Nd}]", "_").toCharArray();
    }

    /**
     * Reemplaza los índices de las letras adivinadas en el refrán oculto.
     *
     * @param character el carácter que se adivinó correctamente.
     * @param indexes una lista de índices donde se debe colocar el carácter adivinando.
     */
    public void replaceIndexesWithCharacter(char character, List<Integer> indexes) {
        for (Integer index : indexes) {
            hiddenSaying[index] = character;
        }
    }

    /**
     * Obtiene el refrán oculto como una cadena de caracteres.
     *
     * @return el refrán oculto como una cadena.
     */
    public String getHiddenSaying() {
        return new String(hiddenSaying);
    }

    /**
     * Obtiene el refrán original.
     *
     * @return el refrán original.
     */
    public String getSaying() {
        return saying;
    }

    /**
     * Revela completamente el refrán, mostrando todas sus letras.
     */
    public void revealAll() {
        hiddenSaying = saying.toCharArray();
    }

    /**
     * Verifica si el refrán ha sido completado correctamente.
     *
     * @return {@code true} si el refrán ha sido completado (adivinadas todas las letras),
     *         {@code false} en caso contrario.
     */
    public boolean isWordCompleted() {
        return saying.equalsIgnoreCase(new String(hiddenSaying));
    }

    /**
     * Establece un nuevo refrán y construye su versión oculta.
     *
     * @param saying el nuevo refrán a establecer.
     */
    public void newSaying(String saying) {
        this.saying = saying;
        hiddenSaying = buildHiddenSaying(saying);
    }
}