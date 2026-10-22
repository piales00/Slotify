package org.slotify;

public class PilaCambios {

  private Cambio[] elementos;
  private int tope;

  public PilaCambios(int capacidad) {
    this.elementos = new Cambio[capacidad];
    this.tope = -1;
  }

  public boolean estaVacia() {
    return tope == -1;
  }

  public boolean estaLlena() {
    return tope == elementos.length - 1;
  }

  public int tamano() {
    return tope + 1;
  }

  public int getCapacidad() {
    return elementos.length;
  }

  public boolean push(Cambio cambio) {
    if (estaLlena()) {
      Utils.error("Pila llena (desbordamiento): este cambio no se podrá deshacer.");
      return false;
    }
    tope++;
    elementos[tope] = cambio;
    return true;
  }

  public Cambio pop() {
    if (estaVacia()) {
      return null;
    }
    Cambio cambio = elementos[tope];
    elementos[tope] = null;
    tope--;
    return cambio;
  }

  public Cambio peek() {
    return estaVacia() ? null : elementos[tope];
  }

  public void vaciar() {
    while (!estaVacia()) {
      pop();
    }
  }

  public void mostrar() {
    if (estaVacia()) {
      System.out.println("  (pila vacía · tope = -1)");
      return;
    }

    for (int i = tope; i >= 0; i--) {
      String marca = i == tope ? "tope →" : "      ";
      System.out.printf("  %s [%d] %s%n", marca, i, elementos[i]);
    }
    System.out.println("  Ocupación: " + tamano() + "/" + elementos.length + "  ·  tope = " + tope);
  }
}
