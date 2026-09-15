package org.Catalogo;

public class Catalogo {

  private int totalProductos;
  private Producto[] productos;
  public int numeroActualProductos;

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

  public void agregarProducto(Producto producto) {
    System.out.println("-------AGREGAR PRODUCTOS-------------");

    if (this.numeroActualProductos == totalProductos) {
      System.out.println("SE HA ALCANZADO EL LIMITE, POR FAVOR EXTENDER EL TAMAÑO");
      return;
    }

    if (this.numeroActualProductos == 0) {
      this.productos[this.numeroActualProductos] = producto;
      producto.posicion = numeroActualProductos;
      numeroActualProductos++;
    } else {
      this.productos[this.numeroActualProductos++] = producto;
      producto.posicion = numeroActualProductos - 1;
    }
  }

  public void eliminarProducto(String nombreProducto) {
    Producto producto = buscarProducto(nombreProducto);
    if (producto == null) {
      return;
    }
    System.out.println(numeroActualProductos);
    System.out.println(producto.posicion);
    for (int i = producto.posicion; i < numeroActualProductos; i++) {
      if (i == numeroActualProductos) {
        productos[i] = null;
        System.out.println("WARNING: EL ALMACENAMIENTO ESTÁ CASI LLENO, CONSIDERAR AUMENTAR TAMAÑO");
        return;
      }

      productos[i] = productos[i + 1];
    }

    numeroActualProductos--;

  }

  public void actualizarProducto(Producto producto, Producto nuevoProducto) {
    productos[producto.posicion] = nuevoProducto;
  }

  public void mostrarProductos() {
    for (Producto producto : productos) {
      if (producto != null)
        System.out.println(producto);
    }

  }

  // Busqueda lineal
  public Producto buscarProducto(String nombre) {

    for (int i = 0; i < numeroActualProductos; i++) {
      if (productos[i].getNombre() == nombre) {
        return productos[i];
      }
    }

    return null;
  }

  public void ordenarProductosPorNombre() {
    // metodo de bubble sort
    for (int i = 0; i < numeroActualProductos - 1; i++) {
      for (int j = 0; j < numeroActualProductos - 1 - i; j++) {
        Producto temp;
        int comparacion = productos[j].getNombre().compareTo(productos[j + 1].getNombre());
        if (comparacion > 0) {
          temp = productos[j];
          productos[j] = productos[j + 1];
          productos[j + 1] = temp;
        }

      }
    }

  }

  // TODO Implementar ordenamiento de productos por rotacion
  public void ordenarProductosPorRotacion() {
    //Selection sort
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
  }

  // TODO Implementar ordenamiento por clasificacion
  public void ordenarProductosPorClasificacion() {
    //Insertion sort
    for (int i = 1; i < numeroActualProductos; i++) {
      Producto actual = productos[i];
      int j = i - 1;
      while (j >= 0 && productos[j].getClasificacion().compareTo(actual.getClasificacion()) > 0) {
        productos[j + 1] = productos[j];
        j--;
      }
      productos[j + 1] = actual;
    }
  }

  // TODO Implementar ordenamiento por peso
  public void ordenarProductosPorPeso() {
    //Shell sort
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
  }

  // TODO Implementar copia de catalogo
  public Catalogo copiarCatalogo() {
  Catalogo copia = new Catalogo(this.totalProductos);

  for (int i = 0; i < this.numeroActualProductos; i++) {
    Producto original = this.productos[i];
    Producto productoCopia = new Producto(
        original.getNombre(),
        original.getSKU(),
        original.getFamilia(),
        original.getVolumen(),
        original.getPeso(),
        original.getRotacion(),
        original.getClasificacion(),
        original.getHerramienta());

    copia.agregarProducto(productoCopia);
  }

  return copia;
}

}
