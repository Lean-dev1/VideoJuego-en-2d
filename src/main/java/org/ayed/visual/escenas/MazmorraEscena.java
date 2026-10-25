package org.ayed.visual.escenas;

import java.awt.Color;
import java.awt.Graphics2D;

import org.ayed.juego.AccionJugador;
import org.ayed.juego.Celda;
import org.ayed.juego.EfectoCelda;
import org.ayed.poe.Item;
import org.ayed.juego.Mazmorra;
import org.ayed.poe.Monstruo;
import org.ayed.poe.Personaje;
import org.ayed.poe.Slot;
import org.ayed.tda.iterador.Iterador;
import org.ayed.tda.lista.Lista;
import org.ayed.visual.GamePanel;

public class MazmorraEscena implements EscenaJuego {
    private static final int POSX_REAPARICION = 300;
    private static final int POSY_REAPARICION = 300;
    private GamePanel gp;
    private MenuPersonajeUI menuUI;
    private Personaje personaje;
    private Mazmorra mazmorra;
    private Lista<Celda> areaAtaqueVisual = null;
    private int temporizadorAtaqueVisual = 0;
    private boolean mostrandoInventario = false;
    private boolean esTurnoJugador = true;
    private int enfriamientoTeclas = 0; 
    private final int ENFRIAMIENTO_TURNO = 12;
    private final int DIMENSION_MAZMORRA = 17; 
    private final double ESCALA_MAZMORRA = 1.8;

    /**
     * PRE: gp != null.
     * POST: Construye la escena de la mazmorra vinculando el jugador e inicializando el nivel.
     */
    public MazmorraEscena(GamePanel gp) {
        this.gp = gp;
        this.personaje = gp.getPersonaje(); 
        this.menuUI = new MenuPersonajeUI(gp, this.personaje);
        this.mazmorra = new Mazmorra(DIMENSION_MAZMORRA, this.personaje, gp.getCatalogo());
        this.mazmorra.generarMonstruos(5);
        
        gp.getKeyHandler().limpiarTeclas();
    }

    /**
     * PRE: Ninguna.
     * POST: Modifica el estado lógico de los temporizadores, procesa turnos del jugador y de la IA.
     */
    @Override
    public void update() {
        Monstruo m;
        AccionJugador accion;
        org.ayed.poe.Item posibleArma;
        org.ayed.poe.Arma arma;
        Celda posJugador;
        org.ayed.tda.iterador.Iterador<Monstruo> itEnemigos;

        if (enfriamientoTeclas > 0) {
            enfriamientoTeclas--;
        }
        
        if (temporizadorAtaqueVisual > 0) {
            temporizadorAtaqueVisual--;
            if (temporizadorAtaqueVisual == 0) {
                areaAtaqueVisual = null; 
            }
        }

        if (esTurnoJugador) {
            if (gp.getKeyHandler().inventoryPressed && enfriamientoTeclas == 0) {
                mostrandoInventario = !mostrandoInventario;
                enfriamientoTeclas = ENFRIAMIENTO_TURNO; 
            }
            
            if (!mostrandoInventario && enfriamientoTeclas == 0) {
                accion = personaje.procesarEntradaMazmorra(gp.getKeyHandler());
                
                if (accion == AccionJugador.ATACAR) {
                    posibleArma = personaje.getEquipamiento().obtenerItem(Slot.ARMA);
                    if (posibleArma != null && posibleArma instanceof org.ayed.poe.Arma) {
                        arma = (org.ayed.poe.Arma) posibleArma;
                        posJugador = mazmorra.getMapaVisual().elemento(personaje.getFila(), personaje.getColumna());
                        
                        // Seteamos el área visual usando el método del arma
                        areaAtaqueVisual = arma.obtenerAreaImpacto(posJugador, personaje.getDireccion());
                        temporizadorAtaqueVisual = 15; 
                        
                        // Imprimí el daño TOTAL del personaje para debuguear en consola cuánta fuerza tiene
                        System.out.println("Daño del personaje con el nuevo CSV: " + personaje.getDañoTotal());
                    }
                }
                
                if (accion != AccionJugador.NADA) {
                    mazmorra.ejecutarTurnoGlobal(accion);
                    enfriamientoTeclas = ENFRIAMIENTO_TURNO;
                    evaluarEstadoDePartida();
                    esTurnoJugador = false;
                }
            }
        } else {
            itEnemigos = mazmorra.obtenerEnemigos().iterador();
            while (itEnemigos.haySiguiente()) {
                m = itEnemigos.dato();
                m.setX(m.getColumna() * gp.tileSize); 
                m.setY(m.getFila() * gp.tileSize);
                itEnemigos.siguiente();
            }
            
            evaluarEstadoDePartida();
            esTurnoJugador = true;
        }

        evaluarEstadoDePartida();
    }
    
