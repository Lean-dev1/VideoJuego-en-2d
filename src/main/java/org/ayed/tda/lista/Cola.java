package org.ayed.tda.lista;

public class Cola<T> {
    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int cantidadDatos;

    /**
     * Constructor de Cola.
     * Postcondiciones: crea una cola vacía con primero y ultimo en null.
     */
    public Cola() {
        primero = null;
        ultimo = null;
        cantidadDatos = 0;
    }

    /**
     * Constructor de copia de Cola.
     * Postcondiciones: crea una cola con los mismos datos y orden que la original.
     *
     * @param cola Cola a copiar.
     *             No puede ser nula.
     * @throws ExcepcionLista si la cola es nula.
     */
    public Cola(Cola<T> cola) {
        if (cola == null) {
            throw new ExcepcionLista("La cola no puede ser nula.");
        }
        primero = null;
        ultimo = null;
        cantidadDatos = 0;
        Nodo<T> cursor = cola.primero;
        while (cursor != null) {
            agregar(cursor.dato);
            cursor = cursor.siguiente;
        }
    }

    /**
     * Agrega el dato al final de la cola.
     * Postcondiciones: el dato queda en la última posición y cantidadDatos aumenta en 1.
     *
     * @param dato Dato a agregar.
     */
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato, ultimo, null);
        if (vacio()) {
            primero = nuevo;
        } else {
            ultimo.siguiente = nuevo;
        }
        ultimo = nuevo;
        cantidadDatos++;
    }

    /**
     * Elimina el siguiente dato de la cola (FIFO).
     * Postcondiciones: devuelve el dato eliminado y cantidadDatos disminuye en 1.
     *
     * @return el siguiente dato de la cola.
     * @throws ExcepcionLista si la cola está vacía.
     */
    public T eliminar() {
        if (vacio()) {
            throw new ExcepcionLista("La cola está vacía.");
        }
        T datoEliminado = primero.dato;
        primero = primero.siguiente;
        if (primero != null) {
            primero.anterior = null;
        } else {
            ultimo = null;
        }
        cantidadDatos--;
        return datoEliminado;
    }

    /**
     * Obtiene el siguiente dato de la cola (FIFO), sin eliminarlo.
     * Postcondiciones: devuelve el dato en la primera posición sin modificar la cola.
     *
     * @return el siguiente dato de la cola.
     * @throws ExcepcionLista si la cola está vacía.
     */
    public T siguiente() {
        if (vacio()) {
            throw new ExcepcionLista("La cola está vacía.");
        }
        return primero.dato;
    }

    /**
     * Obtiene el tamaño de la cola.
     *
     * @return el tamaño de la cola.
     */
    public int tamanio() {
        return cantidadDatos;
    }

    /**
     * Evalúa si la cola está vacía.
     *
     * @return true si la cola está vacía.
     */
    public boolean vacio() {
        return cantidadDatos == 0;
    }
}
