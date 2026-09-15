package org.App;

import org.Almacen.Almacen;
import org.Catalogo.Catalogo;
import org.Catalogo.Producto;
import org.Catalogo.ResultadoBusqueda;
import org.Catalogo.SharedScanner;
import org.Catalogo.Utils;

/*
 * Versión sin menú: recorre todas las funciones del sistema paso a paso,
 * pidiendo datos al usuario en cada etapa.
 */
public class MainGuiado {

  private static final int TOTAL_PASOS = 11;
  private static final Catalogo catalogo = new Catalogo(50);

  public static void main(String[] args) {
    Utils.titulo("SLOTIFY · Recorrido guiado");
    System.out.println("Te acompañaremos paso a paso: crearás tu almacén, lo llenaremos de productos");
    System.out.println("y probarás las búsquedas, el ordenamiento y la eliminación.");
    Utils.pausa();

    pasoCrearAlmacen();
    pasoCargarCatalogo();
    pasoLlenarAlmacen();
    pasoProductosPorPasillo();
    pasoAgregarProducto();
    pasoOrdenarCatalogo();
    pasoBusquedaBinaria();
    pasoBuscarUbicacion();
    pasoRetirarProducto();
    pasoEliminarProducto();
    pasoResumenFinal();

    Utils.exito("¡Recorrido completado! Gracias por usar Slotify.");
    SharedScanner.getInstancia().closeScanner();
  }

  // PASO 1
  private static void pasoCrearAlmacen() {
    encabezado(1, "CREAR TU ALMACÉN");
    System.out.println("Primero vamos a definir el tamaño de tu almacén.");
    System.out.println("Los bordes serán paredes y el sistema reparte pasillos y estanterías automáticamente.");

    int filas = Utils.leerEntero("¿Cuántas filas quieres (" + Almacen.MIN_FILAS + "-" + Almacen.MAX_FILAS + ")? ",
        Almacen.MIN_FILAS, Almacen.MAX_FILAS);
    int columnas = Utils.leerEntero(
        "¿Cuántas columnas quieres (" + Almacen.MIN_COLUMNAS + "-" + Almacen.MAX_COLUMNAS + ")? ",
        Almacen.MIN_COLUMNAS, Almacen.MAX_COLUMNAS);

    Almacen.crearAlmacen(filas, columnas);
    Utils.exito("Almacén de " + filas + "x" + columnas + " creado. Así se ve vacío:");
    System.out.println();
    Almacen.mostrarAlmacen();
    Utils.pausa();
  }

  // PASO 2
  private static void pasoCargarCatalogo() {
    encabezado(2, "CATÁLOGO DE EJEMPLO");
    System.out.println("El sistema carga un catálogo de productos de ejemplo:");
    System.out.println();
    DatosIniciales.cargarProductos(catalogo);
    catalogo.mostrarTabla();
    Utils.pausa();
  }

  // PASO 3
  private static void pasoLlenarAlmacen() {
    encabezado(3, "LLENAR EL ALMACÉN");
    Utils.info("Los productos con más rotación se colocan más cerca de la entrada (▲).");
    int colocados = Almacen.ubicarProductos(catalogo);
    Utils.exito(colocados + " producto(s) colocados.");
    System.out.println();
    Almacen.mostrarAlmacen();
    Utils.pausa();
  }

  // PASO 4
  private static void pasoProductosPorPasillo() {
    encabezado(4, "PRODUCTOS POR PASILLO");
    System.out.println("Así quedaron repartidos los productos en cada pasillo:");
    System.out.println();
    Almacen.mostrarProductosPorPasillo();
    Utils.pausa();
  }

  // PASO 5
  private static void pasoAgregarProducto() {
    encabezado(5, "AGREGAR Y COLOCAR TU PRODUCTO");
    if (!Utils.confirmar("¿Quieres registrar un producto propio?")) {
      Utils.info("Paso omitido.");
      Utils.pausa();
      return;
    }

    Producto producto = Utils.crearNuevoProducto();
    while (catalogo.buscarProductoPorSKU(producto.getSKU()) != null) {
      Utils.error("Ya existe un producto con el SKU " + producto.getSKU() + ".");
      producto.setSKU(Utils.leerTexto("Introduce otro SKU: ").toUpperCase());
    }
    if (!catalogo.agregarProducto(producto)) {
      Utils.pausa();
      return;
    }
    Utils.exito(producto.getNombre() + " agregado al catálogo.");

    if (Almacen.estantesLibres() == 0) {
      Utils.error("El almacén está lleno, no se puede colocar en una estantería.");
      Utils.pausa();
      return;
    }

    System.out.println();
    System.out.println("Ahora elige dónde colocarlo. Solo se puede en una estantería libre [ ].");
    Almacen.mostrarAlmacen();
    boolean colocado = false;
    while (!colocado) {
      int fila = Utils.leerEntero("Fila: ", 0, Almacen.getInstancia().getFilas() - 1);
      int columna = Utils.leerEntero("Columna: ", 0, Almacen.getInstancia().getColumnas() - 1);
      colocado = Almacen.agregarProducto(producto, fila, columna);
      if (colocado) {
        Utils.exito(producto.getNombre() + " colocado en " + Almacen.describirUbicacion(fila, columna) + ".");
      } else {
        Utils.info("Inténtalo de nuevo.");
      }
    }
    Utils.pausa();
  }

