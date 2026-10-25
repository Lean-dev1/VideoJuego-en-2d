package org.ayed.visual;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean inventoryPressed;
    public boolean enterPressed;
    public boolean escPressed;

    @Override
    public void keyTyped(KeyEvent e) {
        // Obligatorio por la interfaz, queda vacío
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) { upPressed = true; }
        if (code == KeyEvent.VK_S) { downPressed = true; }
        if (code == KeyEvent.VK_A) { leftPressed = true; }
        if (code == KeyEvent.VK_D) { rightPressed = true; }
        if (code == KeyEvent.VK_E) { inventoryPressed = true; }
        if (code == KeyEvent.VK_ENTER) { enterPressed = true; }
        if (code == java.awt.event.KeyEvent.VK_ESCAPE) { escPressed = true;}
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) { upPressed = false; }
        if (code == KeyEvent.VK_S) { downPressed = false; }
        if (code == KeyEvent.VK_A) { leftPressed = false; }
        if (code == KeyEvent.VK_D) { rightPressed = false; }
        if (code == KeyEvent.VK_E) { inventoryPressed = false; }
        if (code == KeyEvent.VK_ENTER) { enterPressed = false; }
        if (code == KeyEvent.VK_ENTER) { enterPressed = false; }
        if (code == KeyEvent.VK_ENTER) { enterPressed = false; }
    }

    public void limpiarTeclas() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        inventoryPressed = false;
        escPressed = false;
  //// agregar mas si hace falta
    }
}