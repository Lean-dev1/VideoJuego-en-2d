package org.ayed.juego;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.ayed.poe.Personaje;
import org.ayed.poe.Slot;
import org.ayed.poe.arbol.NodoPasiva;
import org.ayed.poe.ItemInventario;
import org.ayed.poe.Catalogo;
import org.ayed.poe.Item;
import org.ayed.tda.lista.Lista;
import org.ayed.tda.iterador.Iterador;
import org.ayed.tda.vector.VectorDinamico;

public class GestorGuardado {

    private static final String RUTA_POR_DEFECTO = "partida_guardada.csv";

    /**
     * PRE: El objeto personaje no debe ser nulo.
     * POST: Delega el guardado del estado del personaje utilizando la ruta por defecto.
     */
    public static void guardarPartida(Personaje personaje) {
        guardarPartida(personaje, RUTA_POR_DEFECTO);
    }

    /**
     * PRE: El objeto personaje no debe ser nulo y nombreArchivo debe ser una cadena válida.
     * POST: Escribe en el archivo de texto todas las estadísticas, equipamiento, inventario, alijo y nodos del árbol del personaje.
     */
    public static void guardarPartida(Personaje personaje, String nombreArchivo) {
        Lista<NodoPasiva> todosLosNodos;
        Iterador<NodoPasiva> iteradorNodos;
        VectorDinamico<ItemInventario> itemsEnAlijo;
        VectorDinamico<ItemInventario> itemsEnMochila;
        ItemInventario itemActual;
        int i;

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(nombreArchivo))) {
            
            escritor.write("[ESTADISTICAS]\n");
            escritor.write("nivel,oro,xp,puntosHabilidad\n");
            escritor.write(personaje.getNivel() + "," + personaje.getOro() + "," + 
                           personaje.getXp() + "," + personaje.getPuntosHabilidad() + "\n\n");

            escritor.write("[EQUIPAMIENTO]\n");
            escritor.write("slot,idItem\n");
            
            if (personaje.getEquipamiento().obtenerItem(Slot.ARMA) != null) {
                escritor.write("ARMA," + personaje.getEquipamiento().obtenerItem(Slot.ARMA).getID() + "\n");
            }
            if (personaje.getEquipamiento().obtenerItem(Slot.CASCO) != null) {
                escritor.write("CASCO," + personaje.getEquipamiento().obtenerItem(Slot.CASCO).getID() + "\n");
            }
            if (personaje.getEquipamiento().obtenerItem(Slot.ARMADURA) != null) {
                escritor.write("ARMADURA," + personaje.getEquipamiento().obtenerItem(Slot.ARMADURA).getID() + "\n");
            }
            escritor.write("\n");

            escritor.write("[INVENTARIO]\n");
            escritor.write("idItem,x,y\n");
            
            itemsEnMochila = personaje.getInventario().ObtenerListaItems();
            if (itemsEnMochila != null) {
                for (i = 0; i < itemsEnMochila.tamanio(); i++) {
                    itemActual = itemsEnMochila.obtener(i);
                    escritor.write(itemActual.getItem().getID() + "," + itemActual.getX() + "," + itemActual.getY() + "\n");
                }
            }
            escritor.write("\n");

            escritor.write("[ALIJO]\n");
            escritor.write("idItem,x,y\n");
            
            itemsEnAlijo = personaje.getAlijo().obtenerPestaña(0).ObtenerListaItems();
            if (itemsEnAlijo != null) {
                for (i = 0; i < itemsEnAlijo.tamanio(); i++) {
                    itemActual = itemsEnAlijo.obtener(i);
                    escritor.write(itemActual.getItem().getID() + "," + itemActual.getX() + "," + itemActual.getY() + "\n");
                }
            }
            escritor.write("\n");

            escritor.write("[ARBOL]\n");
            escritor.write("idNodo\n");
            
            todosLosNodos = personaje.getArbolHabilidades().obtenerTodas();
            if (todosLosNodos != null) {
                iteradorNodos = todosLosNodos.iterador();
                while (iteradorNodos.haySiguiente()) {
                    if (iteradorNodos.dato().estaAsignada()) {
                        escritor.write(iteradorNodos.dato().obtenerId() + "\n");
                    }
                    iteradorNodos.siguiente();
                }
            }
            System.out.println("Partida guardada exitosamente en: " + nombreArchivo);
            
        } catch (IOException excepcionEntradaSalida) {
            System.err.println("Error al guardar: " + excepcionEntradaSalida.getMessage());
        }
    }

    /**
     * PRE: El archivo en la ruta por defecto debe existir y tener un formato válido.
     * POST: Retorna una instancia de Personaje reconstruida a partir del archivo base, o null si hay un error.
     */
    public static Personaje cargarPartida() {
        return cargarPartida(RUTA_POR_DEFECTO);
    }

    /**
     * PRE: El parámetro nombreArchivo debe ser la ruta a un archivo de guardado válido. El archivo Catalogo.csv debe existir en los recursos.
     * POST: Lee y parsea el archivo de guardado, instanciando un Personaje con sus estadísticas, árbol, equipamiento, inventario y alijo. Retorna null ante cualquier fallo.
     */
    public static Personaje cargarPartida(String nombreArchivo) {
        Personaje personajeCargado = new Personaje(300, 300);
        String bloqueActual = "";
        String linea;
        String[] datos;
        int oroGuardado;
        int idNodo;
        int idItemAlijo;
        int xItemAlijo;
        int yItemAlijo;
        int idItemMochila;
        int xItemMochila;
        int yItemMochila;
        Catalogo catalogoLocal;
        Item itemRecuperado;

        catalogoLocal = new Catalogo();
        catalogoLocal.cargarDesdeArchivo("/Catalogo.csv");

        try (BufferedReader lector = new BufferedReader(new FileReader(nombreArchivo))) {
            
            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                
                if (linea.isEmpty() || linea.startsWith("nivel,") || linea.startsWith("slot,") || linea.startsWith("idItem") || linea.startsWith("idNodo")) {
                    continue;
                }

                if (linea.startsWith("[")) {
                    bloqueActual = linea;
                    continue;
                }

                if (bloqueActual.equals("[ESTADISTICAS]")) {
                    datos = linea.split(",");
                    personajeCargado.setNivel(Integer.parseInt(datos[0]));
                    
                    oroGuardado = Integer.parseInt(datos[1]);
                    personajeCargado.modificarOro(oroGuardado - personajeCargado.getOro()); 
                    
                    personajeCargado.setXp(Integer.parseInt(datos[2]));
                    personajeCargado.setPuntosHabilidad(Integer.parseInt(datos[3]));
                } 
                else if (bloqueActual.equals("[ARBOL]")) {
                    idNodo = Integer.parseInt(linea);
                    personajeCargado.aprenderPasiva(idNodo); 
                }
                else if (bloqueActual.equals("[EQUIPAMIENTO]")) {
                    datos = linea.split(",");
                    itemRecuperado = catalogoLocal.obtenerItemPorID(Integer.parseInt(datos[1]));
                    if (itemRecuperado != null) {
                        personajeCargado.getEquipamiento().equipar(Slot.valueOf(datos[0]), itemRecuperado);
                    }
                }
                else if (bloqueActual.equals("[INVENTARIO]")) {
                    datos = linea.split(",");
                    idItemMochila = Integer.parseInt(datos[0]);
                    xItemMochila = Integer.parseInt(datos[1]);
                    yItemMochila = Integer.parseInt(datos[2]);
                    
                    itemRecuperado = catalogoLocal.obtenerItemPorID(idItemMochila);
                    if (itemRecuperado != null) {
                        personajeCargado.getInventario().colocarItem(xItemMochila, yItemMochila, itemRecuperado);
                    }
                }
                else if (bloqueActual.equals("[ALIJO]")) {
                    datos = linea.split(",");
                    idItemAlijo = Integer.parseInt(datos[0]);
                    xItemAlijo = Integer.parseInt(datos[1]);
                    yItemAlijo = Integer.parseInt(datos[2]);
                    
                    itemRecuperado = catalogoLocal.obtenerItemPorID(idItemAlijo);
                    if (itemRecuperado != null) {
                        personajeCargado.getAlijo().obtenerPestaña(0).colocarItem(xItemAlijo, yItemAlijo, itemRecuperado);
                    }
                }
            }
            return personajeCargado;
            
        } catch (IOException | NumberFormatException excepcionLectura) {
            System.err.println("Error al leer " + nombreArchivo + ": " + excepcionLectura.getMessage());
            return null;
        }
    }
}   