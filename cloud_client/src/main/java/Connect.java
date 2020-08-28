import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class Connect {
    public static Connect instance;
    private static Socket socket;
    private static ObjectDecoderInputStream is;
    private static ObjectEncoderOutputStream os;

    public static synchronized Connect getInstance() {
        if (instance == null) {
            instance = new Connect();
        }
        return instance;
    }
    private Connect(){
        try {
            socket = new Socket("localhost", 8189);
            is = new ObjectDecoderInputStream(socket.getInputStream(), 1024 * 1024 * 100);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  boolean sendMessage(AbstractMessage msg) {
        try {
            os.writeObject(msg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public  AbstractMessage readMessage() {
        Object o = null;
        try {
            o = is.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return (AbstractMessage) o;
    }

}
