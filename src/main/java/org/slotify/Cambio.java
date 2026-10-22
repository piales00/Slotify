package org.slotify;

public class Cambio {
  private TipoCambio tipo;
  private Producto producto;
  private int fila;
  private int columna;

  public Cambio(TipoCambio tipo, Producto producto, int fila, int columna) {
    this.tipo = tipo;
    this.producto = producto;
    this.fila = fila;
    this.columna = columna;
  }

  public TipoCambio getTipo() {
    return tipo;
  }

  public Producto getProducto() {
    return producto;
  }

  public int getFila() {
    return fila;
  }

  public int getColumna() {
    return columna;
  }

  @Override
  public String toString() {
    String accion = tipo == TipoCambio.COLOCAR ? "Colocó" : "Retiró";
    String preposicion = tipo == TipoCambio.COLOCAR ? "en" : "de";
    return accion + " " + producto.getSKU() + " (" + producto.getNombre() + ") " + preposicion
        + " (" + fila + ", " + columna + ")";
  }
}
