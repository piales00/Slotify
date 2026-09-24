package org.slotify;

public class MainGuiado {

  private static final int TOTAL_PASOS = 13;
  private static final Catalogo catalogo = new Catalogo(50);
  private static final ListaPedidos pedidos = new ListaPedidos();

  public static void main(String[] args) {
    Utils.titulo("SLOTIFY · Recorrido guiado");
    System.out.println("Te acompañaremos paso a paso: crearás tu almacén, lo llenaremos de productos");
    System.out.println("probarás la actualización, las búsquedas, el ordenamiento, la eliminación y los pedidos.");
    Utils.pausa();

    pasoCrearAlmacen();
    pasoCargarCatalogo();
    pasoLlenarAlmacen();
    pasoProductosPorPasillo();
    pasoAgregarProducto();
    pasoActualizarProducto();
    pasoOrdenarCatalogo();
    pasoBuscarProducto();
    pasoBuscarUbicacion();
    pasoRetirarProducto();
    pasoEliminarProducto();
    pasoPedidos();
    pasoResumenFinal();

    Utils.exito("¡Recorrido completado! Gracias por usar Slotify.");
    SharedScanner.getInstancia().closeScanner();
  }

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

  private static void pasoCargarCatalogo() {
    encabezado(2, "CATÁLOGO DE EJEMPLO");
    System.out.println("El sistema carga un catálogo de productos de ejemplo:");
    System.out.println();
    DatosIniciales.cargarProductos(catalogo);
    catalogo.mostrarTabla();
    Utils.pausa();
  }

  private static void pasoLlenarAlmacen() {
    encabezado(3, "LLENAR EL ALMACÉN");
    Utils.info("Los productos con más rotación se colocan más cerca de la entrada (▲).");
    int colocados = Almacen.ubicarProductos(catalogo);
    Utils.exito(colocados + " producto(s) colocados.");
    System.out.println();
    Almacen.mostrarAlmacen();
    Utils.pausa();
  }

