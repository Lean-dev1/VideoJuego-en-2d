package org.ayed.poe;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class CofreEstacion extends Estacion {
    private BufferedImage imagenCofre;

    public CofreEstacion(int x, int y, int tamaño, BufferedImage imagen) {
        super(x, y, tamaño, "Alijo");
        this.imagenCofre = imagen;
    }

    @Override
    public void dibujar(Graphics2D g2) {
        // Ahora dibujamos la imagen en lugar de los rectángulos
        if (imagenCofre != null) {
            g2.drawImage(imagenCofre, x, y, tamaño, tamaño, null);
        }
    }
}