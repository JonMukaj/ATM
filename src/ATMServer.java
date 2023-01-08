import java.net.Socket;
import java.net.BindException;
import java.net.ServerSocket;
import java.io.*;
import java.net.SocketException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// ATMServer class serves as the primary server-side class of the application,
// responding to any requests arriving from the client app through the open TCP socket.
public class ATMServer {
    // Initialize welcome socket
    private ServerSocket serverSocket;

    // Class constructor to instantiate a welcome socket at the specified port
    // and to initiate the main loop of the server.
    public ATMServer(int port)
    {
        // Instantiate an array with bank accounts
        ArrayList<BankAccount> bankAccounts = new ArrayList<BankAccount>();
        bankAccounts.add(new BankAccount("02012004", "1111", 1000.00, "Jon Mukaj"));
        bankAccounts.add(new BankAccount("02012005", "2222", 2000.00, "Fabio Marku"));
        bankAccounts.add(new BankAccount("02012006", "3333", 3000.00, "Joana Jaupi"));
        bankAccounts.add(new BankAccount("02012007", "4444", 4000.00, "Kevin Tenolli"));

        try {
            // Open the welcoming TCP socket at the specified port
            serverSocket = new ServerSocket(port);
            Log.output("Server started at port " + port);

            // Main loop of the server (will continue until server explicitly closed)
            while(true) {
                // Await and accept connections by clients, then pass processing off to a new thread
                Socket clientSocket = serverSocket.accept();
                Log.output("LOG: Client with address " + clientSocket.getRemoteSocketAddress() + " connected successfully.");

                // Instantiate a new thread to handle the client's request
                Thread thread = new Thread(new ClientHandler(clientSocket, bankAccounts));
                thread.start();
            }
        }
        catch (BindException b) {
            System.err.println("Address already in use. Try another port!");
        }
        catch(SocketException s) {

        }
        catch(Exception generic) {
            generic.printStackTrace();
        }
        finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void closeServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ATMServer server = null;
        try {
            server = new ATMServer(Integer.parseInt(args[0]));
        }
        catch(NumberFormatException n) {
            System.err.println("Wrong port format! Enter a number!");
        }
    }
}

