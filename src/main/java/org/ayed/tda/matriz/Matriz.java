package org.ayed.tda.matriz;

/**
 * Implementación de una matriz genérica de tamaño fijo.
 *
 * @param <T> Tipo de dato a almacenar en la matriz.
 */
public class Matriz<T> {
    private int filas;
    private int columnas;
    private Object[][] datos;

    /**
     * Constructor para matriz cuadrada inicializada con un valor.
     *
     * @param tamanio Dimensiones de la matriz (tamanio * tamanio). No puede ser menor que 1.
     * @param valor   Valor inicial para todas las celdas.
     * @throws IllegalArgumentException Si el tamaño es menor que 1.
     */
    public Matriz(int tamanio, T valor) {
        if (tamanio < 1) {
            throw new IllegalArgumentException("El tamaño no puede ser menor que 1.");
        }
        this.filas = tamanio;
        this.columnas = tamanio;
        this.datos = new Object[tamanio][tamanio];
        inicializar(valor);
    }

    /**
     * Constructor para matriz cuadrada vacía (inicializada en null).
     *
     * @param tamanio Dimensiones de la matriz (tamanio * tamanio). No puede ser menor que 1.
     * @throws IllegalArgumentException Si el tamaño es menor que 1.
     */
    public Matriz(int tamanio) {
        this(tamanio, null);
    }

    /**
     * Constructor para matriz rectangular inicializada con un valor.
     *
     * @param filas    Cantidad de filas. No puede ser menor que 1.
     * @param columnas Cantidad de columnas. No puede ser menor que 1.
     * @param valor    Valor inicial para todas las celdas.
     * @throws IllegalArgumentException Si filas o columnas son menores que 1.
     */
    public Matriz(int filas, int columnas, T valor) {
        if (filas < 1 || columnas < 1) {
            throw new IllegalArgumentException("Las filas y columnas no pueden ser menores que 1.");
        }
        this.filas = filas;
        this.columnas = columnas;
        this.datos = new Object[filas][columnas];
        inicializar(valor);
    }

    /**
     * Constructor para matriz rectangular vacía (inicializada en null).
     *
     * @param filas    Cantidad de filas. No puede ser menor que 1.
     * @param columnas Cantidad de columnas. No puede ser menor que 1.
     * @throws IllegalArgumentException Si filas o columnas son menores que 1.
     */
    public Matriz(int filas, int columnas) {
        this(filas, columnas, null);
    }

    /**
     * Constructor por copia.
     *
     * @param otra Matriz a copiar.
     * @throws IllegalArgumentException Si la matriz a copiar es nula.
     */
    public Matriz(Matriz<T> otra) {
        if (otra == null) {
            throw new IllegalArgumentException("No se puede copiar una matriz nula.");
        }
        this.filas = otra.filas();
        this.columnas = otra.columnas();
        this.datos = new Object[this.filas][this.columnas];
        
        for (int i = 0; i < this.filas; i++) {
            for (int j = 0; j < this.columnas; j++) {
                this.datos[i][j] = otra.elemento(i, j);
            }
        }
    }

    /**
     * Obtiene el elemento en la posición indicada.
     * <p>
     * El índice debe ser válido (0 <= i < filas, 0 <= j < columnas).
     *
     * @param i Índice de la fila.
     * @param j Índice de la columna.
     * @return El elemento en (i, j).
     * @throws IndiceNoValidoException Si el índice está fuera de rango.
     */
    @SuppressWarnings("unchecked")
    public T elemento(int i, int j) {
        validarIndices(i, j);
        return (T) this.datos[i][j];
    }

    /**
     * Asigna un valor en la posición indicada.
     * <p>
     * El índice debe ser válido (0 <= i < filas, 0 <= j < columnas).
     *
     * @param i     Índice de la fila.
     * @param j     Índice de la columna.
     * @param valor Valor a asignar.
     * @throws IndiceNoValidoException Si el índice está fuera de rango.
     */
    public void asignar(int i, int j, T valor) {
        validarIndices(i, j);
        this.datos[i][j] = valor;
    }

    /**
     * Devuelve la cantidad de filas de la matriz.
     *
     * @return Cantidad de filas.
     */
    public int filas() {
        return this.filas;
    }

    /**
     * Devuelve la cantidad de columnas de la matriz.
     *
     * @return Cantidad de columnas.
     */
    public int columnas() {
        return this.columnas;
    }

    /**
     * Método auxiliar para inicializar la matriz con un valor por defecto.
     * * @param valor El valor a asignar en todas las celdas.
     */
    private void inicializar(T valor) {
        if (valor != null) {
            for (int i = 0; i < this.filas; i++) {
                for (int j = 0; j < this.columnas; j++) {
                    this.datos[i][j] = valor;
                }
            }
        }
    }

    /**
     * Valida que los índices estén dentro del rango de la matriz.
     * * @param i Índice de la fila.
     * @param j Índice de la columna.
     * @throws IndiceNoValidoException Si algún índice es negativo o excede las dimensiones.
     */
    private void validarIndices(int i, int j) {
        if (i < 0 || i >= this.filas || j < 0 || j >= this.columnas) {
            throw new IndiceNoValidoException("Índice fuera de rango: (" + i + ", " + j + ")");
        }
    }
}