package org.slotify;

public class MainFachada {

  private static final Slotify sistema = new Slotify();

  public static void main(String[] args) {
    Utils.titulo("SLOTIFY · Menú (patrón Fachada)");
    System.out.println("Todo lo que verás pasa por una sola clase: Slotify.");
    System.out.println("El main sólo pregunta y muestra; la fachada coordina catálogo, almacén y pedidos.");
    System.out.println();

    prepararSistema();

    int opcion;
    do {
      mostrarMenu();
      opcion = Utils.leerEntero("Elige una opción: ", 0, 20);
      System.out.println();
      ejecutar(opcion);
      if (opcion != 0) {
        Utils.pausa();
      }
    } while (opcion != 0);

    Utils.exito("¡Hasta luego! Gracias por usar Slotify.");
    sistema.cerrar();
  }

  private static void prepararSistema() {
    Utils.titulo("CREAR TU ALMACÉN");
    System.out.println("Los bordes serán paredes y el sistema reparte pasillos y estanterías automáticamente.");
    int filas = Utils.leerEntero("¿Cuántas filas quieres (" + Slotify.MIN_FILAS + "-" + Slotify.MAX_FILAS + ")? ",
        Slotify.MIN_FILAS, Slotify.MAX_FILAS);
    int columnas = Utils.leerEntero(
        "¿Cuántas columnas quieres (" + Slotify.MIN_COLUMNAS + "-" + Slotify.MAX_COLUMNAS + ")? ",
        Slotify.MIN_COLUMNAS, Slotify.MAX_COLUMNAS);

    sistema.crearAlmacen(filas, columnas);
    sistema.cargarCatalogoDeEjemplo();
    int colocados = sistema.llenarAlmacen();
    sistema.cargarPedidosDeEjemplo();

    Utils.exito("Almacén de " + filas + "x" + columnas + " creado con " + sistema.totalProductos()
        + " productos de ejemplo (" + colocados + " colocados) y " + sistema.totalPedidos() + " pedidos.");
    Utils.info("Los productos con más rotación se colocaron más cerca de la entrada (▲).");
    System.out.println();
    sistema.mostrarAlmacen();
    Utils.pausa();
  }

  private static void mostrarMenu() {
    System.out.println();
    Utils.titulo("MENÚ PRINCIPAL");
    System.out.println(" ALMACÉN");
    System.out.println("   1) Ver almacén");
    System.out.println("   2) Ver productos por pasillo");
    System.out.println("   3) Buscar ubicación de un producto");
    System.out.println("   4) Colocar un producto del catálogo");
    System.out.println("   5) Retirar un producto de su ubicación");
    System.out.println(" DESHACER (pila estática · " + sistema.cambiosEnHistorial() + "/"
        + sistema.capacidadHistorial() + ")");
    System.out.println("   6) Deshacer último cambio");
    System.out.println("   7) Ver pila de cambios");
    System.out.println(" CATÁLOGO (arreglo)");
    System.out.println("   8) Ver catálogo");
    System.out.println("   9) Agregar y colocar un producto nuevo");
    System.out.println("  10) Actualizar un producto");
    System.out.println("  11) Ordenar el catálogo");
    System.out.println("  12) Buscar un producto por nombre");
    System.out.println("  13) Eliminar un producto del catálogo");
    System.out.println(" PEDIDOS (lista enlazada)");
    System.out.println("  14) Ver pedidos");
    System.out.println("  15) Registrar un pedido");
    System.out.println("  16) Eliminar un pedido");
    System.out.println("  17) Recorrer la lista");
    System.out.println("  18) Buscar un pedido");
    System.out.println("  19) Ordenar los pedidos");
    System.out.println(" RUTA DE PICKING (lista circular)");
    System.out.println("  20) Armar y recorrer la ruta");
    System.out.println();
    System.out.println("   0) Salir");
  }

  private static void ejecutar(int opcion) {
    switch (opcion) {
      case 1 -> verAlmacen();
      case 2 -> verProductosPorPasillo();
      case 3 -> buscarUbicacion();
      case 4 -> colocarProductoDelCatalogo();
      case 5 -> retirarProducto();
      case 6 -> deshacer();
      case 7 -> verPilaDeCambios();
      case 8 -> verCatalogo();
      case 9 -> agregarProducto();
      case 10 -> actualizarProducto();
      case 11 -> ordenarCatalogo();
      case 12 -> buscarProducto();
      case 13 -> eliminarProducto();
      case 14 -> mostrarPedidos("Pedidos");
      case 15 -> registrarPedido();
      case 16 -> eliminarPedido();
      case 17 -> recorrerPedidos();
      case 18 -> buscarPedido();
      case 19 -> ordenarPedidos();
      case 20 -> rutaPicking();
      default -> {
      }
    }
  }

