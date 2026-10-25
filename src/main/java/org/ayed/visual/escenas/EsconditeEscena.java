package org.ayed.visual.escenas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.ayed.visual.GamePanel;
import org.ayed.visual.PanelInventario;
import org.ayed.visual.TiendaUI;
import org.ayed.juego.GestorGuardado; // Agregado de Lean
import org.ayed.poe.CofreEstacion;
import org.ayed.poe.Estacion;
import org.ayed.poe.MesaEstacion;
import org.ayed.poe.Personaje;
import org.ayed.poe.PuertaEstacion;
import org.ayed.poe.Slot;
import org.ayed.tda.lista.Lista;
import org.ayed.visual.ArbolUI;
import org.ayed.visual.CofreUI;
import org.ayed.poe.Tienda;
import org.ayed.poe.arbol.ArbolHabilidades;
import org.ayed.poe.Item;

/**
 * Gestiona la escena del Escondite, un área segura donde el jugador 
 * puede interactuar con su alijo y prepararse antes de entrar a la mazmorra.
 * Cumple estrictamente con la estructura de control de estados e idiomas de la cátedra.
 */
public class EsconditeEscena implements EscenaJuego {
    private GamePanel panelJuego;
    private Personaje personaje;
    private MenuPersonajeUI menuPersonaje;
    private PanelInventario visorCofre;
    private CofreUI cofreUI;
    private TiendaUI tiendaUI;
    
    private Lista<Estacion> estaciones; 
    private Estacion estacionCercana;   
    
    private CofreEstacion cofreFisico;
    private PuertaEstacion puertaFisica;
    private MesaEstacion mesaFisica; 
    private Tienda tiendaFisica;

    private BufferedImage imagenCofre; 
    private BufferedImage imagenTienda;
    private BufferedImage imagenFondo;

    private final int ESTADO_CAMINANDO = 0;
    private final int ESTADO_MENU_ITEMS = 1;
    private final int ESTADO_TIENDA = 2; 
    private final int ESTADO_ARBOL_HABILIDADES = 3; 
    private final int ESTADO_PAUSA = 4; // Agregado de Lean
    
    private int estadoActual = ESTADO_CAMINANDO;
    private int opcionPausa = 0; // Agregado de Lean

    private final int COFRE_POS_X = 250;
    private final int COFRE_POS_Y = 150;
    private final int PUERTA_POS_X = 350; 
    private final int PUERTA_POS_Y = 100;
    private final int TIENDA_POS_X = 550; 
    private final int TIENDA_POS_Y = 250; 
    
    private final int ESPERA_ACCION = 15; 
    private int esperaTecla = 0; 
    
    private final int MARGEN_IZQUIERDO = 50;
    private final int ALTURA_TITULO_MODO = 40;
    private final int ALTURA_TITULOS_PANEL = 80;
    private final int POS_Y_PANELES = 100;
    private final int ESPACIO_ENTRE_PANEL_Y_PANEL = 40;
    private final int OPACIDAD_MENU = 180;
    private final int X_TEXTO_INICIAL = 50;
    private final int Y_TITULO = 50;
    private final int Y_INSTRUCCIONES = 80;
    private final int Y_ESTADISTICAS = 110;
    private final Color COLOR_FONDO_ESCENA = new Color(35, 35, 35);
    
    private final int LIMITE_IZQUIERDO = 70;
    private final int LIMITE_DERECHO = 650;
    private final int LIMITE_SUPERIOR = 120;
    private final int LIMITE_INFERIOR = 400;
    
    private final String RUTA_FONDO = "/fondos/escondite.png";
    private final String RUTA_COFRE = "/objetos/cofre.png";
    private final String RUTA_TIENDA = "/objetos/tienda.png";
    
    private Lista<Rectangle> obstaculosSolidos;
    private boolean modoDepuracion = false; 
    
    private ArbolHabilidades arbolHabilidades;
    private ArbolUI arbolUI; 
    private final int ARBOL_DESPLAZAMIENTO_X = 400;
    private final int ARBOL_DESPLAZAMIENTO_Y = 260;
    
