package org.ayed.poe;

import java.awt.Graphics2D;

public class MesaEstacion extends Estacion {

    /**
     * Constructor de la estación Mesa de Investigación.
     * PRE: Las coordenadas X e Y, y el tamaño deben ser mayores o iguales a cero.
     * POST: Instancia la estación con el nombre "Mesa de Investigacion".
     */
    public MesaEstacion(int posicionX, int posicionY, int tamanioEstacion) {
        super(posicionX, posicionY, tamanioEstacion, "Mesa de Investigacion", false);
    }

    @Override
    public void dibujar(Graphics2D g2) {
        // Al estar la mesa dibujada en el PNG de fondo del mapa, acá lo dejamos vacío.
        // El hitbox físico ya está funcionando gracias al `super`.
        // Si quisieras dibujar un indicador o un sprite extra, iría acá.
    }
}