package org.ayed.tda.diccionario;

import org.ayed.tda.comparador.Comparador;
import org.ayed.tda.lista.Cola;
import org.ayed.tda.lista.Lista;
import org.ayed.tda.tupla.Tupla;
import org.ayed.tda.vector.VectorEstatico;

/**
 * Diccionario asociativo (clave, valor) que mantiene
 * el orden de los datos en base a su clave y al
 * comparador utilizado. Está implementado con un
 * árbol binario de búsqueda no balanceado.
 *
 * @param <C> El tipo de dato de la clave.
 *            Este tipo debe ser comparable.
 * @param <V> El tipo de dato del valor.
 *            Este tipo no necesita ser comparable obligatoriamente,
 *            pero puede ser útil para el usuario si se decide
 *            implementar métodos para consultar si un valor está
 *            en el diccionario.
 */
public class DiccionarioOrdenado<C, V> {
    private Nodo<C, V> raiz;
    private Comparador<C> comparador;
    private int cantidadDatos;

    /**
     * Constructor.
     *
     * @param comparador Comparador a utilizar.
     *                   No puede ser nulo.
     * @throws ExcepcionDiccionario si el comparador es nulo.
     */
    public DiccionarioOrdenado(Comparador<C> comparador) {
        if (comparador == null) {
        	throw new ExcepcionDiccionario("El comparador no puede ser nulo");
        }
        this.comparador = comparador;
        cantidadDatos = 0;
        this.raiz = null;
    }

    /**
     * Constructor de copia de DiccionarioOrdenado.
     * <p>
     * TIP: Implementar un método que clone un subárbol.
     *
     * @param diccionarioOrdenado Diccionario a copiar.
     *                            No puede ser nulo.
     * @throws ExcepcionDiccionario si el diccionario es nulo.
     */
    public DiccionarioOrdenado(DiccionarioOrdenado<C, V> diccionarioOrdenado) {
        if (diccionarioOrdenado == null) {
        	throw new ExcepcionDiccionario("El diccionario es nulo");
        }
        this.comparador = diccionarioOrdenado.comparador;
        this.cantidadDatos = diccionarioOrdenado.cantidadDatos;
        this.raiz = clonarSubarbol(diccionarioOrdenado.raiz);
    }

    private Nodo<C, V> clonarSubarbol(Nodo<C, V> original) {
        if (original == null) return null;
        // Creamos un nuevo objeto Nodo para cada posición [3]
        Nodo<C, V> nuevo = new Nodo<>(original.clave, original.valor);
        nuevo.hijoIzquierdo = clonarSubarbol(original.hijoIzquierdo);
        nuevo.hijoDerecho = clonarSubarbol(original.hijoDerecho);
        return nuevo;
    }
    
    private Tupla<Nodo<C,V>, Nodo<C,V>> buscarNodoYPadre(C clave) {
        Nodo<C,V> actual = raiz;
        Nodo<C,V> padre = null;
        
        while (actual != null) {
            int comparacion = comparador.comparar(clave, actual.clave);
            if (comparacion == 0) {
                return new Tupla<>(actual, padre);
            }
            padre = actual;
            if (comparacion < 0) {
                actual = actual.hijoIzquierdo;
            } else {
                actual = actual.hijoDerecho;
            }
        }
        return new Tupla<>(null, padre);
    }

