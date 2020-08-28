public class FileAnswer extends AbstractMessage {
    private boolean bool;
    private String text;

    public FileAnswer(boolean bool, String text) {
        this.bool = bool;
        this.text = text;
    }

    public boolean isBool() {
        return bool;
    }

    public String getText() {
        return text;
    }
}
