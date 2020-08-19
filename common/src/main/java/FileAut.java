public class FileAut extends AbstractMessage {
    private String login;
    private String pass;
    private boolean isExist;

    public FileAut(String login, String pass, boolean isExist) {
        this.login = login;
        this.pass = pass;
       this.isExist = isExist;
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public boolean isExist() {
        return isExist;
    }
}
