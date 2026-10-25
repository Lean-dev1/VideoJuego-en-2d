package org.ayed.juego;

import java.util.Objects;

public class Celda {
    private int fila;
    private int columna;
    private EfectoCelda efecto;

    public Celda(int fila, int columna, EfectoCelda efecto) {
        this.fila = fila;
        this.columna = columna;
        this.efecto = efecto;
    }

    public int getFila() { 
        return fila; }
    
    public int getColumna() { 
        return columna; }
    
    public EfectoCelda getEfecto() { 
        return efecto; }
    
        public void setEfecto(EfectoCelda efecto) { this.efecto = efecto; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Celda celda = (Celda) o;
        return fila == celda.fila && columna == celda.columna;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fila, columna);
    }
}