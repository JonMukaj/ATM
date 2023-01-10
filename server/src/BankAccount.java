// Simple class used to store details of a bank account
public class BankAccount {
    public String accountNumber;
    public String pin;
    public double balance;
    public String holderName;

    public BankAccount(String accountNumber, String pin, double balance, String holderName) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
        this.holderName = holderName;
    }
}