  private static void verAlmacen() {
    Utils.titulo("ALMACÉN");
    sistema.mostrarAlmacen();
  }

  private static void verProductosPorPasillo() {
    Utils.titulo("PRODUCTOS POR PASILLO");
    sistema.mostrarProductosPorPasillo();
  }

  private static void buscarUbicacion() {
    Utils.titulo("BUSCAR UBICACIÓN EN EL ALMACÉN");
    String sku = Utils.leerTexto("SKU (ej: SKU-003): ");
    String ubicacion = sistema.describirUbicacionDe(sku);
    if (ubicacion == null) {
      Utils.error("El producto " + sku.toUpperCase() + " no está en el almacén.");
    } else {
      Utils.exito("Está en " + ubicacion + ".");
    }
  }

  private static void colocarProductoDelCatalogo() {
    Utils.titulo("COLOCAR UN PRODUCTO DEL CATÁLOGO");
    if (sistema.estantesLibres() == 0) {
      Utils.error("El almacén está lleno, no hay estanterías libres.");
      return;
    }

    String sku = Utils.leerTexto("SKU del producto a colocar (ej: SKU-001): ");
    Producto producto = sistema.buscarPorSKU(sku);
    if (producto == null) {
      Utils.error("No existe ningún producto con el SKU " + sku.toUpperCase() + ".");
      return;
    }
    if (sistema.ubicacionDe(producto.getSKU()) != null) {
      Utils.error(producto.getNombre() + " ya está en " + sistema.describirUbicacionDe(producto.getSKU())
          + ". Retíralo primero (opción 5).");
      return;
    }

    elegirCeldaYColocar(producto);
  }

  private static void elegirCeldaYColocar(Producto producto) {
    System.out.println();
    System.out.println("Elige dónde colocarlo. Solo se puede en una estantería libre [ ].");
    sistema.mostrarAlmacen();
    boolean colocado = false;
    while (!colocado) {
      int fila = Utils.leerEntero("Fila: ", 0, sistema.getFilas() - 1);
      int columna = Utils.leerEntero("Columna: ", 0, sistema.getColumnas() - 1);
      colocado = sistema.colocarProducto(producto, fila, columna);
      if (colocado) {
        Utils.exito(producto.getNombre() + " colocado en " + sistema.describirUbicacionDe(producto.getSKU()) + ".");
        mostrarCambioApilado();
      } else {
        Utils.info("Inténtalo de nuevo.");
      }
    }
    System.out.println();
    sistema.mostrarAlmacen();
  }

  private static void retirarProducto() {
    Utils.titulo("RETIRAR UN PRODUCTO DE SU UBICACIÓN");
    if (sistema.estantesOcupados() == 0) {
      Utils.info("No hay productos en el almacén.");
      return;
    }

    System.out.println("Elige la fila y columna de una estantería ocupada para retirar su producto.");
    sistema.mostrarAlmacen();
    Producto retirado = null;
    while (retirado == null) {
      int fila = Utils.leerEntero("Fila: ", 0, sistema.getFilas() - 1);
      int columna = Utils.leerEntero("Columna: ", 0, sistema.getColumnas() - 1);
      retirado = sistema.retirarDeUbicacion(fila, columna);
      if (retirado == null) {
        Utils.info("Inténtalo de nuevo.");
      }
    }
    Utils.exito(retirado.getNombre() + " retirado. Sigue en el catálogo, pero ya no ocupa lugar.");
    mostrarCambioApilado();
    System.out.println();
    sistema.mostrarAlmacen();
  }

  private static void mostrarCambioApilado() {
    Utils.info("Apilado para deshacer: " + sistema.ultimoCambio() + "  ("
        + sistema.cambiosEnHistorial() + "/" + sistema.capacidadHistorial() + " en la pila)");
  }

  private static void deshacer() {
    Utils.titulo("DESHACER ÚLTIMO CAMBIO (PILA ESTÁTICA)");
    System.out.println("Se saca el último cambio que entró (LIFO) y se aplica la operación contraria.");
    System.out.println();
    System.out.println("Pila antes de deshacer:");
    sistema.mostrarHistorial();
    System.out.println();

    if (!sistema.hayCambios()) {
      Utils.info("No hay cambios que deshacer. Coloca o retira un producto primero (opciones 4, 5 o 9).");
      return;
    }

    Cambio deshecho = sistema.deshacer();
    if (deshecho != null) {
      Utils.exito("Deshecho: " + deshecho);
      System.out.println();
      sistema.mostrarAlmacen();
    }
    System.out.println();
    System.out.println("Pila después de deshacer:");
    sistema.mostrarHistorial();
  }

