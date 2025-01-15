package game;

import java.util.Queue;

public class HangedGame {
    private Queue<String> currentCollection;
    private String currentWord;
    private String currentAnonymusWord;
    public HangedGame(Queue<String> collection){
        currentCollection = collection;
        currentWord = currentCollection.poll();
        currentAnonymusWord = buildAnonymousWord();
    }
    private String buildAnonymousWord(){
        String copy = currentWord;
        return copy.replaceAll("\\S", "_");
    }

    public String getCurrentAnonymusWord() {
        return currentAnonymusWord;
    }

    public String getCurrentWord() {
        return currentWord;
    }
}
