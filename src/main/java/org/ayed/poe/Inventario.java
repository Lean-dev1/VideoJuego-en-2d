package org.ayed.poe;

import org.ayed.tda.matriz.Matriz;
import org.ayed.tda.vector.VectorDinamico;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

public class Inventario {
    /** atributos */
    private int fil;
    private int col;
    private Matriz<Item> grilla;
    private VectorDinamico<ItemInventario> listaItems;

    /**
     * Constructor del Inventario.
     * <p>
     * <b>Pre:</b> ancho y alto deben ser mayores a 0.
     * <b>Post:</b> Se crea un inventario vacío con las dimensiones especificadas y un catálogo inicializado.
     *
     * @param ancho El ancho de la grilla del inventario.
     * @param alto  La altura de la grilla del inventario.
     * @throws IllegalArgumentException si el ancho o la altura son menores a 1.
     */
    public Inventario(int ancho, int alto) {
        if (ancho < 1 || alto < 1) {
            throw new IllegalArgumentException("Dimensiones invalidas");
        }

        this.col = ancho;
        this.fil = alto;

        this.grilla = new Matriz<>(alto, ancho);
        this.listaItems = new VectorDinamico<>();
    }

    /**
     * Obtiene el item en una coordenada específica.
     * <p>
     * <b>Pre:</b> x e y deben estar dentro de los límites del inventario.
     * <b>Post:</b> Devuelve el Item en la posición o null si no hay nada.
     */
    public Item obtenerItem(int x, int y) {
        if (!validarDimensiones(x,y)) {
            throw new InventarioFueraDeLimitesException("Posición fuera de matriz");
        }
        return grilla.elemento(y, x);
    }
    public VectorDinamico<ItemInventario> ObtenerListaItems() {
    	return listaItems;
    }

/**
     * Verifica si un item con el ID especificado existe en el inventario.
     * PRE: Ninguna.
     * POST: Retorna verdadero si el item se encuentra almacenado, falso en caso contrario.
     * @param id El identificador único del item a buscar.
     * @return Booleano que indica la existencia del item.
     */
    public boolean tieneItem(int id) {
        boolean itemEncontrado;
        itemEncontrado = false;
        
        if (buscarPosItemPorID(id) != -1) {
            itemEncontrado = true;
        }
        
        return itemEncontrado;
    }

    /**
     * Coloca un item en una coordenada específica (x, y).
     * <p>
     * <b>Pre:</b> El item no debe ser nulo, su ID no debe existir y debe haber espacio suficiente.
     * <b>Post:</b> El item queda registrado en la grilla y en la lista de items.
     */
    public void colocarItem(int x, int y, Item item) {
        if (!validarDimensiones(x,y)) {
            throw new InventarioFueraDeLimitesException("Posición fuera de matriz");
        }
        if (!cabeItem(x, y, item)) {
            throw new InventarioFueraDeLimitesException("El item no cabe ahi");
        }
        if (buscarPosItemPorID(item.getID()) != -1) {
            throw new ItemDuplicadoException("ya tienes ese item en tu inventario");
        }
        if (!hayEspacioLibre(y,x,item)) {
            throw new EspacioOcupadoException("Espacio ocupado");
        } else {
            insertarItem(x,y,item);
            listaItems.agregar(new ItemInventario(item, x, y));
        }
    }

    /**
     * Coloca automáticamente un item en el primer espacio disponible.
     * <p>
     * <b>Pre:</b> El item no debe ser nulo y su ID no debe existir previamente.
     * <b>Post:</b> El item se coloca en la primera posición libre encontrada (escaneo por columnas).
     */
    public void colocarItem(Item item) {
        if (buscarPosItemPorID(item.getID()) != -1) {
            throw new ItemDuplicadoException("ya tienes ese item en tu inventario");
        }
        int i = 0; 
        int j = 0;
        boolean hayLugar = false;
        while (j < col && !hayLugar) {
            i = 0; 
            while (i < fil && !hayLugar) {
                if (grilla.elemento(i, j) == null) {
                    if (cabeItem(j, i, item) && hayEspacioLibre(i, j, item)) {
                        hayLugar = true;
                    } else {
                        i++; 
                    }
                } else {
                    i++; 
                }
            }
            if (!hayLugar) {
                j++; 
            }
        }
        if (!hayLugar) {
            throw new InventarioLlenoException();
        }
        insertarItem(j, i, item);
        listaItems.agregar(new ItemInventario(item, j, i));
    }

