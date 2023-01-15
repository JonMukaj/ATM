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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;

import static atm.client.controller.welcomeController.client;

public class withdrawController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField amountField;
    @FXML
    private Label successLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Label fundsLabel;
    @FXML
    private Button fifty;

    @FXML
    private Button hundred;

    @FXML
    private Button seventy;

    @FXML
    private Button twenty;

    @FXML
    private Button twoHundred;

    @FXML
    private Button backBtn;
    @FXML
    private Button withdrawBtn;

    public withdrawController() {

    }

    void withdrawHandler(Button sceneBtn) throws IOException {
        stage = (Stage) sceneBtn.getScene().getWindow();
        root = FXMLLoader.load(Main.class.getResource("withdraw.fxml"));
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    void onBackClicked(ActionEvent event) throws IOException {
        stage = (Stage) backBtn.getScene().getWindow();
        root = FXMLLoader.load(Main.class.getResource("operations.fxml"));
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    void onFiftyClick(ActionEvent event) {
        amountField.setText("50");
    }

    @FXML
    void onHundredClick(ActionEvent event) {
        removeVisibility();
        amountField.setText("100");
    }

    @FXML
    void onSeventyClick(ActionEvent event) {
        removeVisibility();
        amountField.setText("70");
    }

    @FXML
    void onTwentyClick(ActionEvent event) {
        removeVisibility();
        amountField.setText("20");
    }

    @FXML
    void onFivehundredClick(ActionEvent event) {
        removeVisibility();
        amountField.setText("500");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        withdrawBtn.disableProperty().bind(amountField.textProperty().isEmpty());
    }

    @FXML
    void withdraw(ActionEvent event) throws IOException {
        String input = amountField.getText();
        removeVisibility();

        try {
            Integer amount = Integer.parseInt(input);
            if(amount == 0)
                errorLabel.setVisible(true);
            else {
                if(client.withdrawFunds(amount))
                    successLabel.setVisible(true);
                else
                    fundsLabel.setVisible(true);
            }
        }catch (NumberFormatException n) {
            errorLabel.setVisible(true);
        }catch (NullPointerException n) {
            stage = (Stage) withdrawBtn.getScene().getWindow();
            root = FXMLLoader.load(Main.class.getResource("welcome.fxml"));
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }catch (SocketException s) {
            stage = (Stage) withdrawBtn.getScene().getWindow();
            root = FXMLLoader.load(Main.class.getResource("welcome.fxml"));
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    private void removeVisibility() {
        successLabel.setVisible(false);
        errorLabel.setVisible(false);
        fundsLabel.setVisible(false);
    }

}
