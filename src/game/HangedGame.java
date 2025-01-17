package game;

import util.SimpleLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HangedGame {
    private static final String VOWEL_REGEX = "(?i)[aeiou]";
    private static final String CONSONANT_REGEX = "(?i)[b-df-hj-np-tv-z]";
    private static final Logger logger = SimpleLogger.getInstance().getLogger(HangedGame.class);

    private final Queue<String> currentCollection;
    private final HiddenSaying currentSaying;

    // Inicializa el juego con una colección de refranes
    public HangedGame(Queue<String> collection){
        currentCollection = collection;
        currentSaying = new HiddenSaying(currentCollection.poll());
        logger.info("HangedGame created");
    }
    public boolean tryConsonant(char consonant) {
        logger.info("Trying consonant " + consonant);
        return tryCharacter(consonant, CONSONANT_REGEX);
    }

    public boolean tryVowel(char vowel) {
        logger.info("Trying vowel " + vowel);
        return tryCharacter(vowel, VOWEL_REGEX);
    }

    public boolean tryPhrase(String phrase){
        logger.info("Trying phrase " + phrase);
        return (phrase.trim().equalsIgnoreCase(currentSaying.getSaying().trim()));
    }

    private boolean tryCharacter(char character, String regex) {
        List<Integer> indexes = getOccurrenceIndexes(character, regex);
        if (!indexes.isEmpty()) {
            currentSaying.replaceIndexesWithCharacter(character, indexes);
            logger.info("Trying character " + character);
            return true;
        }
        logger.info("Character " + character + " not found");
        return false;
    }

    private List<Integer> getOccurrenceIndexes(char character, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(Character.toString(character));

        List<Integer> indexes = new ArrayList<>();
        while (matcher.find()){
            indexes.add(matcher.start());
        }

        logger.info("Found " + indexes.size() + " occurrences of " + character);
        return indexes;
    }
}
