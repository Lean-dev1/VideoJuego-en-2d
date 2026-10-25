package org.ayed.juego;

import org.ayed.poe.Personaje;

public class Monstruo {
    private Celda celdaActual;
    private boolean alertado;

    
    public boolean estaMuerto() { 
        return false; }
    
    public Celda getCelda() { 
        return this.celdaActual; }
    
    public int getRangoVision() { 
        return 5; } 
    
    
    public void alertar() { 
        this.alertado = true; }
    
    public boolean estaAlertado() { 
        return this.alertado; }
    
    
    public boolean puedeAtacarA(Personaje j) { 
        return false; }
    
    public void atacar(Personaje j) { }
    
    public void moverA(Celda destino) { 
        this.celdaActual = destino; }
}
