package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.stage.Stage;
import model.atmClient;

import java.io.IOException;
import java.net.ConnectException;

public class welcomeController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    public static atmClient client;
    @FXML
    private Button startBtn;
    @FXML
    void start(ActionEvent event) throws IOException{
        try {
            client = new atmClient("127.0.0.1", 8080);
            authController a = new authController();
            a.authenticateHandler(startBtn);
        }catch (IOException i) {
            configController c = new configController();
            c.configHandler(startBtn);
        }

    }

}
