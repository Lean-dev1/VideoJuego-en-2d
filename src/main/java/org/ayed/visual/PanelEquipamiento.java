package org.ayed.visual;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.ayed.poe.Equipamiento;
import org.ayed.poe.Item;
import org.ayed.poe.Slot;

/**
 * Administra la disposición espacial fija y el renderizado de los casilleros del equipamiento
 * del héroe (Casco, Pechera y Arma).
 */
public class PanelEquipamiento {
    private static final int TAMANIO_CASILLERO = 50;
    private static final int ESPACIO_ENTRE_CASILLEROS = 10;
    
    private static final int ANCHO_RECUADRO_FONDO = 180;
    private static final int ALTO_RECUADRO_FONDO = 150;
    private static final int OFFSET_FONDO_X = 10;
    private static final int OFFSET_FONDO_Y = 25;
    private static final int OFFSET_TITULO_Y = 10;
    
    private static final int DESPLAZAMIENTO_CENTRAL_X = 55;
    private static final int MARGEN_BORDE_RESPALDO = 2;
    private static final int REDUCCION_BORDE_RESPALDO = 4;

    /**
     * PRE: grafico2D y equipamiento no deben ser nulos.
     * POST: Renderiza el fondo unificado y distribuye proporcionalmente los casilleros corporales.
     */
    public void dibujar(Graphics2D grafico2D, Equipamiento equipamiento, int desplazamientoX, int desplazamientoY) {
        grafico2D.setColor(new Color(25, 25, 25, 200));
        grafico2D.fillRect(desplazamientoX - OFFSET_FONDO_X, desplazamientoY - OFFSET_FONDO_Y, ANCHO_RECUADRO_FONDO, ALTO_RECUADRO_FONDO);
        
        grafico2D.setColor(Color.WHITE);
        grafico2D.setFont(new Font("Arial", Font.BOLD, 12));
        grafico2D.drawString("EQUIPAMIENTO", desplazamientoX, desplazamientoY - OFFSET_TITULO_Y);

        dibujarCasillero(grafico2D, "Casco", equipamiento.obtenerItem(Slot.CASCO), desplazamientoX + DESPLAZAMIENTO_CENTRAL_X, desplazamientoY);
        
        dibujarCasillero(grafico2D, "Pechera", equipamiento.obtenerItem(Slot.ARMADURA), desplazamientoX + DESPLAZAMIENTO_CENTRAL_X, desplazamientoY + TAMANIO_CASILLERO + ESPACIO_ENTRE_CASILLEROS);
        
        dibujarCasillero(grafico2D, "Arma", equipamiento.obtenerItem(Slot.ARMA), desplazamientoX, desplazamientoY + TAMANIO_CASILLERO + ESPACIO_ENTRE_CASILLEROS);
    }

    /**
     * PRE: grafico2D y nombre no deben ser nulos. x e y representan las coordenadas exactas de la celda.
     * POST: Dibuja la textura del ítem equipado o muestra un texto informativo si el espacio se encuentra vacío.
     */
    private void dibujarCasillero(Graphics2D grafico2D, String nombre, Item item, int x, int y) {
        BufferedImage imagen;

        grafico2D.setColor(new Color(40, 40, 40));
        grafico2D.fillRect(x, y, TAMANIO_CASILLERO, TAMANIO_CASILLERO);
        
        grafico2D.setColor(Color.DARK_GRAY);
        grafico2D.drawRect(x, y, TAMANIO_CASILLERO, TAMANIO_CASILLERO); 
        
        if (item != null) {
            imagen = item.getSprite();
            
            if (imagen != null) {
                grafico2D.drawImage(imagen, x, y, TAMANIO_CASILLERO, TAMANIO_CASILLERO, null);
            } else {
                grafico2D.setColor(Color.ORANGE);
                grafico2D.fillRect(x + MARGEN_BORDE_RESPALDO, y + MARGEN_BORDE_RESPALDO, TAMANIO_CASILLERO - REDUCCION_BORDE_RESPALDO, TAMANIO_CASILLERO - REDUCCION_BORDE_RESPALDO);
                
                grafico2D.setColor(Color.BLACK);
                grafico2D.setFont(new Font("Arial", Font.BOLD, 12));
                grafico2D.drawString("ID:" + item.getID(), x + 10, y + 30);
            }
        } else {
            grafico2D.setColor(Color.GRAY);
            grafico2D.setFont(new Font("Arial", Font.ITALIC, 11));
            grafico2D.drawString(nombre, x + 8, y + 30);
        }
    }

    /**
     * PRE: desplazamientoX y desplazamientoY deben coincidir exactamente con el origen usado en el método dibujar.
     * POST: Evalúa la colisión del clic del mouse contra los casilleros y devuelve el identificador correspondiente.
     * Posee un único punto de salida para cumplir estrictamente la programación estructurada.
     */
    public String obtenerSlotClickeado(int mouseX, int mouseY, int desplazamientoX, int desplazamientoY) {
        String casilleroClickeado = null;

        // CORRECCIÓN MANDAMIENTO 8: Se elimina el múltiple return. Se unifica la salida mediante una variable de control.
        if (clicEnCuadrado(mouseX, mouseY, desplazamientoX + DESPLAZAMIENTO_CENTRAL_X, desplazamientoY)) {
            casilleroClickeado = "CASCO"; 
        } else if (clicEnCuadrado(mouseX, mouseY, desplazamientoX + DESPLAZAMIENTO_CENTRAL_X, desplazamientoY + TAMANIO_CASILLERO + ESPACIO_ENTRE_CASILLEROS)) {
            casilleroClickeado = "ARMADURA";
        } else if (clicEnCuadrado(mouseX, mouseY, desplazamientoX, desplazamientoY + TAMANIO_CASILLERO + ESPACIO_ENTRE_CASILLEROS)) {
            casilleroClickeado = "ARMA";
        }
        
        return casilleroClickeado; 
    }

    /**
     * PRE: -
     * POST: Retorna un valor booleano indicando si las coordenadas del cursor intersectan el casillero evaluado.
     */
    private boolean clicEnCuadrado(int mouseX, int mouseY, int casilleroX, int casilleroY) {
        return mouseX >= casilleroX && mouseX <= casilleroX + TAMANIO_CASILLERO && 
               mouseY >= casilleroY && mouseY <= casilleroY + TAMANIO_CASILLERO;
    }
}