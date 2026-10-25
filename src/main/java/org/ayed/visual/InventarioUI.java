package org.ayed.visual;

import org.ayed.poe.Inventario;

public class InventarioUI {
    private Inventario inventario;
    private int x, y;
    private int tileSize;
    private int anchoTotal;
    private int altoTotal;

    public InventarioUI(Inventario inventario, int tileSize) {
        this.inventario = inventario;
        this.tileSize = tileSize;
        this.anchoTotal = inventario.getCol() * tileSize;
        this.altoTotal = inventario.getFil() * tileSize;
    }

    public void actualizarPosicion(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public boolean contiene(int mx, int my) {
        return mx >= x && mx < x + anchoTotal && my >= y && my < y + altoTotal;
    }

    public int getCeldaX(int mx) { return (mx - x) / tileSize; }
    public int getCeldaY(int my) { return (my - y) / tileSize; }
    public int getX() { return x; }
    public int getY() { return y; }

    public Inventario getInventario() { return inventario; }
    

}