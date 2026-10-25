package org.ayed.poe;

import org.ayed.tda.vector.VectorDinamico;

public class Alijo {
    private VectorDinamico<Inventario> pestañas;
    private final int FILAS_ALIJO = 12;
    private final int COLUMNAS_ALIJO = 12;
    
    public Alijo() {
        this.pestañas = new VectorDinamico<>();
        // Arranca con una pestaña por defecto
        this.pestañas.agregar(new Inventario(COLUMNAS_ALIJO, FILAS_ALIJO));
    }

    // --- MANEJO DE PESTAÑAS ---
    
    /**
     * Agrega una nueva pestaña vacía al final del vector.
     * Esta función será llamada por la Tienda después de validar la compra.
     */
    public void agregarNuevaPestaña() {
        this.pestañas.agregar(new Inventario(COLUMNAS_ALIJO, FILAS_ALIJO));
    }

    // --- SELECTORES ---
    
    public VectorDinamico<Inventario> getPestañas() {
        return this.pestañas;
    }

    public Inventario obtenerPestaña(int indice) {
        if (indice >= 0 && indice < pestañas.tamanio()) {
            return pestañas.obtener(indice);
        }
        return null;
    }
    
    public int cantidadPestañas() {
        return pestañas.tamanio();
    }
}