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

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
