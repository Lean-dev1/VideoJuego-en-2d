package org.ayed.poe;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Entidad {
    private int x, y;
    private int fila, columna;
    private int vidaMax, vidaActual;
    private int manaMax, manaActual;
    private int dañoBase;
    private int armaduraBase;
    
    protected BufferedImage arriba1, arriba2, abajo1, abajo2;
    protected BufferedImage izquierda1, izquierda2, derecha1, derecha2;
    protected BufferedImage quietoFrente, quietoEspalda, quietoDerecha, quietoIzquierda;
    
    protected String direccion = "abajo";
    public boolean enMovimiento = false; 
    protected int spriteCounter = 0;
    protected int spriteNum = 1;
    protected int tamañoSprite = 58; 

    public Entidad(int vida, int mana, int daño, int armadura) {
        this.vidaMax = vida; 
        this.vidaActual = vida;
        this.manaMax = mana; 
        this.manaActual = mana;
        this.dañoBase = daño; 
        this.armaduraBase = armadura;
    }

    public boolean estaVivo() { 
        return this.vidaActual > 0; 
    }

    public void recibirDaño(int dañoEnemigo) {
        int dañoFinal = Math.max(0, dañoEnemigo - this.armaduraBase);
        this.setVidaActual(this.vidaActual - dañoFinal);
    }

    // Getters y Setters con encapsulamiento seguro
    public int getX() { return x; } 
    public void setX(int x) { this.x = x; }
    public int getY() { return y; } 
    public void setY(int y) { this.y = y; }
    public int getFila() { return fila; } 
    public void setFila(int f) { this.fila = f; }
    public int getColumna() { return columna; } 
    public void setColumna(int c) { this.columna = c; }
    public int getVidaActual() { return vidaActual; }
    
    /**
     * POST: Setea la vida actual asegurando que esté dentro del rango [0, vidaMax].
     */
    public void setVidaActual(int v) { 
        this.vidaActual = Math.max(0, Math.min(v, vidaMax)); 
    }
    
    public int getVidaMax() { return vidaMax; }
    public int getManaActual() { return manaActual; }
    public void setManaActual(int m) { this.manaActual = Math.max(0, Math.min(m, manaMax)); }
    public int getManaMax() { return manaMax; }
    public int getDañoBase() { return dañoBase; }
    public int getArmaduraBase() { return armaduraBase; }
    public void setVidaMax(int nuevaVidaMax) { this.vidaMax = nuevaVidaMax; }
    
    public void dibujar(Graphics2D g2) {
        BufferedImage imagen = null;
        switch (direccion) {
            case "arriba": imagen = (!enMovimiento) ? quietoEspalda : (spriteNum == 1 ? arriba1 : arriba2); break;
            case "abajo": imagen = (!enMovimiento) ? quietoFrente : (spriteNum == 1 ? abajo1 : abajo2); break;
            case "izquierda": imagen = (!enMovimiento) ? quietoIzquierda : (spriteNum == 1 ? izquierda1 : izquierda2); break;
            case "derecha": imagen = (!enMovimiento) ? quietoDerecha : (spriteNum == 1 ? derecha1 : derecha2); break;
        }
        if (imagen != null) {
            g2.drawImage(imagen, x, y, tamañoSprite, tamañoSprite, null);
        } else {
            g2.setColor(java.awt.Color.MAGENTA);
            g2.fillRect(x, y, tamañoSprite, tamañoSprite);
        }
    }
}