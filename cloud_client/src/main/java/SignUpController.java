import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    public Button signUp;
    public Button reg;
    public TextField login;
    public PasswordField password;
    private static HashMap<String, String> map;
    private static Socket socket;
    private static ObjectDecoderInputStream is;
    private static ObjectEncoderOutputStream os;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        map = new HashMap<>();
        try {
            socket = new Socket("localhost", 8189);
            is = new ObjectDecoderInputStream(socket.getInputStream(), 1024 * 1024 * 100);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enter(ActionEvent actionEvent) {

        sendFileAut(true);
    }

    public void checkIn(ActionEvent actionEvent) {
        sendFileAut(false);
    }

    private void sendFileAut(boolean bool) {
        String log = login.getText();
        System.out.println(login.getText());
        String pass = password.getText();
        System.out.println(password.getText());
        if (!log.isEmpty() && !pass.isEmpty()) {
            FileAut fileAut = new FileAut(log, pass, bool);
            sendMessage(fileAut);
            Object o = readMessage();
            if (o instanceof FileAnswer) {
                FileAnswer fa = (FileAnswer) o;
                if (fa.isBool()) {
                    signUp.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("sample.fxml"));
                    try {
                        loader.load();
                    }catch (IOException e){
                        System.out.println("что-то пошло не так");
                    }

                    Parent root = loader.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.showAndWait();
                    // закрываем это и открываем основное окно
                } else {
                    printText(fa.getText());
                }
            }
        } else {
            printText("Логин или пароль не введены!");
        }
    }

    private void printText(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(text);

        alert.showAndWait();
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
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return (AbstractMessage) o;
    }

}
