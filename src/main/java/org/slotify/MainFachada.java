package org.slotify;

/**
 * Recorrido guiado escrito SOBRE LA FACHADA.
 *
 * Compáralo con MainGuiado: ahí el main conocía Catalogo, Almacen,
 * ListaPedidos y DatosIniciales, y además orquestaba a mano las operaciones
 * que tocaban dos subsistemas. Aquí el main sólo hace interfaz —imprimir y
 * leer— y todas las llamadas pasan por un único objeto: Slotify.
 */
public class MainFachada {

  private static final int TOTAL_PASOS = 15;

  /** La única referencia al sistema. El main no ve los subsistemas. */
  private static final Slotify sistema = new Slotify();

  public static void main(String[] args) {
    Utils.titulo("SLOTIFY · Recorrido guiado (patrón Fachada)");
    System.out.println("Todo lo que verás pasa por una sola clase: Slotify.");
    System.out.println("El main sólo pregunta y muestra; la fachada coordina catálogo, almacén y pedidos.");
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
    pasoDeshacer();
    pasoEliminarProducto();
    pasoPedidos();
    pasoRutaPicking();
    pasoResumenFinal();

    Utils.exito("¡Recorrido completado! Gracias por usar Slotify.");
    sistema.cerrar();
  }

  private static void pasoCrearAlmacen() {
    encabezado(1, "CREAR TU ALMACÉN");
    System.out.println("Primero vamos a definir el tamaño de tu almacén.");
    System.out.println("Los bordes serán paredes y el sistema reparte pasillos y estanterías automáticamente.");

    int filas = Utils.leerEntero("¿Cuántas filas quieres (" + Slotify.MIN_FILAS + "-" + Slotify.MAX_FILAS + ")? ",
        Slotify.MIN_FILAS, Slotify.MAX_FILAS);
    int columnas = Utils.leerEntero(
        "¿Cuántas columnas quieres (" + Slotify.MIN_COLUMNAS + "-" + Slotify.MAX_COLUMNAS + ")? ",
        Slotify.MIN_COLUMNAS, Slotify.MAX_COLUMNAS);

    sistema.crearAlmacen(filas, columnas);
    Utils.exito("Almacén de " + filas + "x" + columnas + " creado. Así se ve vacío:");
    System.out.println();
    sistema.mostrarAlmacen();
    Utils.pausa();
  }

  private static void pasoCargarCatalogo() {
    encabezado(2, "CATÁLOGO DE EJEMPLO");
    System.out.println("La fachada carga el catálogo de ejemplo con una sola llamada:");
    System.out.println();
    sistema.cargarCatalogoDeEjemplo();
    sistema.mostrarCatalogo();
    Utils.pausa();
  }

  private static void pasoLlenarAlmacen() {
    encabezado(3, "LLENAR EL ALMACÉN");
    Utils.info("Los productos con más rotación se colocan más cerca de la entrada (▲).");
    int colocados = sistema.llenarAlmacen();
    Utils.exito(colocados + " producto(s) colocados.");
    System.out.println();
    sistema.mostrarAlmacen();
    Utils.pausa();
  }

  private static void pasoProductosPorPasillo() {
    encabezado(4, "PRODUCTOS POR PASILLO");
    System.out.println("Así quedaron repartidos los productos en cada pasillo:");
    System.out.println();
    sistema.mostrarProductosPorPasillo();
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
    // La fachada valida el SKU repetido; el main sólo insiste hasta que pase.
    while (!sistema.registrarProducto(producto)) {
      if (!sistema.existeSKU(producto.getSKU())) {
        Utils.pausa();
        return;
      }
      producto.setSKU(Utils.leerTexto("Introduce otro SKU: ").toUpperCase());
    }
    Utils.exito(producto.getNombre() + " agregado al catálogo.");

    if (sistema.estantesLibres() == 0) {
      Utils.error("El almacén está lleno, no se puede colocar en una estantería.");
      Utils.pausa();
      return;
    }

    System.out.println();
    System.out.println("Ahora elige dónde colocarlo. Solo se puede en una estantería libre [ ].");
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
    System.out.println("Así queda el almacén con tu producto:");
    sistema.mostrarAlmacen();
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

    // Una sola llamada: la fachada sincroniza catálogo y almacén.
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
    Utils.pausa();
  }

