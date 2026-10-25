package org.ayed.visual.escenas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import org.ayed.poe.Personaje;
import org.ayed.visual.GamePanel;

public class MuerteEscena implements EscenaJuego {
    private GamePanel gp;
    private Personaje personaje;

    public MuerteEscena(GamePanel gp) {
        this.gp = gp;
        this.gp.requestFocusInWindow();
        this.personaje = gp.getPersonaje();
    }

    @Override
    public void update() {
        // Podrías poner un timer aquí si quieres que el mensaje dure X segundos
    }

    @Override
    public void dibujar(Graphics2D g2) {
        // Fondo negro total
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.getWidth(), gp.getHeight());

        // Texto "HAS MUERTO"
        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 60));
        String texto = "HAS MUERTO";
        int x = (gp.getWidth() - g2.getFontMetrics().stringWidth(texto)) / 2;
        g2.drawString(texto, x, gp.getHeight() / 2);

        // Subtexto
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        String subtexto = "Haz click para regresar al escondite";
        int x2 = (gp.getWidth() - g2.getFontMetrics().stringWidth(subtexto)) / 2;
        g2.drawString(subtexto, x2, (gp.getHeight() / 2) + 50);
    }

    @Override
    public void gestionarClic(int mouseX, int mouseY) {
    	// 1. Reseteamos al personaje a la puerta de entrada del escondite
        personaje.setX(100);
        personaje.setY(200);
        // 2. Cambiamos la escena
        gp.cambiarEscena(gp.getEsconditeEscena());
    }
}