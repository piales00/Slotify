package org.slotify;

public class Slotify {

  private static final int CAPACIDAD_CATALOGO = 50;
  private static final int CAPACIDAD_HISTORIAL = 10;

  public static final int MIN_FILAS = Almacen.MIN_FILAS;
  public static final int MAX_FILAS = Almacen.MAX_FILAS;
  public static final int MIN_COLUMNAS = Almacen.MIN_COLUMNAS;
  public static final int MAX_COLUMNAS = Almacen.MAX_COLUMNAS;

  private final Catalogo catalogo;
  private final ListaPedidos pedidos;
  private final PilaCambios historial;
  private RutaPicking ruta;

  public Slotify() {
    this.catalogo = new Catalogo(CAPACIDAD_CATALOGO);
    this.pedidos = new ListaPedidos();
    this.historial = new PilaCambios(CAPACIDAD_HISTORIAL);
  }

  public record CambioProducto(boolean actualizado, String ubicacionSincronizada) {
    public boolean seSincronizoAlmacen() {
      return ubicacionSincronizada != null;
    }
  }

  public record BajaProducto(Producto producto, String ubicacionLiberada) {
    public boolean seEncontro() {
      return producto != null;
    }

    public boolean seLibero() {
      return ubicacionLiberada != null;
    }
  }

  public enum OrdenCatalogo {
    NOMBRE("Por nombre", "Bubble sort"),
    ROTACION("Por rotación", "Selection sort"),
    CLASIFICACION("Por clasificación", "Insertion sort"),
    PESO("Por peso", "Shell sort"),
    SKU("Por SKU", "Merge sort"),
    VOLUMEN("Por volumen", "Quick sort"),
    FAMILIA("Por familia", "Heap sort");

    private final String criterio;
    private final String algoritmo;

    OrdenCatalogo(String criterio, String algoritmo) {
      this.criterio = criterio;
      this.algoritmo = algoritmo;
    }

    public String getCriterio() {
      return criterio;
    }

    public String getAlgoritmo() {
      return algoritmo;
    }
  }

  public enum OrdenPedidos {
    CODIGO("Por código", "Insertion sort REENLAZANDO nodos", "O(n²), estable"),
    CANTIDAD("Por cantidad ↓", "Selection sort intercambiando datos", "O(n²), no estable"),
    CLIENTE("Por cliente", "Bubble sort intercambiando datos", "O(n) si ya está ordenada");

    private final String criterio;
    private final String algoritmo;
    private final String complejidad;

    OrdenPedidos(String criterio, String algoritmo, String complejidad) {
      this.criterio = criterio;
      this.algoritmo = algoritmo;
      this.complejidad = complejidad;
    }

    public String getCriterio() {
      return criterio;
    }

    public String getAlgoritmo() {
      return algoritmo;
    }

    public String getComplejidad() {
      return complejidad;
    }
  }

  public void crearAlmacen(int filas, int columnas) {
    Almacen.crearAlmacen(filas, columnas);
    historial.vaciar();
    ruta = null;
  }

  public boolean hayAlmacen() {
    return Almacen.existe();
  }

  public int getFilas() {
    return Almacen.existe() ? Almacen.getInstancia().getFilas() : 0;
  }

  public int getColumnas() {
    return Almacen.existe() ? Almacen.getInstancia().getColumnas() : 0;
  }

  public int getPasillos() {
    return Almacen.existe() ? Almacen.getInstancia().getPasillos() : 0;
  }

  public int estantesLibres() {
    return Almacen.existe() ? Almacen.estantesLibres() : 0;
  }

  public int estantesOcupados() {
    return Almacen.existe() ? Almacen.estantesOcupados() : 0;
  }

  public void mostrarAlmacen() {
    Almacen.mostrarAlmacen();
  }

  public void mostrarProductosPorPasillo() {
    Almacen.mostrarProductosPorPasillo();
  }

  public void cargarCatalogoDeEjemplo() {
    DatosIniciales.cargarProductos(catalogo);
  }

  public int totalProductos() {
    return catalogo.numeroActualProductos;
  }

  public void mostrarCatalogo() {
    catalogo.mostrarTabla();
  }

  public boolean existeSKU(String sku) {
    return catalogo.buscarProductoPorSKU(sku) != null;
  }

  public Producto buscarPorSKU(String sku) {
    return catalogo.buscarProductoPorSKU(sku);
  }

  public boolean registrarProducto(Producto producto) {
    if (existeSKU(producto.getSKU())) {
      Utils.error("Ya existe un producto con el SKU " + producto.getSKU() + ".");
      return false;
    }
    return catalogo.agregarProducto(producto);
  }

  public ResultadoBusqueda buscarProducto(String nombre, boolean binaria) {
    return binaria
        ? catalogo.buscarProductoBinario(nombre)
        : catalogo.buscarProductoLineal(nombre);
  }

