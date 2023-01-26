package atm.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import atm.client.model.atmClient;
import java.io.IOException;

public class welcomeController {
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
