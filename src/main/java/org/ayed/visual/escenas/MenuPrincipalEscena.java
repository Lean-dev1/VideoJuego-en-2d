package org.ayed.visual.escenas;

import java.io.File;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.ayed.poe.Personaje;
import org.ayed.visual.GamePanel;
import org.ayed.juego.GestorGuardado;

public class MenuPrincipalEscena implements EscenaJuego {
    private GamePanel gp;
    
    // Estados del menú
    private final int ESTADO_MENU_PRINCIPAL = 0;
    private final int ESTADO_CARGAR_PARTIDA = 1;
    private int estadoActual = ESTADO_MENU_PRINCIPAL;
    
    // Controles
    private int opcionSeleccionada = 0; // Para menú principal
    private int opcionPartidaSeleccionada = 0; // Para lista de archivos
    private int cooldownTecla = 0; 
    private final int MAX_COOLDOWN = 12;
    
    private String[] partidasGuardadas;
    private BufferedImage imagenFondo;
    private final String RUTA_FONDO = "/fondos/escondite.png";

    public MenuPrincipalEscena(GamePanel gp) {
        this.gp = gp;
        actualizarListaPartidas();
        try {
            imagenFondo = ImageIO.read(getClass().getResourceAsStream(RUTA_FONDO));
        } catch (Exception e) {
            System.err.println("Fondo no cargado: " + e.getMessage());
        }
    }

    private void actualizarListaPartidas() {
        File carpeta = new File(".");
        this.partidasGuardadas = carpeta.list((dir, name) -> name.endsWith(".csv"));
        if (this.partidasGuardadas == null || this.partidasGuardadas.length == 0) {
            this.partidasGuardadas = new String[]{"No hay partidas guardadas"};
        }
    }

    @Override
    public void update() {
        if (cooldownTecla > 0) cooldownTecla--;

        if (cooldownTecla == 0) {
            if (estadoActual == ESTADO_MENU_PRINCIPAL) {
                if (gp.getKeyHandler().upPressed) {
                    opcionSeleccionada = (opcionSeleccionada == 0) ? 2 : opcionSeleccionada - 1;
                    cooldownTecla = MAX_COOLDOWN;
                } else if (gp.getKeyHandler().downPressed) {
                    opcionSeleccionada = (opcionSeleccionada + 1) % 3;
                    cooldownTecla = MAX_COOLDOWN;
                } else if (gp.getKeyHandler().enterPressed) {
                    ejecutarAccionSeleccionada();
                    cooldownTecla = MAX_COOLDOWN;
                }
            } else if (estadoActual == ESTADO_CARGAR_PARTIDA) {
                if (gp.getKeyHandler().upPressed) {
                    opcionPartidaSeleccionada = (opcionPartidaSeleccionada == 0) ? partidasGuardadas.length - 1 : opcionPartidaSeleccionada - 1;
                    cooldownTecla = MAX_COOLDOWN;
                } else if (gp.getKeyHandler().downPressed) {
                    opcionPartidaSeleccionada = (opcionPartidaSeleccionada + 1) % partidasGuardadas.length;
                    cooldownTecla = MAX_COOLDOWN;
                } else if (gp.getKeyHandler().enterPressed) {
                    if (!partidasGuardadas[0].equals("No hay partidas guardadas")) {
                        Personaje pjCargado = GestorGuardado.cargarPartida(partidasGuardadas[opcionPartidaSeleccionada]);
                        if (pjCargado != null) {
                            gp.setPersonaje(pjCargado);
                            gp.cambiarEscena(new EsconditeEscena(gp));
                        }
                    }
                    cooldownTecla = MAX_COOLDOWN;
                } else if (gp.getKeyHandler().escPressed) {
                    estadoActual = ESTADO_MENU_PRINCIPAL;
                    cooldownTecla = MAX_COOLDOWN;
                }
            }
        }
    }

    @Override
    public void dibujar(Graphics2D g2) {
        if (imagenFondo != null) {
            g2.drawImage(imagenFondo, 0, 0, gp.getWidth(), gp.getHeight(), null);
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, gp.getWidth(), gp.getHeight());
        }

        if (estadoActual == ESTADO_MENU_PRINCIPAL) {
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            String titulo = "PATH OF EXILE";
            int xTitulo = centrarTextoX(g2, titulo);
            g2.setColor(Color.BLACK);
            g2.drawString(titulo, xTitulo + 4, 184);
            g2.setColor(new Color(200, 50, 50));
            g2.drawString(titulo, xTitulo, 180);

            g2.setFont(new Font("Arial", Font.PLAIN, 24));
            int xOpciones = gp.getWidth() / 2 - 100;
            int yInicial = 280;
            
            g2.setColor(opcionSeleccionada == 0 ? Color.ORANGE : Color.WHITE);
            g2.drawString((opcionSeleccionada == 0 ? "> " : "  ") + "NUEVA PARTIDA", xOpciones - 20, yInicial);
            
            g2.setColor(opcionSeleccionada == 1 ? Color.ORANGE : Color.WHITE);
            g2.drawString((opcionSeleccionada == 1 ? "> " : "  ") + "CARGAR PARTIDA", xOpciones - 20, yInicial + 50);
            
            g2.setColor(opcionSeleccionada == 2 ? Color.ORANGE : Color.WHITE);
            g2.drawString((opcionSeleccionada == 2 ? "> " : "  ") + "SALIR", xOpciones - 20, yInicial + 100);

        } else if (estadoActual == ESTADO_CARGAR_PARTIDA) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 30));
            g2.drawString("SELECCIONÁ UNA PARTIDA", 200, 200);

            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            for (int i = 0; i < partidasGuardadas.length; i++) {
                g2.setColor(i == opcionPartidaSeleccionada ? Color.YELLOW : Color.WHITE);
                String etiqueta = (i == opcionPartidaSeleccionada ? "> " : "  ") + partidasGuardadas[i];
                g2.drawString(etiqueta, 250, 280 + (i * 40));
            }
            g2.setColor(Color.GRAY);
            g2.drawString("Presioná ESC para volver", 250, 500);
        }
    }

    private void ejecutarAccionSeleccionada() {
        if (opcionSeleccionada == 0) {
            gp.setPersonaje(new Personaje(300, 300));
            gp.cambiarEscena(new EsconditeEscena(gp));
        } else if (opcionSeleccionada == 1) {
            actualizarListaPartidas();
            estadoActual = ESTADO_CARGAR_PARTIDA;
        } else if (opcionSeleccionada == 2) {
            System.exit(0);
        }
    }

    private int centrarTextoX(Graphics2D g2, String texto) {
        return gp.getWidth() / 2 - (int) g2.getFontMetrics().getStringBounds(texto, g2).getWidth() / 2;
    }

    @Override
    public void gestionarClic(int mouseX, int mouseY) {}
}