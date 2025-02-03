import server.game.HangedGame;

import java.io.IOException;
import java.util.Scanner;

public class Main {
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
