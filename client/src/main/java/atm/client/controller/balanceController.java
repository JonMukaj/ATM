package atm.client.controller;
import atm.client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class balanceController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label balanceField;

    @FXML
    private Button closeBtn;

    @FXML
    private Label idField;

    @FXML
    private Label nameField;

    @FXML
    private Label surnameField;

    public balanceController() {

    }

    @FXML
    void onClose(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Main.class.getResource("operations.fxml"));
        stage = (Stage) closeBtn.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    void initData(String [] info) {
        idField.setText(info[0]);
        nameField.setText(info[1]);
        surnameField.setText(info[2]);
        balanceField.setText(info[3]);
    }
}
