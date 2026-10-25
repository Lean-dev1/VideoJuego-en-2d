package org.ayed.visual;

import java.awt.Color;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import org.ayed.poe.Inventario;
import org.ayed.poe.Personaje;
import org.ayed.poe.Catalogo;
import org.ayed.visual.escenas.*; 

public class GamePanel extends JPanel implements Runnable {
    public final int tileSize = 16 * 3; 
    private final int screenWidth = tileSize * 16;  
    private final int screenHeight = tileSize * 12; 
    private final int FPS = 60;
    private KeyHandler keyH;
    private Thread gameThread; 
    private Inventario inventarioJugador;
    private Catalogo catalogo;
    private EscenaJuego escenaActual;
    private MenuPrincipalEscena escenaMenu;
    private EsconditeEscena escenaEscondite;
    private MazmorraEscena escenaMazmorra;
    private org.ayed.poe.ItemInventario itemAgarrado = null;
    private MuerteEscena escenaDeMuerte;
    private Personaje personaje; // Integrado de feature/personaje
    
    public int mouseX, mouseY;
    
    public GamePanel(KeyHandler keyH) {
        this.keyH = keyH;
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(false); 
        
        this.personaje = new Personaje(300, 300); // Inicialización del personaje
        
        this.inventarioJugador = new Inventario(5, 3);
        this.catalogo = new Catalogo();
        this.catalogo.cargarDesdeArchivo("Catalogo.csv");
        
        this.escenaMenu = new MenuPrincipalEscena(this);
        this.escenaEscondite = new EsconditeEscena(this);
        this.escenaMazmorra = new MazmorraEscena(this);
        this.escenaDeMuerte = new MuerteEscena(this);
        
        this.escenaActual = escenaMenu;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (escenaActual != null) escenaActual.gestionarClic(e.getX(), e.getY());
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX(); mouseY = e.getY();
            }
        });
        startEngine();
    }

    public void cambiarEscena(EscenaJuego nuevaEscena) { this.escenaActual = nuevaEscena; }
    public void update() { if (escenaActual != null) escenaActual.update(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (escenaActual != null) escenaActual.dibujar(g2);
        g2.dispose(); 
    }
    public void setPersonaje(org.ayed.poe.Personaje nuevoPersonaje) {this.personaje = nuevoPersonaje;}
    public KeyHandler getKeyHandler() { return keyH; }
    public Inventario getInventarioJugador() { return inventarioJugador; }
    public Catalogo getCatalogo() { return catalogo; }
    public EscenaJuego getEsconditeEscena() { return escenaEscondite; }
    public EscenaJuego getMazmorraEscena() { return escenaMazmorra; }
    public org.ayed.poe.ItemInventario getItemAgarrado() { return itemAgarrado; }
    public void setItemAgarrado(org.ayed.poe.ItemInventario item) { this.itemAgarrado = item; }
    public Personaje getPersonaje() { return this.personaje; }

    private void startEngine() { gameThread = new Thread(this); gameThread.start(); }
    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS; 
        double nextDrawTime = System.nanoTime() + drawInterval;
        while (gameThread != null) {
            update(); repaint();
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1000000;
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

	public EscenaJuego getMuerteEscena() {
		// TODO Auto-generated method stub
		return this.escenaDeMuerte;
	}
}