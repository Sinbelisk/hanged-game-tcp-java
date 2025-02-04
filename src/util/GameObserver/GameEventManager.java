package util.GameObserver;

import java.util.ArrayList;
import java.util.List;

public class GameEventManager {
    private final List<GameObserver> players = new ArrayList<GameObserver>();

    public GameEventManager() {

    }

    public void subscribe(GameObserver observer) {
        players.add(observer);
    }

    public void unsubscribe(GameObserver observer) {
        players.remove(observer);
    }
}