    /**
     * Agrega un mapeo {clave, valor} al diccionario.
     * Si ya existía la clave en el diccionario, reemplaza
     * el valor anterior y lo devuelve.
     *
     * @param clave Clave a agregar.
     * @param valor Valor a agregar.
     * @return el valor anterior. Si no había un valor
     * anterior, devuelve null.
     */
    public V agregar(C clave, V valor) {
        if (raiz == null) { // ARBOL VACIO
        	this.raiz = new Nodo<>(clave,valor);
        	cantidadDatos++;
        	return null;
        }
        Tupla<Nodo<C,V>, Nodo<C,V>> resultado = buscarNodoYPadre(clave);
        Nodo<C,V> actual = resultado.clave();
        Nodo<C,V> padre = resultado.valor();
        
        if (actual == null) { //LA CLAVE NO EXISTIA EN EL ARBOL
        	//actual = new Nodo<C,V>(clave,valor, padre, null,null);
        	Nodo<C,V> nuevo = new Nodo<>(clave, valor, padre);
        	cantidadDatos++;
        	int comparacion = comparador.comparar(clave, padre.clave);

        	if (comparacion < 0) {
        	    padre.hijoIzquierdo = nuevo;
        	} else {
        	    padre.hijoDerecho = nuevo;
        	}
        	return null;
        	
        } else  {// la clave ya existia, la actializo
        	V valorAnterior = actual.valor;
        	actual.valor =  valor;
        	return valorAnterior;
        }
        
        
    }

    /**
     * Elimina un mapeo {clave, valor} del diccionario,
     * si existe. Si no existe, el diccionario queda en
     * el mismo estado.
     * <p>
     * NOTA: Para eliminar nodos interiores, se utiliza
     * el sucesor inmediato.
     *
     * @param clave Clave a eliminar.
     * @return el valor eliminado. Si no se eliminó
     * un valor, devuelve null.
     */
    public V eliminar(C clave) {
        if (raiz == null) {
        	return null;
        }
        Tupla<Nodo<C,V>, Nodo<C,V>> resultado = buscarNodoYPadre(clave);
        Nodo<C,V> actual = resultado.clave();
        Nodo<C,V> padre = resultado.valor();
        
        if (actual == null) { //no existe
        	return null;
        } else { //	EXISTE
        	V valorEliminado = actual.valor;
        	Nodo<C,V> hijoderecho = actual.hijoDerecho;
        	Nodo<C,V> hijoizquierdo = actual.hijoIzquierdo;
        	// CASO HOJA
        	if ((hijoderecho == null) && (hijoizquierdo == null) ) { //HOJA
        		//actual = null;
        	    if (padre == null) {
        	        raiz = null;
        	    } else {
        	        if (padre.hijoDerecho == actual) {
        	            padre.hijoDerecho = null;
        	        } else {
        	            padre.hijoIzquierdo = null;
        	        }
        	    }
        	// CASO UN HIJO
        	} else if ( hijoderecho == null) {
        		if (padre == null) {
        	        raiz = hijoizquierdo;
        	        raiz.padre = null;
        	    } else {
        	        if (padre.hijoIzquierdo == actual) {
        	            padre.hijoIzquierdo = hijoizquierdo;
        	        } else {
        	            padre.hijoDerecho = hijoizquierdo;
        	        }
        	        hijoizquierdo.padre = padre;
        	    }
        	} else if( hijoizquierdo == null){
    			if (padre == null) {
    		        raiz = hijoderecho;
    		        raiz.padre = null;
    		    } else {
    		        if (padre.hijoIzquierdo == actual) {
    		            padre.hijoIzquierdo = hijoderecho;
    		        } else {
    		            padre.hijoDerecho = hijoderecho;
    		        }
    		        hijoderecho.padre = padre;
    		    }
        		}
        		
        	//CASO DOS HIJJOS
        	else { // dos hijos
        		Nodo<C,V> sucesor = obtenerSucesorInmediato(actual);
        		//reemplzazo por sucesor y elimino el sucesir de donde estaba antes
        		actual.clave = sucesor.clave;
        		actual.valor = sucesor.valor;
        		Nodo<C,V> padreSucesor = sucesor.padre;
        		if (padreSucesor.hijoIzquierdo == sucesor) {
        		    padreSucesor.hijoIzquierdo = sucesor.hijoDerecho;
        		} else {
        		    padreSucesor.hijoDerecho = sucesor.hijoDerecho;
        		}
        		if (sucesor.hijoDerecho != null) {
        		    sucesor.hijoDerecho.padre = padreSucesor;
        		}
        		
        	}
        		cantidadDatos--;
        		return valorEliminado;
        	}
        }
        
