package controller;

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

    public static String [] info;
    @FXML
    void onBalance(ActionEvent event) throws IOException {
        stage = (Stage) balanceBtn.getScene().getWindow();
        try {
            String response = client.getAccountBalance();
            if(response.equals("")) {
                try {
                    root = FXMLLoader.load(getClass().getResource("welcome.fxml"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            else {
                String[] info = response.split(" ");
                stage = (Stage) balanceBtn.getScene().getWindow();
                FXMLLoader loader= new FXMLLoader(
                        getClass().getResource(
                                "balance.fxml"
                        )
                );
                scene = new Scene(loader.load());
                stage.setScene(scene);

                balanceController controller = loader.getController();
                controller.initData(info);

                stage.show();
            }
        }catch (NullPointerException n) {
            try {
                root = FXMLLoader.load(getClass().getResource("welcome.fxml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
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
        withdrawController w = new withdrawController();
        w.withdrawHandler(withdrawBtn);
    }

//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//
//    }
}
