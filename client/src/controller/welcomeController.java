package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.stage.Stage;
import model.atmClient;

import java.io.IOException;

public class welcomeController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Button startBtn;

    @FXML
    void start(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("operations.fxml"));
        stage = (Stage) startBtn.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        atmClient client = new atmClient("127.0.0.1", 3000);
    }

}