  private static void pasoOrdenarCatalogo() {
    encabezado(7, "ORDENAR EL CATÁLOGO");

    do {
      System.out.println("Catálogo antes de ordenar:");
      sistema.mostrarCatalogo();
      System.out.println();
      System.out.println("¿Por qué criterio quieres ordenar el catálogo?");
      // El menú se construye desde la fachada: el main no sabe qué algoritmos hay.
      Slotify.OrdenCatalogo[] criterios = Slotify.OrdenCatalogo.values();
      for (int i = 0; i < criterios.length; i++) {
        System.out.printf("  %d) %-20s (%s)%n", i + 1, criterios[i].getCriterio(), criterios[i].getAlgoritmo());
      }
      int opcion = Utils.leerEntero("Criterio: ", 1, criterios.length);

      sistema.ordenarCatalogo(criterios[opcion - 1]);
      System.out.println();
      Utils.exito("Catálogo después de ordenar:");
      sistema.mostrarCatalogo();
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
      boolean binaria = tipo == 2;
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
    } while (Utils.confirmar("¿Quieres buscar otro?"));
    Utils.pausa();
  }

  private static void pasoBuscarUbicacion() {
    encabezado(9, "BUSCAR UBICACIÓN EN EL ALMACÉN");
    System.out.println("Busca en qué pasillo está un producto usando su SKU (ej: SKU-003).");

    do {
      String sku = Utils.leerTexto("SKU: ");
      String ubicacion = sistema.describirUbicacionDe(sku);
      if (ubicacion == null) {
        Utils.error("El producto " + sku.toUpperCase() + " no está en el almacén.");
      } else {
        Utils.exito("Está en " + ubicacion + ".");
      }
    } while (Utils.confirmar("¿Quieres buscar otro?"));
    Utils.pausa();
  }

