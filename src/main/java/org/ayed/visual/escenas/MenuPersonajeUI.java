package org.ayed.visual.escenas;

import org.ayed.poe.ItemInventario;
import org.ayed.poe.Inventario;
import org.ayed.poe.Personaje;
import org.ayed.poe.Slot;
import org.ayed.visual.GamePanel;
import org.ayed.visual.PanelInventario;
import org.ayed.visual.InventarioUI;
import org.ayed.visual.PanelEquipamiento;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Interfaz de control y coordinación que unifica la visualización de la mochila,
 * el equipamiento activo y el árbol de habilidades pasivas del personaje.
 */
public class MenuPersonajeUI {
    private GamePanel panelJuego;
    private Personaje personaje;
    private org.ayed.visual.ArbolUI visorArbol;
    public boolean visualizandoArbol = false; 
    private PanelInventario visorMochila;
    private PanelEquipamiento visorEquipamiento;
    private InventarioUI interfazMochila;
    
    private static final int DESPLAZAMIENTO_TITULO_Y = 10;
    private static final int GROSOR_BORDE = 2;
    private static final int DOBLE_GROSOR = GROSOR_BORDE * 2;
    
    private int inicioBloqueX = 0;
    private int centroMochilaY = 0;
    
    private static final int ANCHO_PANEL_EQUIPAMIENTO = 180; 
    private static final int ESPACIO_INTERMEDIO = 40;  
    private static final int ESPACIO_INFERIOR_EQUIPAMIENTO = 50;
    
    private int ultimaPosicionEquipamientoX = 0;
    private int ultimaPosicionEquipamientoY = 0;

    /**
     * PRE: panelJuego y personaje deben estar correctamente instanciados y no ser nulos.
     * POST: Inicializa los visores de mochila, equipamiento y árbol de habilidades vinculados al personaje.
     */
    public MenuPersonajeUI(GamePanel panelJuego, Personaje personaje) {
        this.panelJuego = panelJuego;
        this.personaje = personaje;
        this.visorMochila = new PanelInventario(personaje.getInventario());
        this.interfazMochila = new InventarioUI(personaje.getInventario(), visorMochila.TAMANIO_CELDA);
        this.visorEquipamiento = new PanelEquipamiento();
        this.visorArbol = new org.ayed.visual.ArbolUI(personaje.getArbolHabilidades());
    }

    /**
     * PRE: -
     * POST: Evalúa las dimensiones del panel de juego y calcula el centrado del bloque de inventario.
     */
    private void calcularPosicionesInterfaz() {
        int anchoMochila;
        int altoMochila;
        int anchoTotalConjunto;

        if (panelJuego.getWidth() != 0 && panelJuego.getHeight() != 0) {
            anchoMochila = personaje.getInventario().getCol() * visorMochila.TAMANIO_CELDA; 
            altoMochila = personaje.getInventario().getFil() * visorMochila.TAMANIO_CELDA;
            anchoTotalConjunto = anchoMochila + ESPACIO_INTERMEDIO + ANCHO_PANEL_EQUIPAMIENTO;
            
            this.inicioBloqueX = (panelJuego.getWidth() - anchoTotalConjunto) / 2;
            this.centroMochilaY = (panelJuego.getHeight() - altoMochila) / 2;
        }
    }

    /**
     * PRE: grafico2D no debe ser nulo.
     * POST: Renderiza la mochila y el equipamiento alineados en el centro de la pantalla.
     */
    public void dibujarCentrado(Graphics2D grafico2D) {
        int anchoMochila;
        int coordenadaEquipoX;

        if (this.inicioBloqueX == 0 && this.centroMochilaY == 0) {
            calcularPosicionesInterfaz();
        }
        
        anchoMochila = personaje.getInventario().getCol() * visorMochila.TAMANIO_CELDA;

        grafico2D.setColor(Color.ORANGE);
        grafico2D.drawString("MOCHILA", this.inicioBloqueX, this.centroMochilaY - DESPLAZAMIENTO_TITULO_Y);
        
        this.interfazMochila.actualizarPosicion(this.inicioBloqueX, this.centroMochilaY);
        this.visorMochila.dibujar(grafico2D, this.inicioBloqueX, this.centroMochilaY); 
        
        coordenadaEquipoX = this.inicioBloqueX + anchoMochila + ESPACIO_INTERMEDIO;
        this.ultimaPosicionEquipamientoX = coordenadaEquipoX; 
        this.ultimaPosicionEquipamientoY = this.centroMochilaY; 
        
        this.visorEquipamiento.dibujar(grafico2D, personaje.getEquipamiento(), this.ultimaPosicionEquipamientoX, this.ultimaPosicionEquipamientoY);
    }

