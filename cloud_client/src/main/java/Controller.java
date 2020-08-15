import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public ListView<String> lv;
    public TextField txt;
    public Button send;
   // public Button upload;
  //  public Button delete;
    private static Socket socket;
    private static ObjectDecoderInputStream is;
    private static ObjectEncoderOutputStream os;
    private final String clientFilesPath = "./common/src/main/resources/clientFiles";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket("localhost", 8189);
            is = new ObjectDecoderInputStream(socket.getInputStream(), 1024 * 1024 * 100);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        File dir = new File(clientFilesPath);
        for (String file : dir.list()) {
            lv.getItems().add(file);
        }
    }

    public void sendCommand(ActionEvent actionEvent) throws IOException {
        String command = txt.getText();
        String[] op = command.split(" ");
        if (op[0].equals("./download")) {
            FileRequest fileRequest = new FileRequest(op[1]);
            sendMessage(fileRequest);
            AbstractMessage abstractMessage = readMessage();

            if (abstractMessage instanceof FileMessage) {
                FileMessage fileMessage = (FileMessage) abstractMessage;
                Files.write(Paths.get(clientFilesPath + "/" + fileMessage.getFilename()), fileMessage.getData(), StandardOpenOption.CREATE);
            }

            lv.getItems().add(op[1]);
        } else if (op[0].equals("./upload")) {
            FileMessage fileMessage = new FileMessage(Paths.get(clientFilesPath + "/" + op[1]));
            sendMessage(fileMessage);
        } else if (op[0].equals("./delete")){
           FileDelete fileDelete = new FileDelete(op[1]);
            System.out.println("Команда на удаление файла : " + op[1]);
           sendMessage(fileDelete);
        }
    }

    public static boolean sendMessage(AbstractMessage msg) {
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

    public static AbstractMessage readMessage() {
        Object o = null;
        try {
            o = is.readObject();
            System.out.println(o.toString() + "из readMessage");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return (AbstractMessage) o;
    }

//    public void btnExit(ActionEvent actionEvent) {
//        Platform.exit();
//    }
}


