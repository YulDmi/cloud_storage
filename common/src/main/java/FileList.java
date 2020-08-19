import java.util.ArrayList;

public class FileList extends AbstractMessage {
    private ArrayList<String> list;

    public ArrayList<String> getList() {
        return list;
    }


    public FileList(){
        this.list = new ArrayList<>();
    }
}
