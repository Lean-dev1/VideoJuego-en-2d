package org.ayed.juego;

import org.ayed.tda.grafo.Grafo;
import org.ayed.tda.iterador.Iterador;
import org.ayed.tda.lista.Lista;
import org.ayed.tda.colaPrioridad.ColaPrioridad;
import org.ayed.tda.comparador.Comparador;
import org.ayed.tda.conjunto.Conjunto;
import java.util.Map;

public class BuscadorCaminos {

   
    private class EstadoNodo {
        Celda celdaActual;
        EstadoNodo pasoAnterior;
        int costoInicial; 
        int costoFinal; 

        public EstadoNodo(Celda celda, EstadoNodo anterior, int g, int f) {
            this.celdaActual = celda;
            this.pasoAnterior = anterior;
            this.costoInicial = g;
            this.costoFinal = f;
        }
    }

    
    private class ComparadorAEstrella implements Comparador<EstadoNodo> {
        @Override
        public int comparar(EstadoNodo n1, EstadoNodo n2) {
            // 1. Primero comparamos por el costo total (F)
            if (n1.costoFinal < n2.costoFinal) {
                return -1; 
            } else if (n1.costoFinal > n2.costoFinal) {
                return 1;  
            }
            
            // 2. Primer Desempate: Heurística (El que esté más cerca visualmente)
            int heuristicaN1 = n1.costoFinal - n1.costoInicial;
            int heuristicaN2 = n2.costoFinal - n2.costoInicial;
            
            if (heuristicaN1 < heuristicaN2) {
                return -1; 
            } else if (heuristicaN1 > heuristicaN2) {
                return 1;
            }
            
            // 3. ¡EL DESEMPATE ABSOLUTO! Rompe el bucle de simetría
            // Si llegamos acá, los dos caminos son matemáticamente idénticos.
            // Obligamos a elegir siempre la fila o columna más chica.
            if (n1.celdaActual.getFila() != n2.celdaActual.getFila()) {
                return Integer.compare(n1.celdaActual.getFila(), n2.celdaActual.getFila());
            } else {
                return Integer.compare(n1.celdaActual.getColumna(), n2.celdaActual.getColumna());
            }
        }
    }

public Iterador<Celda> encontrarCamino(Grafo<Celda> grafo, Celda origen, Celda destino) {
        
        // 1. BYPASS: Usamos el Comparador nativo de Java
        java.util.Comparator<EstadoNodo> comparadorJava = new java.util.Comparator<EstadoNodo>() {
            @Override
            public int compare(EstadoNodo n1, EstadoNodo n2) {
                if (n1.costoFinal < n2.costoFinal) return -1; 
                if (n1.costoFinal > n2.costoFinal) return 1;  
                
                int heuristicaN1 = n1.costoFinal - n1.costoInicial;
                int heuristicaN2 = n2.costoFinal - n2.costoInicial;
                
                if (heuristicaN1 < heuristicaN2) return -1; 
                if (heuristicaN1 > heuristicaN2) return 1;
                
                if (n1.celdaActual.getFila() != n2.celdaActual.getFila()) {
                    return Integer.compare(n1.celdaActual.getFila(), n2.celdaActual.getFila());
                } else {
                    return Integer.compare(n1.celdaActual.getColumna(), n2.celdaActual.getColumna());
                }
            }
        };

        // 2. BYPASS: Usamos PriorityQueue y HashSet nativos de Java
        java.util.PriorityQueue<EstadoNodo> celdasPorVisitar = new java.util.PriorityQueue<>(comparadorJava);
        java.util.HashSet<Celda> celdasVisitadas = new java.util.HashSet<>();

        int distanciaEstimadaInicial = calcularHeuristica(origen, destino);
        EstadoNodo inicio = new EstadoNodo(origen, null, 0, distanciaEstimadaInicial);
        celdasPorVisitar.add(inicio); // add en lugar de agregar

        while (!celdasPorVisitar.isEmpty()) { // isEmpty en lugar de vacio
            
            EstadoNodo estadoActual = celdasPorVisitar.poll(); // poll en lugar de eliminar
            Celda celdaActual = estadoActual.celdaActual;

            if (celdaActual.equals(destino)) {
                return armarCaminoDeRegreso(estadoActual);
            }

            if (celdasVisitadas.contains(celdaActual)) { // contains en lugar de contiene
                continue;
            }

            celdasVisitadas.add(celdaActual); // add en lugar de agregar

            java.util.Map<Celda, Integer> vecinos = grafo.obtenerAdyacentes(celdaActual);
            
            for (java.util.Map.Entry<Celda, Integer> vecino : vecinos.entrySet()) {
                Celda celdaVecina = vecino.getKey();
                int costoDeMoverse = vecino.getValue(); 

                if (!celdasVisitadas.contains(celdaVecina)) {
                    int nuevoCostoG = estadoActual.costoInicial + costoDeMoverse;
                    int nuevaDistanciaEstimada = calcularHeuristica(celdaVecina, destino);
                    int nuevoCostoF = nuevoCostoG + nuevaDistanciaEstimada;

                    EstadoNodo nuevoPaso = new EstadoNodo(celdaVecina, estadoActual, nuevoCostoG, nuevoCostoF);
                    celdasPorVisitar.add(nuevoPaso);
                }
            }
        }
        return null;
    }

    /**
     * Reconstruye la ruta iterando hacia atrás desde el destino hasta el origen.
     */
    private Iterador<Celda> armarCaminoDeRegreso(EstadoNodo estadoFinal) {
        Lista<Celda> rutaFinal = new Lista<>();
        EstadoNodo rastreador = estadoFinal;

        while (rastreador != null) {
            rutaFinal.agregar(rastreador.celdaActual, 0); 
            rastreador = rastreador.pasoAnterior;
        }

        return rutaFinal.iterador();
    }

    /**
     * Calcula la distancia ortogonal (sin diagonales).
     */
    private int calcularHeuristica(Celda a, Celda b) {
        int diferenciaFilas = Math.abs(a.getFila() - b.getFila());
        int diferenciaColumnas = Math.abs(a.getColumna() - b.getColumna());
        return diferenciaFilas + diferenciaColumnas;
    }
}