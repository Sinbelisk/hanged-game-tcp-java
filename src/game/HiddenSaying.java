package game;

public class HiddenSaying {
    public String saying;
    public String hiddenSaying;
    public HiddenSaying(String saying){
        this.saying = saying;
        this.hiddenSaying = buildHiddenSaying(saying);
    }
    private String buildHiddenSaying(String saying){
        return saying.replaceAll("\\S", "_");
    }

    public String getHiddenSaying() {
        return hiddenSaying;
    }

    public String getSaying() {
        return saying;
    }

    public void updateHiddenSaying(String hiddenSaying) {
        this.hiddenSaying = hiddenSaying;
    }

    public void newSaying(String saying){
        this.saying = saying;
        hiddenSaying = buildHiddenSaying(saying);
    }
}