  private static void verPilaDeCambios() {
    Utils.titulo("PILA DE CAMBIOS");
    System.out.println("Arreglo de tamaño fijo: el tope es el último cambio que entró y el primero que sale.");
    System.out.println();
    sistema.mostrarHistorial();
  }

  private static void verCatalogo() {
    Utils.titulo("CATÁLOGO");
    sistema.mostrarCatalogo();
  }

  private static void agregarProducto() {
    Utils.titulo("AGREGAR Y COLOCAR UN PRODUCTO NUEVO");
    Producto producto = Utils.crearNuevoProducto();
    while (!sistema.registrarProducto(producto)) {
      if (!sistema.existeSKU(producto.getSKU())) {
        return;
      }
      producto.setSKU(Utils.leerTexto("Introduce otro SKU: ").toUpperCase());
    }
    Utils.exito(producto.getNombre() + " agregado al catálogo.");

    if (sistema.estantesLibres() == 0) {
      Utils.error("El almacén está lleno, no se puede colocar en una estantería.");
      return;
    }
    elegirCeldaYColocar(producto);
  }

  private static void actualizarProducto() {
    Utils.titulo("ACTUALIZAR UN PRODUCTO");
    System.out.println("Catálogo antes de actualizar:");
    sistema.mostrarCatalogo();
    System.out.println();

    Producto original = null;
    while (original == null) {
      String sku = Utils.leerTexto("SKU del producto a actualizar (ej: SKU-001): ");
      original = sistema.buscarPorSKU(sku);
      if (original == null) {
        Utils.error("No existe ningún producto con el SKU " + sku.toUpperCase() + ". Inténtalo de nuevo.");
      }
    }

    System.out.println("Introduce los nuevos datos de " + original.getNombre() + ":");
    Producto nuevo = Utils.crearNuevoProducto();

    Slotify.CambioProducto cambio = sistema.actualizarProducto(original, nuevo);
    while (!cambio.actualizado()) {
      nuevo.setSKU(Utils.leerTexto("Introduce otro SKU: ").toUpperCase());
      cambio = sistema.actualizarProducto(original, nuevo);
    }
    if (cambio.seSincronizoAlmacen()) {
      Utils.info("También se actualizó en " + cambio.ubicacionSincronizada() + ".");
    }
    Utils.exito("Producto actualizado.");

    System.out.println();
    System.out.println("Catálogo después de actualizar:");
    sistema.mostrarCatalogo();
  }

  private static void ordenarCatalogo() {
    Utils.titulo("ORDENAR EL CATÁLOGO");
    System.out.println("¿Por qué criterio quieres ordenar el catálogo?");
    Slotify.OrdenCatalogo[] criterios = Slotify.OrdenCatalogo.values();
    for (int i = 0; i < criterios.length; i++) {
      System.out.printf("  %d) %-20s (%s)%n", i + 1, criterios[i].getCriterio(), criterios[i].getAlgoritmo());
    }
    int opcion = Utils.leerEntero("Criterio: ", 1, criterios.length);

    sistema.ordenarCatalogo(criterios[opcion - 1]);
    System.out.println();
    Utils.exito("Catálogo ordenado " + criterios[opcion - 1].getCriterio().toLowerCase()
        + " · " + criterios[opcion - 1].getAlgoritmo() + ":");
    sistema.mostrarCatalogo();
  }

  private static void buscarProducto() {
    Utils.titulo("BUSCAR UN PRODUCTO POR NOMBRE");
    System.out.println("¿Qué tipo de búsqueda quieres usar?");
    System.out.println("  1) Búsqueda lineal   (revisa los productos uno por uno)");
    System.out.println("  2) Búsqueda binaria  (necesita el catálogo ordenado por nombre)");
    boolean binaria = Utils.leerEntero("Tipo: ", 1, 2) == 2;
    if (binaria) {
      Utils.info("Si el catálogo no está ordenado por nombre, se ordena primero.");
    }

    String nombre = Utils.leerTexto("¿Qué producto quieres buscar? (nombre exacto): ");
    ResultadoBusqueda resultado = sistema.buscarProducto(nombre, binaria);

    if (resultado.getProducto() == null) {
      Utils.error("No encontrado tras " + resultado.getComparaciones() + " comparación(es).");
    } else {
      Utils.exito("Encontrado en " + resultado.getComparaciones() + " comparación(es) de "
          + sistema.totalProductos() + " productos: " + resultado.getProducto());
    }
  }

