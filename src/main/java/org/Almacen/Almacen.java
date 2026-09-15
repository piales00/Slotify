package org.Almacen;

import org.Catalogo.Producto;
import org.Catalogo.Utils;

public class Almacen {

  private static Almacen INSTANCIA;
  private int filas;
  private int columnas;
  private Celda[][] almacen;
  private int pasillos;
  public int espaciosDisponibles;

  private Almacen(int filas, int columnas) {
    this.filas = filas;
    this.columnas = columnas;
    espaciosDisponibles = 0;
    almacen = new Celda[filas][columnas];
  }

  public static Almacen getInstancia(int filas, int columnas) {
    if (INSTANCIA == null) {
      INSTANCIA = new Almacen(filas, columnas);
    }

    return INSTANCIA;
  }

  public static Almacen getInstancia() {
    if (INSTANCIA == null) {
      System.out.println("PRIMERO DEBES INICIALIZAR EL ALMACEN");
      return null;
    } else {
      return INSTANCIA;
    }
  }

  // TODO terminar llenar almacen
  public static void llenarAlmacen() {

    Celda[][] almacen = INSTANCIA.almacen;
    for (int i = 0; i < almacen.length; i++) {
      for (int j = 0; j < almacen[i].length; j++) {

        if (i == 0 || i == almacen.length - 1 || j == 0 || j == almacen[i].length - 1) {
          almacen[i][j] = new Celda(EstadoProducto.PARED);

        } else if (Utils.esPar(j) && j > 0 && i != 1 && i != almacen.length - 2) {

          almacen[i][j] = new Celda(EstadoProducto.ESTANTE);
          INSTANCIA.espaciosDisponibles += 1;
        } else {
          almacen[i][j] = new Celda(EstadoProducto.VACIO);
        }

      }
    }
  }

  public static void agregarProducto(Producto producto, int fila, int columna) {
    Celda[][] almacen = INSTANCIA.almacen;
    Celda celda = new Celda(producto, EstadoProducto.OCUPADO);
    almacen[fila][columna] = celda;
  }

  public static void eliminarProducto(int fila, int columna) {
    Celda[][] almacen = INSTANCIA.almacen;
    almacen[fila][columna].setEstado(EstadoProducto.VACIO);
  }

  public static void mostrarAlmacen() {

    Celda[][] almacen = INSTANCIA.almacen;
    for (int i = 0; i < almacen.length; i++) {
      for (int j = 0; j < almacen[i].length; j++) {

        Celda celda = almacen[i][j];

        if (celda.getEstado() == EstadoProducto.PARED) {
          System.out.print(Utils.GRIS + Utils.BLOQUE + Utils.BLOQUE + Utils.BLOQUE + Utils.BLOQUE + Utils.RESET);
        } else if (celda.getEstado() == EstadoProducto.ESTANTE) {

          System.out.print("[  ]");
        } else {
          System.out.print(Utils.TENUE + Utils.BLOQUE + Utils.BLOQUE + Utils.BLOQUE + Utils.BLOQUE + Utils.RESET);
        }
      }

      System.out.println();
    }

  }

}
