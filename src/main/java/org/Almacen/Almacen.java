package org.Almacen;

import org.Catalogo.Catalogo;
import org.Catalogo.Producto;
import org.Catalogo.Utils;

public class Almacen {

  public static final int MIN_FILAS = 5;
  public static final int MAX_FILAS = 20;
  public static final int MIN_COLUMNAS = 5;
  public static final int MAX_COLUMNAS = 30;

  private static Almacen INSTANCIA;
  private int filas;
  private int columnas;
  private Celda[][] almacen;
  private int pasillos;
  // La entrada está en la pared de abajo, en la primera columna interior
  private int entradaFila;
  private int entradaColumna;

  private Almacen(int filas, int columnas) {
    this.filas = filas;
    this.columnas = columnas;
    almacen = new Celda[filas][columnas];
    entradaFila = filas - 1;
    entradaColumna = 1;
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

  public static boolean existe() {
    return INSTANCIA != null;
  }

  // Crea (o reemplaza) el almacén con el tamaño indicado y lo deja listo para usar
  public static Almacen crearAlmacen(int filas, int columnas) {
    INSTANCIA = new Almacen(filas, columnas);
    llenarAlmacen();
    return INSTANCIA;
  }

  /*
   * Distribución del almacén (ejemplo 7x11, E = estante):
   *
   * ███████████ <- pared
   * █·········█ <- pasillo transversal
   * █·EE·EE·E·█ <- cada estante tiene un pasillo al lado
   * █·EE·EE·E·█
   * █·EE·EE·E·█
   * █·········█ <- pasillo transversal
   * █▲█████████ <- entrada
   *
   * Columnas interiores con patrón: pasillo, estante, estante, pasillo, ...
   */
  public static void llenarAlmacen() {

    Celda[][] almacen = INSTANCIA.almacen;
    int filas = INSTANCIA.filas;
    int columnas = INSTANCIA.columnas;
    INSTANCIA.pasillos = 0;

    for (int i = 0; i < filas; i++) {
      for (int j = 0; j < columnas; j++) {

        // Bordes -> pared
        if (i == 0 || i == filas - 1 || j == 0 || j == columnas - 1) {
          almacen[i][j] = new Celda(EstadoProducto.PARED);
          continue;
        }

        // Primera y última fila interior -> pasillos transversales que conectan todo
        if (i == 1 || i == filas - 2) {
          almacen[i][j] = new Celda(EstadoProducto.PASILLO);
          continue;
        }

        int k = j - 1; // posición dentro del interior
        int numeroPasillo = k / 3 + 1;
        boolean ultimaColumna = j == columnas - 2;

        if (k % 3 == 0) {
          almacen[i][j] = new Celda(EstadoProducto.PASILLO, numeroPasillo);
          INSTANCIA.pasillos = Math.max(INSTANCIA.pasillos, numeroPasillo);
        } else if (k % 3 == 2 && ultimaColumna) {
          // Un estante pegado a la pared no tendría acceso, se convierte en pasillo
          almacen[i][j] = new Celda(EstadoProducto.PASILLO, numeroPasillo + 1);
          INSTANCIA.pasillos = Math.max(INSTANCIA.pasillos, numeroPasillo + 1);
        } else if (k % 3 == 1) {
          // Estante que da al pasillo de su izquierda
          almacen[i][j] = new Celda(EstadoProducto.VACIO, numeroPasillo);
        } else {
          // Estante que da al pasillo de su derecha
          almacen[i][j] = new Celda(EstadoProducto.VACIO, numeroPasillo + 1);
        }
      }
    }

    almacen[INSTANCIA.entradaFila][INSTANCIA.entradaColumna] = new Celda(EstadoProducto.PASILLO);
  }

  public static boolean agregarProducto(Producto producto, int fila, int columna) {
    if (!posicionValida(fila, columna)) {
      Utils.error("La posición (" + fila + ", " + columna + ") está fuera del almacén.");
      return false;
    }

    Celda celda = INSTANCIA.almacen[fila][columna];
    if (celda.getEstado() != EstadoProducto.VACIO) {
      Utils.error("No se puede colocar ahí: la celda es " + celda.getEstado() + ".");
      return false;
    }

    celda.setProducto(producto);
    celda.setEstado(EstadoProducto.OCUPADO);
    return true;
  }

  public static Producto eliminarProducto(int fila, int columna) {
    if (!posicionValida(fila, columna)) {
      Utils.error("La posición (" + fila + ", " + columna + ") está fuera del almacén.");
      return null;
    }

    Celda celda = INSTANCIA.almacen[fila][columna];
    if (celda.getEstado() != EstadoProducto.OCUPADO) {
      Utils.error("En esa posición no hay ningún producto.");
      return null;
    }

    Producto producto = celda.getProducto();
    celda.setProducto(null);
    celda.setEstado(EstadoProducto.VACIO);
    return producto;
  }

  // Busca en la matriz la ubicación de un producto por su SKU. Devuelve {fila, columna} o null
  public static int[] buscarUbicacion(String sku) {
    Celda[][] almacen = INSTANCIA.almacen;
    for (int i = 0; i < almacen.length; i++) {
      for (int j = 0; j < almacen[i].length; j++) {
        Producto producto = almacen[i][j].getProducto();
        if (producto != null && producto.getSKU().equalsIgnoreCase(sku)) {
          return new int[] { i, j };
        }
      }
    }
    return null;
  }

  public static String describirUbicacion(int fila, int columna) {
    Celda celda = INSTANCIA.almacen[fila][columna];
    return "Pasillo " + celda.getPasillo() + " · fila " + fila + " · columna " + columna;
  }

  /*
   * Coloca automáticamente los productos del catálogo que aún no están en el
   * almacén. Los de mayor rotación van a las estanterías más cercanas a la
   * entrada para reducir los recorridos de picking.
   */
  public static int ubicarProductos(Catalogo catalogo) {
    Celda[][] almacen = INSTANCIA.almacen;

    // 1. Estanterías libres ordenadas por distancia a la entrada (insertion sort)
    int[][] libres = new int[INSTANCIA.filas * INSTANCIA.columnas][];
    int totalLibres = 0;
    for (int i = 0; i < almacen.length; i++) {
      for (int j = 0; j < almacen[i].length; j++) {
        if (almacen[i][j].getEstado() == EstadoProducto.VACIO) {
          int[] actual = { i, j };
          int pos = totalLibres - 1;
          while (pos >= 0 && distanciaEntrada(libres[pos]) > distanciaEntrada(actual)) {
            libres[pos + 1] = libres[pos];
            pos--;
          }
          libres[pos + 1] = actual;
          totalLibres++;
        }
      }
    }

    // 2. Productos ordenados por rotación (sin alterar el orden del catálogo original)
    Catalogo copia = catalogo.copiarCatalogo();
    copia.ordenarProductosPorRotacion();

    int colocados = 0;
    int siguienteLibre = 0;
    for (int p = 0; p < copia.numeroActualProductos; p++) {
      Producto producto = copia.getProducto(p);
      if (buscarUbicacion(producto.getSKU()) != null) {
        continue; // ya está colocado
      }
      if (siguienteLibre == totalLibres) {
        Utils.error("No hay más espacio: " + (copia.numeroActualProductos - p) + " producto(s) sin ubicar.");
        break;
      }
      int[] celda = libres[siguienteLibre++];
      agregarProducto(producto, celda[0], celda[1]);
      colocados++;
    }
    return colocados;
  }

  public static void mostrarAlmacen() {
    Celda[][] almacen = INSTANCIA.almacen;
    StringBuilder sb = new StringBuilder();

    // Cabecera: número de columna
    sb.append("    ");
    for (int j = 0; j < INSTANCIA.columnas; j++) {
      sb.append(Utils.GRIS).append(String.format("%-3d", j)).append(Utils.RESET);
    }
    sb.append('\n');

    // Cabecera: número de pasillo encima de cada pasillo vertical
    sb.append("    ");
    for (int j = 0; j < INSTANCIA.columnas; j++) {
      Celda celda = almacen[2][j];
      if (celda.getEstado() == EstadoProducto.PASILLO && celda.getPasillo() > 0) {
        sb.append(Utils.CIAN).append(Utils.NEGRITA)
            .append(String.format("%-3s", "P" + celda.getPasillo())).append(Utils.RESET);
      } else {
        sb.append("   ");
      }
    }
    sb.append('\n');

    for (int i = 0; i < INSTANCIA.filas; i++) {
      sb.append(Utils.GRIS).append(String.format("%2d  ", i)).append(Utils.RESET);
      for (int j = 0; j < INSTANCIA.columnas; j++) {
        sb.append(dibujarCelda(i, j));
      }
      sb.append('\n');
    }

    System.out.print(sb);
    System.out.println();
    System.out.println("  " + Utils.GRIS + "███" + Utils.RESET + " Pared   "
        + Utils.GRIS + " · " + Utils.RESET + " Pasillo   "
        + Utils.VERDE + Utils.NEGRITA + " ▲ " + Utils.RESET + " Entrada   "
        + Utils.TENUE + "[ ]" + Utils.RESET + " Libre   "
        + Utils.VERDE + "[A]" + Utils.RESET + Utils.AMARILLO + "[B]" + Utils.RESET
        + Utils.ROJO + "[C]" + Utils.RESET + " Ocupado (ABC)");

    int capacidad = contarEstado(EstadoProducto.VACIO) + contarEstado(EstadoProducto.OCUPADO);
    int ocupadas = contarEstado(EstadoProducto.OCUPADO);
    int porcentaje = capacidad == 0 ? 0 : ocupadas * 100 / capacidad;
    System.out.println("  Tamaño: " + INSTANCIA.filas + "x" + INSTANCIA.columnas
        + "  |  Pasillos: " + INSTANCIA.pasillos
        + "  |  Ocupación: " + ocupadas + "/" + capacidad + " (" + porcentaje + "%)");
  }

  // Lista los productos agrupados por pasillo
  public static void mostrarProductosPorPasillo() {
    Celda[][] almacen = INSTANCIA.almacen;
    for (int pasillo = 1; pasillo <= INSTANCIA.pasillos; pasillo++) {
      System.out.println(Utils.CIAN + Utils.NEGRITA + "Pasillo " + pasillo + Utils.RESET);
      boolean vacio = true;
      for (int i = 0; i < almacen.length; i++) {
        for (int j = 0; j < almacen[i].length; j++) {
          Celda celda = almacen[i][j];
          if (celda.getEstado() == EstadoProducto.OCUPADO && celda.getPasillo() == pasillo) {
            Producto p = celda.getProducto();
            System.out.printf("   (%2d,%2d)  %s%-10s%s %s%n", i, j,
                Utils.colorClasificacion(p.getClasificacion()), p.getSKU(), Utils.RESET, p.getNombre());
            vacio = false;
          }
        }
      }
      if (vacio) {
        System.out.println(Utils.GRIS + "   (sin productos)" + Utils.RESET);
      }
    }
  }

  private static String dibujarCelda(int fila, int columna) {
    if (fila == INSTANCIA.entradaFila && columna == INSTANCIA.entradaColumna) {
      return Utils.VERDE + Utils.NEGRITA + " ▲ " + Utils.RESET;
    }

    Celda celda = INSTANCIA.almacen[fila][columna];
    switch (celda.getEstado()) {
      case PARED:
        return Utils.GRIS + "███" + Utils.RESET;
      case PASILLO:
        return Utils.GRIS + " · " + Utils.RESET;
      case VACIO:
        return Utils.TENUE + "[ ]" + Utils.RESET;
      default:
        String clasificacion = celda.getProducto().getClasificacion();
        return Utils.colorClasificacion(clasificacion) + Utils.NEGRITA + "[" + clasificacion + "]" + Utils.RESET;
    }
  }

  private static int contarEstado(EstadoProducto estado) {
    int total = 0;
    for (Celda[] fila : INSTANCIA.almacen) {
      for (Celda celda : fila) {
        if (celda.getEstado() == estado) {
          total++;
        }
      }
    }
    return total;
  }

  private static int distanciaEntrada(int[] celda) {
    return Math.abs(celda[0] - INSTANCIA.entradaFila) + Math.abs(celda[1] - INSTANCIA.entradaColumna);
  }

  private static boolean posicionValida(int fila, int columna) {
    return fila >= 0 && fila < INSTANCIA.filas && columna >= 0 && columna < INSTANCIA.columnas;
  }

  public int getFilas() {
    return filas;
  }

  public int getColumnas() {
    return columnas;
  }

  public int getPasillos() {
    return pasillos;
  }
}
