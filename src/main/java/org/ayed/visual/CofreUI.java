package org.ayed.visual; 

import org.ayed.poe.Alijo;
import org.ayed.poe.Inventario;
import org.ayed.poe.ItemInventario;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Clase encargada de la interfaz gráfica del cofre (alijo) del jugador.
 * Administra el renderizado de las diferentes pestañas y la transferencia de ítems.
 */
public class CofreUI {
    private Alijo alijo;
    private int pestanaActiva = 0;
    
    // Visores del inventario actual
    private PanelInventario visorCofre;
    private InventarioUI uiCofre;
    
    // Medidas para las "pestañas" dibujadas
    private final int ANCHO_PESTANA = 40;
    private final int ALTO_PESTANA = 25;
    private final int ESPACIO_PESTANA = 5;

    /**
     * Constructor de CofreUI.
     * @param alijo Instancia lógica del alijo del jugador. No debe ser nulo.
     */
    public CofreUI(Alijo alijo) {
        this.alijo = alijo;
        actualizarVisores();
    }

    /**
     * Actualiza los visores gráficos de la pestaña de inventario actualmente seleccionada.
     * Postcondiciones: Modifica las instancias de visorCofre y uiCofre con la pestaña activa.
     */
    private void actualizarVisores() {
        Inventario inventarioActual = alijo.obtenerPestaña(pestanaActiva);
        this.visorCofre = new PanelInventario(inventarioActual);
        this.uiCofre = new InventarioUI(inventarioActual, visorCofre.TAMANIO_CELDA);
    }

    /**
     * Dibuja los componentes visuales del cofre (pestañas y grilla de ítems).
     * @param g2 Contexto gráfico bidimensional. No debe ser nulo.
     * @param x Coordenada X base de anclaje en pantalla.
     * @param y Coordenada Y base de anclaje en pantalla.
     */
    public void dibujar(Graphics2D g2, int x, int y) {
        int i = 0;
        int px = 0;
        int py = 0;

        // 1. Dibujar las pestañas arriba del cofre
        for (i = 0; i < alijo.cantidadPestañas(); i++) {
            px = x + (i * (ANCHO_PESTANA + ESPACIO_PESTANA));
            py = y - ALTO_PESTANA; // Justo arriba del panel
            
            // Usamos el color corporativo para la pestaña activa
            if (i == pestanaActiva) {
                g2.setColor(Color.ORANGE);
            } else {
                g2.setColor(Color.DARK_GRAY);
            }
            
            g2.fillRect(px, py, ANCHO_PESTANA, ALTO_PESTANA);
            g2.setColor(Color.WHITE);
            g2.drawRect(px, py, ANCHO_PESTANA, ALTO_PESTANA);
            g2.drawString("P" + (i + 1), px + 10, py + 18);
        }

        // 2. Dibujar el panel del inventario
        uiCofre.actualizarPosicion(x, y);
        visorCofre.dibujar(g2, x, y);
    }
    
    /**
     * Establece una nueva posición en coordenadas para la interfaz gráfica del cofre.
     * @param x Nueva coordenada en el eje X.
     * @param y Nueva coordenada en el eje Y.
     */
    public void definirPosicion(int x, int y) {
        if (uiCofre != null) {
            uiCofre.actualizarPosicion(x, y);
        }
    }

    /**
     * Evalúa y gestiona el clic del mouse sobre el área correspondiente al cofre.
     * @param mouseX Coordenada X absoluta del puntero del mouse.
     * @param mouseY Coordenada Y absoluta del puntero del mouse.
     * @param gamePanel Instancia del contenedor principal del juego.
     * @return Devuelve true si el clic incidió sobre una pestaña o grilla del cofre, false en caso contrario.
     */
    public boolean gestionarClic(int mouseX, int mouseY, GamePanel gamePanel) {
        int xActual = uiCofre.getX(); 
        int yActual = uiCofre.getY();
        boolean clicGestionado = false;
        int i = 0;
        int px = 0;
        int py = 0;
        Rectangle rectPestana = null;
        
        // 1. Chequear si clickeó una pestaña (se utiliza while para no usar return interno)
        while (!clicGestionado && i < alijo.cantidadPestañas()) {
            px = xActual + (i * (ANCHO_PESTANA + ESPACIO_PESTANA));
            py = yActual - ALTO_PESTANA;
            
            rectPestana = new Rectangle(px, py, ANCHO_PESTANA, ALTO_PESTANA);
            if (rectPestana.contains(mouseX, mouseY)) {
                pestanaActiva = i;
                actualizarVisores(); // Refresca el panel con el nuevo inventario
                clicGestionado = true;
            }
            i++;
        }

        // 2. Chequear si clickeó en la grilla para agarrar/dejar ítems
        if (!clicGestionado && uiCofre.contiene(mouseX, mouseY)) {
            procesarClicGrilla(mouseX, mouseY, gamePanel);
            clicGestionado = true;
        }
        
        return clicGestionado;
    }

    /**
     * Procesa la lógica interna de interacción con las celdas de la grilla del cofre.
     * @param mouseX Coordenada X absoluta del clic.
     * @param mouseY Coordenada Y absoluta del clic.
     * @param gamePanel Instancia del panel de juego.
     */
    private void procesarClicGrilla(int mouseX, int mouseY, GamePanel gamePanel) {
        int celdaX = uiCofre.getCeldaX(mouseX);
        int celdaY = uiCofre.getCeldaY(mouseY);
        Inventario inventario = uiCofre.getInventario();
        ItemInventario itemAgarrado = gamePanel.getItemAgarrado();
        org.ayed.poe.Item eliminado = null;
        
        if (celdaX < 0 || celdaX >= inventario.getCol() || celdaY < 0 || celdaY >= inventario.getFil()) {
            return; 
        }

        if (itemAgarrado == null) {
            try {
                eliminado = inventario.eliminarItem(celdaX, celdaY);
                if (eliminado != null) {
                    gamePanel.setItemAgarrado(new ItemInventario(eliminado, celdaX, celdaY));
                }
            } catch (Exception excepcion) {}
        } else {
            try {
                inventario.colocarItem(celdaX, celdaY, itemAgarrado.getItem());
                gamePanel.setItemAgarrado(null); 
            } catch (Exception excepcion) { 
                System.out.println("No se pudo colocar en el cofre: " + excepcion.getMessage());
            }
        }
    }
    
    /**
     * Calcula el ancho total en píxeles que ocupa la interfaz del cofre en pantalla.
     * @return Cantidad de píxeles horizontales totales del componente.
     */
    public int obtenerAnchoTotal() {
        return alijo.obtenerPestaña(0).getCol() * visorCofre.TAMANIO_CELDA;
    }
}