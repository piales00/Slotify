package org.App;

import org.Almacen.Almacen;
import org.Catalogo.Catalogo;
import org.Catalogo.Producto;
import org.Catalogo.ResultadoBusqueda;
import org.Catalogo.SharedScanner;
import org.Catalogo.Utils;

public class Main {

  private static final Catalogo catalogo = new Catalogo(50);

  public static void main(String[] args) {
    cargarDatosIniciales();

    Utils.titulo("SLOTIFY · Gestión de almacén");
    Utils.info("Se cargaron " + catalogo.numeroActualProductos + " productos y un almacén de 10x14.");

    boolean salir = false;
    while (!salir) {
      mostrarMenu();
      int opcion = Utils.leerEntero("Elige una opción: ", 0, 12);

      switch (opcion) {
        case 1 -> crearAlmacen();
        case 2 -> verAlmacen();
        case 3 -> ubicarAutomaticamente();
        case 4 -> colocarManualmente();
        case 5 -> retirarDelAlmacen();
        case 6 -> buscarEnAlmacen();
        case 7 -> verProductosPorPasillo();
        case 8 -> verCatalogo();
        case 9 -> agregarProducto();
        case 10 -> eliminarProducto();
        case 11 -> ordenarCatalogo();
        case 12 -> buscarBinaria();
        case 0 -> salir = true;
      }

      if (!salir) {
        Utils.pausa();
      }
    }

    Utils.exito("¡Hasta luego!");
    SharedScanner.getInstancia().closeScanner();
  }

  private static void mostrarMenu() {
    Utils.titulo("MENÚ PRINCIPAL");
    System.out.println(Utils.NEGRITA + "  ALMACÉN" + Utils.RESET);
    System.out.println("   1) Crear nuevo almacén");
    System.out.println("   2) Ver mapa del almacén");
    System.out.println("   3) Ubicar productos automáticamente (por rotación)");
    System.out.println("   4) Colocar producto manualmente");
    System.out.println("   5) Retirar producto de una ubicación");
    System.out.println("   6) Buscar ubicación de un producto");
    System.out.println("   7) Ver productos por pasillo");
    System.out.println(Utils.NEGRITA + "  CATÁLOGO" + Utils.RESET);
    System.out.println("   8) Ver catálogo");
    System.out.println("   9) Agregar producto");
    System.out.println("  10) Eliminar producto");
    System.out.println("  11) Ordenar catálogo");
    System.out.println("  12) Buscar producto por nombre (búsqueda binaria)");
    System.out.println();
    System.out.println("   0) Salir");
  }

  // ---------------- Datos iniciales ----------------

  private static void cargarDatosIniciales() {
    catalogo.agregarProducto(new Producto("Filtro de aceite", "SKU-001", "Filtros", 0.02, 0.5, 120, "A", "Manual"));
    catalogo.agregarProducto(new Producto("Motor eléctrico", "SKU-002", "Motores", 0.40, 35.0, 15, "B", "Montacargas"));
    catalogo.agregarProducto(new Producto("Tornillos M8 (caja)", "SKU-003", "Ferretería", 0.01, 2.0, 200, "A", "Manual"));
    catalogo.agregarProducto(new Producto("Batería 12V", "SKU-004", "Eléctrico", 0.03, 18.5, 60, "B", "Manual"));
    catalogo.agregarProducto(new Producto("Neumático", "SKU-005", "Ruedas", 0.15, 12.0, 45, "B", "Manual"));
    catalogo.agregarProducto(new Producto("Bomba hidráulica", "SKU-006", "Hidráulica", 0.25, 48.0, 8, "C", "Montacargas"));
    catalogo.agregarProducto(new Producto("Bobina de cable", "SKU-007", "Eléctrico", 0.10, 25.0, 30, "B", "Montacargas"));
    catalogo.agregarProducto(new Producto("Aceite de motor 5L", "SKU-008", "Lubricantes", 0.006, 4.5, 150, "A", "Manual"));
    catalogo.agregarProducto(new Producto("Compresor industrial", "SKU-009", "Neumática", 1.20, 180.0, 3, "C", "Grúa"));
    catalogo.agregarProducto(new Producto("Guantes de trabajo", "SKU-010", "EPP", 0.002, 0.2, 180, "A", "Manual"));
    catalogo.agregarProducto(new Producto("Rodamientos", "SKU-011", "Ferretería", 0.004, 1.2, 90, "A", "Manual"));
    catalogo.agregarProducto(new Producto("Transformador", "SKU-012", "Eléctrico", 0.80, 320.0, 2, "C", "Grúa"));

    Almacen.crearAlmacen(10, 14);
    Almacen.ubicarProductos(catalogo);
  }

