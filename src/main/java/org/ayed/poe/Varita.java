package org.ayed.poe;

import org.ayed.juego.Celda;
import org.ayed.juego.EfectoCelda;
import org.ayed.tda.lista.Lista;

public class Varita extends Arma {

    public Varita(int id, int ancho, int alto, String nombre, String rareza, int daño) {
        super(id, ancho, alto, nombre, rareza, daño);
    }

    @Override
    public Lista<Celda> obtenerAreaImpacto(Celda origen, String direccion) {
        Lista<Celda> area = new Lista<>();
        int f = origen.getFila();
        int c = origen.getColumna();

        if (direccion.equals("arriba")) {
            int destinoF = f - 2;
            area.agregar(new Celda(destinoF, c, EfectoCelda.NORMAL));
            area.agregar(new Celda(destinoF, c - 1, EfectoCelda.NORMAL));
            area.agregar(new Celda(destinoF, c + 1, EfectoCelda.NORMAL));
        } else if (direccion.equals("abajo")) {
            int destinoF = f + 2;
            area.agregar(new Celda(destinoF, c, EfectoCelda.NORMAL));
            area.agregar(new Celda(destinoF, c - 1, EfectoCelda.NORMAL));
            area.agregar(new Celda(destinoF, c + 1, EfectoCelda.NORMAL));
        } else if (direccion.equals("izquierda")) {
            int destinoC = c - 2;
            area.agregar(new Celda(f, destinoC, EfectoCelda.NORMAL));
            area.agregar(new Celda(f - 1, destinoC, EfectoCelda.NORMAL));
            area.agregar(new Celda(f + 1, destinoC, EfectoCelda.NORMAL));
        } else if (direccion.equals("derecha")) {
            int destinoC = c + 2;
            area.agregar(new Celda(f, destinoC, EfectoCelda.NORMAL));
            area.agregar(new Celda(f - 1, destinoC, EfectoCelda.NORMAL));
            area.agregar(new Celda(f + 1, destinoC, EfectoCelda.NORMAL));
        }
        return area;
    }
}