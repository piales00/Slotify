package org.slotify;

// La clase de pedido, solo guarda el sku del producto para que si se actualiza el nombre del producto o catálogo
// el pedido no muestre datos viejos
public class Pedido {
  private String codigo;
  private String cliente;
  private String SKU;
  private int cantidad;

  public Pedido(String codigo, String cliente, String SKU, int cantidad) {
    this.codigo = codigo;
    this.cliente = cliente;
    this.SKU = SKU;
    this.cantidad = cantidad;
  }

  // getters y setters
  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String newCodigo) {
    this.codigo = newCodigo;
  }

  public String getCliente() {
    return cliente;
  }

  public void setCliente(String newCliente) {
    this.cliente = newCliente;
  }

  public String getSKU() {
    return SKU;
  }

  public void setSKU(String newSKU) {
    this.SKU = newSKU;
  }

  public int getCantidad() {
    return cantidad;
  }

  public void setCantidad(int newCantidad) {
    this.cantidad = newCantidad;
  }

  @Override
  public String toString() {
    return this.codigo + " - " + this.cliente + " pide " + this.cantidad + " x " + this.SKU;
  }
}
