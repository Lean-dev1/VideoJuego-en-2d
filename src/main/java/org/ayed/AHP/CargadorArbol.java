package org.ayed.AHP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.ayed.tda.grafo.Grafo;

public class CargadorArbol {

    public static Grafo<Pasiva> cargar(String rutaArchivo) {
        Grafo<Pasiva> arbol = new Grafo<>();
        
        // Estructura temporal para relacionar el ID del txt con el objeto en memoria.
        // Restricción del TP: implementar sin colecciones de java.util
        Pasiva[] diccionario = new Pasiva[100]; 

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean leyendoPasivas = false;
            boolean leyendoConexiones = false;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                if (linea.equals("[PASIVAS]")) {
                    leyendoPasivas = true;
                    leyendoConexiones = false;
                    continue;
                } else if (linea.equals("[CONEXIONES]")) {
                    leyendoPasivas = false;
                    leyendoConexiones = true;
                    continue;
                }

                if (leyendoPasivas) {
                    // 1. Aplicar split("\\|") a la línea
                    // 2. Parsear el ID e instanciar el objeto Pasiva
                    // 3. Ejecutar arbol.agregarVertice(nuevaPasiva)
                    // 4. Almacenar temporalmente: diccionario[id] = nuevaPasiva
                } else if (leyendoConexiones) {
                    // 1. Aplicar split(",") a la línea
                    // 2. Obtener los objetos origen y destino del arreglo diccionario
                    // 3. Ejecutar arbol.agregarArista(origen, destino, Grafo.SIN_PESO)
                }
            }
        } catch (IOException e) {
            System.err.println("Error de lectura: " + e.getMessage());
        }

        return arbol;
    }
}
