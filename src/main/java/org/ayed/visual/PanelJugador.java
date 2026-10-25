package org.ayed.visual;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import org.ayed.poe.Personaje;

/**
 * Representa el contenedor gráfico principal que unifica la visualización 
 * del equipamiento activo y la mochila interna del personaje exiliado.
 */
public class PanelJugador extends JPanel {
    private Personaje personaje;
    private PanelEquipamiento visorEquipamiento;
    private PanelInventario visorMochila;

    private static final int ANCHO_PANEL = 350;
    private static final int ALTO_PANEL = 600;
    private static final int COMPONENTE_COLOR_FONDO = 30;

    private static final int COORDENADA_TEXTO_X = 20;
    private static final int TEXTO_EQUIPAMIENTO_Y = 20;
    private static final int TEXTO_MOCHILA_Y = 250;

    private static final int DIBUJO_EQUIPAMIENTO_X = 50;
    private static final int DIBUJO_EQUIPAMIENTO_Y = 40;
    private static final int DIBUJO_MOCHILA_Y = 260;

    /**
     * PRE: personaje debe estar correctamente instanciado y no ser nulo.
     * POST: Construye el panel con dimensiones fijas y enlaza los subvisores de inventario.
     */
    public PanelJugador(Personaje personaje) {
        this.personaje = personaje;
        this.visorEquipamiento = new PanelEquipamiento();
        this.visorMochila = new PanelInventario(personaje.getInventario());
        
        this.setBackground(new Color(COMPONENTE_COLOR_FONDO, COMPONENTE_COLOR_FONDO, COMPONENTE_COLOR_FONDO));
        this.setPreferredSize(new Dimension(ANCHO_PANEL, ALTO_PANEL));
    }

    /**
     * PRE: grafico es el contexto de renderizado provisto por el entorno Swing. No nulo.
     * POST: Ejecuta en cascada el pintado de las secciones de equipamiento y mochila.
     */
    @Override
    protected void paintComponent(Graphics grafico) {
        super.paintComponent(grafico);
        Graphics2D grafico2D = (Graphics2D) grafico;

        grafico2D.setColor(Color.WHITE);
        grafico2D.drawString("EQUIPAMIENTO", COORDENADA_TEXTO_X, TEXTO_EQUIPAMIENTO_Y);
        visorEquipamiento.dibujar(grafico2D, personaje.getEquipamiento(), DIBUJO_EQUIPAMIENTO_X, DIBUJO_EQUIPAMIENTO_Y);

        grafico2D.setColor(Color.WHITE);
        grafico2D.drawString("MOCHILA", COORDENADA_TEXTO_X, TEXTO_MOCHILA_Y);
        visorMochila.dibujar(grafico2D, COORDENADA_TEXTO_X, DIBUJO_MOCHILA_Y);
    }
}