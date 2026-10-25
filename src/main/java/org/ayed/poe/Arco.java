package org.ayed.poe;

import org.ayed.juego.Celda;
import org.ayed.juego.EfectoCelda;
import org.ayed.tda.lista.Lista;

public class Arco extends Arma {

    public Arco(int id, int ancho, int alto, String nombre, String rareza, int daño) {
        super(id, ancho, alto, nombre, rareza, daño);
    }

    @Override
    public Lista<Celda> obtenerAreaImpacto(Celda origen, String direccion) {
        Lista<Celda> area = new Lista<>();
        int f = origen.getFila();
        int c = origen.getColumna();

        for (int i = 1; i <= 3; i++) {
            if (direccion.equals("arriba")) {
                area.agregar(new Celda(f - i, c, EfectoCelda.NORMAL));
            } else if (direccion.equals("abajo")) {
                area.agregar(new Celda(f + i, c, EfectoCelda.NORMAL));
            } else if (direccion.equals("izquierda")) {
                area.agregar(new Celda(f, c - i, EfectoCelda.NORMAL));
            } else if (direccion.equals("derecha")) {
                area.agregar(new Celda(f, c + i, EfectoCelda.NORMAL));
            }
        }
        return area;
    }
}