    /**
     * PRE: panelJuego debe estar correctamente inicializado y no ser nulo.
     * POST: Instancia la escena construyendo las estaciones, cargando los assets gráficos,
     * configurando los límites físicos y vinculando el árbol de habilidades del personaje.
     */
    public EsconditeEscena(GamePanel panelJuego) {
        this.panelJuego = panelJuego;
        this.personaje = panelJuego.getPersonaje(); 
        this.menuPersonaje = new MenuPersonajeUI(panelJuego, personaje);
        this.visorCofre = new PanelInventario(personaje.getAlijo().obtenerPestaña(0));
        this.cofreUI = new CofreUI(personaje.getAlijo());
        this.tiendaUI = new TiendaUI(personaje);
        
        this.obstaculosSolidos = new Lista<Rectangle>(); 
        this.estaciones = new Lista<Estacion>();
        this.estacionCercana = null;
        
        int mesaCol = 3; 
        int mesaFil = 8;
        int mesaX = mesaCol * panelJuego.tileSize;
        int mesaY = mesaFil * panelJuego.tileSize;
        int mesaAncho = panelJuego.tileSize * 3;
        int mesaAlto = (int)(panelJuego.tileSize * 1.5);
        
        try {
            imagenFondo = ImageIO.read(getClass().getResourceAsStream(RUTA_FONDO));
            imagenCofre = ImageIO.read(getClass().getResourceAsStream(RUTA_COFRE));
            imagenTienda = ImageIO.read(getClass().getResourceAsStream(RUTA_TIENDA));
        } catch (Exception e) {
            System.err.println("Error al cargar assets: " + e.getMessage());
        }

        try {
            this.arbolHabilidades = personaje.getArbolHabilidades(); 
            this.arbolUI = new ArbolUI(this.arbolHabilidades);
        } catch (Exception e) {
            System.err.println("Error vinculando árbol: " + e.getMessage());
        }
        
        obstaculosSolidos.agregar(new Rectangle(mesaX, mesaY, mesaAncho, mesaAlto));
        obstaculosSolidos.agregar(new Rectangle(panelJuego.getWidth() - panelJuego.tileSize, 0, panelJuego.tileSize, panelJuego.getHeight())); 
        
        this.cofreFisico = new CofreEstacion(COFRE_POS_X, COFRE_POS_Y, panelJuego.tileSize + 20, imagenCofre);
        this.puertaFisica = new PuertaEstacion(PUERTA_POS_X, PUERTA_POS_Y, panelJuego.tileSize);
        this.mesaFisica = new MesaEstacion(mesaX, mesaY, mesaAncho); 
        this.tiendaFisica = new Tienda(TIENDA_POS_X, TIENDA_POS_Y, panelJuego.tileSize, imagenTienda);
        
        estaciones.agregar(cofreFisico);
        estaciones.agregar(puertaFisica);
        estaciones.agregar(mesaFisica);
        estaciones.agregar(tiendaFisica);

        cargarItemsDePrueba();
    }

    private void cargarItemsDePrueba() {
        try {
            Item espada = panelJuego.getCatalogo().obtenerItemPorID(18); 
            System.out.println(espada.getTipo());
            if (espada != null) {
                personaje.getInventario().colocarItem(espada);
                System.out.println("⚔️ " + espada.getNombre());
                personaje.modificarOro(6000);
            }
            
        } catch (Exception e) {
            System.out.println("ℹ️ [INFO] El personaje ya mantiene sus ítems de la mazmorra. Omitiendo duplicados de prueba.");
        }
    }

