package org.ayed.poe.arbol;

import org.ayed.tda.diccionario.Diccionario;
import org.ayed.tda.grafo.Grafo;
import org.ayed.tda.iterador.Iterador;
import org.ayed.tda.lista.Cola;
import org.ayed.tda.lista.Lista;

/**
 * Árbol de habilidades pasivas del personaje.
 * Internamente usa un Grafo no dirigido donde cada vértice es una NodoPasiva.
 *
 * Faustina/Lean: la interfaz pública que necesitan es:
 *   - obtenerPasiva(id)          -> para mostrar info de una pasiva
 *   - obtenerAdyacentes(id)      -> para saber qué nodos vecinos dibujar como disponibles
 *   - estaAsignada(id)           -> para pintar el nodo como activo o no
 *   - obtenerPuntosDisponibles() -> para mostrar los puntos restantes
 *   - asignarPasiva(id)          -> click en nodo adyacente a uno asignado
 *   - asignarPasivaConCamino(id) -> click en nodo lejano (devuelve lista para animar el camino)
 *   - desasignarPasiva(id)       -> click derecho para quitar
 */
public class ArbolHabilidades {
    // Tamaño estimado del árbol pasivo; ajustar si el archivo tiene más nodos
    private static final int CAPACIDAD_MAXIMA = 1500;

    private final Grafo<NodoPasiva> grafo;
    // nodosById permite buscar una pasiva por id en O(1) sin recorrer el grafo
    private final Diccionario<Integer, NodoPasiva> nodosById;
    private int puntosDisponibles;

    /**
     * Constructor de ArbolHabilidades.
     * Postcondiciones: crea un árbol vacío con los puntos indicados.
     *
     * @param puntosIniciales Puntos de habilidad disponibles al comenzar.
     * @throws ExcepcionArbolHabilidades si los puntos son negativos.
     */
    public ArbolHabilidades(int puntosIniciales) {
        if (puntosIniciales < 0) {
            throw new ExcepcionArbolHabilidades("Los puntos iniciales no pueden ser negativos.");
        }
        this.grafo = new Grafo<>();
        this.nodosById = new Diccionario<>(CAPACIDAD_MAXIMA);
        this.puntosDisponibles = puntosIniciales;
    }

    /**
     * Agrega una pasiva al árbol (sin conexiones todavía).
     * Postcondiciones: la pasiva queda como vértice en el grafo.
     *
     * @param pasiva Pasiva a agregar. No puede ser nula.
     * @throws ExcepcionArbolHabilidades si la pasiva es nula.
     */
    public void agregarPasiva(NodoPasiva pasiva) {
        if (pasiva == null) {
            throw new ExcepcionArbolHabilidades("La pasiva no puede ser nula.");
        }
        grafo.agregarVertice(pasiva);
        nodosById.agregar(pasiva.obtenerId(), pasiva);
    }

    /**
     * Conecta dos pasivas en el árbol (arista no dirigida).
     *
     * @param idOrigen  Id de la pasiva origen.
     * @param idDestino Id de la pasiva destino.
     * @throws ExcepcionArbolHabilidades si alguno de los ids no existe en el árbol.
     */
    public void agregarConexion(int idOrigen, int idDestino) {
        NodoPasiva origen = obtenerPasivaOExcepcion(idOrigen);
        NodoPasiva destino = obtenerPasivaOExcepcion(idDestino);
        grafo.agregarArista(origen, destino, 1);
    }

    /**
     * Marca una pasiva como nodo de inicio sin gastar puntos.
     * Debe llamarse al cargar el árbol para definir el punto de partida.
     * Postcondiciones: la pasiva queda asignada y puntosDisponibles no cambia.
     *
     * @param id Id de la pasiva de inicio.
     * @throws ExcepcionArbolHabilidades si la pasiva no existe.
     */
    public void marcarComoInicio(int id) {
        obtenerPasivaOExcepcion(id).asignar();
    }

