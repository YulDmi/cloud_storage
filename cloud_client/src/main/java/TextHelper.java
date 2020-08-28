import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class TextHelper {

    public static void printAlertText(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, text, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
