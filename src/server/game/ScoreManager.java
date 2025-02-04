package server.game;

public class ScoreManager {
    private int tries;

    public ScoreManager() {
        tries = 0;
    }

    public int getRoundScore() {
        int score = 0;

        if(tries < 5) score = 150;
        else if (tries <= 8) score = 100;
        else if (tries <= 11) score = 70;
        else if (tries <= 15) score = 50;

        return score;
    }

    public void addTry() {
        tries++;
    }
}
