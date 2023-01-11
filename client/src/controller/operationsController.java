package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

import static controller.welcomeController.client;
public class operationsController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Button balanceBtn;

    @FXML
    private Button closeBtn;

    @FXML
    private Button depositBtn;

    @FXML
    private Button withdrawBtn;

    @FXML
    void onBalance(ActionEvent event) throws IOException {
        client.getAccountBalance();
    }

    @FXML
    void onClose(ActionEvent event) throws IOException {
        client.closeConnection();
        stage = (Stage) closeBtn.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("welcome.fxml"));
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void onDeposit(ActionEvent event) throws IOException {
        depositController d = new depositController();
        d.depositHandler(depositBtn);
    }

    @FXML
    void onWithdraw(ActionEvent event) throws IOException {
        stage = (Stage) withdrawBtn.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("withdraw.fxml"));
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
