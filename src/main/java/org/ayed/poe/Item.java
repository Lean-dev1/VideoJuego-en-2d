package org.ayed.poe;

import java.awt.image.BufferedImage;

public class Item {
    private final int id;
    private final int ancho;
    private final int alto;
    
    // Solo tus tres tipos y las rarezas en texto simple
    private String nombre;
    private String tipo;   // Puede ser "Casco", "Armadura" o "Arma"
    private String rareza; // "Normal", "Magico", "Raro", "Unico"
    
    // Solo las estadísticas que vas a usar
    private int vidaExtra;
    private int dañoExtra;
    private int armaduraExtra;
    
    protected BufferedImage sprite;

    public Item(int id, int ancho, int alto, String nombre, String tipo, String rareza) {
        this.id = id;
        this.ancho = ancho;
        this.alto = alto;
        this.nombre = nombre;
        this.tipo = tipo;
        this.rareza = rareza;
        
        // Arrancan en cero por defecto
        this.vidaExtra = 0;
        this.dañoExtra = 0;
        this.armaduraExtra = 0;
    }

    public Item() {
        this.id = 0;
        this.ancho = 0;
        this.alto = 0;
    }

    // --- SETTERS ---
    public void setVidaExtra(int vida) { this.vidaExtra = vida; }
    public void setDañoExtra(int daño) { this.dañoExtra = daño; }
    public void setArmaduraExtra(int armadura) { this.armaduraExtra = armadura; }

    // --- GETTERS ---
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getRareza() { return rareza; }
    public void setRareza(String rareza) { this.rareza = rareza; }
    
    public int getVidaExtra() { return vidaExtra; }
    public int getDañoExtra() { return dañoExtra; }
    public int getArmaduraExtra() { return armaduraExtra; }

    // --- EQUALS y HASHCODE (Volvimos a tu versión original simple) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id && ancho == item.ancho && alto == item.alto;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + id;
        hash = 31 * hash + ancho;
        hash = 31 * hash + alto;
        return hash;
    }

    public int anchoItem() { return ancho; }
    public int altoItem() { return alto; }
    public int getID() { return id; }
    public BufferedImage getSprite() { return this.sprite; }
}