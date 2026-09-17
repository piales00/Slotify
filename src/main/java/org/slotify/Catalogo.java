package org.slotify;

public class Catalogo {

  private int totalProductos;
  private Producto[] productos;
  public int numeroActualProductos;
  private boolean ordenadoPorNombre;

  public Catalogo(int totalProductos) {
    this.totalProductos = totalProductos;
    this.productos = new Producto[totalProductos];
    this.numeroActualProductos = 0;

  }

  public int conseguirTamano(Producto[] lista) {
    int length = 0;

    for (Producto producto : lista) {
      if (producto != null) {
        length++;
      }
    }

    return length;
  }

  public Producto getProducto(int indice) {
    if (indice < 0 || indice >= numeroActualProductos) {
      return null;
    }
    return productos[indice];
  }

  public boolean agregarProducto(Producto producto) {
    if (this.numeroActualProductos == totalProductos) {
      System.out.println("SE HA ALCANZADO EL LIMITE, POR FAVOR EXTENDER EL TAMAÑO");
      return false;
    }

    this.productos[this.numeroActualProductos] = producto;
    producto.posicion = numeroActualProductos;
    numeroActualProductos++;
    ordenadoPorNombre = false;
    return true;
  }

  public Producto eliminarProducto(String nombreProducto) {
    Producto producto = buscarProducto(nombreProducto);
    if (producto == null) {
      return null;
    }

    for (int i = producto.posicion; i < numeroActualProductos - 1; i++) {
      productos[i] = productos[i + 1];
    }
    productos[numeroActualProductos - 1] = null;
    numeroActualProductos--;
    actualizarPosiciones();
    return producto;
  }

  public void actualizarProducto(Producto producto, Producto nuevoProducto) {
    productos[producto.posicion] = nuevoProducto;
    nuevoProducto.posicion = producto.posicion;
    ordenadoPorNombre = false;
  }

  public void mostrarProductos() {
    for (Producto producto : productos) {
      if (producto != null)
        System.out.println(producto);
    }

  }

  public void mostrarTabla() {
    if (numeroActualProductos == 0) {
      System.out.println("  (catálogo vacío)");
      return;
    }

    String formato = "  %-3s %-9s %-24s %-12s %8s %8s %8s %4s  %-11s%n";
    String cabecera = String.format(formato, "#", "SKU", "Nombre", "Familia", "Vol(m3)", "Peso(kg)", "Rotación",
        "ABC", "Picking");
    System.out.print(cabecera);
    System.out.println("  " + "─".repeat(cabecera.length() - 3));

    for (int i = 0; i < numeroActualProductos; i++) {
      Producto p = productos[i];
      String clasificacion = String.format("%4s", p.getClasificacion());
      System.out.printf("  %-3d %-9s %-24s %-12s %8.3f %8.1f %8d %s  %-11s%n",
          i, p.getSKU(), recortar(p.getNombre(), 24), recortar(p.getFamilia(), 12),
          p.getVolumen(), p.getPeso(), p.getRotacion(), clasificacion, p.getHerramienta());
    }
    System.out.println("  Total: " + numeroActualProductos + "/" + totalProductos);
  }

  // Busqueda lineal
  public Producto buscarProducto(String nombre) {

    for (int i = 0; i < numeroActualProductos; i++) {
      if (productos[i].getNombre().equalsIgnoreCase(nombre)) {
        return productos[i];
      }
    }

    return null;
  }

  public Producto buscarProductoPorSKU(String sku) {
    for (int i = 0; i < numeroActualProductos; i++) {
      if (productos[i].getSKU().equalsIgnoreCase(sku)) {
        return productos[i];
      }
    }
    return null;
  }

  public ResultadoBusqueda buscarProductoLineal(String nombre) {
    int comparaciones = 0;
    for (int i = 0; i < numeroActualProductos; i++) {
      comparaciones++;
      if (productos[i].getNombre().equalsIgnoreCase(nombre)) {
        return new ResultadoBusqueda(productos[i], comparaciones);
      }
    }
    return new ResultadoBusqueda(null, comparaciones);
  }

  public ResultadoBusqueda buscarProductoBinario(String nombre) {
    if (!ordenadoPorNombre) {
      ordenarProductosPorNombre();
    }

    int inicio = 0;
    int fin = numeroActualProductos - 1;
    int comparaciones = 0;

    while (inicio <= fin) {
      int medio = (inicio + fin) / 2;
      int comparacion = productos[medio].getNombre().compareToIgnoreCase(nombre);
      comparaciones++;

      if (comparacion == 0) {
        return new ResultadoBusqueda(productos[medio], comparaciones);
      } else if (comparacion < 0) {
        inicio = medio + 1;
      } else {
        fin = medio - 1;
      }
    }

    return new ResultadoBusqueda(null, comparaciones);
  }

  public void ordenarProductosPorNombre() {
    // metodo de bubble sort
    for (int i = 0; i < numeroActualProductos - 1; i++) {
      for (int j = 0; j < numeroActualProductos - 1 - i; j++) {
        Producto temp;
        int comparacion = productos[j].getNombre().compareToIgnoreCase(productos[j + 1].getNombre());
        if (comparacion > 0) {
          temp = productos[j];
          productos[j] = productos[j + 1];
          productos[j + 1] = temp;
        }

      }
    }
    actualizarPosiciones();
    ordenadoPorNombre = true;
  }

