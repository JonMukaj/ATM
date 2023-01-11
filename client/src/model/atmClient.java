package model;

import java.net.Socket;
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
    public atmClient(String address, int port)
    {
        try {
            establishConnection(address, port);
        } catch(IOException i)  {
            System.out.println(i.getMessage());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
//        finally {
//            // Regardless of whether an exception occurs,
//            // the streams and socket should be closed.
//            try {
//                if (socket != null) {
//                    socket.close();
//                }
//                if (inputStreamReader != null) {
//                    inputStreamReader.close();
//                }
//                if (outputStreamWriter != null) {
//                    outputStreamWriter.close();
//                }
//                if (bufferedReader != null) {
//                    bufferedReader.close();
//                }
//                if (bufferedWriter != null) {
//                    bufferedWriter.close();
//                }
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        // Code must be wrapped in try/catch blocks to handle socket exceptions
//        try {
//            cin = new Scanner(System.in);
//
//            // Display instructions to the user
//            printInstructions();
//
//            // Prompt the user for a command
//            System.out.print("Enter a command number (leave empty to quit): ");
//            String command = cin.nextLine();
//
//            boolean running = true;
//            boolean isAuthenticated = false;
//
//            // Loop until the user enters the "quit" command or enters an invalid PIN for the AUTH command
//            while(running) {
//
//                switch(command) {
//                    case "1":       // START command, establish connection
//                        establishConnection(address, port);
//                        break;
//                    case "2":       // CLOSE command, teardown connection
//                        isAuthenticated = false;
//                        closeConnection();
//                        break;
//                    case "3":       // AUTH command, authenticate user
//                        int nrOfTries = 3;
//                        if(!isAuthenticated && socket != null) {
//                            while(nrOfTries != 0) {
//                                isAuthenticated = authenticateUser();
//                                nrOfTries--;
//                                if(isAuthenticated) {
//                                    System.out.println("Authentication successful!");
//                                    break;
//                                }
//                                else {
//                                    if(nrOfTries != 0) {
//                                        System.out.println("Invalid PIN entered. Try again! " + "(" + nrOfTries + " attempts remaining)");
//                                        continue;
//                                    }
//                                    else {
//                                        System.out.println("Authentication failed!");
//                                        closeConnection();
//                                    }
//                                }
//                            }
//                        }
//                        else if (socket == null) {
//                            System.out.println("You must establish a connection before authenticating.");
//                        }
//                        else {
//                            System.out.println("You are already authenticated.");
//                        }
//                        break;
//                    case "4":       // BALANCE command, get account balance
//                        if(isAuthenticated)
//                            getAccountBalance();
//                        else
//                            System.out.println("User not authenticated!");
//                        break;
//                    case "5":       // DEBIT command, withdraw funds
//                        if(isAuthenticated)
//                            withdrawFunds();
//                        else
//                            System.out.println("User not authenticated!");
//                        break;
//                    case "6":       // CREDIT command, deposit funds
//                        if(isAuthenticated)
//                            depositFunds();
//                        else
//                            System.out.println("User not authenticated!");
//                        break;
//                    case "":        // Empty command, quit
//                        running = !quitPrompt(cin);
//                        break;
//                    default:
//                        System.out.println("Unknown or invalid command.");
//                        break;
//                }
//
//                if(running) {
//                    System.out.print("Enter a command number (leave empty to quit): ");
//                    command = cin.nextLine();
//                }
//            }
//        }
//        // To handle any exceptions resulting from the processes above
//        catch(IOException i)
//        {
//            System.out.println(i.getMessage());
//        }
//        catch(Exception e) {
//            e.printStackTrace();
//        }
//        finally {
//            // Regardless of whether an exception occurs,
//            // the streams and socket should be closed.
//            try {
//                if (socket != null) {
//                    socket.close();
//                }
//                if (inputStreamReader != null) {
//                    inputStreamReader.close();
//                }
//                if (outputStreamWriter != null) {
//                    outputStreamWriter.close();
//                }
//                if (bufferedReader != null) {
//                    bufferedReader.close();
//                }
//                if (bufferedWriter != null) {
//                    bufferedWriter.close();
//                }
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

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

    private void establishConnection(String address, int port) throws IOException {
        if(socket != null) {
            System.out.println("You are already connected to an ATM.");
            return;
        }

        // Create TCP socket to server
        socket = new Socket(address, port);
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

    public boolean authenticateUser(String text) throws IOException {
        // Prompt the user for a PIN
//        System.out.print("Enter your PIN: ");
//        String pin = cin.nextLine();
        String pin = (text == "" ? "null" : text);
        // Send the AUTH command to the server
        send("AUTH " + pin);

        // Read the response from the server
        String response = bufferedReader.readLine();

        return response.equals("OK");
    }

    public void getAccountBalance() throws IOException {
        // Send the BALANCE command to the server
        send("BALANCE");

        // Read the response from the server
        String response = bufferedReader.readLine();

        if(response.equals("OK")) {
            response = bufferedReader.readLine();
            System.out.println(response);
        }
        else
            System.out.println("Unidentified error occurred!");

    }

    public boolean withdrawFunds(int amount) throws IOException {
        // Prompt the user for an amount
//        System.out.print("Enter the amount to withdraw: ");
//        String amount = cin.nextLine();

        // Validate amount entered by the user (must be a positive number)
//        if(!amount.matches("[0-9]+")) {
//            System.out.println("Invalid amount entered, must be a positive amount!");
//            return;
//        }

        // Send the DEBIT command to the server
        send("DEBIT " + amount);

        // Read the response from the server (to consume OK or NOTOK message)
        String response = bufferedReader.readLine();

        response = bufferedReader.readLine();
        System.out.println(response);
        return !response.contains("refused");
    }

    public void depositFunds(int amount) throws IOException {
        // Prompt the user for an amount
//        System.out.print("Enter the amount to deposit: ");
//        String amount = cin.nextLine();
        // Validate amount entered by user (must be a positive number)
//        if(!amount.matches("[0-9]+")) {
//            System.out.println("Invalid amount entered, must be a positive amount!");
//            return;
//        }

        // Send the CREDIT command to the server
        send("CREDIT " + amount);

        // Read the response from the server (to consume OK or NOTOK message)
        String response = bufferedReader.readLine();

        response = bufferedReader.readLine();
        System.out.println(response);
    }

    private boolean quitPrompt(Scanner cin) {
        System.out.println("Are you sure you want to quit? (y/n)");
        String command = cin.nextLine();
        if(command.equalsIgnoreCase("y")) {
            System.out.println("Terminating...");
            return true;
        }
        else {
            System.out.println("Unknown or invalid command.");
            return false;
        }
    }

//    public static void main(String[] args) {
//        model.ATMClient client = null;
//        if(args.length == 0)
//            client = new model.ATMClient("127.0.0.1", 3000);
//        else
//            client = new model.ATMClient(args[0], Integer.parseInt(args[1]));
//
//    }
}
