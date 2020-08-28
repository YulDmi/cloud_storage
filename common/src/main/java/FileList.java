import java.io.Serializable;
import java.util.ArrayList;

public class FileList extends AbstractMessage implements Serializable {
    private ArrayList<FileInfo> list;

    public ArrayList<FileInfo> getList() {
        return list;
    }


    public FileList(){
        this.list = new ArrayList<>();
    }

}
