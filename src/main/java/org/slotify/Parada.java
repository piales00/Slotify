package org.slotify;

public class Parada {
  private String codigoPedido;
  private String sku;
  private int cantidad;
  private int fila;
  private int columna;
  private int pasillo;
  private boolean dock;

  public Parada(String codigoPedido, String sku, int cantidad, int fila, int columna, int pasillo) {
    this.codigoPedido = codigoPedido;
    this.sku = sku;
    this.cantidad = cantidad;
    this.fila = fila;
    this.columna = columna;
    this.pasillo = pasillo;
    this.dock = false;
  }

  public static Parada dock(int fila, int columna) {
    Parada parada = new Parada("DOCK", null, 0, fila, columna, 0);
    parada.dock = true;
    return parada;
  }

  public String getCodigoPedido() {
    return codigoPedido;
  }

  public String getSKU() {
    return sku;
  }

  public int getCantidad() {
    return cantidad;
  }

  public int getFila() {
    return fila;
  }

  public int getColumna() {
    return columna;
  }

  public int getPasillo() {
    return pasillo;
  }

  public boolean esDock() {
    return dock;
  }

  public int distanciaA(Parada otra) {
    return Math.abs(fila - otra.fila) + Math.abs(columna - otra.columna);
  }

  @Override
  public String toString() {
    if (dock) {
      return "DOCK (" + fila + ", " + columna + ")";
    }
    return codigoPedido + " · " + cantidad + " x " + sku + " · pasillo " + pasillo
        + " (" + fila + ", " + columna + ")";
  }
}
