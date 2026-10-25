package org.ayed.tda.lista;

import org.ayed.tda.iterador.Iterador;

public class Lista<T> {
    Nodo<T> primero;
    Nodo<T> ultimo;
    int cantidadDatos;

    /**
     * Constructor de Lista.
     * Postcondiciones: crea una lista vacía con primero y ultimo en null.
     */
    public Lista() {
        primero = null;
        ultimo = null;
        cantidadDatos = 0;
    }

    /**
     * Constructor de copia de Lista.
     * Postcondiciones: crea una lista con los mismos datos y orden que la original.
     *
     * @param lista Lista a copiar.
     *              No puede ser nula.
     * @throws ExcepcionLista si la lista es nula.
     */
    public Lista(Lista<T> lista) {
        if (lista == null) {
            throw new ExcepcionLista("La lista no puede ser nula.");
        }
        primero = null;
        ultimo = null;
        cantidadDatos = 0;
        Nodo<T> cursor = lista.primero;
        while (cursor != null) {
            agregar(cursor.dato);
            cursor = cursor.siguiente;
        }
    }

    /**
     * Agrega un dato al final de la lista.
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
     * Obtiene el nodo en el índice indicado.
     * Precondiciones: el índice debe estar entre 0 y cantidadDatos - 1.
     * Postcondiciones: devuelve el nodo en la posición indicada.
     *
     * @param indice Índice del nodo a obtener.
     * @return el nodo en el índice indicado.
     */
    Nodo<T> obtenerNodo(int indice) {
        Nodo<T> cursor = primero;
        int posicionActual = 0;
        while (posicionActual < indice) {
            cursor = cursor.siguiente;
            posicionActual++;
        }
        return cursor;
    }

    /**
     * Agrega un dato a la lista en el índice indicado.
     * <p>
     * Ejemplo:
     * <pre>
     * {@code
     * >> 0 -> 1 -> 5 -> 3
     * agregar(4, 1);
     * >> 0 -> 4 -> 1 -> 5 -> 3
     * }
     * </pre>
     * Postcondiciones: el dato queda en la posición indicada y cantidadDatos aumenta en 1.
     *
     * @param dato   Dato a agregar.
     * @param indice Índice en el que se inserta el dato.
     *               No puede ser negativo.
     *               No puede ser mayor que el tamaño de la lista.
     * @throws ExcepcionLista si el índice no es válido.
     */
    public void agregar(T dato, int indice) {
        if (indice < 0 || indice > cantidadDatos) {
            throw new ExcepcionLista("Índice no válido.");
        }
        if (indice == cantidadDatos) {
            agregar(dato);
        } else {
            Nodo<T> siguiente = obtenerNodo(indice);
            Nodo<T> nuevo = new Nodo<>(dato, siguiente.anterior, siguiente);
            if (siguiente.anterior != null) {
                siguiente.anterior.siguiente = nuevo;
            } else {
                primero = nuevo;
            }
            siguiente.anterior = nuevo;
            cantidadDatos++;
        }
    }

    /**
     * Elimina el último dato de la lista.
     * Postcondiciones: devuelve el dato eliminado y cantidadDatos disminuye en 1.
     *
     * @return el dato eliminado.
     * @throws ExcepcionLista si la lista está vacía.
     */
    public T eliminar() {
        if (vacio()) {
            throw new ExcepcionLista("La lista está vacía.");
        }
        T datoEliminado = ultimo.dato;
        if (cantidadDatos == 1) {
            primero = null;
            ultimo = null;
        } else {
            ultimo = ultimo.anterior;
            ultimo.siguiente = null;
        }
        cantidadDatos--;
        return datoEliminado;
    }

    /**
     * Elimina el dato de la lista en el índice indicado por parámetro.
     * <p>
     * Ejemplo:
     * <pre>
     * {@code
     * >> 0 -> 1 -> 5 -> 3
     * eliminar(1);
     * >> 0 -> 5 -> 3
     * }
     * </pre>
     * Postcondiciones: devuelve el dato eliminado y cantidadDatos disminuye en 1.
     *
     * @param indice Índice del dato a eliminar.
     *               No puede ser negativo.
     *               No puede ser mayor o igual que el tamaño de la lista.
     * @return el dato eliminado.
     * @throws ExcepcionLista si el índice no es válido.
     */
    public T eliminar(int indice) {
        if (indice < 0 || indice >= cantidadDatos) {
            throw new ExcepcionLista("Índice no válido.");
        }
        Nodo<T> nodoAEliminar = obtenerNodo(indice);
        T datoEliminado = nodoAEliminar.dato;
        if (nodoAEliminar.anterior != null) {
            nodoAEliminar.anterior.siguiente = nodoAEliminar.siguiente;
        } else {
            primero = nodoAEliminar.siguiente;
        }
        if (nodoAEliminar.siguiente != null) {
            nodoAEliminar.siguiente.anterior = nodoAEliminar.anterior;
        } else {
            ultimo = nodoAEliminar.anterior;
        }
        cantidadDatos--;
        return datoEliminado;
    }

    /**
     * Obtiene el dato de la lista en el índice indicado.
     * Postcondiciones: devuelve el dato en la posición indicada, sin modificar la lista.
     *
     * @param indice Índice del dato a obtener.
     *               No puede ser negativo.
     *               No puede ser mayor o igual que el tamaño de la lista.
     * @return el dato en el índice indicado.
     * @throws ExcepcionLista si el índice no es válido.
     */
    public T dato(int indice) {
        if (indice < 0 || indice >= cantidadDatos) {
            throw new ExcepcionLista("Índice no válido.");
        }
        return obtenerNodo(indice).dato;
    }

    /**
     * Modifica el dato de la lista en el índice indicado
     * por el dato indicado por parámetro.
     * Postcondiciones: el dato en la posición indicada es reemplazado por el nuevo dato.
     *
     * @param indice Índice del dato a modificar.
     *               No puede ser negativo.
     *               No puede ser mayor o igual que el tamaño de la lista.
     * @throws ExcepcionLista si el índice no es válido.
     */
    public void modificarDato(T dato, int indice) {
        if (indice < 0 || indice >= cantidadDatos) {
            throw new ExcepcionLista("Índice no válido.");
        }
        obtenerNodo(indice).dato = dato;
    }

    /**
     * Obtiene el tamaño de la lista.
     *
     * @return el tamaño de la lista.
     */
    public int tamanio() {
        return cantidadDatos;
    }

    /**
     * Evalúa si la lista está vacía.
     *
     * @return true si la lista está vacía.
     */
    public boolean vacio() {
        return cantidadDatos == 0;
    }

    /**
     * Obtiene un iterador bidireccional posicionado
     * en el primer dato de la lista.
     * Postcondiciones: devuelve un iterador posicionado en el índice 0.
     *
     * @return el iterador.
     * @see Iterador
     */
    public Iterador<T> iterador() {
        return new IteradorLista<>(this);
    }

    /**
     * Obtiene un iterador bidireccional posicionado
     * en el índice indicado por parámetro.
     * Postcondiciones: devuelve un iterador posicionado en el índice indicado.
     *
     * @param indice Índice del nodo inicial del iterador.
     *               No puede ser negativo.
     *               No puede ser mayor que el tamaño de la lista.
     * @return el iterador.
     * @throws ExcepcionLista si el índice no es válido.
     * @see Iterador
     */
    public Iterador<T> iterador(int indice) {
        if (indice < 0 || indice > cantidadDatos) {
            throw new ExcepcionLista("Índice no válido.");
        }
        return new IteradorLista<>(this, indice);
    }
}
