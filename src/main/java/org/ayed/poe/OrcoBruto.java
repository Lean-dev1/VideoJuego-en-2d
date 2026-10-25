package org.ayed.poe;

import org.ayed.juego.Mazmorra;
import org.ayed.juego.Celda;

public class OrcoBruto extends Monstruo {

    public OrcoBruto(int fila, int columna) {
        super(fila, columna, 100, 15);
        this.rangoVision = 6;
        this.rangoAtaque = 1;
        this.colorVisual = java.awt.Color.RED;
    }

    /**
     * PRE: mazmorra y jugador inicializados correctamente.
     * POST: Si está en rango golpea considerando modificadores de terreno, de lo contrario avanza un paso.
     */
    @Override
    protected void tomarDecisionActiva(Mazmorra mazmorra, Personaje jugador) {
        Celda miPos;
        Celda suPos;
        int danoReal;

        if (this.puedeAtacarA(jugador)) {
            miPos = mazmorra.obtenerMapaVisual().elemento(this.getFila(), this.getColumna());
            suPos = mazmorra.obtenerMapaVisual().elemento(jugador.getFila(), jugador.getColumna());
            
            danoReal = mazmorra.calcularDañoFinal(this.getDañoBase(), miPos, suPos);
            jugador.recibirDaño(danoReal);
            System.out.println("¡Orco Bruto te aplasta! Recibís " + danoReal + " de daño físico.");
        } else {
            this.acercarseAJugador(mazmorra, jugador); // Reutiliza el método seguro de la clase base
        }
    }
}