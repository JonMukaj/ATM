package controller;

import Application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.CustomAlert;
import model.atmClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import static controller.welcomeController.client;

public class configController implements Initializable {
    @FXML
    public Button closeBtn;
    @FXML
    public Button connectBtn;
    @FXML
    public TextField ipField;
    @FXML
    public TextField portField;
    private Stage stage;
    private Scene scene;
    private Parent root;
    public configController() {

    }

    public void configHandler(Button startBtn) throws IOException {
        root = FXMLLoader.load(getClass().getResource("config.fxml"));
        stage = (Stage) startBtn.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    void onConnect(ActionEvent event) throws IOException {
        try {
            String ip = ipField.getText();
            int port = Integer.parseInt(portField.getText());
            if(port >= 1024) {
                client = new atmClient(ip, port);
                stage = (Stage) connectBtn.getScene().getWindow();
                root = FXMLLoader.load(getClass().getResource("auth.fxml"));
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            else {
                new CustomAlert("Permission denied!", "Ports (0-1024) are reserved!");
            }
        }
        catch (SocketTimeoutException s) {
            new CustomAlert("Wrong server config!", "Connection timeout out!");
        } catch (NumberFormatException n) {
            new CustomAlert("Wrong format!", "Port must be a number!");
        }catch (UnknownHostException u) {
            new CustomAlert("Unknown host!", "Wrong IP Address!");
        }
    }

    @FXML
    void onClose(ActionEvent event) throws IOException {
        stage = (Stage) closeBtn.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("welcome.fxml"));
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectBtn.disableProperty().bind(ipField.textProperty().isEmpty());
        connectBtn.disableProperty().bind(portField.textProperty().isEmpty());
    }
}
