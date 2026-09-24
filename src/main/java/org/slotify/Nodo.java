package org.slotify;

// Nodo de la lista enlazada de pedidos, guarda el dato del pedido y la referencia al siguiente nodo
// El último tiene de nodo null
public class Nodo {
  private Pedido dato;
  private Nodo siguiente;

  public Nodo(Pedido dato) {
    this.dato = dato;
    this.siguiente = null;
  }

  // getters y setters
  public Pedido getDato() {
    return dato;
  }

  public void setDato(Pedido newDato) {
    this.dato = newDato;
  }

  public Nodo getSiguiente() {
    return siguiente;
  }

  public void setSiguiente(Nodo newSiguiente) {
    this.siguiente = newSiguiente;
  }
}
