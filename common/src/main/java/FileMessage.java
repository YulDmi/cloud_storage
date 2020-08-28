import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage {
    private String filename;
    private byte[] data;
    private int part;
    private int count;

    public FileMessage(String filename, byte[] data,  int count) {
        this.filename = filename;
        this.data = data;
        this.count = count;

    }
    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }

    public int getPart() { return part; }

    public int getCount() { return count; }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setPart(int part) {
        this.part = part;
    }



}