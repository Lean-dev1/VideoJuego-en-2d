package org.ayed.visual.escenas;

import java.awt.Graphics2D;

public interface EscenaJuego {
    void update();               // Cada escena procesa su propia lógica/teclado
    void dibujar(Graphics2D g2); // Cada escena sabe cómo pintarse a sí misma
    public abstract void gestionarClic(int mouseX, int mouseY);
}