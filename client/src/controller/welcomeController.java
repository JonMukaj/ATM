package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.atmClient;

import java.io.IOException;

public class welcomeController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    public static atmClient client;
    @FXML
    private Button startBtn;
    @FXML
    void start(ActionEvent event) throws IOException {
        client = new atmClient("127.0.0.1", 3000);
        authController a = new authController();
        a.authenticateHandler(startBtn);
    }

}
