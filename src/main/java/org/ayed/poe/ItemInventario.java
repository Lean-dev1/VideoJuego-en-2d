package org.ayed.poe;

public class ItemInventario {
	private Item i;
	private int x;
	private int y;
	
	public ItemInventario(Item i, int x, int y) {
		this.i = i;
		this.x = x;
		this.y = y;
	}
	
	/** estas cosas no se sion estrictamente necesarias pero por el momento las voy a agregar */

    public Item getItem() {
        return i;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