    private void evaluarEstadoDePartida() {
        if (!personaje.estaVivo()) {
            personaje.setVidaActual(personaje.getVidaMax());
            personaje.setManaActual(personaje.getManaMax());
            
            gp.getKeyHandler().limpiarTeclas();
            personaje.setX(POSX_REAPARICION);
            personaje.setY(POSY_REAPARICION);
            
            gp.cambiarEscena(gp.getMuerteEscena());
            
        } else if (mazmorra.debeRegresarAlEscondite() || mazmorra.esPartidaGanada()) {
            gp.getKeyHandler().limpiarTeclas(); 
            personaje.setX(POSX_REAPARICION);
            personaje.setY(POSY_REAPARICION);
            
            // 🔄 VOLVÉS AL MISMO ESCONDITE TAL CUAL LO DEJANSTE
            gp.cambiarEscena(gp.getEsconditeEscena()); 
        }
    }

    /**
     * PRE: g2 != null.
     * POST: Renderiza el entorno gráfico completo aplicando escalados y transformaciones de cámara.
     */
    @Override
    public void dibujar(Graphics2D g2) {
        int pjPixelX = personaje.getColumna() * gp.tileSize;
        int pjPixelY = personaje.getFila() * gp.tileSize;

        int centroPantallaX = (int) (gp.getWidth() / (2 * ESCALA_MAZMORRA));
        int centroPantallaY = (int) (gp.getHeight() / (2 * ESCALA_MAZMORRA));
        
        int camaraX = pjPixelX - centroPantallaX + (gp.tileSize / 2);
        int camaraY = pjPixelY - centroPantallaY + (gp.tileSize / 2);

        g2.scale(ESCALA_MAZMORRA, ESCALA_MAZMORRA);
        g2.translate(-camaraX, -camaraY);

        dibujarTerrenoYEntorno(g2);
        dibujarItemsEnSuelo(g2);
        dibujarMonstruos(g2);
        dibujarAtaqueAoE(g2);

        personaje.setX(pjPixelX);
        personaje.setY(pjPixelY);
        personaje.enMovimiento = (enfriamientoTeclas > 0); 
        personaje.dibujar(g2);

        g2.translate(camaraX, camaraY);
        g2.scale(1.0 / ESCALA_MAZMORRA, 1.0 / ESCALA_MAZMORRA);

        dibujarHUD(g2);

        if (mostrandoInventario) {
            menuUI.dibujarCentrado(g2);
            menuUI.dibujarItemEnCursor(g2);
        }
    }

    private void dibujarTerrenoYEntorno(Graphics2D g2) {
        int f;
        int c;
        Celda celda;
        int pantallaX;
        int pantallaY;

        for (f = 0; f < mazmorra.getMapaVisual().filas(); f++) {
            for (c = 0; c < mazmorra.getMapaVisual().columnas(); c++) {
                celda = mazmorra.getMapaVisual().elemento(f, c);
                pantallaX = c * gp.tileSize;
                pantallaY = f * gp.tileSize;

                if (celda.getEfecto() == EfectoCelda.PARED) { g2.setColor(new Color(40, 40, 45)); }
                else if (celda.getEfecto() == EfectoCelda.PUERTA) { g2.setColor(new Color(139, 69, 19)); }
                else if (celda.getEfecto() == EfectoCelda.INCENDIADA) { g2.setColor(new Color(200, 80, 20)); }
                else if (celda.getEfecto() == EfectoCelda.CONGELADA) { g2.setColor(new Color(100, 200, 255)); }
                else if (celda.getEfecto() == EfectoCelda.ELECTRIFICADA) { g2.setColor(new Color(220, 220, 50)); }
                else { g2.setColor(new Color(80, 75, 70)); }

                g2.fillRect(pantallaX, pantallaY, gp.tileSize, gp.tileSize);
                
                if (celda.getEfecto() != EfectoCelda.PARED) {
                    g2.setColor(new Color(60, 55, 50));
                    g2.drawRect(pantallaX, pantallaY, gp.tileSize, gp.tileSize);
                }
            }
        }
    }

