package org.ayed.visual;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import org.ayed.poe.Personaje;
import org.ayed.poe.Slot;
import org.ayed.poe.Tienda;

/**
 * Gestor gráfico encargado de renderizar las opciones de apuestas de ítems del mercader
 * y capturar las transacciones de oro del usuario.
 */
public class TiendaUI {
    private Personaje personaje;
    private Rectangle botonArmaAzar;
    private Rectangle botonCascoAzar;
    private Rectangle botonArmaduraAzar;
    private Rectangle botonExpandirAlijo;
    private Rectangle botonSalir;
    private String mensajeTransaccion;

    /**
     * PRE: personaje debe estar instanciado correctamente y no ser nulo.
     * POST: Construye la interfaz dimensionando y posicionando los botones de compra.
     */
    public TiendaUI(Personaje personaje) {
        this.botonArmaAzar = new Rectangle(300, 150, 200, 35);
        this.botonCascoAzar = new Rectangle(300, 195, 200, 35);
        this.botonArmaduraAzar = new Rectangle(300, 240, 200, 35);
        this.botonExpandirAlijo = new Rectangle(300, 285, 200, 35);
        this.botonSalir = new Rectangle(300, 330, 200, 35);
        this.mensajeTransaccion = "";
        this.personaje = personaje;
    }

    /**
     * PRE: grafico2D y cliente no deben ser nulos.
     * POST: Renderiza el contenedor de la tienda, los costos actuales y el estado de oro del cliente.
     */
    public void dibujar(Graphics2D grafico2D, Personaje cliente) {
        int oroActual;
        oroActual = cliente.getOro();

        grafico2D.setColor(new Color(0, 0, 0, 230));
        grafico2D.fillRect(200, 90, 400, 305);

        grafico2D.setColor(Color.WHITE);
        grafico2D.drawString("MERCADER - APUESTAS DE EQUIPAMIENTO", 280, 115);
        grafico2D.drawString("Oro disponible: " + oroActual + " 💰", 340, 135);

        grafico2D.setColor(new Color(139, 0, 0)); 
        grafico2D.fillRect(botonArmaAzar.x, botonArmaAzar.y, botonArmaAzar.width, botonArmaAzar.height);
        grafico2D.setColor(Color.WHITE);
        grafico2D.drawString("Arma al Azar (" + Tienda.COSTO_ARMA_ALEATORIA + " Oro)", botonArmaAzar.x + 15, botonArmaAzar.y + 22);

        grafico2D.setColor(new Color(70, 130, 180)); 
        grafico2D.fillRect(botonCascoAzar.x, botonCascoAzar.y, botonCascoAzar.width, botonCascoAzar.height);
        grafico2D.setColor(Color.WHITE);
        grafico2D.drawString("Casco al Azar (" + Tienda.COSTO_CASCO_ALEATORIO + " Oro)", botonCascoAzar.x + 15, botonCascoAzar.y + 22);

        grafico2D.setColor(new Color(46, 139, 87)); 
        grafico2D.fillRect(botonArmaduraAzar.x, botonArmaduraAzar.y, botonArmaduraAzar.width, botonArmaduraAzar.height);
        grafico2D.setColor(Color.WHITE);
        grafico2D.drawString("Armadura al Azar (" + Tienda.COSTO_ARMADURA_ALEATORIA + " Oro)", botonArmaduraAzar.x + 10, botonArmaduraAzar.y + 22);

        grafico2D.setColor(new Color(218, 165, 32)); 
        grafico2D.fillRect(botonExpandirAlijo.x, botonExpandirAlijo.y, botonExpandirAlijo.width, botonExpandirAlijo.height);
        grafico2D.setColor(Color.BLACK);
        grafico2D.drawString("Expandir Alijo (" + Tienda.COSTO_EXPANSION_ALIJO + " Oro)", botonExpandirAlijo.x + 15, botonExpandirAlijo.y + 22);

        grafico2D.setColor(Color.GRAY);
        grafico2D.fillRect(botonSalir.x, botonSalir.y, botonSalir.width, botonSalir.height);
        grafico2D.setColor(Color.WHITE);
        grafico2D.drawString("Cerrar Tienda", botonSalir.x + 55, botonSalir.y + 22);

        grafico2D.setColor(Color.YELLOW);
        grafico2D.drawString(this.mensajeTransaccion, 220, 385);
    }

    /**
     * PRE: tiendaFisica, cliente y panelJuego no deben ser nulos. mouseX y mouseY indican el punto de clic.
     * POST: Ejecuta la consulta transaccional en la tienda lógica si se presionó un botón.
     * Retorna verdadero si el menú debe seguir abierto, falso si se presionó salir.
     */
    public boolean gestionarClic(int mouseX, int mouseY, Tienda tiendaFisica, Personaje cliente, GamePanel panelJuego) {
        boolean seguirEnTienda;
        boolean transaccionExitosa;

        seguirEnTienda = true;

        if (botonArmaAzar.contains(mouseX, mouseY)) {
            transaccionExitosa = tiendaFisica.comprarItemAleatorio(personaje, panelJuego.getCatalogo(), Slot.ARMA);
            if (transaccionExitosa) {
                this.mensajeTransaccion = "¡Compraste un arma misteriosa!";
            } else {
                this.mensajeTransaccion = "Oro insuficiente o inventario lleno.";
            }
        } else if (botonCascoAzar.contains(mouseX, mouseY)) {
            transaccionExitosa = tiendaFisica.comprarItemAleatorio(personaje, panelJuego.getCatalogo(), Slot.CASCO);
            if (transaccionExitosa) {
                this.mensajeTransaccion = "¡Compraste un casco misterioso!";
            } else {
                this.mensajeTransaccion = "Oro insuficiente o inventario lleno.";
            }
        } else if (botonArmaduraAzar.contains(mouseX, mouseY)) {
            transaccionExitosa = tiendaFisica.comprarItemAleatorio(personaje, panelJuego.getCatalogo(), Slot.ARMADURA);
            if (transaccionExitosa) {
                this.mensajeTransaccion = "¡Compraste una armadura misteriosa!";
            } else {
                this.mensajeTransaccion = "Oro insuficiente o inventario lleno.";
            }
        } else if (botonExpandirAlijo.contains(mouseX, mouseY)) {
            transaccionExitosa = tiendaFisica.comprarExpansionAlijo(cliente);
            if (transaccionExitosa) {
                this.mensajeTransaccion = "¡Alijo expandido con éxito! +1 Pestaña.";
            } else {
                this.mensajeTransaccion = "Oro insuficiente para expandir el alijo.";
            }
        } else if (botonSalir.contains(mouseX, mouseY)) {
            seguirEnTienda = false;
            this.mensajeTransaccion = ""; 
        }

        return seguirEnTienda;
    }
}