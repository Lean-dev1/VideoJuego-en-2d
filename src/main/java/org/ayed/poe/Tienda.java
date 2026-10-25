package org.ayed.poe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.ayed.tda.vector.VectorDinamico;

public class Tienda extends Estacion {

    // --- COSTOS DE LA TIENDA ---
    public static final int COSTO_EXPANSION_ALIJO = 500;
    public static final int COSTO_ARMA_ALEATORIA = 150;
    public static final int COSTO_CASCO_ALEATORIO = 100;
    public static final int COSTO_ARMADURA_ALEATORIA = 200;
    
    private BufferedImage imagenTienda;

    public Tienda(int posicionX, int posicionY, int tamanioEstacion, BufferedImage imagen) {
        super(posicionX, posicionY, tamanioEstacion, "Mercadito", false);
        this.imagenTienda = imagen;
    }
    
    @Override
    public void dibujar(Graphics2D g2) {
        if (imagenTienda != null) {
            int factorEscala = 5; 
            int visualTamanio = this.tamaño * factorEscala;
            int drawX = this.x - ((visualTamanio - this.tamaño) / 2);
            int drawY = this.y - (visualTamanio - this.tamaño);
            g2.drawImage(imagenTienda, drawX, drawY, visualTamanio + 5, visualTamanio - 25, null);
        } else {
            g2.setColor(Color.ORANGE);
            g2.fillRect(this.x, this.y, this.tamaño, this.tamaño);
        }
    }

    /**
     * Realiza la compra de un ítem aleatorio del catálogo según el slot especificado.
     * <p>
     * <b>PRE:</b> El cliente, el catálogo y el slot buscado deben estar inicializados y no ser nulos.
     * <b>POST:</b> Si el cliente posee el oro suficiente para el slot solicitado y existen ítems 
     * disponibles en el catálogo, descuenta el oro, añade el ítem a su inventario y retorna true. 
     * Caso contrario, no modifica el estado y retorna false.
     */
    public boolean comprarItemAleatorio(Personaje cliente, Catalogo catalogo, Slot slotBuscado) {
        VectorDinamico<Item> itemsFiltrados;
        Item itemElegido;
        int costo;
        int indiceAzar;
        boolean compraExitosa;

        compraExitosa = false;
        costo = 0;

        // 1. Determinamos el costo según el Slot solicitado
        switch (slotBuscado) {
            case ARMA:
                costo = COSTO_ARMA_ALEATORIA;
                break;
            case CASCO:
                costo = COSTO_CASCO_ALEATORIO;
                break;
            case ARMADURA:
                costo = COSTO_ARMADURA_ALEATORIA;
                break;
            default:
                System.out.println("❌ Slot de compra no soportado actualmente.");
                break;
        }

        // 2. Si el costo es válido y el cliente tiene suficiente oro, procedemos
        if (costo > 0 && cliente.getOro() >= costo) {
            itemsFiltrados = catalogo.obtenerItemsPorSlot(slotBuscado);

            // 3. Validamos que el catálogo realmente tenga ítems de ese tipo
            if (itemsFiltrados.tamanio() > 0) {
                indiceAzar = (int) (Math.random() * itemsFiltrados.tamanio());
                itemElegido = itemsFiltrados.obtener(indiceAzar);

                // 4. Impactamos la compra
                cliente.modificarOro(-costo);
                cliente.getInventario().colocarItem(itemElegido);
                System.out.println("🛒 ¡Compra exitosa!: " + itemElegido.getNombre() + " (" + slotBuscado.name() + ")");
                compraExitosa = true;
            }
        }

        return compraExitosa;
    }

    /**
     * Incrementa de forma fija en 1 la pestaña del alijo del personaje.
     */
    public boolean comprarExpansionAlijo(Personaje cliente) {
        if (cliente.getOro() >= COSTO_EXPANSION_ALIJO) {
            cliente.modificarOro(-COSTO_EXPANSION_ALIJO);
            
            if (cliente.getAlijo() != null) {
                cliente.getAlijo().agregarNuevaPestaña(); 
            }
            
            System.out.println("📦 [ALIJO] ¡Pestaña comprada con éxito!");
            return true;
        }
        return false;
    }
}