// Class to handle client requests
class ClientHandler implements Runnable {
    // Initialize socket and IO streams
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
            Log.output(connectionSocket.getRemoteSocketAddress() + " - " + e.getMessage());
        }
    }

    // Processes and replies to all client requests
    public void run() {
        try {
            // Main loop of the client handler (will continue execution until a client disconnects and an exception occurs)
            while (true) {
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
                        Log.output("REQUEST: AUTH " + requestArray[1] + " from " + connectionSocket.getRemoteSocketAddress());
                        authenticateUser(requestArray[1]);
                        break;
                    case "BALANCE":     // BALANCE command
                        Log.output("REQUEST: BALANCE" + " from " + connectionSocket.getRemoteSocketAddress());
                        getBalance();
                        break;
                    case "DEBIT":       // DEBIT command
                        Log.output("REQUEST: DEBIT " + requestArray[1] + " from " + connectionSocket.getRemoteSocketAddress());
                        debitAmount(Double.parseDouble(requestArray[1]));
                        break;
                    case "CREDIT":      // CREDIT command
                        Log.output("REQUEST: CREDIT " + requestArray[1] + " from " + connectionSocket.getRemoteSocketAddress());
                        creditAmount(Double.parseDouble(requestArray[1]));
                        break;
                    default:            // Invalid command
                        Log.output("ERROR: Invalid request received from " + connectionSocket.getRemoteSocketAddress() + ". Ignoring request.");
                        break;
                }
            }
        }
        catch (SocketException s) {
            // If an exception was thrown manually in the try block due to a null request, the client disconnected so
            // an appropriate message is displayed.
            if(s.getMessage().equals("Client disconnected.")) {
                Log.output("LOG: Client with address " + connectionSocket.getRemoteSocketAddress() + " disconnected.");
            }
            else {
                // s.printStackTrace();
                Log.output("LOG: Client with address " + connectionSocket.getRemoteSocketAddress() + " disconnected ungracefully.");
            }
        }
        catch (Exception e) {
            Log.output("EXCEPTION: " + e.getMessage());
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
            catch (IOException e) {
                e.printStackTrace();
            }
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
                    Log.output("RESPONSE: Sent 'OK' to " + connectionSocket.getRemoteSocketAddress() + ".");
                    Log.output("LOG: User " + currentAccount.holderName + " has been authenticated.");
                    return;
                }
            }
            // If the given PIN matches no account, send a negative acknowledgment to the client
            send("NOTOK");
            Log.output("RESPONSE: Sent 'NOTOK' to " + connectionSocket.getRemoteSocketAddress() + ".");
        }
        else {
            // The user is already authenticated
            Log.output("ERROR: Client " + connectionSocket.getRemoteSocketAddress() + " is already authenticated.");
        }
    }

    // Retrieves the balance of the authenticated user
    private void getBalance() throws IOException {
        // Prepare the client to receive data with a positive ACK
        send("OK");
        Log.output("RESPONSE: Sent 'OK' to " + connectionSocket.getRemoteSocketAddress() + ".");

        // Send customer name, account number, and balance to the client
        send(currentAccount.accountNumber + " " +  currentAccount.holderName.toUpperCase() +  " $" + currentAccount.balance);
        Log.output("RESPONSE: Sent '" + currentAccount.accountNumber + " " +  currentAccount.holderName.toUpperCase() +  " $" + currentAccount.balance + "' to " + connectionSocket.getRemoteSocketAddress() + ".");
    }

    // Debits (removes) the specified amount from the authenticated user's account
    private void debitAmount(double debitAmt) throws IOException {
        // Check if user has sufficient funds in balance
        if (currentAccount.balance >= debitAmt) {
            // Debit amount
            currentAccount.balance -= debitAmt;
            Log.output("LOG: Debited " + debitAmt + " from account " + currentAccount.accountNumber);
            send("OK");
            Log.output("RESPONSE: Sent 'OK' to " + connectionSocket.getRemoteSocketAddress() + ".");
            send("Account balance has been updated and now you hold $" + currentAccount.balance + ".");
            Log.output("RESPONSE: Sent 'Account balance has been updated and now you hold $" + currentAccount.balance + " .' to " + connectionSocket.getRemoteSocketAddress() + ".");
        }
        else {
            Log.output("LOG: DEBIT aborted due to insufficient funds in account " + currentAccount.accountNumber + ".");

            // Send a negative ACK (due to insufficient funds)
            send("NOTOK");
            Log.output("RESPONSE: Sent 'NOTOK' to " + connectionSocket.getRemoteSocketAddress() + ".");

            // Send an explanatory message
            send("Debit request refused as account only holds $" + currentAccount.balance + " .");
            Log.output("RESPONSE: Sent 'Debit request refused as account only holds $" + currentAccount.balance + " .' to " + connectionSocket.getRemoteSocketAddress() + ".");
        }
    }

    // Credits (adds) the specified amount to the authenticated user's account
    private void creditAmount(double creditAmt) throws IOException {
        // Add credit to user account
        currentAccount.balance += creditAmt;
        Log.output("LOG: Credited " + creditAmt + " to account " + currentAccount.accountNumber);

        // Send a positive ACK to client
        send("OK");
        Log.output("RESPONSE: Sent 'OK' to " + connectionSocket.getRemoteSocketAddress() + ".");

        // Send an explanatory message
        send("Account balance has been updated and now you hold $" + currentAccount.balance + " .");
        Log.output("RESPONSE: Sent 'Account balance has been updated and now you hold $" + currentAccount.balance + " .' to " + connectionSocket.getRemoteSocketAddress() + ".");
    }
}

// Encapsulated into a single class to avoid defining identical log methods in both ATMServer and ClientHandler classes
class Log {
    // Appends a timestamp to a message printed out through the console
    public static void output(String message) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss z");
        String timeStamp = ZonedDateTime.now().format(format);
        System.out.println(timeStamp + " -- " + message);
    }
}



