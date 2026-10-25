package org.ayed.poe;

import org.ayed.juego.Celda;
import org.ayed.tda.lista.Lista;

public abstract class Arma extends Item {
    private int dañoBase;

    // Nuevo constructor adaptado a la clase Item simple
    public Arma(int id, int ancho, int alto, String nombre, String rareza, int daño) {
        // Al padre le pasamos "Arma" como tipo directamente
        super(id, ancho, alto, nombre, "Arma", rareza);
        
        this.dañoBase = daño;
        
        // Sincronizamos el daño con la variable del padre por las dudas
        this.setDañoExtra(daño);
    }

    /**
     * Calcula qué celdas son impactadas por este arma.
     * @param origen Celda del jugador.
     * @param direccion Hacia dónde mira el jugador.
     * @return Una lista de celdas que reciben el daño.
     */
    public abstract Lista<Celda> obtenerAreaImpacto(Celda origen, String direccion);

    public int getDañoBase() { return dañoBase; }
}