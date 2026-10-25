package org.ayed.visual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import org.ayed.poe.Inventario;
import org.ayed.poe.ItemInventario;
import org.ayed.tda.vector.VectorDinamico;

/**
 * Gestor visual encargado de segmentar y renderizar una matriz bidimensional de almacenamiento,
 * proyectando los ítems según sus dimensiones de ocupación en celdas.
 */
public class PanelInventario {
    private Inventario inventario;
    public final int TAMANIO_CELDA = 38;

    private static final int MARGEN_INTERNO_ITEM = 2;
    private static final int REDUCCION_DOBLE_BORDE = 4;
    private static final int DESPLAZAMIENTO_TEXTO_CENTRO_Y = 5;
    private static final int GROSOR_BORDE_RESALTADO = 2;
    private static final int GROSOR_BORDE_ESTANDAR = 1;

    /**
     * PRE: inventario no debe ser nulo.
     * POST: Asocia el contenedor de datos al panel para su posterior renderizado gráfico.
     */
    public PanelInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    /**
     * PRE: grafico2D no debe ser nulo. desplazamientoX y desplazamientoY determinan el origen en pantalla.
     * POST: Dibuja la cuadrícula base del contenedor y superpone los ítems correspondientes.
     */
    public void dibujar(Graphics2D grafico2D, int desplazamientoX, int desplazamientoY) {
        dibujarGrilla(grafico2D, desplazamientoX, desplazamientoY);
        dibujarItems(grafico2D, desplazamientoX, desplazamientoY);
    }

    /**
     * PRE: grafico2D no debe ser nulo.
     * POST: Genera visualmente las líneas divisorias oscuras que representan la capacidad libre de la matriz.
     */
    private void dibujarGrilla(Graphics2D grafico2D, int desplazamientoX, int desplazamientoY) {
        int i;
        int j;
        grafico2D.setColor(Color.DARK_GRAY);
        
        for (i = 0; i < inventario.getFil(); i++) {
            for (j = 0; j < inventario.getCol(); j++) {
                grafico2D.drawRect(desplazamientoX + (j * TAMANIO_CELDA), desplazamientoY + (i * TAMANIO_CELDA), TAMANIO_CELDA, TAMANIO_CELDA);
            }
        }
    }

    /**
     * PRE: grafico2D no debe ser nulo.
     * POST: Renderiza los ítems existentes en el vector dinámico. Declara variables externamente al ciclo
     * y omite rupturas bruscas de iteración, en estricto cumplimiento normativo.
     */
    private void dibujarItems(Graphics2D grafico2D, int desplazamientoX, int desplazamientoY) {
        VectorDinamico lista = inventario.ObtenerListaItems();
        int i;
        ItemInventario itemInventario;
        int pixelX;
        int pixelY;
        int anchoVisual;
        int altoVisual;
        String texto;
        int anchoTexto;
        int centroX;
        int centroY;

        // CORRECCIÓN MANDAMIENTO 10: Variables declaradas al inicio del método, fuera del bucle
        for (i = 0; i < lista.tamanio(); i++) {
            itemInventario = (ItemInventario) lista.obtener(i);
            
            // CORRECCIÓN MANDAMIENTO 8: Reemplazo del 'continue' por encapsulamiento lógico estructurado
            if (itemInventario != null) {
                pixelX = desplazamientoX + (itemInventario.getX() * TAMANIO_CELDA);
                pixelY = desplazamientoY + (itemInventario.getY() * TAMANIO_CELDA);
                
                anchoVisual = itemInventario.getItem().anchoItem() * TAMANIO_CELDA;
                altoVisual = itemInventario.getItem().altoItem() * TAMANIO_CELDA;

                grafico2D.setColor(new Color(40, 40, 50, 220)); 
                grafico2D.fillRect(pixelX + MARGEN_INTERNO_ITEM, pixelY + MARGEN_INTERNO_ITEM, anchoVisual - REDUCCION_DOBLE_BORDE, altoVisual - REDUCCION_DOBLE_BORDE);
                
                grafico2D.setColor(new Color(200, 200, 200));
                grafico2D.setStroke(new BasicStroke(GROSOR_BORDE_RESALTADO));
                grafico2D.drawRect(pixelX + MARGEN_INTERNO_ITEM, pixelY + MARGEN_INTERNO_ITEM, anchoVisual - REDUCCION_DOBLE_BORDE, altoVisual - REDUCCION_DOBLE_BORDE);
                
                grafico2D.setColor(Color.WHITE);
                texto = String.valueOf(itemInventario.getItem().getNombre());
                
                anchoTexto = grafico2D.getFontMetrics().stringWidth(texto);
                centroX = pixelX + (anchoVisual / 2) - (anchoTexto / 2);
                centroY = pixelY + (altoVisual / 2) + DESPLAZAMIENTO_TEXTO_CENTRO_Y;
                
                grafico2D.drawString(texto, centroX, centroY);
            }
        }
        grafico2D.setStroke(new BasicStroke(GROSOR_BORDE_ESTANDAR));
    }

    /**
     * PRE: nuevoInventario no debe ser nulo.
     * POST: Reemplaza la referencia actual del inventario, posibilitando el cambio de pestañas del alijo.
     */
    public void setInventario(Inventario nuevoInventario) {
        this.inventario = nuevoInventario;
    }
}