package game;

import util.SayingUtils;
import util.SimpleLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Logger;
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

    public HangedGame() throws IOException {
        currentCollection = SayingUtils.getWordsFromDocumentName("easy");
        currentSaying = new HiddenSaying(currentCollection.poll());
    }

    public boolean tryConsonant(char consonant) {
        logger.info("Trying consonant " + consonant);
        return containsCharacter(consonant, CONSONANT_REGEX);
    }

    public boolean tryVowel(char vowel) {
        logger.info("Trying vowel " + vowel);
        return containsCharacter(vowel, VOWEL_REGEX);
    }

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

    public HiddenSaying getCurrentSaying() {
        return currentSaying;
    }

    //TEST AREA
    public static void main(String[] args) throws IOException {
        HangedGame game = new HangedGame();
        System.out.println(game.getCurrentSaying().getSaying());
        System.out.println(game.getCurrentSaying().getHiddenSaying());
        game.tryConsonant('n');
        game.tryVowel('a');
        System.out.println(game.getCurrentSaying().getHiddenSaying());

        Scanner s = new Scanner(System.in);
        while(true){
            System.out.println("Insert phrase: ");
            boolean exists = game.tryPhrase(s.nextLine());
            System.out.println(exists);
            System.out.println(game.getCurrentSaying().getHiddenSaying());
        }
    }
}
