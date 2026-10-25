package org.ayed.tda.colaPrioridad;

import org.ayed.tda.comparador.Comparador;
import org.ayed.tda.vector.VectorDinamico;

public class ColaPrioridad<T> {
    // NOTA: Implementar el vector con factor-of-two
    // para tener complejidades logarítmicas.
    private VectorDinamico<T> datos;
    private Comparador<T> comparador;

    /**
     * Constructor de ColaPrioridad.
     * Postcondiciones: crea una cola de prioridad vacía con el comparador indicado.
     *
     * @param comparador Comparador a utilizar.
     *                   No puede ser nulo.
     * @throws ExcepcionColaPrioridad si el comparador es nulo.
     */
    public ColaPrioridad(Comparador<T> comparador) {
        if (comparador == null) {
            throw new ExcepcionColaPrioridad("El comparador no puede ser nulo.");
        }
        this.comparador = comparador;
        this.datos = new VectorDinamico<>();
    }

    /**
     * Constructor de copia de ColaPrioridad.
     * Postcondiciones: crea una cola de prioridad con los mismos datos y comparador que la original.
     *
     * @param colaPrioridad Cola a copiar.
     *                      No puede ser nula.
     * @throws ExcepcionColaPrioridad si la cola es nula.
     */
    public ColaPrioridad(ColaPrioridad<T> colaPrioridad) {
        if (colaPrioridad == null) {
            throw new ExcepcionColaPrioridad("La cola de prioridad no puede ser nula.");
        }
        this.comparador = colaPrioridad.comparador;
        this.datos = new VectorDinamico<>(colaPrioridad.datos);
    }

    /**
     * Reordena el Heap para mantener el invariante.
     * Desplaza datos hacia arriba, comparando el dato actual
     * con su padre, hasta cumplir con el invariante.
     * Inicia en el último dato del vector.
     * Postcondiciones: el heap cumple el invariante de orden.
     */
    private void heapificarHaciaArriba() {
        int indiceActual = datos.tamanio() - 1;
        int indicePadre = (indiceActual - 1) / 2;
        boolean esMayorQuePadre = indiceActual > 0
                && comparador.comparar(datos.obtener(indiceActual), datos.obtener(indicePadre)) > 0;
        while (esMayorQuePadre) {
            T temporal = datos.obtener(indiceActual);
            datos.cambiar(indiceActual, datos.obtener(indicePadre));
            datos.cambiar(indicePadre, temporal);
            indiceActual = indicePadre;
            indicePadre = (indiceActual - 1) / 2;
            esMayorQuePadre = indiceActual > 0
                    && comparador.comparar(datos.obtener(indiceActual), datos.obtener(indicePadre)) > 0;
        }
    }

    /**
     * Reordena el Heap para mantener el invariante.
     * Desplaza datos hacia abajo, comparando el dato actual
     * con el hijo con mayor prioridad, hasta cumplir con el
     * invariante. Inicia en el primer dato del vector.
     * Postcondiciones: el heap cumple el invariante de orden.
     */
    private void heapificarHaciaAbajo() {
        int indiceActual = 0;
        int indiceHijoIzquierdo = 1;
        boolean tienehijos = indiceHijoIzquierdo < datos.tamanio();
        while (tienehijos) {
            int indiceMayorHijo = indiceHijoIzquierdo;
            int indiceHijoDerecho = indiceHijoIzquierdo + 1;
            if (indiceHijoDerecho < datos.tamanio()
                    && comparador.comparar(datos.obtener(indiceHijoDerecho), datos.obtener(indiceMayorHijo)) > 0) {
                indiceMayorHijo = indiceHijoDerecho;
            }
            boolean esMenorQueHijo = comparador.comparar(datos.obtener(indiceActual), datos.obtener(indiceMayorHijo)) < 0;
            if (esMenorQueHijo) {
                T temporal = datos.obtener(indiceActual);
                datos.cambiar(indiceActual, datos.obtener(indiceMayorHijo));
                datos.cambiar(indiceMayorHijo, temporal);
                indiceActual = indiceMayorHijo;
                indiceHijoIzquierdo = 2 * indiceActual + 1;
                tienehijos = indiceHijoIzquierdo < datos.tamanio();
            } else {
                tienehijos = false;
            }
        }
    }

    /**
     * Agrega el dato a la cola, manteniendo el invariante del Heap.
     * Postcondiciones: el dato queda en su posición correcta según la prioridad.
     *
     * @param dato Dato a agregar.
     */
    public void agregar(T dato) {
        datos.agregar(dato);
        heapificarHaciaArriba();
    }

    /**
     * Elimina el siguiente dato de la cola (mayor prioridad),
     * manteniendo el invariante del Heap.
     * Postcondiciones: devuelve el dato de mayor prioridad y el heap queda reordenado.
     *
     * @return el dato con mayor prioridad en la cola.
     * @throws ExcepcionColaPrioridad si la cola está vacía.
     */
    public T eliminar() {
        if (vacio()) {
            throw new ExcepcionColaPrioridad("La cola de prioridad está vacía.");
        }
        T datoEliminado = datos.obtener(0);
        datos.cambiar(0, datos.obtener(datos.tamanio() - 1));
        datos.eliminar();
        if (!vacio()) {
            heapificarHaciaAbajo();
        }
        return datoEliminado;
    }

    /**
     * Obtiene el siguiente dato de la cola (mayor prioridad), sin eliminarlo.
     * Postcondiciones: devuelve el dato de mayor prioridad sin modificar la cola.
     *
     * @return el dato con mayor prioridad en la cola.
     * @throws ExcepcionColaPrioridad si la cola está vacía.
     */
    public T siguiente() {
        if (vacio()) {
            throw new ExcepcionColaPrioridad("La cola de prioridad está vacía.");
        }
        return datos.obtener(0);
    }

    /**
     * Obtiene el tamaño de la cola.
     *
     * @return el tamaño de la cola.
     */
    public int tamanio() {
        return datos.tamanio();
    }

    /**
     * Evalúa si la cola está vacía.
     *
     * @return true si la cola está vacía.
     */
    public boolean vacio() {
        return datos.vacio();
    }
}