  public void ordenarCatalogo(OrdenCatalogo orden) {
    switch (orden) {
      case NOMBRE -> catalogo.ordenarProductosPorNombre();
      case ROTACION -> catalogo.ordenarProductosPorRotacion();
      case CLASIFICACION -> catalogo.ordenarProductosPorClasificacion();
      case PESO -> catalogo.ordenarProductosPorPeso();
      case SKU -> catalogo.ordenarProductosPorSKU();
      case VOLUMEN -> catalogo.ordenarProductosPorVolumen();
      case FAMILIA -> catalogo.ordenarProductosPorFamilia();
    }
  }

  public int llenarAlmacen() {
    if (!Almacen.existe()) {
      Utils.error("Primero hay que crear el almacén.");
      return 0;
    }
    int colocados = Almacen.ubicarProductos(catalogo);
    historial.vaciar();
    return colocados;
  }

  public boolean colocarProducto(Producto producto, int fila, int columna) {
    if (!Almacen.existe()) {
      Utils.error("Primero hay que crear el almacén.");
      return false;
    }
    boolean colocado = Almacen.agregarProducto(producto, fila, columna);
    if (colocado) {
      historial.push(new Cambio(TipoCambio.COLOCAR, producto, fila, columna));
    }
    return colocado;
  }

  public Producto retirarDeUbicacion(int fila, int columna) {
    if (!Almacen.existe()) {
      Utils.error("Primero hay que crear el almacén.");
      return null;
    }
    Producto retirado = Almacen.eliminarProducto(fila, columna);
    if (retirado != null) {
      historial.push(new Cambio(TipoCambio.RETIRAR, retirado, fila, columna));
    }
    return retirado;
  }

  public boolean hayCambios() {
    return !historial.estaVacia();
  }

  public int cambiosEnHistorial() {
    return historial.tamano();
  }

  public int capacidadHistorial() {
    return historial.getCapacidad();
  }

  public Cambio ultimoCambio() {
    return historial.peek();
  }

  public void mostrarHistorial() {
    historial.mostrar();
  }

  public Cambio deshacer() {
    Cambio cambio = historial.pop();
    if (cambio == null) {
      Utils.error("No hay cambios que deshacer.");
      return null;
    }

    String sku = cambio.getProducto().getSKU();
    int[] ubicacion = Almacen.buscarUbicacion(sku);

    if (cambio.getTipo() == TipoCambio.COLOCAR) {
      if (ubicacion == null || ubicacion[0] != cambio.getFila() || ubicacion[1] != cambio.getColumna()) {
        Utils.error(sku + " ya no está en (" + cambio.getFila() + ", " + cambio.getColumna()
            + "), no se puede deshacer.");
        return null;
      }
      Almacen.eliminarProducto(cambio.getFila(), cambio.getColumna());
      return cambio;
    }

    Producto actual = catalogo.buscarProductoPorSKU(sku);
    if (actual == null) {
      Utils.error(sku + " ya no existe en el catálogo, no se puede volver a colocar.");
      return null;
    }
    if (ubicacion != null) {
      Utils.error(sku + " ya está colocado en " + Almacen.describirUbicacion(ubicacion[0], ubicacion[1]) + ".");
      return null;
    }
    if (!Almacen.agregarProducto(actual, cambio.getFila(), cambio.getColumna())) {
      return null;
    }
    return cambio;
  }

  public String describirUbicacionDe(String sku) {
    int[] ubicacion = ubicacionDe(sku);
    return ubicacion == null ? null : Almacen.describirUbicacion(ubicacion[0], ubicacion[1]);
  }

  public int[] ubicacionDe(String sku) {
    return Almacen.existe() ? Almacen.buscarUbicacion(sku) : null;
  }

  public CambioProducto actualizarProducto(Producto original, Producto nuevo) {
    if (original == null || nuevo == null) {
      return new CambioProducto(false, null);
    }

    Producto choque = catalogo.buscarProductoPorSKU(nuevo.getSKU());
    if (choque != null && choque != original) {
      Utils.error("Ya existe otro producto con el SKU " + nuevo.getSKU() + ".");
      return new CambioProducto(false, null);
    }

    int[] ubicacion = ubicacionDe(original.getSKU());
    catalogo.actualizarProducto(original, nuevo);

    if (ubicacion == null) {
      return new CambioProducto(true, null);
    }

    Almacen.eliminarProducto(ubicacion[0], ubicacion[1]);
    Almacen.agregarProducto(nuevo, ubicacion[0], ubicacion[1]);
    return new CambioProducto(true, Almacen.describirUbicacion(ubicacion[0], ubicacion[1]));
  }