    /**
     * Obtiene el sucesor inmediato del nodo.
     *
     * @param nodo Nodo inicial.
     * @return el sucesor inmediato.
     */
    private Nodo<C, V> obtenerSucesorInmediato(Nodo<C, V> nodo) {
    	Nodo<C,V> actual = nodo.hijoDerecho;
    	while(actual.hijoIzquierdo != null){
    	    actual = actual.hijoIzquierdo;
    	}

    	return actual;
    }
    /**
     * Obtiene un mapeo {clave, valor} del diccionario,
     * si existe.
     *
     * @param clave Clave a buscar.
     * @return el valor buscado. Si no existe, devuelve
     * null.
     */
    public V obtenerValor(C clave) {
	    Nodo<C,V> nodo = buscarNodoYPadre(clave).clave();
	    return nodo == null ? null : nodo.valor;
    }

    /**
     * Devuelve el recorrido inorder del árbol.
     *
     * @return el recorrido.
     */
    public Lista<Tupla<C, V>> inorder() {
        Lista<Tupla<C, V>> lista = new Lista<>();
        recorrerInorder(raiz, lista);
        return lista;
    }

    private void recorrerInorder(Nodo<C, V> nodo, Lista<Tupla<C, V>> lista) {
        if (nodo == null) return;
        recorrerInorder(nodo.hijoIzquierdo, lista);
        lista.agregar(new Tupla<>(nodo.clave, nodo.valor));
        recorrerInorder(nodo.hijoDerecho, lista);
    }

    /**
     * Devuelve el recorrido preorder del árbol.
     *
     * @return el recorrido.
     */
    public Lista<Tupla<C, V>> preorder() {
    	Lista<Tupla<C, V>> lista = new Lista<>();
    	recorrerPreorder(raiz, lista);
    	return lista;
    }
    private void recorrerPreorder(Nodo<C, V> nodo, Lista<Tupla<C, V>> lista) {
    	if (nodo == null) return;
			lista.agregar(new Tupla<>(nodo.clave, nodo.valor));
			recorrerPreorder(nodo.hijoIzquierdo, lista);
			recorrerPreorder(nodo.hijoDerecho, lista);
		}

    /**
     * Devuelve el recorrido postorder del árbol.
     *
     * @return el recorrido.
     */
    public Lista<Tupla<C, V>> postorder() {
        Lista<Tupla<C, V>> lista = new Lista<>();
        recorrerPostorder(raiz, lista);
        return lista;
    }

    private void recorrerPostorder(Nodo<C, V> nodo, Lista<Tupla<C, V>> lista) {
        if (nodo == null) return;
        recorrerPostorder(nodo.hijoIzquierdo, lista);
        recorrerPostorder(nodo.hijoDerecho, lista);
        lista.agregar(new Tupla<>(nodo.clave, nodo.valor));
    }

    /**
     * Devuelve el recorrido en ancho del árbol.
     *
     * @return el recorrido.
     */
    public Lista<Tupla<C, V>> ancho() {
        Lista<Tupla<C, V>> lista = new Lista<>();
        if (raiz == null) return lista;

        Cola<Nodo<C, V>> cola = new Cola<>();
        cola.agregar(raiz);

        while (!cola.vacio()) {
            Nodo<C, V> actual = cola.eliminar();
            lista.agregar(new Tupla<>(actual.clave, actual.valor));
            
            if (actual.hijoIzquierdo != null) cola.agregar(actual.hijoIzquierdo);
            if (actual.hijoDerecho != null) cola.agregar(actual.hijoDerecho);
        }
        return lista;
    }

    /**
     * Obtiene el tamaño del diccionario.
     *
     * @return el tamaño del diccionario.
     */
    public int tamanio() {
        return cantidadDatos;
    }

    /**
     * Evalúa si el diccionario está vacío.
     *
     * @return true si el diccionario está vacío.
     */
    public boolean vacio() {
        return cantidadDatos == 0? true: false;
    }
}
