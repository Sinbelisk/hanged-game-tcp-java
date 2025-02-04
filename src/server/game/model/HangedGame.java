package server.game.model;

import util.SayingUtils;
import util.SimpleLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Clase que implementa la lógica principal del juego del ahorcado. Permite gestionar intentos
 * de adivinar consonantes, vocales o frases completas mientras se mantiene el estado del juego.
 */
public class HangedGame {
    /**
     * Expresión regular para validar vocales (independiente de mayúsculas/minúsculas).
     */
    private static final String VOWEL_REGEX = "(?i)[aeiou]";

    /**
     * Expresión regular para validar consonantes (independiente de mayúsculas/minúsculas).
     */
    private static final String CONSONANT_REGEX = "(?i)[b-df-hj-np-tv-z]";

    /**
     * Logger para registrar las actividades del juego.
     */
    private static final Logger logger = SimpleLogger.getInstance().getLogger(HangedGame.class);

    /**
     * Cola de refranes disponibles para el juego.
     */
    private final Queue<String> currentCollection;

    /**
     * Refrán actual que el jugador está intentando adivinar.
     */
    private final HiddenSaying currentSaying;

    /**
     * Constructor que inicializa el juego con una colección específica de refranes.
     *
     * @param collection una cola de cadenas que representan los refranes disponibles.
     */
    public HangedGame(Queue<String> collection) {
        currentCollection = collection;
        currentSaying = new HiddenSaying(currentCollection.poll());
        logger.info("HangedGame created");
    }

    /**
     * Constructor que inicializa el juego con una colección predeterminada de refranes.
     *
     * @throws IOException si ocurre un error al leer el archivo de refranes predeterminado.
     */
    public HangedGame() throws IOException {
        currentCollection = SayingUtils.getWordsFromDocumentName("easy");
        currentSaying = new HiddenSaying(currentCollection.poll());
    }

    /**
     * Intenta adivinar una consonante en el refrán actual.
     *
     * @param consonant la consonante que el jugador intenta adivinar.
     * @return {@code true} si la consonante está presente en el refrán, {@code false} en caso contrario.
     */
    public boolean tryConsonant(char consonant) {
        logger.info("Trying consonant " + consonant);
        return containsCharacter(consonant, CONSONANT_REGEX);
    }

    /**
     * Intenta adivinar una vocal en el refrán actual.
     *
     * @param vowel la vocal que el jugador intenta adivinar.
     * @return {@code true} si la vocal está presente en el refrán, {@code false} en caso contrario.
     */
    public boolean tryVowel(char vowel) {
        logger.info("Trying vowel " + vowel);
        return containsCharacter(vowel, VOWEL_REGEX);
    }

    /**
     * Intenta adivinar la frase completa.
     *
     * @param phrase la frase completa que el jugador cree que es la correcta.
     * @return {@code true} si la frase coincide exactamente con el refrán actual,
     *         {@code false} en caso contrario.
     */
    public boolean tryPhrase(String phrase) {
        logger.info("Trying phrase " + phrase);
        if (phrase.trim().equalsIgnoreCase(currentSaying.getSaying().trim())) {
            currentSaying.revealAll();
            logger.info("Phrase guessed correctly!");
            return true;
        }
        logger.info("Phrase guessed incorrectly.");
        return false;
    }

    /**
     * Verifica si un carácter está presente en el refrán actual, basándose en el tipo de carácter.
     *
     * @param character el carácter que el jugador intenta adivinar.
     * @param regex la expresión regular que define el tipo de carácter (vocal o consonante).
     * @return {@code true} si el carácter está presente en el refrán, {@code false} en caso contrario.
     */
    private boolean containsCharacter(char character, String regex) {
        if (!Pattern.matches(regex, Character.toString(character))) {
            logger.warning("Invalid character " + character + " for this guess type.");
            return false;
        }

        List<Integer> indexes = getOccurrenceIndexes(character);
        if (!indexes.isEmpty()) {
            currentSaying.replaceIndexesWithCharacter(character, indexes);
            logger.info("Character " + character + " found at positions " + indexes);
            return true;
        }
        logger.info("Character " + character + " not found.");
        return false;
    }

    /**
     * Obtiene los índices donde un carácter específico aparece en el refrán actual.
     *
     * @param character el carácter a buscar.
     * @return una lista de índices donde aparece el carácter en el refrán.
     */
    private List<Integer> getOccurrenceIndexes(char character) {
        String saying = currentSaying.getSaying().toLowerCase();
        List<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < saying.length(); i++) {
            if (saying.charAt(i) == Character.toLowerCase(character)) {
                indexes.add(i);
            }
        }

        logger.info("Found " + indexes.size() + " occurrences of " + character);
        return indexes;
    }

    /**
     * Devuelve el refrán actual que el jugador está intentando adivinar.
     *
     * @return el refrán actual como un objeto {@link HiddenSaying}.
     */
    public HiddenSaying getCurrentSaying() {
        return currentSaying;
    }

    public boolean isGameCompleted(){
        return currentSaying.isWordCompleted();
    }
}
