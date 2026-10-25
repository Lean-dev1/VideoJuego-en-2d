package org.ayed.poe;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import org.ayed.visual.KeyHandler;

public abstract class Estacion {
    protected int x;
    protected int y;
    protected int tamaño;
    protected int rangoInteraccion;
    protected String teclaInteraccion = "[E]";
    protected String nombreEstacion;
    private boolean interaccionVisual = true;

    public Estacion(int x, int y, int tamaño, String nombreEstacion) {
        this.x = x;
        this.y = y;
        this.tamaño = tamaño;
        this.nombreEstacion = nombreEstacion;
        this.rangoInteraccion = tamaño + 16; // Margen de cercanía
    }
    public Estacion(int x, int y, int tamaño, String nombreEstacion, boolean valint) {
        this.x = x;
        this.y = y;
        this.tamaño = tamaño;
        this.nombreEstacion = nombreEstacion;
        this.rangoInteraccion = tamaño + 16; 
        this.interaccionVisual = false;
    }

    // La matemática de distancia se calcula acá, ¡una sola vez para todas las estaciones!
    public boolean jugadorEstaCerca(Personaje p) {
        int distanciaX = Math.abs(p.getX() - this.x);
        int distanciaY = Math.abs(p.getY() - this.y);
        return (distanciaX <= rangoInteraccion && distanciaY <= rangoInteraccion);
    }

    // Cada hijo debe saber cómo dibujarse a sí mismo
    public abstract void dibujar(Graphics2D g2);
    
    public boolean estaSiendoInteractuado(Personaje p, KeyHandler keyH) {
        // Si está cerca Y presiona la tecla de acción (por ejemplo, 'E')
        return jugadorEstaCerca(p) && keyH.inventoryPressed;
    }

    // Feedback visual estándar para todas las estaciones cuando te acercás
    public void dibujarFeedbackInteraccion(Graphics2D g2) {
    	if (interaccionVisual) {
    		int feedbackPadding = 2;
            
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(x - feedbackPadding, y - feedbackPadding, 
                        tamaño + (feedbackPadding * 2), 
                        tamaño + (feedbackPadding * 2));
    	}           
        g2.setColor(Color.WHITE);
        g2.drawString(teclaInteraccion + " " + nombreEstacion, x, y - 10);
    }
}