package org.slotify;

public class NodoParada {
  private Parada dato;
  private NodoParada siguiente;

  public NodoParada(Parada dato) {
    this.dato = dato;
    this.siguiente = null;
  }

  public Parada getDato() {
    return dato;
  }

  public void setDato(Parada newDato) {
    this.dato = newDato;
  }

  public NodoParada getSiguiente() {
    return siguiente;
  }

  public void setSiguiente(NodoParada newSiguiente) {
    this.siguiente = newSiguiente;
  }
}
