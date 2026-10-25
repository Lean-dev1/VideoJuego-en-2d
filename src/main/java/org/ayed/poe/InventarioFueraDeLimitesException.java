package org.ayed.poe;

public class InventarioFueraDeLimitesException extends RuntimeException {
    public InventarioFueraDeLimitesException() {
        super();
    }

    public InventarioFueraDeLimitesException(String message) {
        super(message);
    }
}
