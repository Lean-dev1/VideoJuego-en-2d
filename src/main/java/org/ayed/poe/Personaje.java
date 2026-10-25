package org.ayed.poe;

import javax.imageio.ImageIO;
import org.ayed.visual.KeyHandler;
import org.ayed.juego.AccionJugador;

public class Personaje extends Entidad {
    private Inventario mochila; 
    private Equipamiento equipamiento;
    private Alijo alijo;
    private int ANCHO_MOCHILA = 5;
    private int ALTO_MOCHILA = 6;
    private int velocidad = 4; 
    private int oro, xp, nivel, puntosHabilidad;
    private org.ayed.poe.arbol.ArbolHabilidades arbolHabilidades;
    private int bonificacionArmaduraPasiva = 0; 
    private int bonificacionDañoPasiva = 0;
    private int itemsExtraPorDrop = 0;

    public Personaje(int xInicial, int yInicial) {
        super(100, 50, 10, 8); // Se asignan 8 puntos de armadura base aquí
        this.setX(xInicial);
        this.setY(yInicial);
        this.nivel = 1; this.oro = 0; this.xp = 0; this.puntosHabilidad = 0;
        this.mochila = new Inventario(ANCHO_MOCHILA, ALTO_MOCHILA);
        this.alijo = new Alijo();
        this.equipamiento = new Equipamiento();
        
        try {
            this.arbolHabilidades = org.ayed.poe.arbol.CargadorArbol.cargar("arbol.txt");
        } catch (Exception e) {
            System.out.println("Error crítico cargando el árbol: " + e.getMessage());
            this.arbolHabilidades = new org.ayed.poe.arbol.ArbolHabilidades(0); 
        }
        cargarImagenesPersonaje();
    }

    public Personaje(int vida, int mana, int daño, int armadura, Inventario mochila, Alijo alijo) {
        super(vida, mana, daño, armadura);
        this.mochila = mochila; this.alijo = alijo;
        this.oro = 0; this.xp = 0; this.nivel = 1; this.puntosHabilidad = 1;
        this.setX(0); this.setY(0);
        this.equipamiento = new Equipamiento();
        
        // CORRECCIÓN: Inicialización del árbol también en el segundo constructor
        try {
            this.arbolHabilidades = org.ayed.poe.arbol.CargadorArbol.cargar("arbol.txt");
        } catch (Exception e) {
            System.out.println("Error crítico cargando el árbol: " + e.getMessage());
            this.arbolHabilidades = new org.ayed.poe.arbol.ArbolHabilidades(0); 
        }
        cargarImagenesPersonaje();
    }

    private void cargarImagenesPersonaje() {
        try {
            arriba1 = ImageIO.read(getClass().getResourceAsStream("/player/parriba1.png"));
            arriba2 = ImageIO.read(getClass().getResourceAsStream("/player/parriba2.png"));
            abajo1 = ImageIO.read(getClass().getResourceAsStream("/player/pabajo1.png"));
            abajo2 = ImageIO.read(getClass().getResourceAsStream("/player/pabajo2.png"));
            izquierda1 = ImageIO.read(getClass().getResourceAsStream("/player/pizquierda1.png"));
            izquierda2 = ImageIO.read(getClass().getResourceAsStream("/player/pizquierda2.png"));
            derecha1 = ImageIO.read(getClass().getResourceAsStream("/player/pderecha1.png"));
            derecha2 = ImageIO.read(getClass().getResourceAsStream("/player/pderecha2.png"));
            quietoFrente = ImageIO.read(getClass().getResourceAsStream("/player/pquietodefrente.png"));
            quietoEspalda = ImageIO.read(getClass().getResourceAsStream("/player/pquietodeespalda.png"));
            quietoIzquierda = ImageIO.read(getClass().getResourceAsStream("/player/pquietoizquierda.png"));
            quietoDerecha = ImageIO.read(getClass().getResourceAsStream("/player/pquietoderecha.png"));
        } catch (Exception e) {
            System.out.println("Error cargando sprites: " + e.getMessage());
        }
    }

