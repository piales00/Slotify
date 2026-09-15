package org.Almacen;

import org.Catalogo.Producto;

public class Celda {
  private Producto producto;
  private EstadoProducto estado;
  // Número del pasillo al que pertenece (o al que da acceso si es estantería). 0 = ninguno
  private int pasillo;

  public Celda(EstadoProducto estado) {
    this.estado = estado;
  }

  public Celda(EstadoProducto estado, int pasillo) {
    this.estado = estado;
    this.pasillo = pasillo;
  }

  public Celda(Producto producto, EstadoProducto estado) {
    this.producto = producto;
    this.estado = estado;
  }

  public Producto getProducto() {
    return producto;
  }

  public EstadoProducto getEstado() {
    return estado;
  }

  public int getPasillo() {
    return pasillo;
  }

  public void setEstado(EstadoProducto nuevoEstado) {
    this.estado = nuevoEstado;
  }

  public void setProducto(Producto nuevoProducto) {
    this.producto = nuevoProducto;
  }
}
