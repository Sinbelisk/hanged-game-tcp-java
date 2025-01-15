package game;

import java.util.Queue;

public class HangedGame {
    private Queue<String> currentCollection;
    private final HiddenSaying currentSaying;

    // Inicializa el juego con una colección de refranes
    public HangedGame(Queue<String> collection){
        currentCollection = collection;
        currentSaying = new HiddenSaying(currentCollection.poll());
    }
}
