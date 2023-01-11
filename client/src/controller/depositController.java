package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static controller.welcomeController.client;

public class depositController implements Initializable{
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
    private Button depositBtn;

    @FXML
    private Button backBtn;

    @FXML
    private Button fifty;

    @FXML
    private Button hundred;

    @FXML
    private Button seventy;

    @FXML
    private Button twenty;

    @FXML
    private Button twohundred;

    public depositController() {
    }

    void depositHandler(Button sceneBtn) throws IOException {
        stage = (Stage) sceneBtn.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("deposit.fxml"));
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    void deposit(ActionEvent event) throws IOException {
        String input = amountField.getText();
        successLabel.setVisible(false);
        errorLabel.setVisible(false);
        try {
            Integer amount = Integer.parseInt(input);
            if(amount == 0)
                errorLabel.setVisible(true);
            else {
                client.depositFunds(amount);
                successLabel.setVisible(true);
            }
        }catch (NumberFormatException n) {
            errorLabel.setVisible(true);
        }
    }

    @FXML
    void onBackClicked(ActionEvent event) throws IOException {
        stage = (Stage) backBtn.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("operations.fxml"));
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void onClickFifty(ActionEvent event) {
        removeVisibility();
        amountField.setText("50");
    }

    @FXML
    void onClickHundred(ActionEvent event) {
        removeVisibility();
        amountField.setText("100");
    }

    @FXML
    void onClickSeventy(ActionEvent event) {
        removeVisibility();
        amountField.setText("70");
    }

    @FXML
    void onClickTwenty(ActionEvent event) {
        removeVisibility();
        amountField.setText("20");
    }

    @FXML
    void onClickFivehundred(ActionEvent event) {
        removeVisibility();
        amountField.setText("500");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        depositBtn.disableProperty().bind(amountField.textProperty().isEmpty());
    }

    private void removeVisibility() {
        successLabel.setVisible(false);
        errorLabel.setVisible(false);}
}
