package org.ayed.juego;

import org.ayed.tda.grafo.Grafo;
import org.ayed.tda.iterador.Iterador;
import org.ayed.tda.matriz.Matriz;
import org.ayed.tda.lista.Lista;
import org.ayed.poe.Personaje;
import org.ayed.poe.Slot;
import org.ayed.poe.Arma;
import org.ayed.poe.Catalogo;
import org.ayed.poe.ArqueroEsqueleto;
import org.ayed.poe.Monstruo;
import org.ayed.poe.OrcoBruto;
import org.ayed.tda.diccionario.Diccionario;
import org.ayed.poe.Item;
import org.ayed.poe.MagoCultista;

public class Mazmorra {
    
    public static final int RELIQUIAS_TOTALES = 5;

    private Matriz<Celda> mapaVisual;
    private Grafo<Celda> grafoIA;
    private Lista<Monstruo> enemigos;
    private Personaje personaje; 
    private Diccionario<Celda, Lista<Item>> itemsEnSuelo;
    private Lista<Celda> celdasReliquias; 
    private Catalogo catalogoItems;
    
    private int reliquiasRecolectadas;
    private boolean partidaGanada;
    private boolean regresarAlEscondite;
    private final int ID_RELIQUIA = 99;

    /**
     * PRE: dimension > 0, personajeHeroe != null, catalogo != null.
     * POST: Inicializa la mazmorra construyendo un mapa accesible con reliquias.
     */
    public Mazmorra(int dimension, Personaje personajeHeroe, Catalogo catalogo) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("La dimension debe ser mayor a 0");
        }
        
        this.reliquiasRecolectadas = 0;
        this.partidaGanada = false;
        this.regresarAlEscondite = false;
        this.personaje = personajeHeroe;
        this.mapaVisual = new Matriz<>(dimension); 
        this.catalogoItems = catalogo;
        
        boolean mapaValido = false;
        
        while (!mapaValido) {
            this.grafoIA = new Grafo<>();
            this.enemigos = new Lista<>();
            this.itemsEnSuelo = new Diccionario<>(100);
            this.celdasReliquias = new Lista<>();

            generarNivelAleatorio();
            construirGrafoNavegacion();
            
            mapaValido = esMapaAccesible();
        }
    }

    private void generarNivelAleatorio() {
        Lista<Celda> celdasCaminables;
        Celda celdaPuerta;
        int indiceReliquia;
        Celda centro;
        Item reliquiaVaal;
        Lista<Item> botinSuelo;
        int i;
        int j;

        for (i = 0; i < mapaVisual.filas(); i++) {
            for (j = 0; j < mapaVisual.columnas(); j++) {
                mapaVisual.asignar(i, j, new Celda(i, j, EfectoCelda.PARED));
            }
        }

        tallarLaberintoDFS(1, 1);
        inyectarPeligrosElementales();

        celdasCaminables = new Lista<>();
        
        for (i = 1; i < mapaVisual.filas() - 1; i++) {
            for (j = 1; j < mapaVisual.columnas() - 1; j++) {
                if (mapaVisual.elemento(i, j).getEfecto() == EfectoCelda.NORMAL) {
                    celdasCaminables.agregar(mapaVisual.elemento(i, j));
                }
            }
        }

        celdaPuerta = mapaVisual.elemento(1, 1);
        celdaPuerta.setEfecto(EfectoCelda.PUERTA);
        eliminarCeldaDeLista(celdasCaminables, celdaPuerta);

        this.personaje.setFila(1);
        this.personaje.setColumna(2);

        for (i = 0; i < RELIQUIAS_TOTALES; i++) {
            indiceReliquia = (int) (Math.random() * celdasCaminables.tamanio());
            centro = obtenerCeldaPorIndice(celdasCaminables, indiceReliquia);
            
            reliquiaVaal = catalogoItems.obtenerItemPorID(ID_RELIQUIA);
            
            if (reliquiaVaal == null) {
                reliquiaVaal = new Item(99, 1, 1, "Reliquia Vaal", "Reliquia", "Legendaria");
            }
            botinSuelo = new Lista<>();
            botinSuelo.agregar(reliquiaVaal);
            
            itemsEnSuelo.agregar(centro, botinSuelo); 
            celdasReliquias.agregar(centro);
            eliminarCeldaDeLista(celdasCaminables, centro); 
        }
    }

    private void tallarLaberintoDFS(int fila, int col) {
        int[][] direcciones = { {-4, 0}, {0, 4}, {4, 0}, {0, -4} };
        int saltoFila;
        int saltoCol;
        int nuevaFila;
        int nuevaCol;
        int i;
        int j;
        int k;

        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                if (fila + i < mapaVisual.filas() - 1 && col + j < mapaVisual.columnas() - 1) {
                    mapaVisual.elemento(fila + i, col + j).setEfecto(EfectoCelda.NORMAL);
                }
            }
        }

        mezclarDirecciones(direcciones); 

        for (i = 0; i < direcciones.length; i++) {
            saltoFila = direcciones[i][0];
            saltoCol = direcciones[i][1];
            
            nuevaFila = fila + saltoFila;
            nuevaCol = col + saltoCol;

            if (nuevaFila > 0 && nuevaFila < mapaVisual.filas() - 1 && 
                nuevaCol > 0 && nuevaCol < mapaVisual.columnas() - 1) {
                
                if (mapaVisual.elemento(nuevaFila, nuevaCol).getEfecto() == EfectoCelda.PARED) {
                    
                    if (saltoFila == -4) { 
                        for (j = 0; j < 3; j++) { 
                            if (col + j < mapaVisual.columnas() - 1) {
                                mapaVisual.elemento(fila - 1, col + j).setEfecto(EfectoCelda.NORMAL); 
                            }
                        }
                    } else if (saltoFila == 4) { 
                        for (j = 0; j < 3; j++) { 
                            if (col + j < mapaVisual.columnas() - 1 && fila + 3 < mapaVisual.filas() - 1) {
                                mapaVisual.elemento(fila + 3, col + j).setEfecto(EfectoCelda.NORMAL); 
                            }
                        }
                    } else if (saltoCol == -4) { 
                        for (k = 0; k < 3; k++) { 
                            if (fila + k < mapaVisual.filas() - 1) {
                                mapaVisual.elemento(fila + k, col - 1).setEfecto(EfectoCelda.NORMAL); 
                            }
                        }
                    } else if (saltoCol == 4) { 
                        for (k = 0; k < 3; k++) { 
                            if (fila + k < mapaVisual.filas() - 1 && col + 3 < mapaVisual.columnas() - 1) {
                                mapaVisual.elemento(fila + k, col + 3).setEfecto(EfectoCelda.NORMAL); 
                            }
                        }
                    }
                    
                    tallarLaberintoDFS(nuevaFila, nuevaCol);
                }
            }
        }
    }

    private void mezclarDirecciones(int[][] dirs) {
        int indiceAzar;
        int[] direccionTemporal;
        int i;

        for (i = 0; i < dirs.length; i++) {
            indiceAzar = (int) (Math.random() * dirs.length);
            direccionTemporal = dirs[i];
            dirs[i] = dirs[indiceAzar];
            dirs[indiceAzar] = direccionTemporal;
        }
    }

    private void construirGrafoNavegacion() {
        Celda celda;
        Celda actual;
        int i;
        int j;

        for (i = 0; i < mapaVisual.filas(); i++) {
            for (j = 0; j < mapaVisual.columnas(); j++) {
                celda = mapaVisual.elemento(i, j);
                if (celda.getEfecto() != EfectoCelda.PARED) {
                    grafoIA.agregarVertice(celda);
                }
            }
        }

        for (i = 0; i < mapaVisual.filas(); i++) {
            for (j = 0; j < mapaVisual.columnas(); j++) {
                actual = mapaVisual.elemento(i, j);
                if (actual.getEfecto() == EfectoCelda.PARED) {
                    continue;
                }

                conectarVecino(actual, i - 1, j); 
                conectarVecino(actual, i + 1, j); 
                conectarVecino(actual, i, j - 1); 
                conectarVecino(actual, i, j + 1); 
            }
        }
    }

    /**
     * PRE: cantidad > 0.
     * POST: Ubica de forma aleatoria la cantidad especificada de monstruos en celdas transitables.
     */
    public void generarMonstruos(int cantidad) {
        int generados = 0;
        int limiteFilas = mapaVisual.filas() - 2;
        int limiteColumnas = mapaVisual.columnas() - 2;
        int filaAleatoria;
        int colAleatoria;
        double azar;
        Monstruo nuevoMonstruo;

        while (generados < cantidad) {
            filaAleatoria = (int) (Math.random() * limiteFilas) + 1;
            colAleatoria = (int) (Math.random() * limiteColumnas) + 1;
            
            if (mapaVisual.elemento(filaAleatoria, colAleatoria).getEfecto() != EfectoCelda.PARED) {
                azar = Math.random();
                if (azar < 0.50) {
                    nuevoMonstruo = new OrcoBruto(filaAleatoria, colAleatoria);
                } 
                else if (azar < 0.82) {
                    nuevoMonstruo = new ArqueroEsqueleto(filaAleatoria, colAleatoria);
                } 
                else {
                    nuevoMonstruo = new MagoCultista(filaAleatoria, colAleatoria);
                }
                
                this.enemigos.agregar(nuevoMonstruo);
                generados++;
            }
        }
    }

    private void conectarVecino(Celda origen, int filaVecino, int colVecino) {
        Celda destino;
        int pesoArista;

        if (filaVecino >= 0 && filaVecino < mapaVisual.filas() && 
            colVecino >= 0 && colVecino < mapaVisual.columnas()) {
            
            destino = mapaVisual.elemento(filaVecino, colVecino);
            
            if (destino.getEfecto() != EfectoCelda.PARED) {
                if (destino.getEfecto() == EfectoCelda.NORMAL) {
                    pesoArista = 1;
                } else {
                    pesoArista = 10;
                }
                
                try {
                    grafoIA.agregarArista(origen, destino, pesoArista);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * PRE: accion != null.
     * POST: Ejecuta secuencialmente las etapas del turno (movimiento, trampas, botín, ataque e IA de enemigos).
     */
    public void ejecutarTurnoGlobal(AccionJugador accion) {
        procesarMovimientoJugador(accion);
        procesarEfectosYObjetosSuelo();
        procesarAtaqueJugador(accion);
        procesarTurnoMonstruos();
    }

    private void procesarMovimientoJugador(AccionJugador accion) {
        int dFila = 0;
        int dCol = 0;
        int filaDestino;
        int colDestino;
        Celda celdaDestino;
        
        if (accion == AccionJugador.MOVER_ARRIBA) { dFila = -1; }
        else if (accion == AccionJugador.MOVER_ABAJO) { dFila = 1; }
        else if (accion == AccionJugador.MOVER_IZQUIERDA) { dCol = -1; }
        else if (accion == AccionJugador.MOVER_DERECHA) { dCol = 1; }

        filaDestino = personaje.getFila() + dFila;
        colDestino = personaje.getColumna() + dCol;
        celdaDestino = mapaVisual.elemento(filaDestino, colDestino);

        if (celdaDestino.getEfecto() != EfectoCelda.PARED) {
            personaje.moverEnMazmorra(dFila, dCol);
        }
    }

    private void procesarEfectosYObjetosSuelo() {
        Celda celdaActual = obtenerCeldaPersonaje();
        Lista<Item> itemsAca;
        Iterador<Item> itBotin;
        Item itemDelSuelo;

        if (celdaActual.getEfecto() == EfectoCelda.PUERTA) {
            this.regresarAlEscondite = true;
        } else {
            if (celdaActual.getEfecto() == EfectoCelda.INCENDIADA) {
                personaje.recibirDaño(20);
            } else if (celdaActual.getEfecto() == EfectoCelda.ELECTRIFICADA) {
                personaje.recibirDaño(10);
            }

            itemsAca = itemsEnSuelo.obtenerValor(celdaActual);
            
            if (itemsAca != null && !itemsAca.vacio()) {
                itBotin = itemsAca.iterador();
                while (itBotin.haySiguiente()) {
                    itemDelSuelo = itBotin.dato();
                    if (itemDelSuelo == null) {
                        itBotin.eliminar(); 
                        continue;          
                    }
                    
                    if (itemDelSuelo.getID() == ID_RELIQUIA) {
                        reliquiasRecolectadas++;
                        eliminarCeldaDeLista(celdasReliquias, celdaActual);
                        itBotin.eliminar(); 
                        
                        if (reliquiasRecolectadas >= RELIQUIAS_TOTALES) {
                            this.partidaGanada = true;
                        }
                    } else {
                        try {
                            personaje.getInventario().colocarItem(itemDelSuelo);
                            itBotin.eliminar(); 
                        } catch (org.ayed.poe.ItemDuplicadoException e) {
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }
    private void procesarAtaqueJugador(AccionJugador accion) {
        Item posibleArma;
        Arma arma;
        Lista<Celda> area;

        if (accion == AccionJugador.ATACAR) {
            System.out.println("\n--- [DEBUG] NUEVO INTENTO DE ATAQUE ---");
            posibleArma = personaje.getEquipamiento().obtenerItem(Slot.ARMA);
            if (posibleArma != null && posibleArma instanceof Arma) {
                arma = (Arma) posibleArma;
                
                Celda celdaPj = obtenerCeldaPersonaje();
                // Calculamos el área
                area = arma.obtenerAreaImpacto(celdaPj, personaje.getDireccion());
                
                int dañoTotal = personaje.getDañoTotal();
                aplicarDañoEnArea(area, dañoTotal, celdaPj);
            }
            limpiarMonstruosMuertos();                 
        }
    }

    public int calcularDañoFinal(int dañoOriginal, Celda celdaAtacante, Celda celdaDefensor) {
        double multiplicadorAtacante = 1.0;
        double multiplicadorDefensor = 1.0;

        if (celdaAtacante.getEfecto() == EfectoCelda.CONGELADA) {
            multiplicadorAtacante = 0.1; 
        }
        if (celdaDefensor.getEfecto() == EfectoCelda.ELECTRIFICADA) {
            multiplicadorDefensor = 1.5; 
        }

        return (int) (dañoOriginal * multiplicadorAtacante * multiplicadorDefensor);
    }

    private void limpiarMonstruosMuertos() {
        Iterador<Monstruo> iteradorEnemigos = enemigos.iterador();
        Monstruo monstruo;
        int oroBase;
        int oroExtra;
        int oroTotalFinal;

        while (iteradorEnemigos.haySiguiente()) {
            monstruo = iteradorEnemigos.dato();
            
            if (!monstruo.estaVivo()) {
                this.personaje.ganarExperiencia(monstruo.getXpOtorgada());
                
                oroBase = (int) (Math.random() * 26) + 15; 
                oroExtra = this.personaje.getItemsExtraPorDrop() * 10; 
                oroTotalFinal = oroBase + oroExtra;
                
                this.personaje.modificarOro(oroTotalFinal);
                iteradorEnemigos.eliminar(); 
            } else {
                iteradorEnemigos.siguiente();
            }
        }
    }

    private void aplicarDañoEnArea(Lista<Celda> area, int dañoBase, Celda celdaAtacante) {
        Iterador<Monstruo> itEnemigos = enemigos.iterador();
        Iterador<Celda> itArea;
        Monstruo m;
        Celda c;
        Celda celdaDefensor;
        int dañoReal;
        boolean impactoRegistrado;

        while (itEnemigos.haySiguiente()) {
            m = itEnemigos.dato();
            itArea = area.iterador();
            impactoRegistrado = false;
            
            while (itArea.haySiguiente() && !impactoRegistrado) {
                c = itArea.dato();
                if (m.getFila() == c.getFila() && m.getColumna() == c.getColumna()) {
                    celdaDefensor = mapaVisual.elemento(m.getFila(), m.getColumna());
                    dañoReal = calcularDañoFinal(dañoBase, celdaAtacante, celdaDefensor);
                    m.recibirDaño(dañoReal);
                    impactoRegistrado = true; 
                }
                itArea.siguiente();
            }
            itEnemigos.siguiente();
        }
        limpiarMonstruosMuertos();
    }

    public Grafo<Celda> getGrafoIA() { 
        return this.grafoIA; 
    }

    private void procesarTurnoMonstruos() {
        Iterador<Monstruo> iteradorEnemigos = enemigos.iterador();
        Monstruo monstruo;
        
        while (iteradorEnemigos.haySiguiente()) {
            monstruo = iteradorEnemigos.dato();
            monstruo.ejecutarIA(this, personaje); 
            iteradorEnemigos.siguiente();
        }
    }

    /**
     * PRE: Las estructuras celdasReliquias y grafoIA deben estar inicializadas.
     * POST: Devuelve la lista ordenada de celdas que representa el camino óptimo hacia la reliquia más cercana.
     */
    public Lista<Celda> obtenerRutaGPS() {
        Lista<Celda> rutaGPS = new Lista<>();
        Celda posJugador;
        Celda reliquiaMasCercana = null;
        int distanciaMinima = Integer.MAX_VALUE;
        Iterador<Celda> itReliquias;
        int dist;
        BuscadorCaminos buscador;
        Iterador<Celda> caminoIt;

        if (!celdasReliquias.vacio() && !partidaGanada) {
            posJugador = obtenerCeldaPersonaje();
            itReliquias = celdasReliquias.iterador();
            
            while (itReliquias.haySiguiente()) {
                dist = distanciaGeometrica(posJugador, itReliquias.dato());
                if (dist < distanciaMinima) {
                    distanciaMinima = dist;
                    reliquiaMasCercana = itReliquias.dato();
                }
                itReliquias.siguiente();
            }

            if (reliquiaMasCercana != null) {
                buscador = new BuscadorCaminos();
                caminoIt = buscador.encontrarCamino(grafoIA, posJugador, reliquiaMasCercana);
                
                while (caminoIt != null && caminoIt.haySiguiente()) {
                    rutaGPS.agregar(caminoIt.dato());
                    caminoIt.siguiente();
                }
            }
        }
        return rutaGPS;
    }

    private int distanciaGeometrica(Celda a, Celda b) { 
        return Math.abs(a.getFila() - b.getFila()) + Math.abs(a.getColumna() - b.getColumna());
    }
    
    public Matriz<Celda> getMapaVisual() { return mapaVisual; }
    public Lista<Monstruo> getEnemigos() { return enemigos; }
    public Diccionario<Celda, Lista<Item>> getItemsEnSuelo() { return itemsEnSuelo; }
    
    public Celda obtenerCeldaPersonaje() {
        return mapaVisual.elemento(personaje.getFila(), personaje.getColumna());
    }

    private Celda obtenerCeldaPorIndice(Lista<Celda> lista, int indice) {
        Iterador<Celda> it = lista.iterador();
        int cont = 0;
        Celda c = null;
        while(it.haySiguiente() && cont <= indice) {
            c = it.dato();
            it.siguiente();
            cont++;
        }
        return c;
    }

    private void inyectarPeligrosElementales() {
        Celda actual;
        double azar;
        int i;
        int j;

        for (i = 1; i < mapaVisual.filas() - 1; i++) {
            for (j = 1; j < mapaVisual.columnas() - 1; j++) {
                actual = mapaVisual.elemento(i, j);
                if (actual.getEfecto() == EfectoCelda.NORMAL) {
                    azar = Math.random();
                    if (azar < 0.05) { actual.setEfecto(EfectoCelda.INCENDIADA); }
                    else if (azar < 0.10) { actual.setEfecto(EfectoCelda.CONGELADA); }
                    else if (azar < 0.15) { actual.setEfecto(EfectoCelda.ELECTRIFICADA); }
                }
            }
        }
    }

    private boolean esMapaAccesible() {
        BuscadorCaminos validador = new BuscadorCaminos();
        Celda inicioJugador = obtenerCeldaPersonaje();
        Iterador<Celda> itReliquias = celdasReliquias.iterador();
        boolean mapaEsValido = true;
        Iterador<Celda> camino;

        while (itReliquias.haySiguiente() && mapaEsValido) {
            camino = validador.encontrarCamino(grafoIA, inicioJugador, itReliquias.dato());
            if (camino == null || !camino.haySiguiente()) {
                mapaEsValido = false; 
            } else {
                itReliquias.siguiente();
            }
        }
        return mapaEsValido; 
    }

    private void eliminarCeldaDeLista(Lista<Celda> lista, Celda objetivo) {
        Iterador<Celda> it = lista.iterador();
        Celda actual;
        boolean encontrado = false;

        while (it.haySiguiente() && !encontrado) {
            actual = it.dato();
            if (actual.getFila() == objetivo.getFila() && actual.getColumna() == objetivo.getColumna()) {
                it.eliminar();
                encontrado = true;
            } else {
                it.siguiente();
            }
        }
    }

    public Matriz<Celda> obtenerMapaVisual() { return this.mapaVisual; }
    public Personaje obtenerPersonaje() { return this.personaje; }
    public Lista<Monstruo> obtenerEnemigos() { return this.enemigos; }
    public Diccionario<Celda, Lista<Item>> obtenerItemsEnSuelo() { return this.itemsEnSuelo; }
    public boolean esPartidaGanada() { return this.partidaGanada; }
    public boolean debeRegresarAlEscondite() { return this.regresarAlEscondite; }
}