package org.Catalogo;

import java.util.Scanner;
import java.util.Hashmap;
import java.util.Map;

public class Catalogo {

  private Scanner sc = new Scanner(System.in);
  private Producto[] productos = new Producto[100];
  public static int tamano;

  public int conseguirTamano(Producto[] lista) {
    int length = 0;

    for (Producto producto : lista) {
      if (producto != null) {
        length++;
      }
    }

    return length;
  }

  public void agregarProducto(Producto producto, int posicion) {
    System.out.println("-------AGREGAR PRODUCTOS-------------");
    tamano = conseguirTamano(productos);

    if (tamano == productos.length) {
      System.out.println("La lista ya está llena");
    } else {
      for (int i = tamano; i > posicion; i++) {
        productos[i] = productos[i - 1];

      }
    }
    productos[posicion].posicion = posicion;
    productos[posicion] = producto;
    tamano++;
  }

  // TODO: Implementar eliminar producto
  public void eliminarProducto(String nombreProducto) {
    Producto producto = buscarProducto(nombreProducto);

    if (producto == null) {
      System.out.println("El");
    }

  }

  // TODO: Implementar actualizar producto
  public void actualizarProducto(Producto producto) {
  }

  // TODO: Implementar mostrar producto
  public void mostrarProducto(Producto producto) {

  }

  public Producto buscarProducto(String nombre) {
    for (int i = 0; i < tamano; i++) {
      if (productos[i].getNombre() == nombre) {
        return productos[i];
      }
    }
    return null;
  }

}
