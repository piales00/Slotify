package org.App;

import org.Catalogo.Producto;
import org.Catalogo.Catalogo;

public class Main {
  public static void main(String[] args) {
    System.out.println("products testing");
    System.out.println("Creando producto");
    Catalogo catalogo = new Catalogo(10);
    Producto producto2 = new Producto("Filtro");
    Producto producto1 = new Producto("Balsa");

    System.out.println("Agregando producto");
    catalogo.agregarProducto(producto2);
    catalogo.agregarProducto(producto1);
    catalogo.mostrarProductos();

    Producto productoSearch = catalogo.buscarProducto("Filtro");

    System.out.println("Busqueda de producto: " + productoSearch);

    System.out.println("ELIMINAR PRODUCTO");
    catalogo.eliminarProducto("Balsa");
    catalogo.mostrarProductos();
  }
}
