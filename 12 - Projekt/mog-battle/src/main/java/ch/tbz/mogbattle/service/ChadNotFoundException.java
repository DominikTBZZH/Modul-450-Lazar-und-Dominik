package ch.tbz.mogbattle.service;

public class ChadNotFoundException extends RuntimeException {

    public ChadNotFoundException(Long id) {
        super("Chad mit id " + id + " existiert nicht");
    }
}
