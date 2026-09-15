package org.Catalogo;

public class ResultadoBusqueda {
  private Producto producto;
  private int comparaciones;

  public ResultadoBusqueda(Producto producto, int comparaciones) {
    this.producto = producto;
    this.comparaciones = comparaciones;
  }

  public Producto getProducto() {
    return producto;
  }

  public int getComparaciones() {
    return comparaciones;
  }
}
