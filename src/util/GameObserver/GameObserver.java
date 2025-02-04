package util.GameObserver;

public interface GameObserver {
    void enteredLobby();
    void exitedLobby();
    void gameOver();
    void gameStarted();
}
