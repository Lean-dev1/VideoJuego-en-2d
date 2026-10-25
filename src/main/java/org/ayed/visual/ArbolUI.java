package org.ayed.visual;

import org.ayed.poe.arbol.ArbolHabilidades;
import org.ayed.poe.arbol.NodoPasiva;
import org.ayed.tda.lista.Lista;
import org.ayed.tda.lista.Cola;
import org.ayed.tda.diccionario.Diccionario;

import java.awt.*;

/**
 * Clase encargada de la representación gráfica del árbol de habilidades pasivas.
 * Vincula la lógica del Grafo no dirigido con coordenadas espaciales en la interfaz
 * gráfica, controlando el renderizado de conexiones, estados de nodos y clics de mouse.
 */
public class ArbolUI {
    // Constantes de Configuración de Negocio y UI
    private static final int CAPACIDAD_MAX_DICCIONARIO = 1500;
    private static final int GEOMETRIA_CENTRO_X = 400;     
    private static final int GEOMETRIA_BASE_Y = 60;        
    private static final int GEOMETRIA_ESPACIADO_Y = 90;   
    private static final int GEOMETRIA_ESPACIADO_X = 110;  
    private static final int RADIO_NODO = 16;
    private static final int FUENTE_TAMANIO_TITULO = 18;
    private static final int FUENTE_TAMANIO_NODO = 11;

    private ArbolHabilidades arbol;
    private Diccionario<Integer, Point> posiciones; 

    /**
     * Constructor de ArbolUI.
     * Precondiciones: arbol no debe ser nulo.
     * Postcondiciones: inicializa la estructura de posiciones fijando una capacidad máxima 
     * de 1500 elementos y dispara el cálculo geométrico dinámico por niveles (BFS).
     *
     * @param arbol Instancia lógica del árbol de habilidades.
     */
    public ArbolUI(ArbolHabilidades arbol) {
        this.arbol = arbol;
        this.posiciones = new Diccionario<>(CAPACIDAD_MAX_DICCIONARIO); 
        inicializarPosicionesDinamicas();
    }
    
    /**
     * Calcula de forma dinámica las coordenadas espaciales (X, Y) de todos los nodos.
     * Precondiciones: el objeto arbol interno debe contener nodos válidos y el diccionario 
     * de posiciones debe estar instanciado.
     * Postcondiciones: agrupa las pasivas por capas utilizando un recorrido en anchura (BFS) 
     * y distribuye horizontalmente de manera simétrica los nodos en la pantalla, insertando 
     * cada id con su respectivo objeto Point en el diccionario de posiciones.
     */
    private void inicializarPosicionesDinamicas() {
        Lista<NodoPasiva> todas = arbol.obtenerTodas();
        Integer idRaiz = null;
        int i = 0;
        int j = 0;
        int nivelContador = 0;
        int maxNivel = 0;
        int nivelActual = 0;
        int cantidadNodos = 0;
        Integer actual = null;
        Integer vecinoId = null;
        Integer idNodoGeometria = null;
        double posicionX = 0;
        int posicionY = 0;
        boolean raizEncontrada = false;
        
        Cola<Integer> cola = null;
        Diccionario<Integer, Integer> niveles = null;
        Diccionario<Integer, Lista<Integer>> nodosPorNivel = null;
        Lista<Integer> listaNivel = null;
        Lista<NodoPasiva> adyacentes = null;

        if (todas == null || todas.vacio()) {
            return;
        }

        // Encontrar el nodo raíz o inicial sin usar break
        while (!raizEncontrada && i < todas.tamanio()) {
            if (todas.dato(i).estaAsignada()) {
                idRaiz = todas.dato(i).obtenerId();
                raizEncontrada = true;
            }
            i++;
        }
        
        if (idRaiz == null) {
            return;
        }
        if (arbol.obtenerPasiva(idRaiz) == null) {
            idRaiz = todas.dato(0).obtenerId();
        }

        // Inicialización de TDAs para el BFS
        cola = new Cola<>();
        niveles = new Diccionario<>(CAPACIDAD_MAX_DICCIONARIO);
        nodosPorNivel = new Diccionario<>(CAPACIDAD_MAX_DICCIONARIO);
        
        cola.agregar(idRaiz);
        niveles.agregar(idRaiz, 0);
        
        while (!cola.vacio()) {
            actual = cola.eliminar();
            nivelActual = niveles.obtenerValor(actual) != null ? niveles.obtenerValor(actual) : 0;
            if (nivelActual > maxNivel) {
                maxNivel = nivelActual;
            }
            
            listaNivel = nodosPorNivel.obtenerValor(nivelActual);
            if (listaNivel == null) {
                listaNivel = new Lista<>();
                nodosPorNivel.agregar(nivelActual, listaNivel);
            }
            
            // Eliminado el ciclo 'for' redundante de búsqueda (Infracción al Mandamiento 7)
            listaNivel.agregar(actual);
            
            adyacentes = arbol.obtenerAdyacentes(actual);
            if (adyacentes != null) {
                for (j = 0; j < adyacentes.tamanio(); j++) {
                    vecinoId = adyacentes.dato(j).obtenerId();
                    if (niveles.obtenerValor(vecinoId) == null) {
                        niveles.agregar(vecinoId, nivelActual + 1);
                        cola.agregar(vecinoId);
                    }
                }
            }
        }  
        
        // Distribución geométrica en base a la estructura obtenida (variables declaradas externamente)
        for (nivelContador = 0; nivelContador <= maxNivel; nivelContador++) {
            listaNivel = nodosPorNivel.obtenerValor(nivelContador);
            if (listaNivel == null) {
                continue;
            }
            cantidadNodos = listaNivel.tamanio();
            for (i = 0; i < cantidadNodos; i++) {
                idNodoGeometria = listaNivel.dato(i);
                posicionX = GEOMETRIA_CENTRO_X + (i - (cantidadNodos - 1) / 2.0) * GEOMETRIA_ESPACIADO_X;
                posicionY = GEOMETRIA_BASE_Y + nivelContador * GEOMETRIA_ESPACIADO_Y;
                posiciones.agregar(idNodoGeometria, new Point((int) posicionX, posicionY));
            }
        }
    }

