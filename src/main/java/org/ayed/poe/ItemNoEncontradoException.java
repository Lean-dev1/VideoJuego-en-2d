package org.ayed.poe;

public class ItemNoEncontradoException extends RuntimeException {
    public ItemNoEncontradoException() {
        super();
    }

    public ItemNoEncontradoException(String message) {
        super(message);
    }
}
