import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private static Connect connect;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connect = Connect.getInstance();
    }

    public void enter(ActionEvent actionEvent) {
        sendFileAut(true);
    }

    public void checkIn(ActionEvent actionEvent) {
        sendFileAut(false);
    }

    private void sendFileAut(boolean bool) {
        String log = login.getText();
        String pass = password.getText();
        if (!log.isEmpty() && !pass.isEmpty()) {
            FileAut fileAut = new FileAut(log, pass, bool);
            connect.sendMessage(fileAut);
            Object o = connect.readMessage();
            if (o instanceof FileAnswer) {
                FileAnswer fa = (FileAnswer) o;
                if (fa.isBool()) {
                    if (!fa.getText().isEmpty()) {
                        TextHelper.printAlertText(fa.getText());
                    }
                    startNewScene();
                } else {
                    TextHelper.printAlertText(fa.getText());
                }
            }
        } else {
            TextHelper.printAlertText("Логин или пароль не введены!");
        }
    }

    private void startNewScene() {
        signUp.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("sample.fxml"));
        try {
            loader.load();
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
           TextHelper.printAlertText("что-то пошло не так");
        }
    }

}