  // ---------------- Almacén ----------------

  private static void crearAlmacen() {
    Utils.titulo("CREAR ALMACÉN");
    if (Almacen.existe()) {
      Utils.info("El almacén actual se reemplazará y quedará vacío.");
    }

    int filas = Utils.leerEntero("Número de filas (" + Almacen.MIN_FILAS + "-" + Almacen.MAX_FILAS + "): ",
        Almacen.MIN_FILAS, Almacen.MAX_FILAS);
    int columnas = Utils.leerEntero("Número de columnas (" + Almacen.MIN_COLUMNAS + "-" + Almacen.MAX_COLUMNAS + "): ",
        Almacen.MIN_COLUMNAS, Almacen.MAX_COLUMNAS);

    Almacen.crearAlmacen(filas, columnas);
    Utils.exito("Almacén de " + filas + "x" + columnas + " creado.");
    Almacen.mostrarAlmacen();
  }

  private static void verAlmacen() {
    if (!hayAlmacen()) {
      return;
    }
    Utils.titulo("MAPA DEL ALMACÉN");
    Almacen.mostrarAlmacen();
  }

  private static void ubicarAutomaticamente() {
    if (!hayAlmacen()) {
      return;
    }
    Utils.titulo("UBICACIÓN AUTOMÁTICA");
    int colocados = Almacen.ubicarProductos(catalogo);
    Utils.exito(colocados + " producto(s) colocados. Los de mayor rotación quedan más cerca de la entrada.");
    Almacen.mostrarAlmacen();
  }

  private static void colocarManualmente() {
    if (!hayAlmacen()) {
      return;
    }
    Utils.titulo("COLOCAR PRODUCTO");
    Almacen.mostrarAlmacen();

    Producto producto = pedirProductoPorSKU();
    if (producto == null) {
      return;
    }
    int[] actual = Almacen.buscarUbicacion(producto.getSKU());
    if (actual != null) {
      Utils.error("Ese producto ya está en " + Almacen.describirUbicacion(actual[0], actual[1]) + ".");
      return;
    }

    int fila = Utils.leerEntero("Fila: ", 0, Almacen.getInstancia().getFilas() - 1);
    int columna = Utils.leerEntero("Columna: ", 0, Almacen.getInstancia().getColumnas() - 1);
    if (Almacen.agregarProducto(producto, fila, columna)) {
      Utils.exito(producto.getNombre() + " colocado en " + Almacen.describirUbicacion(fila, columna) + ".");
    }
  }

  private static void retirarDelAlmacen() {
    if (!hayAlmacen()) {
      return;
    }
    Utils.titulo("RETIRAR PRODUCTO");
    Almacen.mostrarAlmacen();

    int fila = Utils.leerEntero("Fila: ", 0, Almacen.getInstancia().getFilas() - 1);
    int columna = Utils.leerEntero("Columna: ", 0, Almacen.getInstancia().getColumnas() - 1);
    Producto retirado = Almacen.eliminarProducto(fila, columna);
    if (retirado != null) {
      Utils.exito(retirado.getNombre() + " retirado de la ubicación.");
    }
  }

  private static void buscarEnAlmacen() {
    if (!hayAlmacen()) {
      return;
    }
    Utils.titulo("BUSCAR EN ALMACÉN");
    String sku = Utils.leerTexto("SKU del producto: ");
    int[] ubicacion = Almacen.buscarUbicacion(sku);

    if (ubicacion == null) {
      Utils.error("El producto " + sku.toUpperCase() + " no está ubicado en el almacén.");
    } else {
      Utils.exito("Encontrado en " + Almacen.describirUbicacion(ubicacion[0], ubicacion[1]) + ".");
    }
  }

