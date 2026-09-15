package org.Almacen;

import java.util.Scanner;
import org.Catalogo.*;

public class MainPrueba {

  public static final String RESET = "[0m";
  public static final String NEGRITA = "[1m";
  public static final String TENUE = "[2m";
  public static final String ROJO = "[31m";
  public static final String VERDE = "[32m";
  public static final String AMARILLO = "[33m";
  public static final String AZUL = "[34m";
  public static final String CIAN = "[36m";
  public static final String GRIS = "[90m";

  public static void main(String[] args) {

    Scanner sc = SharedScanner.getInstancia().getScanner();
    int numeroFilas = 0;
    int numeroColumnas = 0;
    while (numeroFilas < 5 && numeroColumnas < 5) {
      System.out.println("Dime el numero de filas que vas a querer para tu almacen:");
      numeroFilas = sc.nextInt();
      sc.nextLine();

      System.out.println("Dime el numero de columnas que vas a querer para tu almacen:");
      numeroColumnas = sc.nextInt();
      sc.nextLine();
    }
    Almacen almacen = Almacen.getInstancia(numeroFilas, numeroColumnas);

    Almacen.llenarAlmacen();

    Almacen.mostrarAlmacen();

    System.out.println("Espacios disponibles: " + almacen.espaciosDisponibles);
  }
}
