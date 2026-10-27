package ch.tbz.bank.software;

public class Account {

    static int counter = 0;
    private int id;
    private String userLastName;
    private Currency currency;
    private double balance;

    public Account(String userLastName, Currency currency, double startBalance) {
        counter++;
        id = counter;
        this.userLastName = userLastName;
        this.currency = currency;
        this.balance = startBalance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void printBalance() {
        System.out.printf("Aktueller Kontostand: %.2f %s\n", this.balance, this.currency);
    }

    public boolean withdraw(double amount) {
        if (amount > balance) {
            return false;
        } else {
            balance -= amount;
            return true;
        }
    }

    public double getBalance() {return balance;}
    public Currency getCurrency() {
        return currency;
    }
    public String getUserLastName() {
        return userLastName;
    }
    public int getId() {return id;}

    public void pseudoDeleteAccount() {
        this.userLastName = null;
        this.balance = 0;
        this.currency = null;
        this.id = 0;
    }

}
