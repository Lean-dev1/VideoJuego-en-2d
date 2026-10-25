package org.ayed.poe;

import org.ayed.tda.vector.VectorDinamico;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Catalogo {
    private VectorDinamico<Item> itemsDisponibles;

    public Catalogo() {
        this.itemsDisponibles = new VectorDinamico<>();
    }

    /**
     * Carga el catálogo completo desde un archivo de configuración.
     * <p>
     * <b>PRE:</b> La ruta debe ser válida y accesible.
     * <b>POST:</b> Instancia y almacena polimórficamente todos los ítems leídos en el vector.
     */
    public void cargarDesdeArchivo(String ruta) {
        File archivo;
        String linea;
        String[] datos;
        String nombre;
        String tipo;
        String rareza;
        String tipoLower;
        int id;
        int ancho;
        int alto;
        int vida;
        int dano;
        int armadura;
        Item nuevoItem;

        archivo = new File(ruta);
        
try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                datos = linea.split(",");
                
                id = Integer.parseInt(datos[0].trim());
                ancho = Integer.parseInt(datos[1].trim());
                alto = Integer.parseInt(datos[2].trim());
                nombre = datos[3].trim();
                tipo = datos[4].trim(); 
                rareza = datos[5].trim();
                vida = Integer.parseInt(datos[6].trim());
                dano = Integer.parseInt(datos[7].trim());
                armadura = Integer.parseInt(datos[8].trim());
                
                // Pasamos el NOMBRE a minúsculas para buscar "espada", "arco" o "varita"
                String nombreLower = nombre.toLowerCase();

                // SOLUCIÓN: Ahora evalúa según lo que contiene el NOMBRE del ítem
                if (nombreLower.contains("espada") || nombreLower.contains("daga") || 
                    nombreLower.contains("hacha") || nombreLower.contains("filo") || nombreLower.contains("quebrantahuesos")) {
                    
                    nuevoItem = new Espada(id, ancho, alto, nombre, rareza, dano);
                    
                } else if (nombreLower.contains("arco") || nombreLower.contains("ballesta") || nombreLower.contains("viento")) {
                    
                    nuevoItem = new Arco(id, ancho, alto, nombre, rareza, dano);
                    
                } else if (nombreLower.contains("varita") || nombreLower.contains("baston") || nombreLower.contains("vara")) {
                    
                    nuevoItem = new Varita(id, ancho, alto, nombre, rareza, dano);
                    
                } else {
                    // Aquí siguen cayendo Casco, Armadura, Reliquias, etc.
                    nuevoItem = new Item(id, ancho, alto, nombre, tipo, rareza);
                    nuevoItem.setDañoExtra(dano);
                }

                nuevoItem.setVidaExtra(vida);
                nuevoItem.setArmaduraExtra(armadura);

                itemsDisponibles.agregar(nuevoItem);
            }
        } catch (Exception e) {
            System.out.println("Error al cargar el catálogo: " + e.getMessage());
        }
    }

    /**
     * Retorna una copia polimórfica filtrada de todos los ítems pertenecientes a un Slot específico.
     * <p>
     * <b>PRE:</b> El slot buscado no debe ser nulo.
     * <b>POST:</b> Devuelve un VectorDinamico con los ítems que coinciden con el tipo del Slot.
     */
    public VectorDinamico<Item> obtenerItemsPorSlot(Slot slotBuscado) {
        VectorDinamico<Item> filtrados;
        Item actual;
        Item clonado;
        int i;

        filtrados = new VectorDinamico<>();
        i = 0;

        while (i < itemsDisponibles.tamanio()) {
            actual = itemsDisponibles.obtener(i);
            
            // Comparamos el tipo del ítem contra el nombre del Enum (ej: "CASCO", "ARMADURA", "ARMA")
            if (actual.getTipo().equalsIgnoreCase(slotBuscado.name())) {
                clonado = clonarItemPolimorfico(actual);
                if (clonado != null) {
                    filtrados.agregar(clonado);
                }
            }
            i++;
        }

        return filtrados;
    }

    /**
     * Auxiliar privado para encapsular la clonación polimórfica de ítems.
     */
    private Item clonarItemPolimorfico(Item actual) {
        Item copia;
        Arma armaOriginal;

        copia = null;

        if (actual instanceof Arma) {
            armaOriginal = (Arma) actual;
            if (actual instanceof Espada) {
                copia = new Espada(actual.getID(), actual.anchoItem(), actual.altoItem(), actual.getNombre(), actual.getRareza(), armaOriginal.getDañoBase());
            } else if (actual instanceof Arco) {
                copia = new Arco(actual.getID(), actual.anchoItem(), actual.altoItem(), actual.getNombre(), actual.getRareza(), armaOriginal.getDañoBase());
            } else if (actual instanceof Varita) {
                copia = new Varita(actual.getID(), actual.anchoItem(), actual.altoItem(), actual.getNombre(), actual.getRareza(), armaOriginal.getDañoBase());
            }
            
            if (copia != null) {
                copia.setVidaExtra(actual.getVidaExtra());
                copia.setArmaduraExtra(actual.getArmaduraExtra());
            }
        } else {
            copia = new Item(actual.getID(), actual.anchoItem(), actual.altoItem(), actual.getNombre(), actual.getTipo(), actual.getRareza());
            copia.setVidaExtra(actual.getVidaExtra());
            copia.setDañoExtra(actual.getDañoExtra());
            copia.setArmaduraExtra(actual.getArmaduraExtra());
        }

        return copia;
    }

    // Mantenemos este método por compatibilidad si se usa en otras partes del TP
    public Item obtenerItemPorID(int id) {
        Item resultado;
        Item actual;
        int i;

        resultado = null;
        i = 0;

        while (resultado == null && i < itemsDisponibles.tamanio()) {
            actual = itemsDisponibles.obtener(i);
            if (actual.getID() == id) {
                resultado = clonarItemPolimorfico(actual);
            }
            i++;
        }
        return resultado;
    }
}