    @Override
    public void update() {
        int indiceEstacion;
        boolean encontradaEstacion;
        Estacion estacionEvaluar;
        int xPrevio;
        int yPrevio;
        Rectangle hitboxPersonaje;
        int indiceObstaculo;
        boolean hayColision;

        if (esperaTecla > 0) {
            esperaTecla--;
        }

        // 1. MANEJO DEL MENÚ DE PAUSA (Con tecla ESC)
        if (panelJuego.getKeyHandler().escPressed) {
            if (estadoActual == ESTADO_CAMINANDO) {
                estadoActual = ESTADO_PAUSA;
                opcionPausa = 0;
            } else if (estadoActual == ESTADO_PAUSA) {
                estadoActual = ESTADO_CAMINANDO;
                limpiarEntradas();
            }
            panelJuego.getKeyHandler().escPressed = false;
            esperaTecla = ESPERA_ACCION;
            return;
        }

        if (estadoActual == ESTADO_PAUSA) {
            if (esperaTecla == 0) {
                if (panelJuego.getKeyHandler().upPressed) {
                    opcionPausa = (opcionPausa == 0) ? 2 : opcionPausa - 1;
                    esperaTecla = ESPERA_ACCION;
                } else if (panelJuego.getKeyHandler().downPressed) {
                    opcionPausa = (opcionPausa + 1) % 3;
                    esperaTecla = ESPERA_ACCION;
                } else if (panelJuego.getKeyHandler().enterPressed) {
                    ejecutarAccionPausa();
                    panelJuego.getKeyHandler().enterPressed = false;
                    esperaTecla = ESPERA_ACCION;
                }
            }
            return; 
        }

        // 2. RECONOCIMIENTO DE ENTORNO
        estacionCercana = null; 
        indiceEstacion = 0;
        encontradaEstacion = false;
        
        while (!encontradaEstacion && indiceEstacion < estaciones.tamanio()) {
            estacionEvaluar = estaciones.dato(indiceEstacion);
            if (estacionEvaluar.jugadorEstaCerca(personaje)) {
                estacionCercana = estacionEvaluar;
                encontradaEstacion = true;
            }
            indiceEstacion++;
        }

        // 3. TOGGLE UNIVERSAL CON LA TECLA E (inventoryPressed)
        if (panelJuego.getKeyHandler().inventoryPressed && esperaTecla == 0) {
            if (estadoActual == ESTADO_CAMINANDO) {
                if (estacionCercana instanceof Tienda) {
                    estadoActual = ESTADO_TIENDA; 
                } else if (estacionCercana instanceof MesaEstacion) {
                    estadoActual = ESTADO_ARBOL_HABILIDADES;
                } else if (estacionCercana instanceof CofreEstacion) {
                    estadoActual = ESTADO_MENU_ITEMS; 
                } else {
                    estadoActual = ESTADO_MENU_ITEMS; // Abre inventario por defecto
                }
            } else {
                // Si la vuelve a apretar estando en cualquier menú, lo cierra.
                estadoActual = ESTADO_CAMINANDO; 
            }
            limpiarEntradas();
            esperaTecla = ESPERA_ACCION; 
            return;
        }

        // 4. INTERACCIONES Y MOVIMIENTO (Solo si estamos caminando)
        if (estadoActual == ESTADO_CAMINANDO) {
            
            // Interacción con ENTER
            if (estacionCercana != null && estacionCercana.estaSiendoInteractuado(personaje, panelJuego.getKeyHandler()) && esperaTecla == 0) {
                if (estacionCercana instanceof CofreEstacion) {
                    estadoActual = ESTADO_MENU_ITEMS;
                } 
                else if (estacionCercana instanceof PuertaEstacion) {
                    panelJuego.cambiarEscena(new MazmorraEscena(panelJuego)); 
                } 
                else if (estacionCercana instanceof Tienda) {
                    estadoActual = ESTADO_TIENDA;
                } 
                else if (estacionCercana instanceof MesaEstacion) {
                    estadoActual = ESTADO_ARBOL_HABILIDADES;
                }
                limpiarEntradas();
                esperaTecla = ESPERA_ACCION;
                return;
            }

            xPrevio = personaje.getX();
            yPrevio = personaje.getY();
            
            personaje.actualizarMovimientoEscondite(panelJuego.getKeyHandler());

            if (personaje.getX() < LIMITE_IZQUIERDO) personaje.setX(LIMITE_IZQUIERDO);
            if (personaje.getX() > LIMITE_DERECHO) personaje.setX(LIMITE_DERECHO);
            if (personaje.getY() < LIMITE_SUPERIOR) personaje.setY(LIMITE_SUPERIOR);
            if (personaje.getY() > LIMITE_INFERIOR) personaje.setY(LIMITE_INFERIOR);
            
            hitboxPersonaje = new Rectangle(personaje.getX(), personaje.getY(), 64, 64);
            indiceObstaculo = 0;
            hayColision = false;
            
            while (!hayColision && indiceObstaculo < obstaculosSolidos.tamanio()) {
                if (hitboxPersonaje.intersects(obstaculosSolidos.dato(indiceObstaculo))) {
                    personaje.setX(xPrevio); 
                    personaje.setY(yPrevio);
                    hayColision = true;
                }
                indiceObstaculo++;
            }
        }
    }