  public void ordenarProductosPorRotacion() {
    // Selection sort
    for (int i = 0; i < numeroActualProductos - 1; i++) {
      int indiceMayor = i;
      Producto temp;
      for (int j = i + 1; j < numeroActualProductos; j++) {
        if (productos[j].getRotacion() > productos[indiceMayor].getRotacion()) {
          indiceMayor = j;
        }
      }
      temp = productos[i];
      productos[i] = productos[indiceMayor];
      productos[indiceMayor] = temp;
    }
    actualizarPosiciones();
    ordenadoPorNombre = false;
  }

  public void ordenarProductosPorClasificacion() {
    // Insertion sort
    for (int i = 1; i < numeroActualProductos; i++) {
      Producto actual = productos[i];
      int j = i - 1;
      while (j >= 0 && productos[j].getClasificacion().compareTo(actual.getClasificacion()) > 0) {
        productos[j + 1] = productos[j];
        j--;
      }
      productos[j + 1] = actual;
    }
    actualizarPosiciones();
    ordenadoPorNombre = false;
  }

  public void ordenarProductosPorPeso() {
    // Shell sort
    for (int gap = numeroActualProductos / 2; gap > 0; gap /= 2) {
      for (int i = gap; i < numeroActualProductos; i++) {
        Producto actual = productos[i];
        int j;
        for (j = i; j >= gap && productos[j - gap].getPeso() > actual.getPeso(); j -= gap) {
          productos[j] = productos[j - gap];
        }
        productos[j] = actual;
      }
    }
    actualizarPosiciones();
    ordenadoPorNombre = false;
  }

  public void ordenarProductosPorSKU() {
    if (numeroActualProductos > 1) {
      Producto[] auxiliar = new Producto[numeroActualProductos];
      mergeSort(auxiliar, 0, numeroActualProductos - 1);
    }
    actualizarPosiciones();
    ordenadoPorNombre = false;
  }

  private void mergeSort(Producto[] auxiliar, int inicio, int fin) {
    if (inicio >= fin) {
      return;
    }
    int medio = (inicio + fin) / 2;
    mergeSort(auxiliar, inicio, medio);
    mergeSort(auxiliar, medio + 1, fin);
    mezclar(auxiliar, inicio, medio, fin);
  }

  private void mezclar(Producto[] auxiliar, int inicio, int medio, int fin) {
    for (int k = inicio; k <= fin; k++) {
      auxiliar[k] = productos[k];
    }
    int i = inicio;
    int j = medio + 1;
    int k = inicio;
    while (i <= medio && j <= fin) {
      if (auxiliar[i].getSKU().compareToIgnoreCase(auxiliar[j].getSKU()) <= 0) {
        productos[k++] = auxiliar[i++];
      } else {
        productos[k++] = auxiliar[j++];
      }
    }
    while (i <= medio) {
      productos[k++] = auxiliar[i++];
    }
    while (j <= fin) {
      productos[k++] = auxiliar[j++];
    }
  }

  public void ordenarProductosPorVolumen() {
    quickSort(0, numeroActualProductos - 1);
    actualizarPosiciones();
    ordenadoPorNombre = false;
  }

  private void quickSort(int inicio, int fin) {
    if (inicio >= fin) {
      return;
    }
    int indicePivote = particionar(inicio, fin);
    quickSort(inicio, indicePivote - 1);
    quickSort(indicePivote + 1, fin);
  }

  private int particionar(int inicio, int fin) {
    double pivote = productos[fin].getVolumen();
    int i = inicio - 1;
    for (int j = inicio; j < fin; j++) {
      if (productos[j].getVolumen() <= pivote) {
        i++;
        intercambiar(i, j);
      }
    }
    intercambiar(i + 1, fin);
    return i + 1;
  }

  public void ordenarProductosPorFamilia() {
    int n = numeroActualProductos;
    for (int i = n / 2 - 1; i >= 0; i--) {
      hundir(n, i);
    }
    for (int fin = n - 1; fin > 0; fin--) {
      intercambiar(0, fin);
      hundir(fin, 0);
    }
    actualizarPosiciones();
    ordenadoPorNombre = false;
  }

  private void hundir(int tamano, int raiz) {
    int mayor = raiz;
    int izquierdo = 2 * raiz + 1;
    int derecho = 2 * raiz + 2;
    if (izquierdo < tamano && compararFamilia(productos[izquierdo], productos[mayor]) > 0) {
      mayor = izquierdo;
    }
    if (derecho < tamano && compararFamilia(productos[derecho], productos[mayor]) > 0) {
      mayor = derecho;
    }
    if (mayor != raiz) {
      intercambiar(raiz, mayor);
      hundir(tamano, mayor);
    }
  }

  private int compararFamilia(Producto a, Producto b) {
    return a.getFamilia().compareToIgnoreCase(b.getFamilia());
  }

  private void intercambiar(int i, int j) {
    Producto temp = productos[i];
    productos[i] = productos[j];
    productos[j] = temp;
  }

  public Catalogo copiarCatalogo() {
    Catalogo copia = new Catalogo(this.totalProductos);

    for (int i = 0; i < this.numeroActualProductos; i++) {
      Producto original = this.productos[i];
      Producto nuevoProducto = new Producto(
          original.getNombre(),
          original.getSKU(),
          original.getFamilia(),
          original.getVolumen(),
          original.getPeso(),
          original.getRotacion(),
          original.getClasificacion(),
          original.getHerramienta());

      copia.agregarProducto(nuevoProducto);
    }

    return copia;
  }

  private void actualizarPosiciones() {
    for (int i = 0; i < numeroActualProductos; i++) {
      productos[i].posicion = i;
    }
  }

  private String recortar(String texto, int maximo) {
    if (texto == null) {
      return "-";
    }
    return texto.length() <= maximo ? texto : texto.substring(0, maximo - 1) + "…";
  }

}
