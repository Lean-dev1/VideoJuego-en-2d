package org.ayed.poe;

import java.awt.Graphics2D;
import org.ayed.visual.KeyHandler; // Asegurate de importar tu KeyHandler

public class PuertaEstacion extends Estacion {

    public PuertaEstacion(int x, int y, int tamaño) {
        super(x, y, tamaño, "Ir a la Mazmorra");
        // Cambiamos el texto visual solo para la puerta
        this.teclaInteraccion = "[ENTER]"; 
    }

    @Override
    public void dibujar(Graphics2D g2) {
        // Sigue vacío porque se usa el fondo
    }

    @Override
    public boolean estaSiendoInteractuado(Personaje p, KeyHandler keyH) {
        // NUEVA LÓGICA: En vez de usar inventoryPressed, usamos la variable de ENTER.
        // Reemplazá 'enterPressed' por cómo se llame exactamente el booleano en tu KeyHandler
        return jugadorEstaCerca(p) && keyH.enterPressed; 
    }
}