package org.slotify;

// Igual que ResultadoBusqueda pero para pedidos: devuelve el pedido encontrado (o null)
// junto con cuántas comparaciones costó encontrarlo, para poder medir la búsqueda.
public class ResultadoBusquedaPedido {
  private Pedido pedido;
  private int comparaciones;
  private int posicion;

  public ResultadoBusquedaPedido(Pedido pedido, int comparaciones, int posicion) {
    this.pedido = pedido;
    this.comparaciones = comparaciones;
    this.posicion = posicion;
  }

  public Pedido getPedido() {
    return pedido;
  }

  public int getComparaciones() {
    return comparaciones;
  }

  // Posición (0, 1, 2...) del nodo dentro de la lista, o -1 si no se encontró
  public int getPosicion() {
    return posicion;
  }
}
