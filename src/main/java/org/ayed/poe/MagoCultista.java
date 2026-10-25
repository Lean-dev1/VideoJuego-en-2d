package org.ayed.poe;

import org.ayed.juego.Mazmorra;
import org.ayed.juego.Celda;
import org.ayed.juego.EfectoCelda;

public class MagoCultista extends Monstruo {

    public MagoCultista(int fila, int columna) {
        super(fila, columna, 60, 20);
        this.rangoVision = 7;
        this.rangoAtaque = 2;
        this.colorVisual = java.awt.Color.MAGENTA;
    }

    /**
     * PRE: mazmorra y jugador no nulos.
     * POST: Ejecuta comportamiento táctico mágico (Blink/Teleport si está acorralado, ataque puro o persecución).
     */
    @Override
    protected void tomarDecisionActiva(Mazmorra mazmorra, Personaje jugador) {
        int distancia = Math.abs(this.getFila() - jugador.getFila()) + 
                        Math.abs(this.getColumna() - jugador.getColumna());

        if (distancia <= 1) {
            if (!teletransportarseTactico(mazmorra, jugador)) {
                atacar(mazmorra, jugador);
            }
        } else if (distancia <= this.rangoAtaque) {
            atacar(mazmorra, jugador);
        } else {
            this.acercarseAJugador(mazmorra, jugador);
        }
    }

    /**
     * PRE: jugador válido.
     * POST: Infinge daño puro ignorando las reducciones del mapa visual.
     */
    private void atacar(Mazmorra mazmorra, Personaje jugador) {
        jugador.recibirDaño(this.getDañoBase());
        System.out.println("¡El Mago conjura un hechizo oscuro! -" + this.getDañoBase() + " de daño puro.");
    }

    /**
     * PRE: mazmorra y jugador válidos y cargados.
     * POST: Busca aleatoriamente una posición libre que mantenga su rango ideal de ataque.
     * Cumple estrictamente los mandamientos de estructuración de bucles y asignación de variables.
     */
    private boolean teletransportarseTactico(Mazmorra mazmorra, Personaje jugador) {
        int intentosMaximos = 15;
        int contadorIntentos = 0;
        boolean exito = false;
 
        int desplazamientoFila;
        int desplazamientoColumna;
        int filaDestino;
        int colDestino;
        int distAlJugador;
        Celda destino;
        
        while (!exito && contadorIntentos < intentosMaximos) {
            desplazamientoFila = (int) (Math.random() * 7) - 3; 
            desplazamientoColumna = (int) (Math.random() * 7) - 3; 
            
            filaDestino = this.getFila() + desplazamientoFila;
            colDestino = this.getColumna() + desplazamientoColumna;
            
            distAlJugador = Math.abs(filaDestino - jugador.getFila()) + Math.abs(colDestino - jugador.getColumna());

            if (distAlJugador >= 2 && distAlJugador <= this.rangoAtaque + 1 &&
                filaDestino >= 0 && filaDestino < mazmorra.obtenerMapaVisual().filas() &&
                colDestino >= 0 && colDestino < mazmorra.obtenerMapaVisual().columnas()) {
                
                destino = mazmorra.obtenerMapaVisual().elemento(filaDestino, colDestino);
                
                if (destino.getEfecto() != EfectoCelda.PARED) {
                    this.moverA(destino);
                    System.out.println("¡El Mago Cultista usa Traslación Oscura para reposicionarse!");
                    exito = true;
                }
            }
            contadorIntentos++;
        }
        return exito;
    }
}