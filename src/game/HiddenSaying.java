package game;

import java.util.Arrays;
import java.util.List;

public class HiddenSaying {
    public String saying;
    public char[] hiddenSaying;
    public HiddenSaying(String saying){
        this.saying = saying;
        this.hiddenSaying = buildHiddenSaying(saying);
    }
    private char[] buildHiddenSaying(String saying) {
        return saying.replaceAll("[\\p{L}\\p{N}]", "_").toCharArray();
    }

    public void replaceIndexesWithCharacter(char character, List<Integer> indexes){
        for (Integer index : indexes) {
            hiddenSaying[index] = character;
        }
    }
    public String getHiddenSaying() {
        return Arrays.toString(hiddenSaying);
    }

    public String getSaying() {
        return saying;
    }

    public void newSaying(String saying){
        this.saying = saying;
        hiddenSaying = buildHiddenSaying(saying);
    }
}
