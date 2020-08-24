import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo implements Serializable {
    static final long serialVersionUID = 1L;

    private String fileName;
    private long size;

    public FileInfo(Path path) {
    this.fileName = path.getFileName().toString();
        try {
            this.size = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Files.isDirectory(path)) {
            this.size = -1L;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public long getSize() {
        return size;
    }
}
