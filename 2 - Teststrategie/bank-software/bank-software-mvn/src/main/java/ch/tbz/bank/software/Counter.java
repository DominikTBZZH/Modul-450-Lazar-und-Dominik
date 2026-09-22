package ch.tbz.bank.software;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Counter {
    private Scanner sc;
    private static int counterId = 0;
    private Bank bank;

    public Counter(Bank bank) {
        sc = new Scanner(System.in);
        counterId = counterId + 1;
        this.bank = bank;
        System.out.println("Willkommen am Schalter " + counterId + "!");
    }

    public int chooseAccount() {
        do {
            System.out.println("___" );
            System.out.println("Was möchten Sie tun? Tippen Sie ...");
            System.out.println("eine Kontonummer, um das Konto zu bearbeiten.");
            System.out.println("\"a\" für alle Konten anzeigen.");
            System.out.println("\"e\" für Konto erstellen.");
            System.out.println("\"w\" für Wechselkurs abfragen.");
            System.out.println("\"q\" für Beenden.");
            System.out.print("> Eingabe: ");

            String str = sc.nextLine();
            str = str.toLowerCase();
            Pattern pattern = Pattern.compile("\\d|a|e|w|q");
            Matcher matcher = pattern.matcher(str);

            if (!matcher.find()) {
                System.out.println("! Ungültige Eingabe: Bitte eine Zahl, \"a\", \"e\", \"u\" oder \"q\" eingeben!");
                continue;
            }
            else {
                switch (str.substring(0, 1)) {
                    case "q" -> {
                        sayGoodbye();
                        return 0;
                    }
                    case "a" -> bank.printAccountsList();
                    case "w" -> getExchangeRate();
                    case "e" -> {
                        Account acc = createAccount();
                        bank.printAccountDetails(acc);
                    }
                }
            }

            try {
                int accNr =Integer.parseInt(str);
                Account acc = bank.getAccount(accNr);
                if (acc == null) {
                    throw new AccountExeption("Ein Konto mit dieser Nummer ist nicht vorhanden!");
                }
                bank.printAccountDetails(acc);
                return accNr;
            } catch (Exception e) {
                if (e instanceof AccountExeption) {
                    System.out.println(e.getMessage());
                }
                if (e instanceof java.util.InputMismatchException) {
                    System.out.println("! Ungültige Eingabe, bitte nochmals!");
                    sc.nextLine();
                }
            }
        } while (true);
    }

    public boolean editAccount(int accountNo) {

        Account acc = bank.getAccount(accountNo);

        System.out.println("Was möchten Sie tun? Tippen Sie ...");
        System.out.println("\"e\" für einzahlen.");
        System.out.println("\"a\" für abheben.");
        System.out.println("\"k\" für Kontostand abfragen.");
        System.out.println("\"ü\" für auf anderes Konto überweisen.");
        System.out.println("\"l\" für Konto löschen.");
        System.out.println("\"w\" für Konto wechseln.");
        System.out.println("\"q\" für beenden.");

        do {
            System.out.print("> Gewünschte Aktion: ");
            String input = sc.nextLine();
            input = input.substring(0,1);
            input = input.toLowerCase();

            Pattern pattern = Pattern.compile("[aekülwq]");
            Matcher matcher = pattern.matcher(input);

            if (!matcher.find()) {
                System.out.println("! Ungültige Eingabe: Bitte eine der Aktionen auswählen!");
                continue;
            }

            switch (input) {
                case "e" -> {
                    deposit(acc);
                    acc.printBalance();
                }
                case "a" -> {
                    withdraw(acc);
                    acc.printBalance();
                }
                case "k" -> acc.printBalance();
                case "ü" -> transfer(acc);
                case "q" -> {
                    sayGoodbye();
                    sc.close();
                    return false;
                }
                case "l" -> {
                    if (getConfirmation()) {
                        bank.deleteAccount(acc);
                    }
                    else {
                        System.out.println("! Aktion abgebrochen.");
                    }
                    return true;
                }
                case "w" -> {
                    return true;
                }
            }

        } while (true);
    }

    private boolean getConfirmation() {
        System.out.print("> Soll das Konto wirklich gelöscht werden? (j/n): ");
        String str = sc.nextLine();
        str = str.substring(0,1);
        str = str.toLowerCase();

        return str.equals("j");
    }

    private void transfer(Account accFrom) {
        System.out.println("___" );
        System.out.println("> Auf welches Konto soll überwiesen werden?:");
        bank.printOtherAccounts(accFrom);

        do {
            System.out.print("> Eingabe: ");
            String input = sc.nextLine();
            input = input.toLowerCase();
            Pattern pattern = Pattern.compile("^\\d+$");
            Matcher matcher = pattern.matcher(input);

            if (!matcher.find()) {
                System.out.println("! Ungültige Eingabe ! Bitte eine Zahl eingeben!");
            } else {
                try {
                    int accNr = Integer.parseInt(input);
                    Account accTo = bank.getAccount(accNr);
                    if (accTo == accFrom) {
                        throw new AccountExeption("! Bitte ein anderes Konto als das momentane Konto auswählen!");
                    }

                    if (accTo == null) {
                        throw new AccountExeption("! Ein Konto mit dieser Nummer ist nicht vorhanden!");
                    }

                    transferAmount(accFrom, accTo);
                    bank.printBalance(accFrom);
                    return;

                } catch (Exception e) {
                    if (e instanceof AccountExeption) {
                        System.out.println(e.getMessage());
                        return;
                    }

                    System.out.println("! Ungültige Eingabe, bitte nochmals!");
                }
            }
        } while(true);
    }

    public void transferAmount(Account accFrom, Account accTo) {

        do {
            System.out.print("> Welchen Betrag möchten Sie überweisen (" + accFrom.getCurrency() + ")? ");
            try {
                String str = sc.nextLine();
                double amount = Double.parseDouble(str);
                boolean res = accFrom.withdraw(amount);
                if (!res) {
                    throw new AccountExeption(("! Kontostand zu niedrig! (momentan " + accFrom.getBalance() + " " + accFrom.getCurrency() + ")"));
                }

                if (accFrom.getCurrency() != accTo.getCurrency() ) {
                    amount = convertCurrency(amount, accFrom.getCurrency(), accTo.getCurrency());
                }

                accTo.deposit(amount);
                return;

            } catch (Exception e) {
                if (e instanceof AccountExeption) {
                    System.out.println(e.getMessage());
                    continue;
                }
                System.out.println("! Ungültige Eingabe, bitte nochmals!");
            }
        } while(true);
    }

    private double convertCurrency(double amount, Currency currencyFrom, Currency currencyTo) {
        final double RATIO_USD_TO_CHF = 1.11;
        final double RATIO_USD_TO_EUR = 0.91;
        final double RATIO_CHF_TO_USD = 0.9;

        if (currencyFrom == Currency.USD && currencyTo == Currency.CHF) {
            return amount * RATIO_USD_TO_CHF;
        }

        if (currencyFrom == Currency.USD && currencyTo == Currency.EUR) {
            return amount * RATIO_USD_TO_EUR;
        }

        if (currencyFrom == Currency.CHF && currencyTo == Currency.USD) {
            return amount * RATIO_CHF_TO_USD;
        }

        System.out.println("! Es wurde keine Umrechnung vorgenommen.");
        return amount;
    }

    private void deposit(Account a) {
        do {
            System.out.print("> Welchen Betrag möchten Sie einzahlen (" + a.getCurrency() + ")? ");
            try {
                String str = sc.nextLine();
                double amount = Double.parseDouble(str);
                a.deposit(amount);
                return;
            } catch (Exception e) {
                System.out.println("! Ungültige Eingabe, bitte nochmals!");
            }
        } while(true);
    }

    private void withdraw(Account acc) {
        do {
            System.out.print("> Welchen Betrag möchten Sie abheben (" + acc.getCurrency() + ")? ");
            try {
                String str = sc.nextLine();
                double amount = Double.parseDouble(str);
                boolean res = acc.withdraw(amount);
                if (!res) {
                    throw new AccountExeption(("! Kontostand zu niedrig (momentan " + acc.getBalance() + " " + acc.getCurrency() + ")."));
                }
                return;
            } catch (Exception e) {
                if (e instanceof AccountExeption) {
                    System.out.println(e.getMessage());
                    continue;
                }

                System.out.println("! Ungültige Eingabe, bitte nochmals!");
            }
        } while(true);
    }

    public Account createAccount(){
        String name;
        String currency;
        double startBalance = 0.0;

        System.out.print("> Bitte Nachnamen eingeben: ");
        name = sc.nextLine();

        do {
            System.out.print("> Bitte Währungskürzel eingeben (z. Bsp. CHF, EUR, USD): ");
            currency = sc.nextLine();
            currency = currency.toUpperCase();

            Pattern pattern = Pattern.compile("^[A-Z]{3}$");
            Matcher matcher = pattern.matcher(currency);

            if (!matcher.find()) {
                System.out.println("! Ungültige Eingabe !");
                continue;
            }

            Currency currencyEnum;
            switch (currency) {
                case "USD" -> currencyEnum = Currency.USD;
                case "EUR" -> currencyEnum = Currency.EUR;
                case "CHF" -> currencyEnum = Currency.CHF;
                default -> {
                    System.out.println("! Die eingegebene Währung ist nicht bekannt, es wird USD verwendet.");
                    currencyEnum = Currency.USD;
                }
            }

            return bank.createAccount(name, currencyEnum, startBalance);
        } while(true);
    }

    public void sayGoodbye() {
        System.out.println("Auf Wiedersehen!");
    }

    public class AccountExeption extends Exception {
        public AccountExeption(String errorMessage) {
            super(errorMessage);
        }
    }

    private void getExchangeRate() {

        String currencies = "USD|EUR|CHF";
        String currencyFrom ;
        String currencyTo;

        do {
            System.out.print("> Bitte zwei Währungskürzel eingeben (z. Bsp. \"CHF USD\" für von CHF zu USD): ");
            String currency = sc.nextLine();
            currency = currency.toUpperCase();

            Pattern pattern = Pattern.compile("^(" + currencies + ")[ |,|>](" + currencies + ")$");
            Matcher matcher = pattern.matcher(currency);

            if (!matcher.find()) {
                System.out.println("! Ungültige Eingabe oder unbekannte Währung !");
                continue;
            }

            currencyFrom = matcher.group(1);
            currencyTo = matcher.group(2);
            break;

        } while(true);

        ExchangeRateOkhttp er = new ExchangeRateOkhttp();
        double res = er.getExchangeRate(currencyFrom, currencyTo);
        if (res != 0.0) {
            System.out.println("1 " + currencyFrom + " = " + res + " " + currencyTo);
        }
    }

}
