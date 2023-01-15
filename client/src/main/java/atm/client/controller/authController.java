package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import java.io.IOException;

import static controller.welcomeController.client;

public class authController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private boolean isAuthenticated;
    Integer nrOfTries = 3;
    @FXML
    private Label wrongField;
    @FXML
    private Button authenticateBtn;
    @FXML
    private Button closeBt;
    @FXML
    private PasswordField pinField;

    public authController() {}

    @FXML
    void authenticate(ActionEvent event) throws IOException{
        stage = (Stage) authenticateBtn.getScene().getWindow();

        try {
            isAuthenticated = client.authenticateUser(pinField.getText());
            nrOfTries--;

            if(isAuthenticated) {
                System.out.println("Authentication successful!");
                root = FXMLLoader.load(getClass().getResource("operations.fxml"));
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            else {
                if(nrOfTries != 0) {
                    wrongField.setText("Invalid PIN. Try again! " + "(" + nrOfTries + " attempts remaining)");
                    pinField.setText("");
                    System.out.println("Invalid PIN. Try again! " + "(" + nrOfTries + " attempts remaining)");
                }
                else {
                    System.out.println("Authentication failed!");
                    close(null);
                }
            }
        } catch (NullPointerException n) {
            root = FXMLLoader.load(getClass().getResource("welcome.fxml"));
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }
    @FXML
    void close(ActionEvent event) throws IOException {
        stage = (Stage) closeBt.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("welcome.fxml"));
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        client.closeConnection();
    }

    void authenticateHandler(Button startBtn) throws IOException {
        root = FXMLLoader.load(getClass().getResource("auth.fxml"));
        stage = (Stage) startBtn.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