    /**
     * Asigna una pasiva que esté directamente conectada a una ya asignada.
     * Postcondiciones: la pasiva queda asignada y puntosDisponibles disminuye en 1.
     *
     * @param id Id de la pasiva a asignar.
     * @throws ExcepcionArbolHabilidades si la pasiva no existe, ya está asignada,
     *                                   no está conectada a ninguna pasiva asignada,
     *                                   o no hay puntos disponibles.
     */
    public void asignarPasiva(int id) {
        NodoPasiva pasiva = obtenerPasivaOExcepcion(id);
        if (pasiva.estaAsignada()) {
            throw new ExcepcionArbolHabilidades("La pasiva ya está asignada.");
        }
        if (puntosDisponibles <= 0) {
            throw new ExcepcionArbolHabilidades("No hay puntos de habilidad disponibles.");
        }
        boolean tieneVecinoAsignado = tieneVecinoAsignado(pasiva);
        if (!tieneVecinoAsignado) {
            throw new ExcepcionArbolHabilidades("La pasiva no está conectada a ninguna pasiva asignada.");
        }
        pasiva.asignar();
        puntosDisponibles--;
    }

    /**
     * Asigna una pasiva usando BFS para encontrar el camino más corto
     * desde cualquier pasiva ya asignada. Asigna todas las pasivas
     * intermedias del camino, gastando un punto por cada una.
     * Postcondiciones: todas las pasivas del camino quedan asignadas.
     *
     * Faustina/Lean: devuelve la lista ordenada de pasivas asignadas
     * (desde la más cercana al origen hasta el objetivo),
     * útil para animar el camino de a un nodo por vez.
     *
     * @param id Id de la pasiva objetivo.
     * @return lista con las pasivas asignadas en orden origen → objetivo.
     * @throws ExcepcionArbolHabilidades si la pasiva no existe, ya está asignada,
     *                                   no hay camino posible, o no hay puntos suficientes.
     */
    public Lista<NodoPasiva> asignarPasivaConCamino(int id) {
        NodoPasiva objetivo = obtenerPasivaOExcepcion(id);
        if (objetivo.estaAsignada()) {
            throw new ExcepcionArbolHabilidades("La pasiva ya está asignada.");
        }
        Lista<NodoPasiva> camino = buscarCaminoMinimo(objetivo);
        if (camino == null || camino.vacio()) {
            throw new ExcepcionArbolHabilidades("No hay camino hacia la pasiva desde el árbol asignado.");
        }
        if (puntosDisponibles < camino.tamanio()) {
            throw new ExcepcionArbolHabilidades("No hay suficientes puntos de habilidad. Se necesitan "
                    + camino.tamanio() + " y hay " + puntosDisponibles + ".");
        }
        Iterador<NodoPasiva> it = camino.iterador();
        while (it.haySiguiente()) {
            it.dato().asignar();
            puntosDisponibles--;
            it.siguiente();
        }
        return camino;
    }

    /**
     * Desasigna una pasiva y devuelve el punto de habilidad,
     * siempre que hacerlo no desconecte otras pasivas ya asignadas.
     * Postcondiciones: la pasiva queda desasignada y puntosDisponibles aumenta en 1.
     *
     * @param id Id de la pasiva a desasignar.
     * @throws ExcepcionArbolHabilidades si la pasiva no existe, no está asignada,
     *                                   o desasignarla desconectaría otras pasivas.
     */
    public void desasignarPasiva(int id) {
        NodoPasiva pasiva = obtenerPasivaOExcepcion(id);
        if (!pasiva.estaAsignada()) {
            throw new ExcepcionArbolHabilidades("La pasiva no está asignada.");
        }
        if (!esSeguroDesasignar(pasiva)) {
            throw new ExcepcionArbolHabilidades("No se puede desasignar: otras pasivas dependen de esta para estar conectadas.");
        }
        pasiva.desasignar();
        puntosDisponibles++;
    }

    /**
     * Agrega puntos de habilidad (por ejemplo, al subir de nivel).
     *
     * @param puntos Cantidad de puntos a agregar. No puede ser negativo.
     * @throws ExcepcionArbolHabilidades si los puntos son negativos.
     */
    public void agregarPuntos(int puntos) {
        if (puntos < 0) {
            throw new ExcepcionArbolHabilidades("Los puntos a agregar no pueden ser negativos.");
        }
        puntosDisponibles += puntos;
    }

