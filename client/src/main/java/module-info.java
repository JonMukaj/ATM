module atm.client {
    requires javafx.controls;
    requires javafx.fxml;

    opens atm.client.controller to javafx.fxml;
    exports atm.client;
}