package org.ayed.tda.vector;

/**
 * Implementación de un Vector Dinámico.
 * <p>
 * Esta estructura de datos crece automáticamente cuando se llena y libera memoria
 * cuando se vacía significativamente. Permite acceso aleatorio, inserción y
 * eliminación de elementos.
 * <p>
 * Los métodos que modifican la estructura del vector (como {@link #agregar(Object, int)} y
 * {@link #eliminar(int)}) desplazan los elementos según sea necesario.
 *
 * @param <T> Tipo de dato a almacenar en el vector.
 */
public class VectorDinamico<T> {
    
    private static final int CAPACIDAD_INICIAL = 0;
    private T[] datos;
    private int cantidadElementos;

    /**
     * Constructor de Vector.
     * Inicializa un vector con capacidad inicial 0.
     */
    @SuppressWarnings("unchecked")
    public VectorDinamico() {
        this.datos = (T[]) new Object[CAPACIDAD_INICIAL];
        this.cantidadElementos = 0;
    }

    /**
     * Constructor de copia de Vector.
     *
     * @param otro Vector a copiar.
     * @throws IllegalArgumentException si el vector a copiar es nulo.
     */
    @SuppressWarnings("unchecked")
    public VectorDinamico(VectorDinamico<T> otro) {
        if (otro == null) {
            throw new IllegalArgumentException("El vector a copiar no puede ser nulo.");
        }
        this.cantidadElementos = otro.cantidadElementos;
        this.datos = (T[]) new Object[otro.capacidad()];
        for (int i = 0; i < this.cantidadElementos; i++) {
            this.datos[i] = otro.datos[i];
        }
    }

    /**
     * Agrega un dato al final del vector.
     * <p>
     * Si el vector está lleno, aumenta su capacidad.
     *
     * @param dato Dato a agregar.
     */
    public void agregar(T dato) {
        agrandarSiEsNecesario();
        this.datos[cantidadElementos] = dato;
        this.cantidadElementos++;
    }

    /**
     * Agrega un dato al vector en el índice indicado, desplazando los elementos posteriores.
     *
     * @param dato   Dato a agregar.
     * @param indice Índice en el que se inserta el dato.
     * Debe estar entre 0 y el tamaño lógico del vector (inclusive).
     * @throws IndiceFueraDeRangoException si el índice es menor a 0 o mayor que el tamaño lógico.
     */
    public void agregar(T dato, int indice) {
        if (indice < 0 || indice > cantidadElementos) {
            throw new IndiceFueraDeRangoException("El índice " + indice + " está fuera de rango para agregar.");
        }
        agrandarSiEsNecesario();
        
        
        for (int i = cantidadElementos; i > indice; i--) {
            this.datos[i] = this.datos[i - 1];
        }
        
        this.datos[indice] = dato;
        this.cantidadElementos++;
    }

    /**
     * Elimina el último dato del vector.
     * <p>
     * Reduce la capacidad del vector si es necesario, dependiendo de la estrategia.
     *
     * @return el dato eliminado.
     * @throws VectorVacioException si el vector está vacío.
     */
    public T eliminar() {
        if (vacio()) {
            throw new VectorVacioException("No se puede eliminar de un vector vacío.");
        }
        
        T datoEliminado = this.datos[cantidadElementos - 1];
        this.datos[cantidadElementos - 1] = null; 
        this.cantidadElementos--;
        
        achicarSiEsNecesario();
        return datoEliminado;
    }

    /**
     * Elimina el dato del vector en el índice indicado, desplazando los elementos posteriores.
     *
     * @param indice Índice del dato a eliminar.
     * Debe estar entre 0 y tamaño lógico - 1.
     * @return el dato eliminado.
     * @throws VectorVacioException        si el vector está vacío.
     * @throws IndiceFueraDeRangoException si el índice es menor a 0 o mayor o igual al tamaño lógico.
     */
    public T eliminar(int indice) {
        if (vacio()) {
            throw new VectorVacioException("No se puede eliminar de un vector vacío.");
        }
        validarIndice(indice);
        
        T datoEliminado = this.datos[indice];
        
       
        for (int i = indice; i < cantidadElementos - 1; i++) {
            this.datos[i] = this.datos[i + 1];
        }
        
        this.datos[cantidadElementos - 1] = null;
        this.cantidadElementos--;
        
        achicarSiEsNecesario();
        return datoEliminado;
    }

    /**
     * Obtiene el dato del vector en el índice indicado.
     *
     * @param indice Índice del dato a obtener.
     * Debe estar entre 0 y tamaño lógico - 1.
     * @return el dato en el índice indicado.
     * @throws IndiceFueraDeRangoException si el índice es menor a 0 o mayor o igual al tamaño lógico.
     */
    public T obtener(int indice) {
        validarIndice(indice);
        return this.datos[indice];
    }

    /**
     * Modifica el dato del vector en el índice indicado.
     *
     * @param indice Índice del dato a modificar.
     * Debe estar entre 0 y tamaño lógico - 1.
     * @param dato   Nuevo dato.
     * @throws IndiceFueraDeRangoException si el índice es menor a 0 o mayor o igual al tamaño lógico.
     */
    public void cambiar(int indice, T dato) {
        validarIndice(indice);
        this.datos[indice] = dato;
    }

    /**
     * Obtiene el tamaño lógico del vector (cantidad de elementos almacenados).
     *
     * @return el tamaño del vector.
     */
    public int tamanio() {
        return this.cantidadElementos;
    }

    /**
     * Obtiene el tamaño físico actual del vector (capacidad).
     *
     * @return la capacidad actual del vector.
     */
    public int capacidad() {
        return this.datos.length;
    }

    /**
     * Evalúa si el vector está vacío.
     *
     * @return true si el vector está vacío.
     */
    public boolean vacio() {
        return this.cantidadElementos == 0;
    }

    /**
     * Valida que el índice esté dentro del rango de los elementos actuales (tamaño lógico).
     */
    private void validarIndice(int indice) {
        if (indice < 0 || indice >= cantidadElementos) {
            throw new IndiceFueraDeRangoException("El índice " + indice + " está fuera de rango.");
        }
    }

    /**
     * Duplica la capacidad del arreglo si está lleno.
     */
    @SuppressWarnings("unchecked")
    private void agrandarSiEsNecesario() {
        if (cantidadElementos == this.datos.length) {
            int nuevaCapacidad = (this.datos.length == 0) ? 1 : this.datos.length * 2;
            T[] nuevosDatos = (T[]) new Object[nuevaCapacidad];
            for (int i = 0; i < cantidadElementos; i++) {
                nuevosDatos[i] = this.datos[i];
            }
            this.datos = nuevosDatos;
        }
    }

    /**
     * Reduce la capacidad del arreglo a la mitad si los elementos ocupan menos de la mitad del espacio.
     * Esto cumple con el requisito de uso eficiente de memoria de las pruebas.
     */
    @SuppressWarnings("unchecked")
    private void achicarSiEsNecesario() {
        if (this.datos.length > 0 && cantidadElementos * 2 < this.datos.length) {
            int nuevaCapacidad = this.datos.length / 2;
            T[] nuevosDatos = (T[]) new Object[nuevaCapacidad];
            for (int i = 0; i < cantidadElementos; i++) {
                nuevosDatos[i] = this.datos[i];
            }
            this.datos = nuevosDatos;
        }
    }
}