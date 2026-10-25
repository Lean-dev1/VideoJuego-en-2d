package org.ayed.visual;

import javax.swing.JFrame;

public class GameWindow {

    private JFrame window;
    private GamePanel gamePanel;
    private KeyHandler keyHandler;

    public GameWindow() {
        window = new JFrame();
        keyHandler = new KeyHandler(); 
        
        gamePanel = new GamePanel(keyHandler); 

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("GraphMortal - Test Visual");

        window.add(gamePanel);
        window.addKeyListener(keyHandler); 

        window.pack(); 
        window.setLocationRelativeTo(null); 
        
        // Forzamos el foco de atención en la ventana
        window.setFocusable(true);
        window.requestFocusInWindow();
    }

    public void start() {
        window.setVisible(true);
    }
}