  public BajaProducto eliminarProducto(String nombre) {
    Producto eliminado = catalogo.eliminarProducto(nombre);
    if (eliminado == null) {
      return new BajaProducto(null, null);
    }

    int[] ubicacion = ubicacionDe(eliminado.getSKU());
    if (ubicacion == null) {
      return new BajaProducto(eliminado, null);
    }

    String descripcion = Almacen.describirUbicacion(ubicacion[0], ubicacion[1]);
    Almacen.eliminarProducto(ubicacion[0], ubicacion[1]);
    return new BajaProducto(eliminado, descripcion);
  }


  public void cargarPedidosDeEjemplo() {
    DatosIniciales.cargarPedidos(pedidos);
  }

  public boolean sinPedidos() {
    return pedidos.estaVacia();
  }

  public int totalPedidos() {
    return pedidos.getNumeroActualPedidos();
  }

  public int contarNodos() {
    return pedidos.contarNodos();
  }

  public int totalUnidadesPedidas() {
    return pedidos.totalUnidades();
  }

  public boolean existeCodigoPedido(String codigo) {
    return pedidos.existeCodigo(codigo);
  }

  public void mostrarPedidos() {
    pedidos.mostrarTabla();
  }

  public void mostrarEnlaces() {
    pedidos.mostrarEnlaces();
  }

  public void recorrerPedidos() {
    pedidos.recorrer();
  }

  public Pedido pedidoEnPosicion(int posicion) {
    return pedidos.obtenerPorPosicion(posicion);
  }

  public Pedido registrarPedido(String codigo, String cliente, String sku, int cantidad, boolean alInicio) {
    if (pedidos.existeCodigo(codigo)) {
      Utils.error("Ya existe un pedido con el código " + codigo + ".");
      return null;
    }

    Producto producto = catalogo.buscarProductoPorSKU(sku);
    if (producto == null) {
      Utils.error("No existe ningún producto con el SKU " + sku + " en el catálogo.");
      return null;
    }

    Pedido pedido = new Pedido(codigo, cliente, producto.getSKU(), cantidad);
    if (alInicio) {
      pedidos.insertarAlInicio(pedido);
    } else {
      pedidos.insertarAlFinal(pedido);
    }
    return pedido;
  }

  public Pedido eliminarPedido(String codigo) {
    return pedidos.eliminarPorCodigo(codigo);
  }

  public ResultadoBusquedaPedido buscarPedidoPorCodigo(String codigo) {
    return pedidos.buscarPorCodigo(codigo);
  }

  public Pedido buscarPedidoPorSKU(String sku) {
    return pedidos.buscarPorSKU(sku);
  }

  public int contarPedidosDeCliente(String cliente) {
    return pedidos.contarPedidosDeCliente(cliente);
  }

  public void ordenarPedidos(OrdenPedidos orden) {
    switch (orden) {
      case CODIGO -> pedidos.ordenarPorCodigo();
      case CANTIDAD -> pedidos.ordenarPorCantidad();
      case CLIENTE -> pedidos.ordenarPorCliente();
    }
  }

  public int armarRuta() {
    if (!Almacen.existe()) {
      Utils.error("Primero hay que crear el almacén.");
      return 0;
    }

    ruta = new RutaPicking(Almacen.getEntradaFila(), Almacen.getEntradaColumna());
    int omitidos = 0;
    for (Pedido pedido : pedidos.aArreglo()) {
      int[] ubicacion = Almacen.buscarUbicacion(pedido.getSKU());
      if (ubicacion == null) {
        Utils.info(pedido.getCodigo() + ": " + pedido.getSKU() + " no está en el almacén, queda fuera de la ruta.");
        omitidos++;
        continue;
      }
      ruta.agregarParada(new Parada(pedido.getCodigo(), pedido.getSKU(), pedido.getCantidad(),
          ubicacion[0], ubicacion[1], Almacen.pasilloDe(ubicacion[0], ubicacion[1])));
    }
    return omitidos;
  }

  public int paradasEnRuta() {
    return ruta == null ? 0 : ruta.getNumeroParadas();
  }

  public int distanciaRuta() {
    return ruta == null ? 0 : ruta.distanciaTotal();
  }

  public void ordenarRuta() {
    if (ruta != null) {
      ruta.ordenarSerpentina();
    }
  }

  public void mostrarRuta() {
    if (ruta == null) {
      System.out.println("  (todavía no se armó la ruta)");
      return;
    }
    ruta.mostrarTabla();
    System.out.println();
    ruta.mostrarEnlaces();
  }

  public void recorrerRuta() {
    if (ruta != null) {
      ruta.recorrer();
    }
  }

  public Parada recogerParada(String codigoPedido) {
    return ruta == null ? null : ruta.recogerParada(codigoPedido);
  }

  public void cerrar() {
    SharedScanner.getInstancia().closeScanner();
  }
}