    /**
     * Elimina un item por su ID.
     * <p>
     * <b>Pre:</b> El ID debe existir en el inventario.
     * <b>Post:</b> El item es removido de todas las celdas de la grilla y de la lista.
     */
    public Item eliminarItem(int id) {
        int pos = buscarPosItemPorID(id);
        if (pos == -1) {
            throw new ItemNoEncontradoException("No se encontro ningun item con ese ID");
        }
        ItemInventario iteminv = listaItems.obtener(pos);
        borrarItemGrilla(iteminv.getX(), iteminv.getY(), iteminv.getItem());
        listaItems.eliminar(pos);
        return iteminv.getItem();
    }

    /**
     * Elimina el item que ocupa una coordenada específica.
     * <p>
     * <b>Pre:</b> (x,y) deben ser coordenadas válidas y no estar vacías.
     * <b>Post:</b> Se elimina el item completo que ocupaba esa celda.
     */
    public Item eliminarItem(int x, int y) {
        if (!validarDimensiones(x,y)) {
            throw new InventarioFueraDeLimitesException("Posicion fuera del limite");
        }
        if (grilla.elemento(y, x) == null) {
            throw new ItemNoEncontradoException("Nada en esa posición");
        }
        int pos = buscarPosItemPorID(grilla.elemento(y, x).getID());
        ItemInventario iteminv = listaItems.obtener(pos);

        borrarItemGrilla(iteminv.getX(), iteminv.getY(), iteminv.getItem());
        listaItems.eliminar(pos);
        return iteminv.getItem();
    }

    /**
     * Transfiere una lista de items al inventario como una única transacción atómica.
     * <p>
     * <b>Pre:</b> La lista de items no debe contener IDs duplicados entre sí ni con el inventario actual.
     * <b>Post:</b> Si hay espacio para todos, se insertan. Si uno falla, el inventario vuelve a su estado original.
     */
    public void transferirItems(VectorDinamico<Item> items) {
        validarDuplicadosEnLista(items);
        int cantidadInsertados = 0;
        boolean operacionExitosa = true;
        RuntimeException errorCapturado = null;
        int i = 0;
        
        while (operacionExitosa && i < items.tamanio()) {
            Item item = items.obtener(i);
            try {
                colocarItem(item);
                ++cantidadInsertados;
            }
            catch (ItemDuplicadoException | InventarioLlenoException e) {
                operacionExitosa = false;
                errorCapturado = e;
            }
            ++i;
        }
        
        if (!operacionExitosa) {
            revertirCambios(cantidadInsertados, items);
            throw errorCapturado;
        }
    }

