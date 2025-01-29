package game;

public class Player {
    private final String nick;
    private int score;
    private int tries;

    public Player(String nick) {
        this.nick = nick;
        score = 0;
        tries = 0;
    }

    public void addTry() {
        tries++;
    }

    public int getScore() {
        if (tries < 5) return 150;
        else if (tries < 9) return 100;
        else if (tries < 12) return 70;
        else if (tries < 16) return 50;
        else return 0;
    }

    public int getTries() {
        return tries;
    }
}
