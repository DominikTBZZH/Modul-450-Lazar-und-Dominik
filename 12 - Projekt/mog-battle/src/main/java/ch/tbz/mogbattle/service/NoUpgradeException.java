package ch.tbz.mogbattle.service;

public class NoUpgradeException extends RuntimeException {

    public NoUpgradeException(String name) {
        super(name + " ist bereits an der Spitze, kein Upgrade verfügbar");
    }
}