    /**
     * Obtiene los puntos de habilidad disponibles.
     *
     * @return los puntos disponibles.
     */
    public int obtenerPuntosDisponibles() {
        return puntosDisponibles;
    }

    /**
     * Obtiene una pasiva por su id.
     *
     * @param id Id de la pasiva.
     * @return la pasiva, o null si no existe.
     */
    public NodoPasiva obtenerPasiva(int id) {
        return nodosById.obtenerValor(id);
    }

    /**
     * Obtiene todas las pasivas del árbol.
     * Faustina/Lean: usen esto para recorrer y dibujar todos los nodos del árbol.
     *
     * @return lista con todas las pasivas cargadas.
     */
    public Lista<NodoPasiva> obtenerTodas() {
        return nodosById.valores();
    }

    /**
     * Evalúa si una pasiva está asignada.
     *
     * @param id Id de la pasiva.
     * @return true si la pasiva está asignada.
     * @throws ExcepcionArbolHabilidades si la pasiva no existe.
     */
    public boolean estaAsignada(int id) {
        return obtenerPasivaOExcepcion(id).estaAsignada();
    }

    /**
     * Obtiene los vecinos de una pasiva en el árbol.
     * Faustina/Lean: usen esto para saber qué nodos vecinos mostrar como disponibles.
     *
     * @param id Id de la pasiva.
     * @return lista con los vecinos directos en el árbol.
     * @throws ExcepcionArbolHabilidades si la pasiva no existe.
     */
    public Lista<NodoPasiva> obtenerAdyacentes(int id) {
        NodoPasiva pasiva = obtenerPasivaOExcepcion(id);
        return vecinosDe(pasiva);
    }

    // --- Métodos auxiliares privados ---

    private NodoPasiva obtenerPasivaOExcepcion(int id) {
        NodoPasiva pasiva = nodosById.obtenerValor(id);
        if (pasiva == null) {
            throw new ExcepcionArbolHabilidades("La pasiva con id " + id + " no existe en el árbol.");
        }
        return pasiva;
    }

    /**
     * Convierte los adyacentes del Grafo (Map de java.util) en una Lista del proyecto.
     */
    private Lista<NodoPasiva> vecinosDe(NodoPasiva nodo) {
        Lista<NodoPasiva> vecinos = new Lista<>();
        for (NodoPasiva vecino : grafo.obtenerAdyacentes(nodo).keySet()) {
            vecinos.agregar(vecino);
        }
        return vecinos;
    }

    private boolean tieneVecinoAsignado(NodoPasiva pasiva) {
        Lista<NodoPasiva> vecinos = vecinosDe(pasiva);
        Iterador<NodoPasiva> it = vecinos.iterador();
        boolean encontrado = false;
        while (it.haySiguiente() && !encontrado) {
            encontrado = it.dato().estaAsignada();
            it.siguiente();
        }
        return encontrado;
    }