  private static void eliminarProducto() {
    Utils.titulo("ELIMINAR UN PRODUCTO DEL CATÁLOGO");
    sistema.mostrarCatalogo();
    System.out.println();

    Slotify.BajaProducto baja = sistema.eliminarProducto(Utils.leerTexto("Nombre del producto a eliminar: "));
    if (!baja.seEncontro()) {
      Utils.error("No existe ningún producto con ese nombre.");
      return;
    }
    if (baja.seLibero()) {
      Utils.info("También se retiró de " + baja.ubicacionLiberada() + ".");
    }
    Utils.exito(baja.producto().getNombre() + " eliminado del catálogo.");
  }

  private static void registrarPedido() {
    Utils.titulo("REGISTRAR UN PEDIDO");
    System.out.println("  1) Al inicio  (será la nueva cabeza de la lista)");
    System.out.println("  2) Al final   (se recorre hasta el último nodo y se engancha ahí)");
    boolean alInicio = Utils.leerEntero("¿Dónde lo insertas? ", 1, 2) == 1;

    Pedido pedido = null;
    while (pedido == null) {
      String codigo = Utils.leerTexto("Código del pedido (ej: PED-010): ").toUpperCase();
      String cliente = Utils.leerTexto("Cliente: ");
      String sku = Utils.leerTexto("SKU del producto pedido (ej: SKU-001): ");
      int cantidad = Utils.leerEntero("Cantidad: ", 1, Integer.MAX_VALUE);

      pedido = sistema.registrarPedido(codigo, cliente, sku, cantidad, alInicio);
      if (pedido == null) {
        Utils.info("Inténtalo de nuevo.");
      }
    }

    if (alInicio) {
      Utils.exito("Pedido " + pedido.getCodigo() + " insertado al inicio: ahora es la cabeza de la lista.");
    } else {
      Utils.exito("Pedido " + pedido.getCodigo() + " insertado al final: se enganchó al último nodo.");
    }
    mostrarPedidos("Lista actual");
  }

  private static void eliminarPedido() {
    Utils.titulo("ELIMINAR UN PEDIDO");
    if (sistema.sinPedidos()) {
      Utils.info("No hay pedidos.");
      return;
    }

    mostrarPedidos("Pedidos");
    System.out.println("El nodo anterior pasará a apuntar al siguiente.");
    String codigo = Utils.leerTexto("Código del pedido a eliminar: ");
    Pedido eliminado = sistema.eliminarPedido(codigo);
    if (eliminado == null) {
      Utils.error("No existe ningún pedido con el código " + codigo.toUpperCase() + ".");
      return;
    }
    Utils.exito("Pedido eliminado: " + eliminado);
    mostrarPedidos("Lista tras eliminar");
  }

  private static void recorrerPedidos() {
    Utils.titulo("RECORRIDO DE LA LISTA");
    System.out.println("No hay índices: se empieza en la cabeza y se siguen los enlaces uno a uno.");
    System.out.println();
    sistema.recorrerPedidos();

    System.out.println();
    Utils.info("Nodos contados siguiendo enlaces: " + sistema.contarNodos()
        + "  (el contador dice " + sistema.totalPedidos() + ")");
    Utils.info("Unidades pedidas en total: " + sistema.totalUnidadesPedidas());

    if (!sistema.sinPedidos()) {
      int posicion = Utils.leerEntero("¿Qué posición quieres leer? (0 a "
          + (sistema.totalPedidos() - 1) + "): ", 0, sistema.totalPedidos() - 1);
      Utils.exito("nodo[" + posicion + "] = " + sistema.pedidoEnPosicion(posicion));
      Utils.info("Costó " + (posicion + 1) + " salto(s). En un arreglo habría sido un acceso directo.");
    }
  }

