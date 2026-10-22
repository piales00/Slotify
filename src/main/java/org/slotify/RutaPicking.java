package org.slotify;

public class RutaPicking {

  private NodoParada dock;
  private NodoParada cola;
  private int numeroParadas;

  public RutaPicking(int filaDock, int columnaDock) {
    this.dock = new NodoParada(Parada.dock(filaDock, columnaDock));
    this.dock.setSiguiente(dock);
    this.cola = dock;
    this.numeroParadas = 0;
  }

  public int getNumeroParadas() {
    return numeroParadas;
  }

  public boolean estaVacia() {
    return dock.getSiguiente() == dock;
  }

  public void agregarParada(Parada parada) {
    NodoParada nuevo = new NodoParada(parada);
    nuevo.setSiguiente(dock);
    cola.setSiguiente(nuevo);
    cola = nuevo;
    numeroParadas++;
  }

  public Parada recogerParada(String codigoPedido) {
    if (codigoPedido == null) {
      return null;
    }

    String buscado = codigoPedido.trim();
    NodoParada anterior = dock;
    NodoParada actual = dock.getSiguiente();

    while (actual != dock) {
      if (actual.getDato().getCodigoPedido().equalsIgnoreCase(buscado)) {
        anterior.setSiguiente(actual.getSiguiente());
        if (actual == cola) {
          cola = anterior;
        }
        actual.setSiguiente(null);
        numeroParadas--;
        return actual.getDato();
      }
      anterior = actual;
      actual = actual.getSiguiente();
    }

    return null;
  }

  public int distanciaTotal() {
    int total = 0;
    NodoParada actual = dock;
    do {
      total += actual.getDato().distanciaA(actual.getSiguiente().getDato());
      actual = actual.getSiguiente();
    } while (actual != dock);
    return total;
  }

  public void recorrer() {
    if (estaVacia()) {
      System.out.println("  (no hay paradas: el operario no sale del dock)");
      return;
    }

    int paso = 1;
    int acumulado = 0;
    NodoParada actual = dock;
    do {
      NodoParada siguiente = actual.getSiguiente();
      int tramo = actual.getDato().distanciaA(siguiente.getDato());
      acumulado += tramo;
      System.out.printf("  paso %d · %-8s → %-8s  %2d celda(s)  ·  acumulado: %d%n",
          paso, actual.getDato().getCodigoPedido(), siguiente.getDato().getCodigoPedido(), tramo, acumulado);
      paso++;
      actual = siguiente;
    } while (actual != dock);
    System.out.println("  De vuelta en el dock: " + (paso - 1) + " tramo(s), " + acumulado + " celda(s) recorridas.");
  }

  public void ordenarSerpentina() {
    if (estaVacia()) {
      return;
    }

    int[] ordenPasillo = ordenDeVisitaDePasillos();
    NodoParada i = dock.getSiguiente();
    while (i != cola) {
      NodoParada menor = i;
      NodoParada j = i.getSiguiente();

      while (j != dock) {
        if (vaAntes(j.getDato(), menor.getDato(), ordenPasillo)) {
          menor = j;
        }
        j = j.getSiguiente();
      }

      if (menor != i) {
        Parada temp = i.getDato();
        i.setDato(menor.getDato());
        menor.setDato(temp);
      }

      i = i.getSiguiente();
    }
  }

  private int[] ordenDeVisitaDePasillos() {
    int mayor = 0;
    NodoParada actual = dock.getSiguiente();
    while (actual != dock) {
      mayor = Math.max(mayor, actual.getDato().getPasillo());
      actual = actual.getSiguiente();
    }

    boolean[] tieneParadas = new boolean[mayor + 1];
    actual = dock.getSiguiente();
    while (actual != dock) {
      tieneParadas[actual.getDato().getPasillo()] = true;
      actual = actual.getSiguiente();
    }

    int[] orden = new int[mayor + 1];
    int visitados = 0;
    for (int pasillo = 0; pasillo <= mayor; pasillo++) {
      if (tieneParadas[pasillo]) {
        orden[pasillo] = visitados;
        visitados++;
      }
    }
    return orden;
  }

  private boolean vaAntes(Parada a, Parada b, int[] ordenPasillo) {
    if (a.getPasillo() != b.getPasillo()) {
      return a.getPasillo() < b.getPasillo();
    }

    boolean subiendo = ordenPasillo[a.getPasillo()] % 2 == 0;
    if (a.getFila() != b.getFila()) {
      return subiendo ? a.getFila() > b.getFila() : a.getFila() < b.getFila();
    }
    return a.getColumna() < b.getColumna();
  }

  public void mostrarTabla() {
    if (estaVacia()) {
      System.out.println("  (sin paradas)");
      return;
    }

    String formato = "  %-3s %-9s %-9s %9s %8s %-9s%n";
    String cabecera = String.format(formato, "#", "Pedido", "SKU", "Cantidad", "Pasillo", "Celda");
    System.out.print(cabecera);
    System.out.println("  " + "─".repeat(cabecera.length() - 3));

    int i = 1;
    NodoParada actual = dock.getSiguiente();
    while (actual != dock) {
      Parada parada = actual.getDato();
      System.out.printf(formato, i, parada.getCodigoPedido(), parada.getSKU(), parada.getCantidad(),
          parada.getPasillo(), "(" + parada.getFila() + ", " + parada.getColumna() + ")");
      i++;
      actual = actual.getSiguiente();
    }
  }

  public void mostrarEnlaces() {
    StringBuilder sb = new StringBuilder("  ");
    NodoParada actual = dock;
    do {
      sb.append("[").append(actual.getDato().getCodigoPedido()).append("] → ");
      actual = actual.getSiguiente();
    } while (actual != dock);
    sb.append("[DOCK] (vuelve al inicio)");
    System.out.println(sb);
  }
}
