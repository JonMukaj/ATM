package atm.client.model;

import atm.client.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CustomAlert {
    public CustomAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Main.class.getResourceAsStream("images/icon.png")));
        alert.getDialogPane().setMinHeight(80);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.show();
    }
}
