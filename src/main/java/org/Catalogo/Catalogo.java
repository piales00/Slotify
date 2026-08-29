package org.Catalogo;

import java.util.Scanner;

public class Catalogo {

  private int totalProductos;
  private Producto[] productos;
  private int numeroActualProductos;

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

  // TODO: Implementar eliminar producto
  public void eliminarProducto(String nombreProducto) {
    Producto producto = buscarProducto(nombreProducto);
    System.out.println("Hola estoy aqui");
    if (producto == null) {
      System.out.println("El producto no existe");
      return;
    }
    System.out.println(numeroActualProductos);
    System.out.println("el producto a eliminar es " + producto);
    for (int i = producto.posicion; i < numeroActualProductos; i++) {
      if (i == numeroActualProductos) {
        productos[i] = null;
        System.out.println("WARNING: EL ALMACENAMIENTO ESTÁ CASI LLENO, CONSIDERAR AUMENTAR TAMAÑO");
        return;
      }

      System.out.println("estoy reemplazando el valor de " + productos[i] + " por " + productos[i + 1]);
      productos[i] = productos[i + 1];
      numeroActualProductos--;
    }

  }

  // TODO: Implementar actualizar producto
  public void actualizarProducto(Producto producto) {
  }

  public void mostrarProductos() {
    for (Producto producto : productos) {
      if (producto != null)
        System.out.println(producto);
    }

  }

  public Producto buscarProducto(String nombre) {

    for (int i = 0; i < numeroActualProductos; i++) {
      if (productos[i].getNombre() == nombre) {
        return productos[i];
      }
    }

    return null;
  }

}
