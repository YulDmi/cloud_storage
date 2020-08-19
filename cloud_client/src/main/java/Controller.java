import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public ListView<String> lv;
    public ListView<String> lv2;
    public TextField textField;
    public Button send;
    public Button upload;
    public Button delete;
//    private static Socket socket;
//    private static ObjectDecoderInputStream is;
//    private static ObjectEncoderOutputStream os;
    private final String clientFilesPath = "./common/src/main/resources/clientFiles";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        try {
//            socket = new Socket("localhost", 8189);
//            is = new ObjectDecoderInputStream(socket.getInputStream(), 1024 * 1024 * 100);
//            os = new ObjectEncoderOutputStream(socket.getOutputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        sendFileList();

        File dir = new File(clientFilesPath);
        for (String file : dir.list()) {
            lv.getItems().add(file);
        }

    }

    private void sendFileList() {
        FileList fl = new FileList();
     SignUpController.sendMessage(fl);
        Object o = SignUpController.readMessage();
        if (o instanceof FileList) {
            for (String file : ((FileList) o).getList()) {
                lv2.getItems().add(file);
            }
        }
    }

    public void sendCommand() {
        String fileName = lv2.getSelectionModel().getSelectedItem();
            FileRequest fileRequest = new FileRequest(fileName);
            SignUpController.sendMessage(fileRequest);
            new Thread(() -> {
                try {
                    while (true) {
                        Object o = SignUpController.readMessage();
                        if (o instanceof FileMessage) {
                            FileMessage fileMessage = (FileMessage) o;
                            Path fp = Paths.get(clientFilesPath + "/" + fileMessage.getFilename());
                            if (fileMessage.getPart() == 1) {
                                if (Files.exists(fp)) {
                                    Files.delete(fp);
                                }
                                Files.write(fp, fileMessage.getData(), StandardOpenOption.CREATE);
                                System.out.println("Создана часть " + fileMessage.getPart() + " из : " + fileMessage.getCount());
                            }
                            if (fileMessage.getPart() > 1) {
                                Files.write(fp, fileMessage.getData(), StandardOpenOption.APPEND);
                                System.out.println("Создана часть " + fileMessage.getPart() + " из : " + fileMessage.getCount());
                            }
                            if (fileMessage.getPart() == fileMessage.getCount()) {
                                System.out.println("Файл загружен. Размер файла " + Files.size(fp));

                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            lv.getItems().remove(fileName);
            lv.getItems().add(fileName);

    }


//    public static boolean sendMessage(AbstractMessage msg) {
//        try {
//          os.writeObject(msg);
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            try {
//                socket.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//        return false;
//    }
//
//    public static AbstractMessage readMessage() {
//        Object o = null;
//        try {
//            o = is.readObject();
//        } catch (ClassNotFoundException | IOException e) {
//            e.printStackTrace();
//        }
//        return (AbstractMessage) o;
//    }


//    public void btnExit(javafx.event.ActionEvent actionEvent) {
//        Platform.exit();
//    }

    public void sendDelete(ActionEvent actionEvent) {
        String fileName = lv2.getSelectionModel().getSelectedItem();
        if (fileName != null) {
            FileDelete fileDelete = new FileDelete(fileName);
            System.out.println("Команда на удаление файла : " + fileName);
            SignUpController.sendMessage(fileDelete);
        }
        lv2.getItems().clear();
        sendFileList();
    }

    public void upload(ActionEvent actionEvent) {
        String fileName = lv.getSelectionModel().getSelectedItem();
        System.out.println(fileName);
        File file = new File(clientFilesPath + "/" + fileName);
        int bufferSize = 1024 * 1024 * 10;
        byte[] b = new byte[bufferSize];
        int count = (int) (file.length() / bufferSize);
        if (file.length() % bufferSize != 0) {
            count++;
        }
        FileMessage fm = new FileMessage(file.getName(), b, count);
        try (FileInputStream fis = new FileInputStream(file)) {
            for (int i = 1; i <= count; i++) {
                int read = fis.read(b);
                fm.setPart(i);
                if (read < bufferSize) {
                    fm.setData(Arrays.copyOfRange(b, 0, read));
                } else {
                    fm.setData(b);
                }
               SignUpController.sendMessage(fm);
                System.out.println("Отправлена часть : " + fm.getPart());
            }
            lv2.getItems().clear();
            sendFileList();
            System.out.println("весь файл отправлен. Размер файла : " + file.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


