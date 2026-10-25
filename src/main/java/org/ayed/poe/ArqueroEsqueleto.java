package org.ayed.poe;

import org.ayed.juego.Mazmorra;
import org.ayed.juego.Celda;
import org.ayed.juego.EfectoCelda;

public class ArqueroEsqueleto extends Monstruo {

    public ArqueroEsqueleto(int fila, int columna) {
        super(fila, columna, 40, 10);
        this.rangoVision = 8;
        this.rangoAtaque = 3;
        this.colorVisual = java.awt.Color.GREEN;
    }

    /**
     * PRE: mazmorra y jugador válidos.
     * POST: Aplica kiting si el jugador está adyacente, dispara a distancia si está en rango, o avanza.
     */
    @Override
    protected void tomarDecisionActiva(Mazmorra mazmorra, Personaje jugador) {
        int distanciaFisica = Math.abs(this.getFila() - jugador.getFila()) + 
                              Math.abs(this.getColumna() - jugador.getColumna());

        if (distanciaFisica <= 1) {
            if (!huir(mazmorra, jugador)) {
                atacar(mazmorra, jugador); 
            }
        } else if (distanciaFisica <= this.rangoAtaque) {
            atacar(mazmorra, jugador);
        } else {
            this.acercarseAJugador(mazmorra, jugador);
        }
    }

    /**
     * PRE: mazmorra y jugador válidos.
     * POST: Inflige daño perforante calculando el terreno.
     */
    private void atacar(Mazmorra mazmorra, Personaje jugador) {
        Celda miPos = mazmorra.obtenerMapaVisual().elemento(this.getFila(), this.getColumna());
        Celda suPos = mazmorra.obtenerMapaVisual().elemento(jugador.getFila(), jugador.getColumna());
        
        int danoReal = mazmorra.calcularDañoFinal(this.getDañoBase(), miPos, suPos);
        jugador.recibirDaño(danoReal);
        System.out.println("¡Arquero Esqueleto dispara! Recibís " + danoReal + " de daño perforante.");
    }

    /**
     * PRE: mazmorra y jugador válidos.
     * POST: Intenta alejarse en línea recta o diagonal. Retorna true si pudo cambiar de posición.
     */
    private boolean huir(Mazmorra mazmorra, Personaje jugador) {
        int diffFila = this.getFila() - jugador.getFila();
        int diffCol = this.getColumna() - jugador.getColumna();

        if (intentarMover(mazmorra, this.getFila() + diffFila, this.getColumna() + diffCol)) return true;

        if (diffFila != 0) { 
            if (intentarMover(mazmorra, this.getFila(), this.getColumna() + 1)) return true;
            if (intentarMover(mazmorra, this.getFila(), this.getColumna() - 1)) return true;
        } else if (diffCol != 0) { 
            if (intentarMover(mazmorra, this.getFila() + 1, this.getColumna())) return true;
            if (intentarMover(mazmorra, this.getFila() - 1, this.getColumna())) return true;
        }
        
        return false;
    }

    /**
     * PRE: límites de matriz válidos en la mazmorra.
     * POST: Valida que la celda de destino no sea pared y realiza el movimiento.
     */
    private boolean intentarMover(Mazmorra mazmorra, int filaDestino, int colDestino) {
        if (filaDestino >= 0 && filaDestino < mazmorra.obtenerMapaVisual().filas() &&
            colDestino >= 0 && colDestino < mazmorra.obtenerMapaVisual().columnas()) {
            
            Celda celdaEscape = mazmorra.obtenerMapaVisual().elemento(filaDestino, colDestino);
            if (celdaEscape.getEfecto() != EfectoCelda.PARED) {
                this.moverA(celdaEscape);
                return true;
            }
        }
        return false;
    }
}