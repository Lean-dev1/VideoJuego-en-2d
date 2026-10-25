package org.ayed.tda.lista;

public class Pila<T> {
    private Nodo<T> ultimo;
    private int cantidadDatos;

    /**
     * Constructor de Pila.
     * Postcondiciones: crea una pila vacía con ultimo en null.
     */
    public Pila() {
        ultimo = null;
        cantidadDatos = 0;
    }

    /**
     * Constructor de copia de Pila.
     * Postcondiciones: crea una pila con los mismos datos y orden que la original.
     *
     * @param pila Pila a copiar.
     *             No puede ser nula.
     * @throws ExcepcionLista si la pila es nula.
     */
    public Pila(Pila<T> pila) {
        if (pila == null) {
            throw new ExcepcionLista("La pila no puede ser nula.");
        }
        ultimo = null;
        cantidadDatos = 0;
        Pila<T> pilaAuxiliar = new Pila<>();
        Nodo<T> cursor = pila.ultimo;
        while (cursor != null) {
            pilaAuxiliar.agregar(cursor.dato);
            cursor = cursor.anterior;
        }
        Nodo<T> cursorAux = pilaAuxiliar.ultimo;
        while (cursorAux != null) {
            agregar(cursorAux.dato);
            cursorAux = cursorAux.anterior;
        }
    }

    /**
     * Agrega el dato al tope de la pila.
     * Postcondiciones: el dato queda en el tope y cantidadDatos aumenta en 1.
     *
     * @param dato Dato a agregar.
     */
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato, ultimo, null);
        if (!vacio()) {
            ultimo.siguiente = nuevo;
        }
        ultimo = nuevo;
        cantidadDatos++;
    }

    /**
     * Elimina el siguiente dato de la pila (LIFO).
     * Postcondiciones: devuelve el dato eliminado y cantidadDatos disminuye en 1.
     *
     * @return el siguiente dato de la pila.
     * @throws ExcepcionLista si la pila está vacía.
     */
    public T eliminar() {
        if (vacio()) {
            throw new ExcepcionLista("La pila está vacía.");
        }
        T datoEliminado = ultimo.dato;
        ultimo = ultimo.anterior;
        if (ultimo != null) {
            ultimo.siguiente = null;
        }
        cantidadDatos--;
        return datoEliminado;
    }

    /**
     * Obtiene el siguiente dato de la pila (LIFO), sin eliminarlo.
     * Postcondiciones: devuelve el dato en el tope sin modificar la pila.
     *
     * @return el siguiente dato de la pila.
     * @throws ExcepcionLista si la pila está vacía.
     */
    public T siguiente() {
        if (vacio()) {
            throw new ExcepcionLista("La pila está vacía.");
        }
        return ultimo.dato;
    }

    /**
     * Obtiene el tamaño de la pila.
     *
     * @return el tamaño de la pila.
     */
    public int tamanio() {
        return cantidadDatos;
    }

    /**
     * Evalúa si la pila está vacía.
     *
     * @return true si la pila está vacía.
     */
    public boolean vacio() {
        return cantidadDatos == 0;
    }
}
