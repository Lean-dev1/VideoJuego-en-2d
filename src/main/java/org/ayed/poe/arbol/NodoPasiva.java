package org.ayed.poe.arbol;

import org.ayed.tda.diccionario.Diccionario;

public class NodoPasiva {
    private final int id;
    private final String nombre;
    // estadisticas guarda pares (nombre -> valor), ej: ("vida", 10), ("armadura", 5)
    private final Diccionario<String, Integer> estadisticas;
    private boolean asignada;

    private static final int CAPACIDAD_ESTADISTICAS = 10;

    /**
     * Constructor de NodoPasiva.
     * Postcondiciones: crea una pasiva no asignada con las estadísticas vacías.
     *
     * @param id     Identificador único de la pasiva.
     * @param nombre Nombre descriptivo de la pasiva.
     */
    public NodoPasiva(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.estadisticas = new Diccionario<>(CAPACIDAD_ESTADISTICAS);
        this.asignada = false;
    }

    /**
     * Agrega una estadística a la pasiva.
     * Postcondiciones: la estadística queda registrada con su valor.
     *
     * @param nombreEstadistica Nombre de la estadística (ej: "vida", "armadura").
     * @param valor             Valor de la estadística.
     */
    public void agregarEstadistica(String nombreEstadistica, int valor) {
        estadisticas.agregar(nombreEstadistica, valor);
    }

    
    /**
     * Obtiene el valor de una estadística.
     * Postcondiciones: devuelve el valor de la estadística, o null si no existe.
     *
     * @param nombreEstadistica Nombre de la estadística.
     * @return el valor de la estadística.
     */
    public Integer obtenerEstadistica(String nombreEstadistica) {
        return estadisticas.obtenerValor(nombreEstadistica);
    }

    public int obtenerId() {
        return id;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public boolean estaAsignada() {
        return asignada;
    }

    public void asignar() {
        asignada = true;
    }

    public void desasignar() {
        asignada = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodoPasiva otro = (NodoPasiva) o;
        return id == otro.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return nombre + " (id=" + id + ", asignada=" + asignada + ")";
    }
}
