package org.ayed.poe;

import javax.imageio.ImageIO;

public class Proteccion extends Item {
    private int puntosDefensa;

    // Nuevo constructor adaptado
    public Proteccion(int id, int ancho, int alto, String nombre, String tipo, String rareza, int defensa, String rutaImagen) {
        // Pasamos los datos al Item padre
        super(id, ancho, alto, nombre, tipo, rareza); 
        
        this.puntosDefensa = defensa;
        
        // Sincronizamos la armadura con la variable del padre
        this.setArmaduraExtra(defensa);
        
        cargarImagen(rutaImagen);
    }

    /**
     * Busca la imagen en la burbuja del Classpath y la decodifica.
     */
    private void cargarImagen(String ruta) {
        try {
            this.sprite = ImageIO.read(getClass().getResourceAsStream(ruta));
        } catch (Exception e) {
            System.out.println("Error cargando sprite de protección en ruta " + ruta + ": " + e.getMessage());
        }
    }

    public int getPuntosDefensa() {
        return this.puntosDefensa;
    }
}