  private static void pasoRetirarProducto() {
    encabezado(10, "RETIRAR UN PRODUCTO DE SU UBICACIÓN");
    if (sistema.estantesOcupados() == 0) {
      Utils.info("No hay productos en el almacén. Paso omitido.");
      Utils.pausa();
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
    Utils.exito(retirado.getNombre() + " retirado. Sigue en el catálogo, pero ya no ocupa lugar:");
    mostrarCambioApilado();
    System.out.println();
    sistema.mostrarAlmacen();
    Utils.pausa();
  }

  private static void pasoEliminarProducto() {
    encabezado(12, "ELIMINAR UN PRODUCTO DEL CATÁLOGO");
    System.out.println("Catálogo antes de eliminar:");
    sistema.mostrarCatalogo();
    System.out.println();

    // Una sola llamada da de baja en el catálogo y libera la celda.
    Slotify.BajaProducto baja = sistema.eliminarProducto(Utils.leerTexto("Nombre del producto a eliminar: "));
    while (!baja.seEncontro()) {
      Utils.error("No existe ningún producto con ese nombre. Inténtalo de nuevo.");
      baja = sistema.eliminarProducto(Utils.leerTexto("Nombre del producto a eliminar: "));
    }
    if (baja.seLibero()) {
      Utils.info("También se retiró de " + baja.ubicacionLiberada() + ".");
    }
    Utils.exito(baja.producto().getNombre() + " eliminado del catálogo.");

    System.out.println();
    System.out.println("Catálogo después de eliminar:");
    sistema.mostrarCatalogo();
    Utils.pausa();
  }

  private static void pasoPedidos() {
    encabezado(13, "PEDIDOS (LISTA ENLAZADA)");
    System.out.println("Los pedidos se guardan en una lista enlazada: cada nodo lleva un pedido y");
    System.out.println("apunta al siguiente, así que la lista crece y decrece sin un tamaño fijo.");
    System.out.println();

    sistema.cargarPedidosDeEjemplo();
    mostrarPedidos("Pedidos de ejemplo");

    System.out.println();
    System.out.println("Primero vas a insertar un pedido al inicio: será la nueva cabeza de la lista.");
    insertarPedido(true);
    mostrarPedidos("Lista tras insertar al inicio");

    System.out.println();
    System.out.println("Ahora uno al final: se recorre la lista hasta el último nodo y se engancha ahí.");
    insertarPedido(false);
    mostrarPedidos("Lista tras insertar al final");

    System.out.println();
    System.out.println("Ahora elimina un pedido: el nodo anterior pasará a apuntar al siguiente.");
    eliminarPedido();
    mostrarPedidos("Lista tras eliminar");
    Utils.pausa();

    recorrerPedidos();
    buscarPedido();
    ordenarPedidos();
    Utils.pausa();
  }

  private static void recorrerPedidos() {
    System.out.println();
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
    System.out.println();
    Utils.titulo("BÚSQUEDA EN LA LISTA");
    System.out.println("Solo puede ser lineal: para llegar al nodo del medio ya habría que");
    System.out.println("recorrer media lista, así que la búsqueda binaria no aporta nada aquí.");
    System.out.println();

    boolean repetir = true;
    while (repetir) {
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

      repetir = Utils.confirmar("¿Quieres buscar otro pedido?");
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
    System.out.println();
    Utils.titulo("ORDENAR LA LISTA");
    System.out.println("Una lista se puede ordenar de dos formas: cambiando los enlaces entre");
    System.out.println("nodos, o dejando los nodos quietos e intercambiando los datos.");

    Slotify.OrdenPedidos[] criterios = Slotify.OrdenPedidos.values();
    boolean repetir = true;
    while (repetir) {
      System.out.println();
      for (int i = 0; i < criterios.length; i++) {
        System.out.printf("  %d) %-14s · %-38s · %s%n", i + 1,
            criterios[i].getCriterio(), criterios[i].getAlgoritmo(), criterios[i].getComplejidad());
      }
      int opcion = Utils.leerEntero("Elige un ordenamiento (1-" + criterios.length + "): ", 1, criterios.length);

      Slotify.OrdenPedidos orden = criterios[opcion - 1];
      sistema.ordenarPedidos(orden);
      Utils.exito("Lista ordenada " + orden.getCriterio().toLowerCase() + " · " + orden.getAlgoritmo() + ".");

      mostrarPedidos("Lista ordenada");
      repetir = Utils.confirmar("¿Quieres probar otro ordenamiento?");
    }
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

  private static void insertarPedido(boolean alInicio) {
    Pedido pedido = null;
    while (pedido == null) {
      String codigo = Utils.leerTexto("Código del pedido (ej: PED-010): ").toUpperCase();
      String cliente = Utils.leerTexto("Cliente: ");
      String sku = Utils.leerTexto("SKU del producto pedido (ej: SKU-001): ");
      int cantidad = Utils.leerEntero("Cantidad: ", 1, Integer.MAX_VALUE);

      // La fachada valida el código repetido y que el SKU exista en el catálogo.
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
  }

  private static void eliminarPedido() {
    Pedido eliminado = null;
    while (eliminado == null) {
      String codigo = Utils.leerTexto("Código del pedido a eliminar: ");
      eliminado = sistema.eliminarPedido(codigo);
      if (eliminado == null) {
        Utils.error("No existe ningún pedido con el código " + codigo.toUpperCase() + ". Inténtalo de nuevo.");
      }
    }
    Utils.exito("Pedido eliminado: " + eliminado);
  }

  private static void mostrarCambioApilado() {
    Utils.info("Apilado para deshacer: " + sistema.ultimoCambio() + "  ("
        + sistema.cambiosEnHistorial() + "/" + sistema.capacidadHistorial() + " en la pila)");
  }

  private static void pasoDeshacer() {
    encabezado(11, "DESHACER CAMBIOS (PILA ESTÁTICA)");
    System.out.println("Cada vez que colocas o retiras un producto, el cambio se apila en un arreglo de");
    System.out.println("tamaño fijo. Deshacer saca el último que entró (LIFO) y aplica la operación contraria.");
    System.out.println();
    System.out.println("Pila de cambios:");
    sistema.mostrarHistorial();

    while (sistema.hayCambios() && Utils.confirmar("¿Quieres deshacer el último cambio?")) {
      Cambio deshecho = sistema.deshacer();
      if (deshecho != null) {
        Utils.exito("Deshecho: " + deshecho);
        System.out.println();
        sistema.mostrarAlmacen();
      }
      System.out.println();
      System.out.println("Pila de cambios:");
      sistema.mostrarHistorial();
    }

    if (!sistema.hayCambios()) {
      Utils.info("La pila está vacía: no quedan cambios que deshacer.");
    }
    Utils.pausa();
  }

  private static void pasoRutaPicking() {
    encabezado(14, "RUTA DE PICKING (LISTA CIRCULAR)");
    System.out.println("El operario sale del dock (▲), pasa por la ubicación de cada pedido y vuelve al");
    System.out.println("dock. Por eso la ruta es una lista circular: la última parada apunta al dock.");
    System.out.println();

    int omitidos = sistema.armarRuta();
    if (omitidos > 0) {
      Utils.info(omitidos + " pedido(s) quedaron fuera porque su producto no está en el almacén.");
    }
    if (sistema.paradasEnRuta() == 0) {
      Utils.info("No hay paradas: el operario no tiene que salir del dock. Paso omitido.");
      Utils.pausa();
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
    Utils.pausa();
  }

  private static void mostrarRuta(String titulo) {
    System.out.println();
    System.out.println(titulo + " (" + sistema.paradasEnRuta() + " parada(s), "
        + sistema.distanciaRuta() + " celda(s) en total):");
    sistema.mostrarRuta();
  }

  private static void pasoResumenFinal() {
    encabezado(15, "ESTADO FINAL");
    System.out.println("Así terminan tu catálogo, tu almacén y tus pedidos:");
    System.out.println();
    sistema.mostrarCatalogo();
    System.out.println();
    sistema.mostrarAlmacen();
    System.out.println();
    sistema.mostrarPedidos();
    System.out.println();
  }

  private static void encabezado(int paso, String titulo) {
    Utils.titulo("PASO " + paso + "/" + TOTAL_PASOS + " · " + titulo);
  }
}