    private void dibujarItemsEnSuelo(Graphics2D g2) {
        int f;
        int c;
        Celda celda;
        Lista<Item> items;
        int pantallaX;
        int pantallaY;
        Item primerItem;

        for (f = 0; f < mazmorra.getMapaVisual().filas(); f++) {
            for (c = 0; c < mazmorra.getMapaVisual().columnas(); c++) {
                celda = mazmorra.getMapaVisual().elemento(f, c);
                items = mazmorra.getItemsEnSuelo().obtenerValor(celda);
                
                if (items != null && !items.vacio()) {
                    pantallaX = c * gp.tileSize;
                    pantallaY = f * gp.tileSize;
                    
                    primerItem = items.dato(0);
                    
                    if (primerItem != null) {
                        if (primerItem.getID() == 99) {
                            g2.setColor(new Color(255, 50, 50)); 
                            g2.fillOval(pantallaX + 16, pantallaY + 16, gp.tileSize - 32, gp.tileSize - 32);
                            g2.setColor(new Color(255, 215, 0, 100));
                            g2.fillOval(pantallaX + 8, pantallaY + 8, gp.tileSize - 16, gp.tileSize - 16);
                        } else {
                            g2.setColor(Color.YELLOW);
                            g2.fillRect(pantallaX + 20, pantallaY + 20, gp.tileSize - 40, gp.tileSize - 40);
                        }
                    }
                }
            }
        }
    }

    private void dibujarMonstruos(Graphics2D g2) {
        Iterador<Monstruo> itMonstruos = mazmorra.obtenerEnemigos().iterador();
        Monstruo m;

        while (itMonstruos.haySiguiente()) {
            m = itMonstruos.dato();
            
            if (m.estaVivo()) {
                g2.setColor(m.getColorVisual());
                g2.fillRect(m.getX(), m.getY(), gp.tileSize, gp.tileSize);
                g2.setColor(Color.BLACK);
                g2.drawRect(m.getX(), m.getY(), gp.tileSize, gp.tileSize);
            }
            
            itMonstruos.siguiente();
        }
    }

    private void dibujarHUD(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(10, 10, 250, 80);
        g2.setColor(Color.WHITE);
        g2.drawRect(10, 10, 250, 80);
        
        g2.setFont(g2.getFont().deriveFont(16f));
        
        if (personaje.getVidaActual() <= personaje.getVidaMax() * 0.3) {
            g2.setColor(Color.RED);
        } else {
            g2.setColor(Color.GREEN);
        }
        g2.drawString("Vida: " + personaje.getVidaActual() + "/" + personaje.getVidaMax(), 20, 35);
        
        if (mazmorra.esPartidaGanada()) {
            g2.setColor(Color.CYAN);
            g2.drawString("¡MISIÓN COMPLETADA!", 20, 60);
        } else {
            g2.setColor(Color.ORANGE);
            g2.drawString("Sigue el GPS para buscar reliquias.", 20, 60);
        }
    }

    @Override
    public void gestionarClic(int mouseX, int mouseY) {
        if (mostrandoInventario) {
            menuUI.gestionarClic(mouseX, mouseY);
        }
    }

    private void dibujarAtaqueAoE(Graphics2D g2) {
        org.ayed.tda.iterador.Iterador<Celda> it;
        Celda c;
        int pixelX;
        int pixelY;

        if (areaAtaqueVisual != null && temporizadorAtaqueVisual > 0) {
            g2.setColor(new Color(255, 0, 0, 120)); 
            
            it = areaAtaqueVisual.iterador();
            while (it.haySiguiente()) {
                c = it.dato();
                pixelX = c.getColumna() * gp.tileSize;
                pixelY = c.getFila() * gp.tileSize;
                
                g2.fillRect(pixelX, pixelY, gp.tileSize, gp.tileSize);
                it.siguiente();
            }
        }
    }
}