  private static void pasoProductosPorPasillo() {
    encabezado(4, "PRODUCTOS POR PASILLO");
    System.out.println("Así quedaron repartidos los productos en cada pasillo:");
    System.out.println();
    Almacen.mostrarProductosPorPasillo();
    Utils.pausa();
  }

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
    System.out.println();
    System.out.println("Así queda el almacén con tu producto:");
    Almacen.mostrarAlmacen();
    Utils.pausa();
  }

  private static void pasoActualizarProducto() {
    encabezado(6, "ACTUALIZAR UN PRODUCTO");
    if (!Utils.confirmar("¿Quieres actualizar los datos de un producto?")) {
      Utils.info("Paso omitido.");
      Utils.pausa();
      return;
    }

    System.out.println("Catálogo antes de actualizar:");
    catalogo.mostrarTabla();
    System.out.println();

    Producto original = null;
    while (original == null) {
      String sku = Utils.leerTexto("SKU del producto a actualizar (ej: SKU-001): ");
      original = catalogo.buscarProductoPorSKU(sku);
      if (original == null) {
        Utils.error("No existe ningún producto con el SKU " + sku.toUpperCase() + ". Inténtalo de nuevo.");
      }
    }

    System.out.println("Introduce los nuevos datos de " + original.getNombre() + ":");
    Producto nuevo = Utils.crearNuevoProducto();
    Producto existente = catalogo.buscarProductoPorSKU(nuevo.getSKU());
    while (existente != null && existente != original) {
      Utils.error("Ya existe otro producto con el SKU " + nuevo.getSKU() + ".");
      nuevo.setSKU(Utils.leerTexto("Introduce otro SKU: ").toUpperCase());
      existente = catalogo.buscarProductoPorSKU(nuevo.getSKU());
    }

    int[] ubicacion = Almacen.buscarUbicacion(original.getSKU());
    catalogo.actualizarProducto(original, nuevo);
    if (ubicacion != null) {
      Almacen.eliminarProducto(ubicacion[0], ubicacion[1]);
      Almacen.agregarProducto(nuevo, ubicacion[0], ubicacion[1]);
      Utils.info("También se actualizó en " + Almacen.describirUbicacion(ubicacion[0], ubicacion[1]) + ".");
    }
    Utils.exito("Producto actualizado.");

    System.out.println();
    System.out.println("Catálogo después de actualizar:");
    catalogo.mostrarTabla();
    Utils.pausa();
  }

  private static void pasoOrdenarCatalogo() {
    encabezado(7, "ORDENAR EL CATÁLOGO");

    do {
      System.out.println("Catálogo antes de ordenar:");
      catalogo.mostrarTabla();
      System.out.println();
      System.out.println("¿Por qué criterio quieres ordenar el catálogo?");
      System.out.println("  1) Por nombre         (Bubble sort)");
      System.out.println("  2) Por rotación       (Selection sort)");
      System.out.println("  3) Por clasificación  (Insertion sort)");
      System.out.println("  4) Por peso           (Shell sort)");
      System.out.println("  5) Por SKU            (Merge sort)");
      System.out.println("  6) Por volumen        (Quick sort)");
      System.out.println("  7) Por familia        (Heap sort)");
      int criterio = Utils.leerEntero("Criterio: ", 1, 7);

      switch (criterio) {
        case 1 -> catalogo.ordenarProductosPorNombre();
        case 2 -> catalogo.ordenarProductosPorRotacion();
        case 3 -> catalogo.ordenarProductosPorClasificacion();
        case 4 -> catalogo.ordenarProductosPorPeso();
        case 5 -> catalogo.ordenarProductosPorSKU();
        case 6 -> catalogo.ordenarProductosPorVolumen();
        case 7 -> catalogo.ordenarProductosPorFamilia();
      }
      System.out.println();
      Utils.exito("Catálogo después de ordenar:");
      catalogo.mostrarTabla();
      System.out.println();
    } while (Utils.confirmar("¿Quieres probar otro ordenamiento?"));
    Utils.pausa();
  }

  private static void pasoBuscarProducto() {
    encabezado(8, "BUSCAR UN PRODUCTO POR NOMBRE");

    do {
      System.out.println("¿Qué tipo de búsqueda quieres usar?");
      System.out.println("  1) Búsqueda lineal   (revisa los productos uno por uno)");
      System.out.println("  2) Búsqueda binaria  (necesita el catálogo ordenado por nombre)");
      int tipo = Utils.leerEntero("Tipo: ", 1, 2);
      if (tipo == 2) {
        Utils.info("Si el catálogo no está ordenado por nombre, se ordena primero.");
      }

      String nombre = Utils.leerTexto("¿Qué producto quieres buscar? (nombre exacto): ");
      ResultadoBusqueda resultado = tipo == 1
          ? catalogo.buscarProductoLineal(nombre)
          : catalogo.buscarProductoBinario(nombre);
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

  private static void pasoBuscarUbicacion() {
    encabezado(9, "BUSCAR UBICACIÓN EN EL ALMACÉN");
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

  private static void pasoRetirarProducto() {
    encabezado(10, "RETIRAR UN PRODUCTO DE SU UBICACIÓN");
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

  private static void pasoEliminarProducto() {
    encabezado(11, "ELIMINAR UN PRODUCTO DEL CATÁLOGO");
    System.out.println("Catálogo antes de eliminar:");
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

    System.out.println();
    System.out.println("Catálogo después de eliminar:");
    catalogo.mostrarTabla();
    Utils.pausa();
  }

  private static void pasoPedidos() {
    encabezado(12, "PEDIDOS (LISTA ENLAZADA)");
    System.out.println("Los pedidos no se guardan en un array como el catálogo, sino en una lista enlazada:");
    System.out.println("cada nodo guarda un pedido y apunta al siguiente, así que la lista crece y");
    System.out.println("decrece sin tener un tamaño máximo fijo.");
    System.out.println();
    DatosIniciales.cargarPedidos(pedidos);
    Utils.info("Se cargaron " + pedidos.getNumeroActualPedidos() + " pedidos de ejemplo. Puedes insertar y eliminar los que quieras.");
    System.out.println();

    boolean continuar = true;
    while (continuar) {
      System.out.println("Pedidos registrados (" + pedidos.getNumeroActualPedidos() + "):");
      pedidos.mostrarTabla();
      if (!pedidos.estaVacia()) {
        System.out.println();
        pedidos.mostrarEnlaces();
      }
      System.out.println();
      System.out.println("¿Qué quieres hacer?");
      System.out.println("  1) Insertar un pedido al inicio de la lista");
      System.out.println("  2) Insertar un pedido al final de la lista");
      System.out.println("  3) Eliminar un pedido por su código");
      System.out.println("  4) Terminar con los pedidos");
      int opcion = Utils.leerEntero("Opción: ", 1, 4);

      switch (opcion) {
        case 1 -> insertarPedido(true);
        case 2 -> insertarPedido(false);
        case 3 -> eliminarPedido();
        case 4 -> continuar = false;
      }
      System.out.println();
    }
    Utils.pausa();
  }

  private static void insertarPedido(boolean alInicio) {
    if (catalogo.numeroActualProductos == 0) {
      Utils.error("El catálogo está vacío, no hay nada que pedir.");
      return;
    }

    String codigo = Utils.leerTexto("Código del pedido (ej: PED-001): ").toUpperCase();
    while (pedidos.existeCodigo(codigo)) {
      Utils.error("Ya existe un pedido con el código " + codigo + ".");
      codigo = Utils.leerTexto("Introduce otro código: ").toUpperCase();
    }

    String cliente = Utils.leerTexto("Cliente: ");

    Producto producto = null;
    while (producto == null) {
      String sku = Utils.leerTexto("SKU del producto pedido (ej: SKU-001): ");
      producto = catalogo.buscarProductoPorSKU(sku);
      if (producto == null) {
        Utils.error("No existe ningún producto con ese SKU en el catálogo. Inténtalo de nuevo.");
      }
    }

    int cantidad = Utils.leerEntero("Cantidad: ", 1, Integer.MAX_VALUE);
    Pedido pedido = new Pedido(codigo, cliente, producto.getSKU(), cantidad);

    if (alInicio) {
      pedidos.insertarAlInicio(pedido);
      Utils.exito("Pedido " + codigo + " insertado al inicio: ahora es la cabeza de la lista.");
    } else {
      pedidos.insertarAlFinal(pedido);
      Utils.exito("Pedido " + codigo + " insertado al final: se enganchó al último nodo.");
    }
  }

  private static void eliminarPedido() {
    if (pedidos.estaVacia()) {
      Utils.error("No hay pedidos que eliminar.");
      return;
    }

    String codigo = Utils.leerTexto("Código del pedido a eliminar: ");
    Pedido eliminado = pedidos.eliminarPorCodigo(codigo);
    if (eliminado == null) {
      Utils.error("No existe ningún pedido con el código " + codigo.toUpperCase() + ".");
      return;
    }
    Utils.exito("Pedido eliminado: " + eliminado);
    Utils.info("El nodo anterior pasó a apuntar al siguiente, así que el nodo eliminado queda fuera.");
  }

  private static void pasoResumenFinal() {
    encabezado(13, "ESTADO FINAL");
    System.out.println("Así terminan tu catálogo, tu almacén y tus pedidos:");
    System.out.println();
    catalogo.mostrarTabla();
    System.out.println();
    Almacen.mostrarAlmacen();
    System.out.println();
    pedidos.mostrarTabla();
    System.out.println();
  }

  private static void encabezado(int paso, String titulo) {
    Utils.titulo("PASO " + paso + "/" + TOTAL_PASOS + " · " + titulo);
  }
}