  private static void buscarPedido() {
    Utils.titulo("BÚSQUEDA EN LA LISTA");
    System.out.println("Solo puede ser lineal: para llegar al nodo del medio ya habría que");
    System.out.println("recorrer media lista, así que la búsqueda binaria no aporta nada aquí.");
    System.out.println();
    System.out.println("  1) Por código");
    System.out.println("  2) Por SKU");

    if (Utils.leerEntero("Buscar: ", 1, 2) == 1) {
      String codigo = Utils.leerTexto("Código del pedido a buscar (ej: PED-002): ");
      ResultadoBusquedaPedido resultado = sistema.buscarPedidoPorCodigo(codigo);

      if (resultado.getPedido() == null) {
        Utils.error("No existe ningún pedido con ese código.");
        Utils.info("Se recorrió la lista entera: " + resultado.getComparaciones() + " comparación(es).");
      } else {
        Utils.exito("Encontrado: " + resultado.getPedido());
        Utils.info("Estaba en la posición " + resultado.getPosicion()
            + " y costó " + resultado.getComparaciones() + " comparación(es).");
      }
      return;
    }

    String sku = Utils.leerTexto("SKU para buscar el primer pedido que lo pide (ej: SKU-001): ");
    Pedido porSKU = sistema.buscarPedidoPorSKU(sku);
    if (porSKU == null) {
      Utils.error("Ningún pedido de la lista incluye ese SKU.");
    } else {
      Utils.exito("Primer pedido con ese SKU: " + porSKU);
      Utils.info("Pedidos de " + porSKU.getCliente() + ": " + sistema.contarPedidosDeCliente(porSKU.getCliente()));
    }
  }

  private static void ordenarPedidos() {
    Utils.titulo("ORDENAR LA LISTA");
    System.out.println("Una lista se puede ordenar de dos formas: cambiando los enlaces entre");
    System.out.println("nodos, o dejando los nodos quietos e intercambiando los datos.");
    System.out.println();

    Slotify.OrdenPedidos[] criterios = Slotify.OrdenPedidos.values();
    for (int i = 0; i < criterios.length; i++) {
      System.out.printf("  %d) %-14s · %-38s · %s%n", i + 1,
          criterios[i].getCriterio(), criterios[i].getAlgoritmo(), criterios[i].getComplejidad());
    }
    int opcion = Utils.leerEntero("Elige un ordenamiento (1-" + criterios.length + "): ", 1, criterios.length);

    Slotify.OrdenPedidos orden = criterios[opcion - 1];
    sistema.ordenarPedidos(orden);
    Utils.exito("Lista ordenada " + orden.getCriterio().toLowerCase() + " · " + orden.getAlgoritmo() + ".");
    mostrarPedidos("Lista ordenada");
  }

  private static void mostrarPedidos(String titulo) {
    System.out.println();
    System.out.println(titulo + " (" + sistema.totalPedidos() + "):");
    sistema.mostrarPedidos();
    if (!sistema.sinPedidos()) {
      System.out.println();
      sistema.mostrarEnlaces();
    }
  }

  private static void rutaPicking() {
    Utils.titulo("RUTA DE PICKING (LISTA CIRCULAR)");
    System.out.println("El operario sale del dock (▲), pasa por la ubicación de cada pedido y vuelve al");
    System.out.println("dock. Por eso la ruta es una lista circular: la última parada apunta al dock.");
    System.out.println();

    int omitidos = sistema.armarRuta();
    if (omitidos > 0) {
      Utils.info(omitidos + " pedido(s) quedaron fuera porque su producto no está en el almacén.");
    }
    if (sistema.paradasEnRuta() == 0) {
      Utils.info("No hay paradas: el operario no tiene que salir del dock.");
      return;
    }

    mostrarRuta("Ruta en el orden de los pedidos");
    int distanciaInicial = sistema.distanciaRuta();

    System.out.println();
    System.out.println("Ahora se ordena en serpentina (S-shape): pasillo por pasillo, subiendo en uno y");
    System.out.println("bajando en el siguiente, para no tener que retroceder.");
    sistema.ordenarRuta();
    mostrarRuta("Ruta en serpentina");
    Utils.info("Distancia: " + distanciaInicial + " → " + sistema.distanciaRuta() + " celda(s).");
    Utils.pausa();

    System.out.println();
    Utils.titulo("RECORRIDO DE LA RUTA");
    System.out.println("Se empieza en el dock y se siguen los enlaces hasta volver a él: no hay null.");
    System.out.println();
    sistema.recorrerRuta();

    System.out.println();
    System.out.println("Al recoger un pedido su parada sale de la ruta: la anterior pasa a apuntar a la siguiente.");
    while (sistema.paradasEnRuta() > 0 && Utils.confirmar("¿Quieres recoger una parada?")) {
      String codigo = Utils.leerTexto("Código del pedido recogido (ej: PED-002): ");
      Parada recogida = sistema.recogerParada(codigo);
      if (recogida == null) {
        Utils.error("Ese pedido no está en la ruta.");
      } else {
        Utils.exito("Recogido: " + recogida);
        mostrarRuta("Ruta restante");
      }
    }
  }

  private static void mostrarRuta(String titulo) {
    System.out.println();
    System.out.println(titulo + " (" + sistema.paradasEnRuta() + " parada(s), "
        + sistema.distanciaRuta() + " celda(s) en total):");
    sistema.mostrarRuta();
  }
}
