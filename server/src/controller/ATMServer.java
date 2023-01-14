package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.BankAccount;
import javafx.concurrent.Task;
import java.net.*;
import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

// ATMServer class serves as the primary server-side class of the application,
// responding to any requests arriving from the client app through the open TCP socket.
public class ATMServer implements Initializable {
    private Stage stage;
    @FXML
    private Button close;

    @FXML
    private TextArea logsField;

    @FXML
    private TextField portField;

    @FXML
    private Label infoLabel;

    @FXML
    private Label bindLabel;

    @FXML
    private Button stop;
    @FXML
    private Button start;
    // Initialize welcome socket
    private ServerSocket serverSocket;
    private int serverPort;
    private ATMServer thisServer;
    private Thread serverThread;
    private Server serverTask;
    private boolean isRunning;
    private static StringProperty content;

    ArrayList<BankAccount> bankAccounts;
    public ATMServer() {

    }

    // Class constructor to instantiate a welcome socket at the specified port
    // and to initiate the main loop of the server.
    public ATMServer(int port) throws BindException
    {
        // Instantiate an array with bank accounts
        bankAccounts = new ArrayList<>();
        bankAccounts.add(new BankAccount("02012004", "1111", 1000.00, "Jon Mukaj"));
        bankAccounts.add(new BankAccount("02012005", "2222", 2000.00, "Fabio Marku"));
        bankAccounts.add(new BankAccount("02012006", "3333", 3000.00, "Joana Jaupi"));
        bankAccounts.add(new BankAccount("02012007", "4444", 4000.00, "Kevin Tenolli"));

        try {
            this.serverPort = port;
            // Open the welcoming TCP socket at the specified port
            serverSocket = new ServerSocket(serverPort);
            serverTask = new Server();
            isRunning = true;
//            System.out.println(connectionThreads.size());
            serverThread = new Thread(serverTask);
            serverThread.start();
        }
        catch(IOException i) {
            System.out.println(Log.output("EXCEPTION: " + i.getMessage()));
            Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("EXCEPTION: " + i.getMessage())));
        }
    }

    @FXML
    private void closeServer() {
        try {
            isRunning = false;
            if(serverSocket != null)
                serverSocket.close();
            if(serverThread != null)
                serverThread.stop();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void onStop(ActionEvent event) throws InterruptedException {
        System.out.println(Log.output("Server closed at port " + Integer.parseInt(portField.getText())));
        stage = (Stage) stop.getScene().getWindow();
        stage.close();
        thisServer.closeServer();
    }
    @FXML
    void onClose(ActionEvent event) throws InterruptedException {
        stage = (Stage) close.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onStart(ActionEvent event) {
        try {
            bindLabel.setVisible(false);
            int port = Integer.parseInt(portField.getText());
            setContent("");
            thisServer = new ATMServer(port);
            System.out.println(Log.output("Server started at port " + portField.getText()));
//            setContent(content.get() + Log.output("Server started at port " + portField.getText()));
            portField.setVisible(false);
            start.setVisible(false);
            close.setVisible(false);
            stop.setVisible(true);
            infoLabel.setText("Server IP: " + Inet4Address.getLocalHost().getHostAddress() + "\tPort: " + port);
            infoLabel.setVisible(true);
        }catch (NumberFormatException n) {
            bindLabel.setText("Wrong input!");
            bindLabel.setVisible(true);
        }catch (BindException b) {
            bindLabel.setText("Port already in use!");
            bindLabel.setVisible(true);
        } catch (UnknownHostException e) {
            bindLabel.setText("Cannot determine ip of host!");
            bindLabel.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        portField.setVisible(true);
        start.setVisible(true);
        close.setVisible(true);
        stop.setVisible(false);
        infoLabel.setVisible(false);

        content = new SimpleStringProperty();
        start.disableProperty().bind(portField.textProperty().isEmpty());
        logsField.textProperty().bind(getContent());
    }

    private static StringProperty getContent() {
        return content;
    }

    private static void setContent(String content) {
        ATMServer.content.set(content);
    }

    private class Server extends Task {
        private static ArrayList<Thread> connectionThreads;
        private  static ArrayList<ClientHandler> connectionClients;

        public Server() {

        }
        @Override
        protected Object call() throws Exception {
            // Main loop of the server (will continue until server explicitly closed)
            while (isRunning) {
                // Await and accept connections by clients, then pass processing off to a new thread
                Socket clientSocket = serverSocket.accept();
                System.out.println(Log.output("LOG: Client with address " + clientSocket.getRemoteSocketAddress() + " connected successfully."));
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        setContent(content.get() + "\n" + Log.output("LOG: Client with address " + clientSocket.getRemoteSocketAddress() + " connected successfully."));
                    }
                });
                // Instantiate a new thread to handle the client's request
                new Thread(new ClientHandler(clientSocket,bankAccounts)).start();
            }
            return null;
        }
    }
    // Class to handle client requests
    private class ClientHandler extends Task {
        private final Socket connectionSocket;
        private InputStreamReader inputStreamReader;
        private OutputStreamWriter outputStreamWriter;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;
        private final ArrayList<BankAccount> bankAccounts;

        // Variable to hold the client's account after the client is authenticated
        private BankAccount currentAccount = null;

        // Boolean variable to hold state for user authentication
        private boolean isAuthenticated = false;

        // Instantiate the socket and IO streams

        public ClientHandler(Socket connectionSocket, ArrayList<BankAccount> bankAccounts) {
            this.connectionSocket = connectionSocket;
            this.bankAccounts = bankAccounts;

            try {
                // Instantiate I/O streams
                createStreams();
            }
            catch(Exception e) {
                System.out.println(Log.output(connectionSocket.getRemoteSocketAddress() + " - " + e.getMessage()));
                Platform.runLater(() -> setContent(content.get() + "\n" + Log.output(connectionSocket.getRemoteSocketAddress() + " - " + e.getMessage())));
            }
        }

        // Sends a message to the client through BufferedWriter
        private void send(String message) throws IOException {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }

        // Created stream objects for the connection socket to enable easier I/O
        private void createStreams() throws IOException{
            // Instantiate stream reader, write from the socket streams
            inputStreamReader = new InputStreamReader(connectionSocket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(connectionSocket.getOutputStream());

            // Wrap the above streams in buffered readers/writers
            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
        }

        // Checks if the user can be authenticated with the specific PIN provided
        private void authenticateUser(String pin) throws IOException {
            if(!isAuthenticated) {
                // Check if pin is valid
                for (BankAccount bankAccount : bankAccounts) {
                    if (bankAccount.pin.equals(pin)) {
                        // Send success message to client
                        isAuthenticated = true;
                        currentAccount = bankAccount;
                        send("OK");
                        System.out.println(Log.output("RESPONSE: Sent 'OK' to " + connectionSocket.getRemoteSocketAddress() + "."));
                        System.out.println(Log.output("LOG: User " + currentAccount.holderName + " has been authenticated."));
                        Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("RESPONSE: Sent 'OK' to " + connectionSocket.getRemoteSocketAddress() + ".") + "\n" + Log.output("LOG: User " + currentAccount.holderName + " has been authenticated.")));
                        return;
                    }
                }
                // If the given PIN matches no account, send a negative acknowledgment to the client
                send("NOTOK");
                System.out.println(Log.output("RESPONSE: Sent 'NOTOK' to " + connectionSocket.getRemoteSocketAddress() + "."));
                Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("RESPONSE: Sent 'NOTOK' to " + connectionSocket.getRemoteSocketAddress() + ".")));
            }
            else {
                // The user is already authenticated
                System.out.println(Log.output("ERROR: Client " + connectionSocket.getRemoteSocketAddress() + " is already authenticated."));
                Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("ERROR: Client " + connectionSocket.getRemoteSocketAddress() + " is already authenticated.")));
            }
        }

        // Retrieves the balance of the authenticated user
        private void getBalance() throws IOException {
            // Prepare the client to receive data with a positive ACK
            send("OK");
            System.out.println(Log.output("RESPONSE: Sent 'OK' to " + connectionSocket.getRemoteSocketAddress() + "."));
            Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("RESPONSE: Sent 'OK' to " + connectionSocket.getRemoteSocketAddress() + ".")));

            // Send customer name, account number, and balance to the client
            send(currentAccount.accountNumber + " " +  currentAccount.holderName.toUpperCase() +  " $" + currentAccount.balance);
            System.out.println(Log.output("RESPONSE: Sent '" + currentAccount.accountNumber + " " +  currentAccount.holderName.toUpperCase() +  " $" + currentAccount.balance + "' to " + connectionSocket.getRemoteSocketAddress() + "."));
            Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("RESPONSE: Sent '" + currentAccount.accountNumber + " " +  currentAccount.holderName.toUpperCase() +  " $" + currentAccount.balance + "' to " + connectionSocket.getRemoteSocketAddress() + ".")));
        }

        // Debits (removes) the specified amount from the authenticated user's account
        private void debitAmount(double debitAmt) throws IOException {
            // Check if user has sufficient funds in balance
            if (currentAccount.balance >= debitAmt) {
                // Debit amount
                currentAccount.balance -= debitAmt;
                System.out.println(Log.output("LOG: Debited " + debitAmt + " from account " + currentAccount.accountNumber));
                Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("LOG: Debited " + debitAmt + " from account " + currentAccount.accountNumber)));

                send("OK");
                System.out.println(Log.output("RESPONSE: Sent 'OK' to " + connectionSocket.getRemoteSocketAddress() + "."));
                Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("RESPONSE: Sent 'OK' to " + connectionSocket.getRemoteSocketAddress() + ".")));

                send("Account balance has been updated and now you hold $" + currentAccount.balance + ".");
                System.out.println(Log.output("RESPONSE: Sent 'Account balance has been updated and now you hold $" + currentAccount.balance + " .' to " + connectionSocket.getRemoteSocketAddress() + "."));
                Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("RESPONSE: Sent 'Account balance has been updated and now you hold $" + currentAccount.balance + " .' to " + connectionSocket.getRemoteSocketAddress() + ".")));
            }
            else {
                System.out.println(Log.output("LOG: DEBIT aborted due to insufficient funds in account " + currentAccount.accountNumber + "."));
                Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("LOG: DEBIT aborted due to insufficient funds in account " + currentAccount.accountNumber + ".")));

                // Send a negative ACK (due to insufficient funds)
                send("NOTOK");
                System.out.println(Log.output("RESPONSE: Sent 'NOTOK' to " + connectionSocket.getRemoteSocketAddress() + "."));
                Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("RESPONSE: Sent 'NOTOK' to " + connectionSocket.getRemoteSocketAddress() + ".")));

                // Send an explanatory message
                send("Debit request refused as account only holds $" + currentAccount.balance + ".");
                System.out.println(Log.output("RESPONSE: Sent 'Debit request refused as account only holds $" + currentAccount.balance + ".' to " + connectionSocket.getRemoteSocketAddress() + "."));
                Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("RESPONSE: Sent 'Debit request refused as account only holds $" + currentAccount.balance + ".' to " + connectionSocket.getRemoteSocketAddress() + ".")));
            }
        }

        // Credits (adds) the specified amount to the authenticated user's account
        private void creditAmount(double creditAmt) throws IOException {
            // Add credit to user account
            currentAccount.balance += creditAmt;
            System.out.println(Log.output("LOG: Credited " + creditAmt + " to account " + currentAccount.accountNumber));
            Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("LOG: Credited " + creditAmt + " to account " + currentAccount.accountNumber)));

            // Send a positive ACK to client
            send("OK");
            System.out.println(Log.output("RESPONSE: Sent 'OK' to " + connectionSocket.getRemoteSocketAddress() + "."));
            Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("RESPONSE: Sent 'OK' to " + connectionSocket.getRemoteSocketAddress() + ".")));

            // Send an explanatory message
            send("Account balance has been updated and now you hold $" + currentAccount.balance + ".");
            System.out.println(Log.output("RESPONSE: Sent 'Account balance has been updated and now you hold $" + currentAccount.balance + ".' to " + connectionSocket.getRemoteSocketAddress() + "."));
            Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("RESPONSE: Sent 'Account balance has been updated and now you hold $" + currentAccount.balance + ".' to " + connectionSocket.getRemoteSocketAddress() + ".")));
        }

        // Processes and replies to all client requests
        @Override
        protected Object call() throws Exception {
            try {
                // Main loop of the client handler (will continue execution until a client disconnects and an exception occurs)
                while (isRunning) {

                    // Get client's request and parse it
                    String request = bufferedReader.readLine();
                    // If there is no request, the client has disconnected
                    if(request == null) {
                        throw new SocketException("Client disconnected.");
                    }

                    // Request message is split in case it is an AUTH, DEBIT, or CREDIT request which contain two arguments each
                    String[] requestArray = request.split(" ");
                    switch (requestArray[0]) {
                        case "AUTH":        // AUTH command
                            System.out.println(Log.output("REQUEST: AUTH " + "****" + " from " + connectionSocket.getRemoteSocketAddress()));
                            Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("REQUEST: AUTH " + "****" + " from " + connectionSocket.getRemoteSocketAddress())));
                            authenticateUser(requestArray[1]);
                            break;
                        case "BALANCE":     // BALANCE command
                            System.out.println(Log.output("REQUEST: BALANCE" + " from " + connectionSocket.getRemoteSocketAddress()));
                            Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("REQUEST: BALANCE" + " from " + connectionSocket.getRemoteSocketAddress())));
                            getBalance();
                            break;
                        case "DEBIT":       // DEBIT command
                            System.out.println(Log.output("REQUEST: DEBIT " + requestArray[1] + " from " + connectionSocket.getRemoteSocketAddress()));
                            Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("REQUEST: DEBIT " + requestArray[1] + " from " + connectionSocket.getRemoteSocketAddress())));
                            debitAmount(Double.parseDouble(requestArray[1]));
                            break;
                        case "CREDIT":      // CREDIT command
                            System.out.println(Log.output("REQUEST: CREDIT " + requestArray[1] + " from " + connectionSocket.getRemoteSocketAddress()));
                            Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("REQUEST: CREDIT " + requestArray[1] + " from " + connectionSocket.getRemoteSocketAddress())));
                            creditAmount(Double.parseDouble(requestArray[1]));
                            break;
                        default:            // Invalid command
                            System.out.println(Log.output("ERROR: Invalid request received from " + connectionSocket.getRemoteSocketAddress() + ". Ignoring request."));
                            Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("ERROR: Invalid request received from " + connectionSocket.getRemoteSocketAddress() + ". Ignoring request.")));
                            break;
                    }
                }
            }
            catch (SocketException s) {
                // If an exception was thrown manually in the try block due to a null request, the client disconnected so
                // an appropriate message is displayed.
                if(s.getMessage().equals("Client disconnected.")) {
                    System.out.println(Log.output("LOG: Client with address " + connectionSocket.getRemoteSocketAddress() + " disconnected."));
                    Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("LOG: Client with address " + connectionSocket.getRemoteSocketAddress() + " disconnected.")));
                }
                else {
                    // s.printStackTrace();
                    System.out.println(Log.output("LOG: Client with address " + connectionSocket.getRemoteSocketAddress() + " disconnected ungracefully."));
                    Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("LOG: Client with address " + connectionSocket.getRemoteSocketAddress() + " disconnected ungracefully.")));
                }
            }
            catch (Exception e) {
                System.out.println(Log.output("EXCEPTION: " + e.getMessage()));
                Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("EXCEPTION: " + e.getMessage())));
            }
            finally {
                // Regardless of whether an exception occurs,
                // the streams and socket should be closed.
                try {
                    if (connectionSocket != null) {
                        connectionSocket.close();
                    }
                    if (inputStreamReader != null) {
                        inputStreamReader.close();
                    }
                    if (outputStreamWriter != null) {
                        outputStreamWriter.close();
                    }
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (bufferedWriter != null) {
                        bufferedWriter.close();
                    }
                }
                catch (IOException i) {
                    System.out.println(Log.output("IOEXCEPTION: " + i.getMessage()));
                    Platform.runLater(() -> setContent(content.get() + "\n" + Log.output("IOEXCEPTION: " + i.getMessage())));
                }
            }
            return null;
        }
    }


    // Encapsulated into a single class to avoid defining identical log methods in both ATMServer and ClientHandler classes
    private static class Log {
        public static String output(String message) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss z");
            String timeStamp = ZonedDateTime.now().format(format);
//            System.out.println(timeStamp + " -- " + message);
            return timeStamp + " -- " + message;
        }
    }
}






