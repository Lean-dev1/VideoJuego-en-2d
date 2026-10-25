package org.ayed.poe;

import org.ayed.tda.diccionario.Diccionario; 

public class Equipamiento {
    private Diccionario<Slot, Item> slots;

    public Equipamiento() {
        this.slots = new Diccionario<>(10);
    }

    /**
     * Coloca un ítem en el slot indicado. 
     * Al usar Enums, ya no necesitás validar con un IF si el slot es válido.
     */
    // Pre: El slot y el item no deben ser nulos.
    // Post: El ítem queda guardado en el slot correspondiente del diccionario.
    public void equipar(Slot slot, Item item) {
        this.slots.agregar(slot, item);
    }

    /**
     * Devuelve el ítem que se encuentra en el slot. 
     * Si el slot está vacío, retorna null.
     */
    // Pre: El slot no debe ser nulo.
    // Post: Retorna el Item asociado o null si no existe.
    public Item obtenerItem(Slot slot) {
        return this.slots.obtenerValor(slot);
    }

    /**
     * Quita el ítem del slot y lo retorna.
     */
    // Pre: El slot no debe ser nulo.
    // Post: Remueve el ítem del diccionario y lo devuelve. Si no había nada, retorna null.
    public Item desequipar(Slot slot) {
        Item itemRemovido = this.slots.obtenerValor(slot);
        if (itemRemovido != null) {
            this.slots.eliminar(slot);
        }
        return itemRemovido;
    }

    // Pre: -
    // Post: Todos los compartimentos de equipamiento (slots) del personaje pasan a estar vacíos.
    public void desequiparTodo() {
        int i;
        Slot[] todosLosSlots = Slot.values();
        Slot slotActual;

        for (i = 0; i < todosLosSlots.length; i++) {
            slotActual = todosLosSlots[i];
            if (this.slots.obtenerValor(slotActual) != null) {
                this.slots.eliminar(slotActual);
            }
        }
    }
}