    /**
     * Dibuja los componentes visuales del árbol y le añade la cabecera de texto de estadísticas.
     * Precondiciones: g2 no debe ser nulo. Las coordenadas del diccionario deben estar calculadas.
     * Postcondiciones: renderiza en la pantalla el esqueleto gráfico completo del árbol y dibuja 
     * un texto informativo centrado con la cantidad actual de puntos disponibles del personaje.
     *
     * @param g2             Contexto gráfico bidimensional sobre el que se dibuja.
     * @param desplazamientoX Desplazamiento en el eje X para el control de cámara (scroll).
     * @param desplazamientoY Desplazamiento en el eje Y para el control de cámara (scroll).
     */
    public void dibujarCompleto(Graphics2D g2, int desplazamientoX, int desplazamientoY) {
        String texto = null;
        int anchoTexto = 0;

        this.dibujar(g2, desplazamientoX, desplazamientoY);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, FUENTE_TAMANIO_TITULO));
        texto = "Puntos Disponibles: " + arbol.obtenerPuntosDisponibles();
        anchoTexto = g2.getFontMetrics().stringWidth(texto);
        g2.drawString(texto, GEOMETRIA_CENTRO_X + desplazamientoX - (anchoTexto / 2), 30 + desplazamientoY);
    }

    /**
     * Dibuja las aristas y los vértices que constituyen el grafo en la interfaz.
     * Precondiciones: g2 no debe ser nulo.
     * Postcondiciones: plasma en la pantalla primero los segmentos rectilíneos (aristas) 
     * evitando duplicados, y encima de ellos los círculos rotulados correspondientes a cada 
     * nodo, variando su paleta de colores según estén asignados, disponibles o bloqueados.
     *
     * @param g2             Contexto gráfico bidimensional sobre el que se dibuja.
     * @param desplazamientoX Desplazamiento horizontal de la visualización.
     * @param desplazamientoY Desplazamiento vertical de la visualización.
     */
    public void dibujar(Graphics2D g2, int desplazamientoX, int desplazamientoY) {
        Lista<NodoPasiva> todas = arbol.obtenerTodas();
        int i = 0;
        int j = 0;
        int idOrigen = 0;
        int idDestino = 0;
        int dibujoX = 0;
        int dibujoY = 0;
        int idNodo = 0;
        int anchoNombre = 0;
        boolean origenAsignado = false;
        boolean destinoAsignado = false;
        boolean asignada = false;
        boolean disponible = false;
        String nombre = null;
        
        Point puntoOrigen = null;
        Point puntoDestino = null;
        Point posicionNodo = null;
        Lista<NodoPasiva> adyacentes = null;
        Lista<NodoPasiva> vecinos = null;
        NodoPasiva nodo = null;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (todas == null) {
            return;
        }

        // DIBUJAR ARISTAS (Variables declaradas fuera del ciclo)
        g2.setStroke(new BasicStroke(3));
        for (i = 0; i < todas.tamanio(); i++) {
            idOrigen = todas.dato(i).obtenerId();
            puntoOrigen = posiciones.obtenerValor(idOrigen);
            if (puntoOrigen == null) {
                continue;
            }

            adyacentes = arbol.obtenerAdyacentes(idOrigen);
            if (adyacentes == null) {
                continue;
            }

            for (j = 0; j < adyacentes.tamanio(); j++) {
                idDestino = adyacentes.dato(j).obtenerId();
                
                if (idOrigen < idDestino) {
                    puntoDestino = posiciones.obtenerValor(idDestino);
                    
                    if (puntoDestino != null) {
                        origenAsignado = arbol.estaAsignada(idOrigen);
                        destinoAsignado = arbol.estaAsignada(idDestino);

                        if (origenAsignado && destinoAsignado) {
                            g2.setColor(Color.ORANGE);       
                        } else if (origenAsignado || destinoAsignado) {
                            g2.setColor(Color.LIGHT_GRAY);   
                        } else {
                            g2.setColor(Color.DARK_GRAY);    
                        }
                        g2.drawLine(puntoOrigen.x + desplazamientoX, puntoOrigen.y + desplazamientoY, puntoDestino.x + desplazamientoX, puntoDestino.y + desplazamientoY);
                    }
                }
            }
        }

        // DIBUJAR NODOS (Variables declaradas fuera del ciclo)
        g2.setFont(new Font("Arial", Font.PLAIN, FUENTE_TAMANIO_NODO));
        for (i = 0; i < todas.tamanio(); i++) {
            nodo = todas.dato(i);
            idNodo = nodo.obtenerId();
            posicionNodo = posiciones.obtenerValor(idNodo);
            if (posicionNodo == null) {
                continue;
            }
            
            asignada = nodo.estaAsignada();
            disponible = false;
            
            if (!asignada) {
                vecinos = arbol.obtenerAdyacentes(idNodo);
                if (vecinos != null) {
                    j = 0;
                    // Cambiado el for con break por un while controlado lógicamente (Mandamiento 8)
                    while (!disponible && j < vecinos.tamanio()) {
                        if (vecinos.dato(j).estaAsignada()) {
                            disponible = true;
                        }
                        j++;
                    }
                }
            }

            if (asignada) {
                g2.setColor(Color.ORANGE); 
            } else if (disponible) {
                g2.setColor(new Color(240, 230, 140)); 
            } else {
                g2.setColor(Color.GRAY); 
            }
            dibujoX = posicionNodo.x + desplazamientoX;
            dibujoY = posicionNodo.y + desplazamientoY;
            g2.fillOval(dibujoX - RADIO_NODO, dibujoY - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);
            
            if (disponible) {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1.5f));
            }
            g2.drawOval(dibujoX - RADIO_NODO, dibujoY - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);
            g2.setColor(Color.WHITE);
            nombre = nodo.obtenerNombre();
            anchoNombre = g2.getFontMetrics().stringWidth(nombre);
            g2.drawString(nombre, dibujoX - (anchoNombre / 2), dibujoY + RADIO_NODO + 14);
        }
    }
    
    /**
     * Resuelve qué nodo fue cliqueado de acuerdo a la ubicación del puntero del mouse.
     * Precondiciones: las coordenadas espaciales ya deben estar cargadas en el diccionario.
     * Postcondiciones: calcula mediante Pitágoras la distancia euclídea entre el clic y el centro 
     * de cada círculo. Si es menor o igual al radio (16px), retorna el identificador de dicho 
     * nodo. Caso contrario o ante una lista de nodos vacía, retorna null.
     *
     * @param mouseX         Coordenada X absoluta del clic del mouse.
     * @param mouseY         Coordenada Y absoluta del clic del mouse.
     * @param desplazamientoX Desplazamiento horizontal actual de la cámara.
     * @param desplazamientoY Desplazamiento vertical actual de la cámara.
     * @return El identificador Integer de la pasiva correspondiente, o null si el clic fue al vacío.
     */
    public Integer obtenerNodoEnPosicion(int mouseX, int mouseY, int desplazamientoX, int desplazamientoY) {
        Lista<NodoPasiva> todas = arbol.obtenerTodas();
        Integer idEncontrado = null;
        int i = 0;
        int idNodo = 0;
        int nodoX = 0;
        int nodoY = 0;
        double distancia = 0;
        Point posicionNodo = null;

        if (todas == null) {
            return null;
        }

        // Se reemplazó el ciclo for con return interno por un while (Mandamiento 8)
        while (idEncontrado == null && i < todas.tamanio()) {
            idNodo = todas.dato(i).obtenerId();
            posicionNodo = posiciones.obtenerValor(idNodo);
            if (posicionNodo != null) {
                nodoX = posicionNodo.x + desplazamientoX;
                nodoY = posicionNodo.y + desplazamientoY;
                
                distancia = Math.sqrt(Math.pow(mouseX - nodoX, 2) + Math.pow(mouseY - nodoY, 2));
                if (distancia <= RADIO_NODO) {
                    idEncontrado = idNodo;
                }
            }
            i++;
        }
        return idEncontrado;
    }
}