    /**
     * PRE: grafico2D no debe ser nulo. x e y representan el origen asignado por la escena contenedora.
     * POST: Dibuja la mochila en la coordenada absoluta provista y desplaza el equipamiento a la parte inferior.
     */
    public void dibujarEnPosicion(Graphics2D grafico2D, int x, int y) {
        int altoMochila;

        grafico2D.setColor(Color.WHITE);
        grafico2D.drawString("MOCHILA", x, y - DESPLAZAMIENTO_TITULO_Y);
        
        this.interfazMochila.actualizarPosicion(x, y);
        this.visorMochila.dibujar(grafico2D, x, y);
        
        altoMochila = personaje.getInventario().getFil() * visorMochila.TAMANIO_CELDA;
        
        this.ultimaPosicionEquipamientoX = x; 
        this.ultimaPosicionEquipamientoY = y + altoMochila + ESPACIO_INFERIOR_EQUIPAMIENTO;
        
        this.visorEquipamiento.dibujar(grafico2D, personaje.getEquipamiento(), this.ultimaPosicionEquipamientoX, this.ultimaPosicionEquipamientoY);
    }

    /**
     * PRE: grafico2D no debe ser nulo.
     * POST: Si existe un ítem en estado flotante, se renderiza centrado en la posición actual del cursor.
     */
    public void dibujarItemEnCursor(Graphics2D grafico2D) {
        ItemInventario itemAgarrado;
        int anchoItem;
        int altoItem;
        BufferedImage graficoItem;

        itemAgarrado = panelJuego.getItemAgarrado();
        
        // CORRECCIÓN MANDAMIENTO 8: Se elimina el 'return' intermedio mediante anidación lógica limpia
        if (itemAgarrado != null) {
            anchoItem = itemAgarrado.getItem().anchoItem() * visorMochila.TAMANIO_CELDA; 
            altoItem = itemAgarrado.getItem().altoItem() * visorMochila.TAMANIO_CELDA;
            
            grafico2D.setColor(Color.ORANGE);
            grafico2D.fillRect(panelJuego.mouseX - GROSOR_BORDE, panelJuego.mouseY - GROSOR_BORDE, anchoItem + DOBLE_GROSOR, altoItem + DOBLE_GROSOR);
            
            graficoItem = itemAgarrado.getItem().getSprite();
            
            if (graficoItem != null) {
                grafico2D.drawImage(graficoItem, panelJuego.mouseX, panelJuego.mouseY, anchoItem, altoItem, null);
            } else {
                grafico2D.setColor(Color.BLUE);
                grafico2D.fillRect(panelJuego.mouseX, panelJuego.mouseY, anchoItem, altoItem);
            }
        }
    }

    /**
     * PRE: grafico2D no debe ser nulo.
     * POST: Aplica un filtro oscurecedor sobre el lienzo y renderiza la pantalla del árbol de habilidades.
     */
    public void dibujarPantallaArbol(Graphics2D grafico2D) {
        grafico2D.setColor(new Color(0, 0, 0, 220));
        grafico2D.fillRect(0, 0, panelJuego.getWidth(), panelJuego.getHeight());
        
        this.visorArbol.dibujarCompleto(grafico2D, 0, 0);
    }