    /**
     * Guarda el estado actual del inventario en un archivo.
     * <p>
     * <b>Pre:</b> La ruta debe ser válida y tener permisos de escritura.
     * <b>Post:</b> Se genera un archivo con las dimensiones y la lista de items (ID, x, y, ancho, alto).
     */
    public void guardar(String ruta) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta))) {
            escribirDimensiones(writer);
            escribirItems(writer);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar archivo");
        }
    }
    
    /**
     * Carga el estado del inventario desde un archivo.
     * <p>
     * <b>Pre:</b> El archivo debe existir y seguir el formato de guardado.
     * <b>Post:</b> Se reconstruye el inventario. Si falla, el inventario se reinicia vacío.
     */
    public void cargar(String filepath) {
        File archivo = new File(filepath);

        if (!archivo.exists()) {
            throw new ArchivoNoExistenteException("El archivo no existe");
        }

        try (Scanner scanner = new Scanner(archivo)) {

            int[] dimensiones = leerDimensiones(scanner);
            int nuevasFil = dimensiones[0];
            int nuevasCol = dimensiones[1];

            Matriz<Item> nuevaGrilla = new Matriz<>(nuevasFil, nuevasCol);
            VectorDinamico<ItemInventario> nuevaLista = new VectorDinamico<>();

            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine().trim();
                if (linea.isEmpty()) continue;

                ItemInventario itemInv = parsearItem(linea);

                insertarItemEnEstructurasTemporales(
                    nuevaGrilla,
                    nuevaLista,
                    itemInv.getX(),
                    itemInv.getY(),
                    itemInv.getItem()
                );
            }

            this.fil = nuevasFil;
            this.col = nuevasCol;
            this.grilla = nuevaGrilla;
            this.listaItems = nuevaLista;

        } catch (Exception e) {
            this.grilla = new Matriz<>(0, 0);
            this.listaItems = new VectorDinamico<>();
        }
    }

    public int getFil() {
        return fil;
    }

    public int getCol() {
        return col;
    }
    /**Devuelve true si las coordenadas están dentro del rango (0..col-1) y (0..fil-1).
     */
    private boolean validarDimensiones(int x, int y) {
        return !(x < 0 || y < 0 || x >= col || y >= fil);
    }

    /** Devuelve true si el bloque de celdas requerido por el item está libre de otros items.
     */
    private boolean hayEspacioLibre(int i, int j, Item item) {
        boolean libre = true;
        int x = 0;
        int y = 0;
        while (libre && y < item.altoItem()) {
            x = 0;
            while (libre && x < item.anchoItem()) {
                if (grilla.elemento(i + y, j + x) != null) {
                    libre = false;
                }
                x++;
            }
            y++;
        }
        return libre;
    }

    /**
     Devuelve true si el tamaño del item no excede los bordes de la grilla desde la posición (x,y).
     */
    private boolean cabeItem(int x, int y, Item item) {
        return !(y + item.altoItem() > fil || x + item.anchoItem() > col);
    }
    
    /**
     * <b>Pre:</b> El item debe caber y el espacio estar libre.
     * <b>Post:</b> Ocupa las celdas correspondientes en la grilla con la referencia al item.
     */
    private void insertarItem(int x, int y, Item item) {
        for (int i = 0; i < item.altoItem(); i++) {
            for (int j = 0; j < item.anchoItem(); j++) {
                grilla.asignar(y + i, x + j, item);
            }
        }
    }

    /**
     * <b>Pre:</b> El item debe existir en la grilla en la posición (x,y).
     * <b>Post:</b> Setea a null todas las celdas que ocupaba el item.
     */
    private void borrarItemGrilla(int x, int y, Item item) {
        for (int i = 0; i < item.altoItem(); i++) {
            for (int j = 0; j < item.anchoItem(); j++) {
                grilla.asignar(y + i, x + j, null);
            }
        }
    }

    /**
   Devuelve el índice en la lista del item con el ID buscado, o -1 si no existe.
     */
    private int buscarPosItemPorID(int id) {
        int i = 0;
        int posicion = -1;
        boolean encontrado = false;
        while (i < listaItems.tamanio() && !encontrado) {
            if (listaItems.obtener(i).getItem().getID() == id) {
                encontrado = true;
                posicion = i; 
            } else {
                i++;
            }
        }
        return posicion;
    }
    private String formatearItem(ItemInventario itemInv) {
        Item item = itemInv.getItem();

        // Guardamos todo en el mismo orden: 
        // ID, X, Y, Ancho, Alto, Nombre, Tipo, Rareza, Vida, Daño, Armadura
        return item.getID() + "," +
               itemInv.getX() + "," +
               itemInv.getY() + "," +
               item.anchoItem() + "," +
               item.altoItem() + "," +
               item.getNombre() + "," +
               item.getTipo() + "," +
               item.getRareza() + "," +
               item.getVidaExtra() + "," +
               item.getDañoExtra() + "," +
               item.getArmaduraExtra();
    }

    /**
     * <b>Pre:</b> Lista de items no nula.
     * <b>Post:</b> Lanza excepción si encuentra dos items con el mismo ID en la lista.
     */
    private void validarDuplicadosEnLista(VectorDinamico<Item> items) {
        int i = 0;
        boolean hayDuplicado = false;
        while (i < items.tamanio() && !hayDuplicado) {
            int j = i + 1;
            while (j < items.tamanio() && !hayDuplicado) {
                if (items.obtener(i).getID() == items.obtener(j).getID()) {
                    hayDuplicado = true;
                }
                j++;
            }
            i++;
        }
        if (hayDuplicado) {
            throw new ItemDuplicadoException("La lista contiene IDs duplicados");
        }
    }

    /**
     * <b>Pre:</b> cant es el número de items que se llegaron a insertar exitosamente.
     * <b>Post:</b> Elimina los últimos 'cant' items agregados a la lista para volver al estado anterior.
     */
    private void revertirCambios(int cant, VectorDinamico<Item> items) {
        for (int i = 0; i < cant; i++) {
            ItemInventario ultimo = listaItems.obtener(listaItems.tamanio() - 1);
            eliminarItem(ultimo.getItem().getID());
        }
    }

    /**
     * <b>Pre:</b> grillaTemp y listaTemp deben estar inicializadas.
     * <b>Post:</b> Carga el item en las estructuras temporales usadas durante el proceso de carga de archivo.
     */
    private void insertarItemEnEstructurasTemporales(
            Matriz<Item> grillaTemp,
            VectorDinamico<ItemInventario> listaTemp,
            int x,
            int y,
            Item item) {
        for (int i = 0; i < item.altoItem(); i++) {
            for (int j = 0; j < item.anchoItem(); j++) {
                grillaTemp.asignar(y + i, x + j, item);
            }
        }
        listaTemp.agregar(new ItemInventario(item, x, y));
    }
    private int[] leerDimensiones(Scanner scanner) {
        int fil = Integer.parseInt(scanner.nextLine());
        int col = Integer.parseInt(scanner.nextLine());

        return new int[]{fil, col};
    }
    private ItemInventario parsearItem(String linea) {
        String[] partes = linea.split(",");

        // Exigimos que la línea tenga los 11 datos
        if (partes.length < 11) {
            throw new RuntimeException("Formato de guardado inválido o antiguo");
        }

        // Aplicamos .trim() por seguridad
        int id = Integer.parseInt(partes[0].trim());
        int x = Integer.parseInt(partes[1].trim());
        int y = Integer.parseInt(partes[2].trim());
        int ancho = Integer.parseInt(partes[3].trim());
        int alto = Integer.parseInt(partes[4].trim());
        
        String nombre = partes[5].trim();
        String tipo = partes[6].trim();
        String rareza = partes[7].trim();
        int vidaExtra = Integer.parseInt(partes[8].trim());
        int danoExtra = Integer.parseInt(partes[9].trim());
        int armaduraExtra = Integer.parseInt(partes[10].trim());

        Item itemReconstruido;
        String tipoLower = tipo.toLowerCase();

        // Reconstruimos el objeto guiándonos 100% por el TIPO
        if (tipoLower.equals("espada") || tipoLower.equals("daga") || 
            tipoLower.equals("hacha") || tipoLower.equals("mazo")) {
            
            itemReconstruido = new Espada(id, ancho, alto, nombre, rareza, danoExtra);
            
        } else if (tipoLower.equals("arco") || tipoLower.equals("ballesta")) {
            
            itemReconstruido = new Arco(id, ancho, alto, nombre, rareza, danoExtra);
            
        } else if (tipoLower.equals("varita") || tipoLower.equals("baston")) {
            
            itemReconstruido = new Varita(id, ancho, alto, nombre, rareza, danoExtra);
            
        } else {
            // Si es Casco, Armadura, Reliquia, etc.
            itemReconstruido = new Item(id, ancho, alto, nombre, tipo, rareza);
            itemReconstruido.setDañoExtra(danoExtra); // Por si daño no es 0
        }

        // Seteamos los demás stats a todos
        itemReconstruido.setVidaExtra(vidaExtra);
        itemReconstruido.setArmaduraExtra(armaduraExtra);

        return new ItemInventario(itemReconstruido, x, y);
    }
    private void escribirDimensiones(BufferedWriter writer) throws IOException {
        writer.write(String.valueOf(fil));
        writer.newLine();
        writer.write(String.valueOf(col));
        writer.newLine();
    }
    private void escribirItems(BufferedWriter writer) throws IOException {
        for (int i = 0; i < listaItems.tamanio(); i++) {
            writer.write(formatearItem(listaItems.obtener(i)));
            writer.newLine();
        }
    }
    /**
     * Vacía por completo el inventario.
     * <p>
     * <b>Pre:</b> -
     * <b>Post:</b> Todas las celdas de la grilla pasan a estar vacías (null) 
     * y se eliminan todos los registros del vector de ítems, 
     * manteniendo las dimensiones originales.
     */
    public void vaciar() {
        int i;
        int j;
        for (i = 0; i < this.fil; i++) {
            for (j = 0; j < this.col; j++) {
                this.grilla.asignar(i, j, null);
            }
        }
        // Reiniciamos el vector dinámico asignándole una nueva instancia vacía
        this.listaItems = new VectorDinamico<>();
    }

}