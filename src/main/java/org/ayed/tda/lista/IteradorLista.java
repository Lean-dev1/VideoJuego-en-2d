package org.ayed.tda.lista;

import org.ayed.tda.iterador.Iterador;
import org.ayed.tda.iterador.ExcepcionNoHayDato;

class IteradorLista<T> implements Iterador<T> {
    private Lista<T> lista;
    private Nodo<T> cursor;
    private int indice;

    /**
     * Constructor de IteradorLista. Posiciona el iterador en el primer elemento.
     * Postcondiciones: el cursor queda en el primer nodo de la lista (o null si está vacía).
     *
     * @param lista Lista a iterar.
     */
    IteradorLista(Lista<T> lista) {
        this.lista = lista;
        this.cursor = lista.primero;
        this.indice = 0;
    }

    /**
     * Constructor de IteradorLista. Posiciona el iterador en el índice indicado.
     * Postcondiciones: el cursor queda en el nodo del índice indicado (o null si es el final).
     *
     * @param lista  Lista a iterar.
     * @param indice Índice inicial del iterador.
     */
    IteradorLista(Lista<T> lista, int indice) {
        this.lista = lista;
        this.indice = indice;
        if (indice == lista.cantidadDatos) {
            this.cursor = null;
        } else {
            this.cursor = lista.obtenerNodo(indice);
        }
    }

    @Override
    public T dato() {
        if (cursor == null) {
            throw new ExcepcionNoHayDato("No hay dato: el iterador está al final.");
        }
        return cursor.dato;
    }

    @Override
    public boolean haySiguiente() {
        return cursor != null;
    }

    @Override
    public void siguiente() {
        if (cursor == null) {
            throw new ExcepcionNoHayDato("No hay dato siguiente.");
        }
        cursor = cursor.siguiente;
        indice++;
    }

    @Override
    public boolean hayAnterior() {
        if (cursor == null) {
            return lista.ultimo != null;
        }
        return cursor.anterior != null;
    }

    @Override
    public void anterior() {
        if (!hayAnterior()) {
            throw new ExcepcionNoHayDato("No hay dato anterior.");
        }
        if (cursor == null) {
            cursor = lista.ultimo;
        } else {
            cursor = cursor.anterior;
        }
        indice--;
    }

    @Override
    public void agregar(T dato) {
        lista.agregar(dato, indice);
        indice++;
    }

    @Override
    public void modificarDato(T dato) {
        if (cursor == null) {
            throw new ExcepcionNoHayDato("No hay dato: el iterador está al final.");
        }
        cursor.dato = dato;
    }

    @Override
    public T eliminar() {
        if (cursor == null) {
            throw new ExcepcionNoHayDato("No hay dato: el iterador está al final.");
        }
        Nodo<T> siguiente = cursor.siguiente;
        T datoEliminado = lista.eliminar(indice);
        cursor = siguiente;
        return datoEliminado;
    }
}