    /**
     * PRE: mouseX y mouseY representan las coordenadas absolutas donde ocurrió la interacción.
     * POST: Procesa el impacto del clic sobre los componentes visuales activos. Retorna verdadero
     * si el evento fue consumido por la interfaz, impidiendo su propagación al escenario.
     */
    public boolean gestionarClic(int mouseX, int mouseY) {
        boolean seConsumioClic;
        Integer idClickeado;
        String casillero;

        // CORRECCIÓN MANDAMIENTO 8: Único punto de salida estructurado con variable de control
        seConsumioClic = false;

        if (this.visualizandoArbol) {
            idClickeado = visorArbol.obtenerNodoEnPosicion(mouseX, mouseY, 0, 0);
            
            if (idClickeado != null) {
                personaje.aprenderPasiva(idClickeado);
            }
            seConsumioClic = true; 
        } else {
            if (this.interfazMochila.contiene(mouseX, mouseY)) {
                procesarClicInventario(this.interfazMochila, mouseX, mouseY);
                seConsumioClic = true;
            } else {
                casillero = visorEquipamiento.obtenerSlotClickeado(mouseX, mouseY, this.ultimaPosicionEquipamientoX, this.ultimaPosicionEquipamientoY);
                if (casillero != null) {
                    procesarClicEquipamiento(casillero);
                    seConsumioClic = true;
                }
            }
        }
        
        return seConsumioClic;
    }

    /**
     * PRE: casillero debe representar una constante válida de ranura ("CASCO", "ARMADURA", "ARMA").
     * POST: Alterna el estado del casillero interactuando con el elemento sostenido en el cursor del mouse.
     */
    private void procesarClicEquipamiento(String casillero) {
        ItemInventario itemAgarrado;
        var equipamiento = personaje.getEquipamiento();
        org.ayed.poe.Item itemDesequipado;
        org.ayed.poe.Item itemAnterior;

        itemAgarrado = panelJuego.getItemAgarrado();

        if (itemAgarrado == null) {
            itemDesequipado = equipamiento.desequipar(Slot.valueOf(casillero));
            if (itemDesequipado != null) {
                panelJuego.setItemAgarrado(new ItemInventario(itemDesequipado, 0, 0));
            }
        } else {
            itemAnterior = equipamiento.desequipar(Slot.valueOf(casillero));
            equipamiento.equipar(Slot.valueOf(casillero), itemAgarrado.getItem());
            
            if (itemAnterior != null) {
                panelJuego.setItemAgarrado(new ItemInventario(itemAnterior, 0, 0));
            } else {
                panelJuego.setItemAgarrado(null);
            }
        }
    }

    /**
     * PRE: interfaz no debe ser nula. mouseX y mouseY corresponden a la posición de la cuadrícula.
     * POST: Inserta o extrae ítems de la matriz de inventario respetando las restricciones espaciales.
     */
    private void procesarClicInventario(InventarioUI interfaz, int mouseX, int mouseY) {
        int celdaX;
        int celdaY;
        Inventario inventario;
        ItemInventario itemAgarrado;
        org.ayed.poe.Item itemEliminado;

        celdaX = interfaz.getCeldaX(mouseX);
        celdaY = interfaz.getCeldaY(mouseY);
        inventario = interfaz.getInventario();
        
        if (celdaX >= 0 && celdaX < inventario.getCol() && celdaY >= 0 && celdaY < inventario.getFil()) {
            itemAgarrado = panelJuego.getItemAgarrado();

            if (itemAgarrado == null) {
                try {
                    itemEliminado = inventario.eliminarItem(celdaX, celdaY);
                    if (itemEliminado != null) {
                        panelJuego.setItemAgarrado(new ItemInventario(itemEliminado, celdaX, celdaY));
                    }
                } catch (Exception excepcion) { 
                    /* Casillero vacío - Silenciado bajo diseño estructural */ 
                }
            } else {
                try {
                    inventario.colocarItem(celdaX, celdaY, itemAgarrado.getItem());
                    panelJuego.setItemAgarrado(null); 
                } catch (Exception excepcion) { 
                    System.out.println("No se pudo colocar en mochila: " + excepcion.getMessage());
                }
            }
        }
    }
}