    private void ejecutarAccionPausa() {
        if (opcionPausa == 0) {
            estadoActual = ESTADO_CAMINANDO;
            limpiarEntradas();
        } 
        else if (opcionPausa == 1) {
            System.out.println("Guardando progreso sin salir...");
            GestorGuardado.guardarPartida(personaje);
            estadoActual = ESTADO_CAMINANDO;
            limpiarEntradas(); 
        } 
        else if (opcionPausa == 2) {
            System.out.println("Guardando y volviendo al menú principal...");
            GestorGuardado.guardarPartida(personaje);
            panelJuego.cambiarEscena(new MenuPrincipalEscena(panelJuego));
        }
    }

    @Override
    public void dibujar(Graphics2D g2) {
        dibujarEntorno(g2);
        dibujarEntidades(g2);
        
        if (estadoActual == ESTADO_MENU_ITEMS) {
            dibujarInterfazInventarios(g2);
            menuPersonaje.dibujarItemEnCursor(g2);
        }
        else if (estadoActual == ESTADO_TIENDA) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, panelJuego.getWidth(), panelJuego.getHeight());
            tiendaUI.dibujar(g2, personaje);
        }
        else if (estadoActual == ESTADO_ARBOL_HABILIDADES) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, panelJuego.getWidth(), panelJuego.getHeight());
            arbolUI.dibujarCompleto(g2, panelJuego.getWidth() / 2 - ARBOL_DESPLAZAMIENTO_X, panelJuego.getHeight() / 2 - ARBOL_DESPLAZAMIENTO_Y);
        }
        else if (estadoActual == ESTADO_PAUSA) {
            dibujarMenuPausa(g2);
        }
    }

    private void dibujarEntorno(Graphics2D g2) {
        int i;
        Rectangle hitbox;
        String textoStats;

        if (imagenFondo != null) {
            g2.drawImage(imagenFondo, 0, 0, panelJuego.getWidth(), panelJuego.getHeight(), null);
        } else {
            g2.setColor(COLOR_FONDO_ESCENA);
            g2.fillRect(0, 0, panelJuego.getWidth(), panelJuego.getHeight());
        }

        if (modoDepuracion) {
            g2.setColor(new Color(204, 85, 40)); 
            g2.setStroke(new BasicStroke(2));
            
            i = 0;
            while (i < obstaculosSolidos.tamanio()) {
                hitbox = obstaculosSolidos.dato(i);
                g2.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
                i++;
            }
        }

        g2.setColor(Color.ORANGE); 
        g2.drawString("¡BIENVENIDO AL ESCONDITE SEGURO!", X_TEXTO_INICIAL, Y_TITULO);
        g2.setColor(Color.WHITE);
        g2.drawString("Movete con WASD. Camina hacia el borde derecho para ir a la Mazmorra.", X_TEXTO_INICIAL, Y_INSTRUCCIONES);
        
        textoStats = "Nivel: " + personaje.getNivel() + 
                     " | Oro: " + personaje.getOro() + 
                     " | Daño: " + personaje.getDañoTotal() + 
                     " | Armadura: " + personaje.getArmaduraTotal() + 
                     " | Vida: " + personaje.getVidaMax();
        g2.drawString(textoStats, X_TEXTO_INICIAL, Y_ESTADISTICAS);
    }
    
    private void dibujarEntidades(Graphics2D g2) {
        int i = 0;
        while (i < estaciones.tamanio()) {
            estaciones.dato(i).dibujar(g2);
            i++;
        }
        
        if (estacionCercana != null && estadoActual == ESTADO_CAMINANDO) {
            estacionCercana.dibujarFeedbackInteraccion(g2);
        }
        
        personaje.dibujar(g2);
    }

    private void dibujarInterfazInventarios(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, OPACIDAD_MENU));
        g2.fillRect(0, 0, panelJuego.getWidth(), panelJuego.getHeight());
        
        if (estacionCercana instanceof CofreEstacion) {
            dibujarMochilaYAlijo(g2);
        } else {
            menuPersonaje.dibujarCentrado(g2);
        }
    }

    private void dibujarMochilaYAlijo(Graphics2D g2) {
        int anchoAlijo;
        int posicionXMochila;

        g2.setColor(Color.ORANGE);
        g2.drawString("MODO TRASPASO: ALIJO VINCULADO", MARGEN_IZQUIERDO, ALTURA_TITULO_MODO);
        g2.drawString("ALIJO SEGURO", MARGEN_IZQUIERDO, ALTURA_TITULOS_PANEL);
        
        anchoAlijo = personaje.getAlijo().obtenerPestaña(0).getCol() * visorCofre.TAMANIO_CELDA;
        posicionXMochila = MARGEN_IZQUIERDO + anchoAlijo + ESPACIO_ENTRE_PANEL_Y_PANEL;
        
        cofreUI.definirPosicion(MARGEN_IZQUIERDO, POS_Y_PANELES);
        cofreUI.dibujar(g2, MARGEN_IZQUIERDO, POS_Y_PANELES);
        menuPersonaje.dibujarEnPosicion(g2, posicionXMochila, POS_Y_PANELES);
    }

    private void dibujarMenuPausa(Graphics2D g2) {
        String titulo;
        int xTitulo;
        int xOpc;
        int yOpc;
        int espaciado;

        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, panelJuego.getWidth(), panelJuego.getHeight());

        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 48));
        g2.setColor(Color.WHITE);
        titulo = "PAUSA";
        xTitulo = panelJuego.getWidth() / 2 - (int)g2.getFontMetrics().getStringBounds(titulo, g2).getWidth() / 2;
        g2.drawString(titulo, xTitulo, 200);

        g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 24));
        xOpc = panelJuego.getWidth() / 2 - 150;
        yOpc = 320;
        espaciado = 60;
        
        g2.setColor(opcionPausa == 0 ? Color.ORANGE : Color.WHITE);
        g2.drawString((opcionPausa == 0 ? "> " : "  ") + "CONTINUAR", xOpc, yOpc);
        
        g2.setColor(opcionPausa == 1 ? Color.ORANGE : Color.WHITE);
        g2.drawString((opcionPausa == 1 ? "> " : "  ") + "GUARDAR PROGRESO", xOpc, yOpc + espaciado);

        g2.setColor(opcionPausa == 2 ? Color.ORANGE : Color.WHITE);
        g2.drawString((opcionPausa == 2 ? "> " : "  ") + "GUARDAR Y SALIR AL MENÚ", xOpc, yOpc + (espaciado * 2));
    }

    @Override
    public void gestionarClic(int coordenadaX, int coordenadaY) {
        int desplazamientoUIX;
        int desplazamientoUIY;
        Integer idNodo;
        boolean compraExitosa;

        if (estadoActual == ESTADO_ARBOL_HABILIDADES) {
            desplazamientoUIX = panelJuego.getWidth() / 2 - ARBOL_DESPLAZAMIENTO_X;
            desplazamientoUIY = panelJuego.getHeight() / 2 - ARBOL_DESPLAZAMIENTO_Y;

            idNodo = arbolUI.obtenerNodoEnPosicion(coordenadaX, coordenadaY, desplazamientoUIX, desplazamientoUIY);
            
            if (idNodo != null) {
                compraExitosa = personaje.aprenderPasiva(idNodo);
                
                if (compraExitosa) {
                    System.out.println("¡Transacción exitosa! El HUD se actualizará al instante.");
                } else {
                    System.out.println("No se pudo comprar el nodo (faltan puntos o ya lo tenés).");
                }
            }
        } 
        else if (estadoActual == ESTADO_MENU_ITEMS) {
            if (estacionCercana instanceof CofreEstacion) {
                if (!cofreUI.gestionarClic(coordenadaX, coordenadaY, panelJuego)) {
                    menuPersonaje.gestionarClic(coordenadaX, coordenadaY);
                }
            } else {
                menuPersonaje.gestionarClic(coordenadaX, coordenadaY);
            }
        } 
        else if (estadoActual == ESTADO_TIENDA) {
            if (!tiendaUI.gestionarClic(coordenadaX, coordenadaY, tiendaFisica, personaje, panelJuego)) {
                estadoActual = ESTADO_CAMINANDO; 
            }
        }
    }

    private void limpiarEntradas() {
        panelJuego.getKeyHandler().upPressed = false;
        panelJuego.getKeyHandler().downPressed = false;
        panelJuego.getKeyHandler().leftPressed = false;
        panelJuego.getKeyHandler().rightPressed = false;
        panelJuego.getKeyHandler().enterPressed = false;
        panelJuego.getKeyHandler().escPressed = false;
        panelJuego.getKeyHandler().inventoryPressed = false;
    }
}