  // PASO 6
  private static void pasoOrdenarCatalogo() {
    encabezado(6, "ORDENAR EL CATÁLOGO");
    System.out.println("¿Por qué criterio quieres ordenar el catálogo?");
    System.out.println("  1) Por nombre         (Bubble sort)");
    System.out.println("  2) Por rotación       (Selection sort)");
    System.out.println("  3) Por clasificación  (Insertion sort)");
    System.out.println("  4) Por peso           (Shell sort)");
    int criterio = Utils.leerEntero("Criterio: ", 1, 4);

    switch (criterio) {
      case 1 -> catalogo.ordenarProductosPorNombre();
      case 2 -> catalogo.ordenarProductosPorRotacion();
      case 3 -> catalogo.ordenarProductosPorClasificacion();
      case 4 -> catalogo.ordenarProductosPorPeso();
    }
    Utils.exito("Catálogo ordenado:");
    System.out.println();
    catalogo.mostrarTabla();
    Utils.pausa();
  }

  // PASO 7
  private static void pasoBusquedaBinaria() {
    encabezado(7, "BÚSQUEDA BINARIA POR NOMBRE");
    Utils.info("La búsqueda binaria necesita el catálogo ordenado por nombre; si no lo está, se ordena primero.");

    do {
      String nombre = Utils.leerTexto("¿Qué producto quieres buscar? (nombre exacto): ");
      ResultadoBusqueda resultado = catalogo.buscarProductoBinario(nombre);
      Producto producto = resultado.getProducto();

      if (producto == null) {
        Utils.error("No encontrado tras " + resultado.getComparaciones() + " comparación(es).");
      } else {
        Utils.exito("Encontrado en " + resultado.getComparaciones() + " comparación(es) de "
            + catalogo.numeroActualProductos + " productos: " + producto);
      }
    } while (Utils.confirmar("¿Quieres buscar otro?"));
    Utils.pausa();
  }

  // PASO 8
  private static void pasoBuscarUbicacion() {
    encabezado(8, "BUSCAR UBICACIÓN EN EL ALMACÉN");
    System.out.println("Busca en qué pasillo está un producto usando su SKU (ej: SKU-003).");

    do {
      String sku = Utils.leerTexto("SKU: ");
      int[] ubicacion = Almacen.buscarUbicacion(sku);
      if (ubicacion == null) {
        Utils.error("El producto " + sku.toUpperCase() + " no está en el almacén.");
      } else {
        Utils.exito("Está en " + Almacen.describirUbicacion(ubicacion[0], ubicacion[1]) + ".");
      }
    } while (Utils.confirmar("¿Quieres buscar otro?"));
    Utils.pausa();
  }

  // PASO 9
  private static void pasoRetirarProducto() {
    encabezado(9, "RETIRAR UN PRODUCTO DE SU UBICACIÓN");
    if (Almacen.estantesOcupados() == 0) {
      Utils.info("No hay productos en el almacén. Paso omitido.");
      Utils.pausa();
      return;
    }

    System.out.println("Elige la fila y columna de una estantería ocupada para retirar su producto.");
    Almacen.mostrarAlmacen();
    Producto retirado = null;
    while (retirado == null) {
      int fila = Utils.leerEntero("Fila: ", 0, Almacen.getInstancia().getFilas() - 1);
      int columna = Utils.leerEntero("Columna: ", 0, Almacen.getInstancia().getColumnas() - 1);
      retirado = Almacen.eliminarProducto(fila, columna);
      if (retirado == null) {
        Utils.info("Inténtalo de nuevo.");
      }
    }
    Utils.exito(retirado.getNombre() + " retirado. Sigue en el catálogo, pero ya no ocupa lugar:");
    System.out.println();
    Almacen.mostrarAlmacen();
    Utils.pausa();
  }

  // PASO 10
  private static void pasoEliminarProducto() {
    encabezado(10, "ELIMINAR UN PRODUCTO DEL CATÁLOGO");
    System.out.println("Productos actuales:");
    catalogo.mostrarTabla();
    System.out.println();

    Producto eliminado = null;
    while (eliminado == null) {
      String nombre = Utils.leerTexto("Nombre del producto a eliminar: ");
      eliminado = catalogo.eliminarProducto(nombre);
      if (eliminado == null) {
        Utils.error("No existe ningún producto llamado \"" + nombre + "\". Inténtalo de nuevo.");
      }
    }

    int[] ubicacion = Almacen.buscarUbicacion(eliminado.getSKU());
    if (ubicacion != null) {
      Almacen.eliminarProducto(ubicacion[0], ubicacion[1]);
      Utils.info("También se retiró de " + Almacen.describirUbicacion(ubicacion[0], ubicacion[1]) + ".");
    }
    Utils.exito(eliminado.getNombre() + " eliminado del catálogo.");
    Utils.pausa();
  }

  // PASO 11
  private static void pasoResumenFinal() {
    encabezado(11, "ESTADO FINAL");
    System.out.println("Así terminan tu catálogo y tu almacén:");
    System.out.println();
    catalogo.mostrarTabla();
    System.out.println();
    Almacen.mostrarAlmacen();
    System.out.println();
  }

  private static void encabezado(int paso, String titulo) {
    Utils.titulo("PASO " + paso + "/" + TOTAL_PASOS + " · " + titulo);
  }
}
