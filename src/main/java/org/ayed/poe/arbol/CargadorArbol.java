package org.ayed.poe.arbol;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Carga un ArbolHabilidades desde un archivo de texto.
 *
 * Formato del archivo:
 * <pre>
 *   # Líneas que comienzan con # son comentarios
 *   PUNTOS 20
 *   NODO 1 Vida Aumentada
 *   NODO 2 Armadura Aumentada
 *   ESTADISTICA 1 vida 10
 *   ESTADISTICA 2 armadura 15
 *   ARISTA 1 2
 *   INICIO 1
 * </pre>
 *
 * PUNTOS      → puntos iniciales del árbol
 * NODO        → id y nombre de la pasiva
 * ESTADISTICA → id de la pasiva, nombre del stat y valor numérico
 * ARISTA      → conexión entre dos pasivas por id
 * INICIO      → id del nodo de inicio (asignado sin costo)
 */
public class CargadorArbol {

    private static final String COMENTARIO = "#";
    private static final String PUNTOS = "PUNTOS";
    private static final String NODO = "NODO";
    private static final String ESTADISTICA = "ESTADISTICA";
    private static final String ARISTA = "ARISTA";
    private static final String INICIO = "INICIO";

    /**
     * Carga un ArbolHabilidades desde un archivo de texto.
     *
     * @param ruta Ruta al archivo de texto.
     * @return el árbol cargado con todos los nodos, conexiones y puntos.
     * @throws ExcepcionArbolHabilidades si el archivo no existe o tiene un formato inválido.
     */
    public static ArbolHabilidades cargar(String ruta) {
        ArbolHabilidades arbol = null;
        boolean esVacia;
        boolean esComentario;
        try {
            BufferedReader lector = new BufferedReader(new FileReader(ruta));
            String linea = lector.readLine();
            while (linea != null) {
                linea = linea.trim();
                esVacia = linea.isEmpty();
                esComentario = linea.startsWith(COMENTARIO);
                if (!esVacia && !esComentario) {
                    arbol = procesarLinea(linea, arbol);
                }
                linea = lector.readLine();
            }
            lector.close();
        } catch (IOException e) {
            throw new ExcepcionArbolHabilidades("No se pudo leer el archivo: " + ruta);
        }
        if (arbol == null) {
            throw new ExcepcionArbolHabilidades("El archivo está vacío o no tiene puntos definidos.");
        }
        return arbol;
    }

    /**
     * Procesa una línea del archivo y actualiza el árbol.
     * La línea PUNTOS debe ser la primera en el archivo.
     */
    private static ArbolHabilidades procesarLinea(String linea, ArbolHabilidades arbol) {
        String[] partes = linea.split(" ", 3);
        String tipo = partes[0];
        if (tipo.equals(PUNTOS)) {
            int puntos = Integer.parseInt(partes[1]);
            arbol = new ArbolHabilidades(puntos);
        } else if (tipo.equals(NODO)) {
            if (arbol == null) {
                throw new ExcepcionArbolHabilidades("El archivo debe definir PUNTOS antes de los nodos.");
            }
            int id = Integer.parseInt(partes[1]);
            String nombre = partes[2];
            arbol.agregarPasiva(new NodoPasiva(id, nombre));
        } else if (tipo.equals(ESTADISTICA)) {
            if (arbol == null) {
                throw new ExcepcionArbolHabilidades("El archivo debe definir PUNTOS antes de las estadísticas.");
            }
            String[] subPartes = linea.split(" ");
            int id = Integer.parseInt(subPartes[1]);
            String nombreStat = subPartes[2];
            int valor = Integer.parseInt(subPartes[3]);
            NodoPasiva pasiva = arbol.obtenerPasiva(id);
            if (pasiva == null) {
                throw new ExcepcionArbolHabilidades("Estadística para id inexistente: " + id);
            }
            pasiva.agregarEstadistica(nombreStat, valor);
        } else if (tipo.equals(ARISTA)) {
            if (arbol == null) {
                throw new ExcepcionArbolHabilidades("El archivo debe definir PUNTOS antes de las aristas.");
            }
            int idOrigen = Integer.parseInt(partes[1]);
            int idDestino = Integer.parseInt(partes[2].trim());
            arbol.agregarConexion(idOrigen, idDestino);
        } else if (tipo.equals(INICIO)) {
            if (arbol == null) {
                throw new ExcepcionArbolHabilidades("El archivo debe definir PUNTOS antes del inicio.");
            }
            int id = Integer.parseInt(partes[1]);
            arbol.marcarComoInicio(id);
        }
        return arbol;
    }
}