    public void actualizarMovimientoEscondite(KeyHandler keyH) {
        enMovimiento = false;
        if (keyH.upPressed) { setY(getY() - velocidad); direccion = "arriba"; enMovimiento = true; }
        else if (keyH.downPressed) { setY(getY() + velocidad); direccion = "abajo"; enMovimiento = true; }
        else if (keyH.leftPressed) { setX(getX() - velocidad); direccion = "izquierda"; enMovimiento = true; }
        else if (keyH.rightPressed) { setX(getX() + velocidad); direccion = "derecha"; enMovimiento = true; }

        if (enMovimiento) {
            spriteCounter++;
            if (spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }

    /**
     * PRE: El equipamiento debe estar inicializado.
     * POST: Calcula la armadura total sumando la base del personaje, el equipamiento defensivo (Casco y Armadura) y las pasivas.
     */
    public int getArmaduraTotal() {
        int armaduraCalculada;
        Item posibleCasco;
        Item posibleArmadura;
        
        armaduraCalculada = this.getArmaduraBase(); 

        // Evaluamos el Casco
        posibleCasco = this.equipamiento.obtenerItem(Slot.CASCO);
        if (posibleCasco != null) {
            // Buscamos directamente la armadura extra que le cargó el Catálogo
            armaduraCalculada += posibleCasco.getArmaduraExtra();
        }
        
        // Evaluamos la Armadura
        posibleArmadura = this.equipamiento.obtenerItem(Slot.ARMADURA);
        if (posibleArmadura != null) {
            // Lo mismo para la pechera
            armaduraCalculada += posibleArmadura.getArmaduraExtra();
        }
        
        armaduraCalculada += this.bonificacionArmaduraPasiva;

        return armaduraCalculada;
    }

    /**
     * PRE: cantidad >= 0
     * POST: Reduce la vida actual en base al daño recibido mitigado por la armadura total calculada.
     */
    @Override
    public void recibirDaño(int cantidad) {
        int armaduraActual = getArmaduraTotal();
        int dañoReal = Math.max(0, cantidad - armaduraActual);
        
        // CORRECCIÓN: setVidaActual ahora previene nativamente valores negativos
        this.setVidaActual(this.getVidaActual() - dañoReal); 
        
        System.out.println("Recibís " + cantidad + " de daño. Tu armadura bloquea " + armaduraActual + ". Daño final: " + dañoReal);
    }

    public void moverEnMazmorra(int deltaFila, int deltaColumna) {
        this.setFila(this.getFila() + deltaFila);
        this.setColumna(this.getColumna() + deltaColumna);
    }

    /**
     * PRE: cantidad >= 0
     * POST: Procesa la ganancia de experiencia, calcula subidas de nivel e incrementa puntos en el árbol.
     */
    public void ganarExperiencia(int cantidad) {
        this.xp += cantidad;
        int umbralNivel = this.nivel * 100;

        while (this.xp >= umbralNivel) {
            this.xp -= umbralNivel;
            this.nivel++;
            
            this.arbolHabilidades.agregarPuntos(1); 
            
            System.out.println("¡LEVEL UP! Ahora eres nivel " + this.nivel);
            umbralNivel = this.nivel * 100;
        }
    }

    /**
     * PRE: idNodo válido dentro de la configuración del árbol.
     * POST: Intenta desbloquear un nodo del árbol. Aplica mejoras si se efectúa con éxito.
     */
    public boolean aprenderPasiva(int idNodo) {
        try {
            this.arbolHabilidades.asignarPasiva(idNodo);
            org.ayed.poe.arbol.NodoPasiva nodoComprado = this.arbolHabilidades.obtenerPasiva(idNodo);
            aplicarEstadisticasDelNodo(nodoComprado);
            
            System.out.println("¡Aprendiste una nueva habilidad: " + nodoComprado.obtenerNombre() + "!");
            return true;
        } catch (org.ayed.poe.arbol.ExcepcionArbolHabilidades e) {
            System.out.println("No puedes desbloquear esto: " + e.getMessage());
            return false;
        }
    }

    private void aplicarEstadisticasDelNodo(org.ayed.poe.arbol.NodoPasiva nodo) {
        Integer extraDaño = nodo.obtenerEstadistica("ataque");
        if (extraDaño != null) {
            this.bonificacionDañoPasiva += extraDaño;
            System.out.println("¡Ataque incrementado! Daño pasivo extra: " + this.bonificacionDañoPasiva);
        }

        Integer extraDefensa = nodo.obtenerEstadistica("defensa");
        if (extraDefensa != null) {
            this.bonificacionArmaduraPasiva += extraDefensa;
            System.out.println("¡Piel de hierro! Armadura pasiva extra: " + this.bonificacionArmaduraPasiva);
        }

        Integer extraVida = nodo.obtenerEstadistica("vida");
        if (extraVida != null) {
            this.setVidaMax(this.getVidaMax() + extraVida);
            this.setVidaActual(this.getVidaActual() + extraVida); 
        }
    }

    /**
     * POST: Calcula el daño total sumando la base, el arma equipada y las bonificaciones pasivas.
     */
    public int getDañoTotal() {
        int dañoCalculado = this.getDañoBase(); 

        org.ayed.poe.Item posibleArma = this.getEquipamiento().obtenerItem(Slot.ARMA);
        if (posibleArma instanceof org.ayed.poe.Arma) {
            org.ayed.poe.Arma arma = (org.ayed.poe.Arma) posibleArma;
            dañoCalculado += arma.getDañoBase();
        }

        dañoCalculado += this.bonificacionDañoPasiva;
        return dañoCalculado;
    }

    public AccionJugador procesarEntradaMazmorra(KeyHandler keyH) {
        if (keyH.upPressed) {
            this.direccion = "arriba";
            alternarSpriteTurno();
            return AccionJugador.MOVER_ARRIBA;
        }
        else if (keyH.downPressed) {
            this.direccion = "abajo";
            alternarSpriteTurno();
            return AccionJugador.MOVER_ABAJO;
        }
        else if (keyH.leftPressed) {
            this.direccion = "izquierda";
            alternarSpriteTurno();
            return AccionJugador.MOVER_IZQUIERDA;
        }
        else if (keyH.rightPressed) {
            this.direccion = "derecha";
            alternarSpriteTurno();
            return AccionJugador.MOVER_DERECHA;
        }
        else if (keyH.enterPressed) {
            alternarSpriteTurno();
            return AccionJugador.ATACAR;
        }
        
        return AccionJugador.NADA;
    }
	 // Pre: El personaje está vivo y ha finalizado o salido de una mazmorra.
	 // Post: La vida actual del personaje se iguala a su vida máxima total calculada.
	 public void restaurarVidaAlMaximo() {
		 this.setVidaActual(this.getVidaMax());
	 }
	 
	 public void procesarMuertePorDañoLetal() {
		    this.vaciarMochila();
		    this.desequiparTodosLosObjetos();
		}

    private void desequiparTodosLosObjetos() {
		this.equipamiento.desequiparTodo();
		
	}

	private void vaciarMochila() {
		this.mochila.vaciar();
		
	}
	
    /**
     * Permite actualizar el nivel del personaje desde el sistema de carga de partidas.
     */
    public void setNivel(int nuevoNivel) {
        this.nivel = nuevoNivel;
    }
    @Override
    public int getVidaMax() {
        int vidaCalculada;
        Item posibleCasco;
        Item posibleArmadura;
        
        vidaCalculada = super.getVidaMax(); // Trae la vida máxima base de Entidad

        // Sumamos la vida extra del casco
        posibleCasco = this.equipamiento.obtenerItem(Slot.CASCO);
        if (posibleCasco != null) {
            vidaCalculada += posibleCasco.getVidaExtra();
        }

        // Sumamos la vida extra de la pechera
        posibleArmadura = this.equipamiento.obtenerItem(Slot.ARMADURA);
        if (posibleArmadura != null) {
            vidaCalculada += posibleArmadura.getVidaExtra();
        }

        return vidaCalculada;
    }


	public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getDireccion() { return this.direccion; }
    public void alternarSpriteTurno() { spriteNum = (spriteNum == 1) ? 2 : 1; }
    
    public org.ayed.poe.arbol.ArbolHabilidades getArbolHabilidades() { return this.arbolHabilidades; }
    public int getNivel() { return nivel; }
    public int getOro() { return oro; }
    public int getXp() { return xp; }
    public int getPuntosHabilidad() { return puntosHabilidad; }
    public void modificarOro(int cantidad) { this.oro = Math.max(0, this.oro + cantidad); }
    public Inventario getInventario() { return this.mochila; }
    public Alijo getAlijo() { return this.alijo; }
    public Equipamiento getEquipamiento() { return this.equipamiento; }
    public int getItemsExtraPorDrop() { return this.itemsExtraPorDrop; }
    public void setXp(int xp) { this.xp = xp; }
    public void setPuntosHabilidad(int puntosHabilidad) { this.puntosHabilidad = puntosHabilidad; }
}