package org.ayed.poe;

public class InventarioLlenoException extends RuntimeException {
    public InventarioLlenoException() {
        super();
    }

    public InventarioLlenoException(String message) {
        super(message);
    }
}
