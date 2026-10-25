package org.ayed.poe;

import java.awt.Color;
import org.ayed.juego.Celda;
import org.ayed.juego.EfectoCelda;
import org.ayed.juego.Mazmorra;
import org.ayed.juego.BuscadorCaminos;
import org.ayed.tda.iterador.Iterador;

public abstract class Monstruo extends Entidad {
    
    protected int rangoVision;
    protected int oroQueSuelta;
    protected boolean alertado; 
    protected int rangoAtaque;
    protected Color colorVisual;
    protected int xpOtorgada;

    /**
     * PRE: fila >= 0, columna >= 0, vida > 0, daño > 0.
     * POST: Inicializa un enemigo en las coordenadas indicadas, en estado pasivo.
     */
    public Monstruo(int fila, int columna, int vida, int daño) {
        super(vida, 0, daño, 0); 
        this.setFila(fila);
        this.setColumna(columna);
        
        this.rangoVision = 6; 
        this.oroQueSuelta = 15;
        this.alertado = false; 
        this.rangoAtaque = 1; 
        this.colorVisual = Color.WHITE;
        this.xpOtorgada = 50;
    }
    
    public Color getColorVisual() {
        return this.colorVisual;
    }
    
    /**
     * PRE: El monstruo debe estar vivo y el jugador no debe ser nulo.
     * POST: Ejecuta el turno de la IA. Si no está alertado, comprueba distancia o patrulla. 
     * Si está alertado, delega la decisión táctica a la subclase.
     */
    public void ejecutarIA(Mazmorra mazmorra, Personaje jugador) {
        if (!this.estaVivo() || jugador == null) return;

        int distancia = Math.abs(this.getFila() - jugador.getFila()) + 
                        Math.abs(this.getColumna() - jugador.getColumna());

        // 1. CHEQUEO DE DETECCIÓN
        if (!this.estaAlertado() && distancia <= this.rangoVision) {
            this.alertar(); 
            System.out.println("¡Un " + this.getClass().getSimpleName() + " te vio!");
        }

        // 2. COMPORTAMIENTO
        if (!this.estaAlertado()) {
            moverAleatorio(mazmorra); 
        } else {
            tomarDecisionActiva(mazmorra, jugador);
        }
    }

    /**
     * PRE: mazmorra y jugador no nulos. El monstruo debe estar alertado.
     * POST: Ejecuta el comportamiento ofensivo o evasivo propio de la especie del monstruo.
     */
    protected abstract void tomarDecisionActiva(Mazmorra mazmorra, Personaje jugador);
    
    /**
     * PRE: jugador no nulo.
     * POST: Aplica el daño base del monstruo directamente al personaje objetivo.
     */
    protected void realizarAtaque(Personaje jugador) {
        jugador.recibirDaño(this.getDañoBase());
        System.out.println("El monstruo te ataca por " + this.getDañoBase() + " de daño.");
    }

    /**
     * PRE: objetivo no nulo.
     * POST: Determina mediante distancia de Manhattan si el objetivo está en rango de ataque.
     */
    public boolean puedeAtacarA(Personaje objetivo) {
        if (objetivo != null) {
            int difFila = Math.abs(this.getFila() - objetivo.getFila());
            int difCol = Math.abs(this.getColumna() - objetivo.getColumna());
            return (difFila + difCol) <= this.rangoAtaque; 
        }
        return false;
    }

    /**
     * PRE: mazmorra no nula.
     * POST: Mueve al monstruo a una celda adyacente libre de manera aleatoria.
     */
    private void moverAleatorio(Mazmorra mazmorra) {
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int azar = (int)(Math.random() * 4); 
        
        int nf = this.getFila() + dirs[azar][0];
        int nc = this.getColumna() + dirs[azar][1];
        
        Celda vecina = mazmorra.obtenerMapaVisual().elemento(nf, nc);
        if (vecina.getEfecto() != EfectoCelda.PARED) {
            this.moverA(vecina);
        }
    }

    /**
     * PRE: mazmorra y jugador no nulos.
     * POST: Calcula la ruta óptima mediante grafos y A* y desplaza al monstruo un paso hacia el jugador.
     */
    protected void acercarseAJugador(Mazmorra mazmorra, Personaje jugador) {
        BuscadorCaminos buscador = new BuscadorCaminos();
        Celda origen = mazmorra.obtenerMapaVisual().elemento(this.getFila(), this.getColumna());
        Celda destino = mazmorra.obtenerMapaVisual().elemento(jugador.getFila(), jugador.getColumna());
        
        Iterador<Celda> camino = buscador.encontrarCamino(mazmorra.getGrafoIA(), origen, destino);
        
        if (camino != null && camino.haySiguiente()) {
            camino.siguiente(); // Consume la celda origen (donde ya está parado)
            if (camino.haySiguiente()) {
                camino.siguiente(); // Avanza efectivamente a la celda del siguiente paso
                this.moverA(camino.dato());
            }
        }
    }

    public void alertar() { this.alertado = true; }
    public boolean estaAlertado() { return this.alertado; }
    
    /**
     * PRE: destino no nulo.
     * POST: Modifica las coordenadas de fila y columna de la entidad.
     */
    public void moverA(Celda destino) {
        if (destino != null) {
            this.setFila(destino.getFila());
            this.setColumna(destino.getColumna());
        }
    }
    
    public int getXpOtorgada() { return this.xpOtorgada; }
    public int getRangoVision() { return rangoVision; }
    public int getOroQueSuelta() { return oroQueSuelta; }
}