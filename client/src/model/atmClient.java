package model;

import java.net.*;
import java.util.Scanner;
import java.io.*;

// model.ATMClient class serves as the primary client-side class of the application,
// providing an interface for the user to interact with the server.
public class atmClient {
    // Initialize socket and IO streams
    private Socket socket = null;
    private InputStreamReader inputStreamReader = null;
    private OutputStreamWriter outputStreamWriter = null;
    private BufferedReader bufferedReader = null;
    private BufferedWriter bufferedWriter = null;
    private Scanner cin = null;

    // Class constructor to instantiate a TCP connection to the server
    // at the specified address and port.
    public atmClient(String address, int port) throws IOException, SocketTimeoutException, UnknownHostException
    {
        establishConnection(address, port);
    }

    // Simple method to send a message to the server through BufferedWriter
    private void send(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private static void printInstructions() {
        System.out.println("To interact with the ATM, you may enter the following commands: ");
        System.out.println("1. 'START' to establish a connection with an ATM");
        System.out.println("2. 'CLOSE' to close the connection with an ATM");
        System.out.println("3. 'AUTH <PIN>' to authenticate with the ATM");
        System.out.println("4. 'BALANCE' to check your balance");
        System.out.println("5. 'DEBIT <amount>' to withdraw money from your account");
        System.out.println("6. 'CREDIT <amount>' to deposit money into your account");
    }

    private void establishConnection(String address, int port) throws IOException, SocketTimeoutException, UnknownHostException {
        if(socket != null) {
            System.out.println("You are already connected to an ATM.");
            return;
        }
        // Create TCP socket to server
        socket = new Socket();
        socket.connect(new InetSocketAddress(address, port), 5000);

        System.out.println("Connected successfully to server at " + address + ":" + port);

        // Instantiate stream reader, write from the socket streams
        inputStreamReader = new InputStreamReader(socket.getInputStream());
        outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

        // Wrap the above streams in buffered readers/writers
        bufferedReader = new BufferedReader(inputStreamReader);
        bufferedWriter = new BufferedWriter(outputStreamWriter);
    }

    public void closeConnection() throws IOException {
        if(socket == null) {
            System.out.println("You are not connected to an ATM.");
            return;
        }

        // Close the socket and the streams
        if (socket != null) {
            socket.close();
            socket = null;
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

        System.out.println("Connection closed successfully.");
    }

    public boolean authenticateUser(String text) throws IOException, NullPointerException {
        // Prompt the user for a PIN
        String pin = (text.equals("")  ? "null" : text);
        // Send the AUTH command to the server
        send("AUTH " + pin);

        // Read the response from the server
        String response = bufferedReader.readLine();

        return response.equals("OK");
    }

    public String getAccountBalance() throws IOException, NullPointerException {
        // Send the BALANCE command to the server
        send("BALANCE");

        // Read the response from the server
        String response = bufferedReader.readLine();

        if(response.equals("OK")) {
            response = bufferedReader.readLine();
            System.out.println(response);
        }
        else {
            response = "";
            System.out.println("Unidentified error occurred!");
        }
        return response;
    }

    public boolean withdrawFunds(int amount) throws IOException, NullPointerException {

        // Send the DEBIT command to the server
        send("DEBIT " + amount);

        // Read the response from the server (to consume OK or NOTOK message)
        String response = bufferedReader.readLine();
        System.out.println(response);

        return !response.contains("refused");
    }

    public boolean depositFunds(int amount) throws IOException, NullPointerException {
        bufferedReader.readLine();
        // Send the CREDIT command to the server
        send("CREDIT " + amount);

        // Read the response from the server (to consume OK or NOTOK message)

        String response = bufferedReader.readLine();
        System.out.println(response);
        return !(response == null);
    }

}
