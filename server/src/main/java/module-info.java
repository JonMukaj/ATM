module atm.server {
    requires javafx.controls;
    requires javafx.fxml;


    opens atm.server.controller to javafx.fxml;
    exports atm.server;
}