  private static void verProductosPorPasillo() {
    if (!hayAlmacen()) {
      return;
    }
    Utils.titulo("PRODUCTOS POR PASILLO");
    Almacen.mostrarProductosPorPasillo();
  }

  // ---------------- Catálogo ----------------

  private static void verCatalogo() {
    Utils.titulo("CATÁLOGO DE PRODUCTOS");
    catalogo.mostrarTabla();
  }

  private static void agregarProducto() {
    Utils.titulo("AGREGAR PRODUCTO");
    Producto producto = Utils.crearNuevoProducto();

    if (catalogo.buscarProductoPorSKU(producto.getSKU()) != null) {
      Utils.error("Ya existe un producto con el SKU " + producto.getSKU() + ".");
      return;
    }
    if (catalogo.agregarProducto(producto)) {
      Utils.exito(producto.getNombre() + " agregado al catálogo.");
    }
  }

  private static void eliminarProducto() {
    Utils.titulo("ELIMINAR PRODUCTO");
    String nombre = Utils.leerTexto("Nombre del producto a eliminar: ");
    Producto eliminado = catalogo.eliminarProducto(nombre);

    if (eliminado == null) {
      Utils.error("No existe ningún producto llamado \"" + nombre + "\".");
      return;
    }

    // Si estaba en el almacén, también se retira de su estantería
    if (Almacen.existe()) {
      int[] ubicacion = Almacen.buscarUbicacion(eliminado.getSKU());
      if (ubicacion != null) {
        Almacen.eliminarProducto(ubicacion[0], ubicacion[1]);
        Utils.info("También se retiró del almacén.");
      }
    }
    Utils.exito(eliminado.getNombre() + " eliminado del catálogo.");
  }

  private static void ordenarCatalogo() {
    Utils.titulo("ORDENAR CATÁLOGO");
    System.out.println("  1) Por nombre         (Bubble sort)");
    System.out.println("  2) Por rotación       (Selection sort)");
    System.out.println("  3) Por clasificación  (Insertion sort)");
    System.out.println("  4) Por peso           (Shell sort)");
    int opcion = Utils.leerEntero("Criterio: ", 1, 4);

    switch (opcion) {
      case 1 -> catalogo.ordenarProductosPorNombre();
      case 2 -> catalogo.ordenarProductosPorRotacion();
      case 3 -> catalogo.ordenarProductosPorClasificacion();
      case 4 -> catalogo.ordenarProductosPorPeso();
    }
    Utils.exito("Catálogo ordenado.");
    catalogo.mostrarTabla();
  }

  private static void buscarBinaria() {
    Utils.titulo("BÚSQUEDA BINARIA");
    Utils.info("El catálogo se ordena por nombre antes de buscar (requisito de la búsqueda binaria).");
    String nombre = Utils.leerTexto("Nombre exacto del producto: ");
    ResultadoBusqueda resultado = catalogo.buscarProductoBinario(nombre);

    Producto producto = resultado.getProducto();
    if (producto == null) {
      Utils.error("No encontrado tras " + resultado.getComparaciones() + " comparación(es).");
      return;
    }

    Utils.exito("Encontrado en " + resultado.getComparaciones() + " comparación(es) de "
        + catalogo.numeroActualProductos + " productos.");
    System.out.println("  " + producto);
    if (Almacen.existe()) {
      int[] ubicacion = Almacen.buscarUbicacion(producto.getSKU());
      System.out.println("  Ubicación: " + (ubicacion == null ? "sin ubicar"
          : Almacen.describirUbicacion(ubicacion[0], ubicacion[1])));
    }
  }

  // ---------------- Auxiliares ----------------

  private static boolean hayAlmacen() {
    if (!Almacen.existe()) {
      Utils.error("Primero debes crear un almacén (opción 1).");
      return false;
    }
    return true;
  }

  private static Producto pedirProductoPorSKU() {
    String sku = Utils.leerTexto("SKU del producto: ");
    Producto producto = catalogo.buscarProductoPorSKU(sku);
    if (producto == null) {
      Utils.error("No existe ningún producto con SKU " + sku.toUpperCase() + ".");
    }
    return producto;
  }
}