    /**
     * BFS multi-fuente: parte desde todos los nodos asignados simultáneamente
     * y encuentra el camino más corto hasta el objetivo.
     *
     * @param objetivo Nodo al que queremos llegar.
     * @return lista de nodos NO asignados a asignar (en orden desde el más cercano al origen
     *         hasta el objetivo), o null si no hay camino.
     */
    private Lista<NodoPasiva> buscarCaminoMinimo(NodoPasiva objetivo) {
        Diccionario<Integer, NodoPasiva> predecesor = new Diccionario<>(CAPACIDAD_MAXIMA);
        Diccionario<Integer, Boolean> visitado = new Diccionario<>(CAPACIDAD_MAXIMA);
        Cola<NodoPasiva> cola = new Cola<>();
        NodoPasiva nodo;
        NodoPasiva actual;
        Lista<NodoPasiva> vecinos;
        Iterador<NodoPasiva> itVecinos;
        NodoPasiva vecino;

        Lista<NodoPasiva> todos = nodosById.valores();
        Iterador<NodoPasiva> it = todos.iterador();
        while (it.haySiguiente()) {
            nodo = it.dato();
            if (nodo.estaAsignada()) {
                visitado.agregar(nodo.obtenerId(), true);
                cola.agregar(nodo);
            }
            it.siguiente();
        }

        if (cola.vacio()) {
            return null;
        }

        boolean encontrado = false;
        while (!cola.vacio() && !encontrado) {
            actual = cola.eliminar();
            if (actual.equals(objetivo)) {
                encontrado = true;
            } else {
                vecinos = vecinosDe(actual);
                itVecinos = vecinos.iterador();
                while (itVecinos.haySiguiente()) {
                    vecino = itVecinos.dato();
                    if (visitado.obtenerValor(vecino.obtenerId()) == null) {
                        visitado.agregar(vecino.obtenerId(), true);
                        predecesor.agregar(vecino.obtenerId(), actual);
                        cola.agregar(vecino);
                    }
                    itVecinos.siguiente();
                }
            }
        }

        if (!encontrado) {
            return null;
        }

        // Reconstruir camino desde objetivo hasta el primer nodo asignado
        // El nodo asignado (fuente) NO se incluye: ya está asignado
        Lista<NodoPasiva> camino = new Lista<>();
        NodoPasiva cursor = objetivo;
        boolean llegamosFuente = cursor.estaAsignada();
        while (!llegamosFuente) {
            camino.agregar(cursor, 0);
            cursor = predecesor.obtenerValor(cursor.obtenerId());
            llegamosFuente = cursor.estaAsignada();
        }

        return camino;
    }

    /**
     * Verifica que desasignar la pasiva no desconecte el árbol asignado.
     * Hace un BFS sobre los nodos asignados excluyendo la pasiva dada
     * y verifica que todos los demás asignados siguen siendo alcanzables.
     */
    private boolean esSeguroDesasignar(NodoPasiva pasiva) {
        Lista<NodoPasiva> asignados = new Lista<>();
        Lista<NodoPasiva> todos = nodosById.valores();
        Iterador<NodoPasiva> it = todos.iterador();
        NodoPasiva nodo;
        while (it.haySiguiente()) {
            nodo = it.dato();
            if (nodo.estaAsignada() && !nodo.equals(pasiva)) {
                asignados.agregar(nodo);
            }
            it.siguiente();
        }

        if (asignados.vacio()) {
            return true;
        }

        Diccionario<Integer, Boolean> visitado = new Diccionario<>(CAPACIDAD_MAXIMA);
        Cola<NodoPasiva> cola = new Cola<>();
        NodoPasiva inicio = asignados.dato(0);
        visitado.agregar(inicio.obtenerId(), true);
        cola.agregar(inicio);

        NodoPasiva actual;
        Lista<NodoPasiva> vecinos;
        Iterador<NodoPasiva> itVecinos;
        NodoPasiva vecino;
        boolean estaAsignadoYNoEsLaPasiva;
        boolean noVisitado;
        while (!cola.vacio()) {
            actual = cola.eliminar();
            vecinos = vecinosDe(actual);
            itVecinos = vecinos.iterador();
            while (itVecinos.haySiguiente()) {
                vecino = itVecinos.dato();
                estaAsignadoYNoEsLaPasiva = vecino.estaAsignada() && !vecino.equals(pasiva);
                noVisitado = visitado.obtenerValor(vecino.obtenerId()) == null;
                if (estaAsignadoYNoEsLaPasiva && noVisitado) {
                    visitado.agregar(vecino.obtenerId(), true);
                    cola.agregar(vecino);
                }
                itVecinos.siguiente();
            }
        }

        boolean todoAlcanzado = true;
        Iterador<NodoPasiva> itAsignados = asignados.iterador();
        while (itAsignados.haySiguiente() && todoAlcanzado) {
            todoAlcanzado = visitado.obtenerValor(itAsignados.dato().obtenerId()) != null;
            itAsignados.siguiente();
        }
        return todoAlcanzado;
    }
}
