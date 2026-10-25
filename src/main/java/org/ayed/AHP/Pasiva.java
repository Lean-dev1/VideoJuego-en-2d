package org.ayed.AHP;

public class Pasiva {
    private int id;
    private String nombre;
    private String estadistica;
    private boolean asignada;

    public Pasiva(int id, String nombre, String estadistica) {
        this.id = id;
        this.nombre = nombre;
        this.estadistica = estadistica;
        this.asignada = false;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEstadistica() { return estadistica; }
    public boolean isAsignada() { return asignada; }

    public void asignar() {
        this.asignada = true;
    }
}