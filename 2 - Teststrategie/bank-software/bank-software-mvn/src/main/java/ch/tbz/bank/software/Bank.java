package ch.tbz.bank.software;

import java.util.ArrayList;
public class Bank {

    private ArrayList<Account> accounts = new ArrayList<>();

    public Bank() {

    }

    public Account createAccount(String name, Currency currency, double startBalance) {
        Account a = new Account(name, currency, startBalance);
        addAccount(a);
        return a;
    }

    private void addAccount(Account a) {
        accounts.add(a);
    }

    public void deleteAccount(Account a) {
        System.out.println("Konto mit Nummer " + a.getId() + " wurde gelöscht.");
        accounts.remove(a);
    }

    public Account getAccount(int nr) {
        for (Account a : accounts) {
            if (a.getId() == nr) {
                return a;
            }
        }

        return null;
    }

    public void printAccountDetails(Account a) {
        if (!accounts.contains(a)) {
            System.out.println("Das Konto " +a.getId() + " existiert nicht mehr!");
            return;
        }
        System.out.println("___" );
        System.out.println("Kontonummer: " + a.getId());
        System.out.println("Nachname: " + a.getUserLastName());
        System.out.printf("Kontostand: %.2f %s\n", a.getBalance(), a.getCurrency());
    }

    public void printBalance(Account a) {
        System.out.printf("Neuer Kontostand: %.2f %s\n", a.getBalance(), a.getCurrency());
    }

    public void printAccountsList() {
        for (Account a: accounts) {
            System.out.println("Nr. " + a.getId() + ": " + a.getUserLastName() + " (" + a.getCurrency() + ")");
        }
    }

    public void printOtherAccounts(Account acc) {
        for (Account a: accounts) {
            if (a != acc) {
                System.out.println("Nr. " + a.getId() + ": " + a.getUserLastName());
            }
        }
    }

    public int getNumberOfAccounts() {
        return accounts.size();
    }

}
