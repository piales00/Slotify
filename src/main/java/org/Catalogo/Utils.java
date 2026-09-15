package org.Catalogo;

import java.util.Scanner;

public class Utils {

  // Colores ANSI para la consola
  public static final String RESET = "[0m";
  public static final String NEGRITA = "[1m";
  public static final String TENUE = "[2m";
  public static final String ROJO = "[31m";
  public static final String VERDE = "[32m";
  public static final String AMARILLO = "[33m";
  public static final String AZUL = "[34m";
  public static final String CIAN = "[36m";
  public static final String GRIS = "[90m";

  public static Producto crearNuevoProducto() {
    Producto p = new Producto();

    p.setNombre(leerTexto("Nombre del producto: "));
    p.setSKU(leerTexto("SKU: ").toUpperCase());
    p.setFamilia(leerTexto("Familia: "));
    p.setVolumen(leerDecimal("Volumen (m3): ", 0));
    p.setPeso(leerDecimal("Peso (kg): ", 0));
    p.setRotacion(leerEntero("Rotación (unidades/mes): ", 0, Integer.MAX_VALUE));
    p.setClasificacion(leerClasificacion());

    System.out.println("Herramienta de picking:  1) Manual   2) Montacargas   3) Grúa");
    int herramienta = leerEntero("Opción: ", 1, 3);
    String[] herramientas = { "Manual", "Montacargas", "Grúa" };
    p.setHerramienta(herramientas[herramienta - 1]);

    return p;
  }

  public static boolean esPar(int valor) {

    if (valor % 2 == 0) {
      return true;
    }
    return false;
  }

  // ---------------- Lectura de datos validada ----------------

  public static String leerTexto(String mensaje) {
    Scanner sc = SharedScanner.getInstancia().getScanner();
    while (true) {
      System.out.print(mensaje);
      String texto = sc.nextLine().trim();
      if (!texto.isEmpty()) {
        return texto;
      }
      error("El valor no puede estar vacío.");
    }
  }

  public static int leerEntero(String mensaje, int min, int max) {
    while (true) {
      String texto = leerTexto(mensaje);
      try {
        int valor = Integer.parseInt(texto);
        if (valor >= min && valor <= max) {
          return valor;
        }
        error("Debe estar entre " + min + " y " + max + ".");
      } catch (NumberFormatException e) {
        error("Introduce un número entero.");
      }
    }
  }

  public static double leerDecimal(String mensaje, double min) {
    while (true) {
      String texto = leerTexto(mensaje).replace(',', '.');
      try {
        double valor = Double.parseDouble(texto);
        if (valor >= min) {
          return valor;
        }
        error("Debe ser mayor o igual a " + min + ".");
      } catch (NumberFormatException e) {
        error("Introduce un número (ej: 2.5).");
      }
    }
  }

  public static String leerClasificacion() {
    while (true) {
      String texto = leerTexto("Clasificación ABC (A/B/C): ").toUpperCase();
      if (texto.equals("A") || texto.equals("B") || texto.equals("C")) {
        return texto;
      }
      error("Solo se permite A, B o C.");
    }
  }

  public static void pausa() {
    System.out.print(GRIS + "\nPresiona ENTER para continuar..." + RESET);
    SharedScanner.getInstancia().getScanner().nextLine();
  }

  // ---------------- Mensajes con formato ----------------

  public static void titulo(String texto) {
    String linea = "═".repeat(texto.length() + 4);
    System.out.println();
    System.out.println(CIAN + "╔" + linea + "╗");
    System.out.println("║  " + NEGRITA + texto + RESET + CIAN + "  ║");
    System.out.println("╚" + linea + "╝" + RESET);
  }

  public static void exito(String texto) {
    System.out.println(VERDE + "✔ " + texto + RESET);
  }

  public static void error(String texto) {
    System.out.println(ROJO + "✘ " + texto + RESET);
  }

  public static void info(String texto) {
    System.out.println(AZUL + "ℹ " + texto + RESET);
  }

  public static String colorClasificacion(String clasificacion) {
    if (clasificacion == null) {
      return GRIS;
    }
    switch (clasificacion) {
      case "A":
        return VERDE;
      case "B":
        return AMARILLO;
      default:
        return ROJO;
    }
  }
}
