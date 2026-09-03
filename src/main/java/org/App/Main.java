package org.App;

import org.Catalogo.Producto;
import org.Catalogo.SharedScanner;
import org.Catalogo.Catalogo;
import org.Catalogo.Utils;
import java.util.Scanner;

public class Main {
  public static final Scanner sc = new Scanner(System.in);

  public static void main(String[] args) {
    System.out.println("MAIN TESTING");
    System.out.println("CREANDO PRODUCTOS");
    Catalogo catalogo = new Catalogo(10);
    Producto producto1 = new Producto("Filtro");
    Producto producto2 = new Producto("Motor");
    Producto producto3 = new Producto("Avion");

    System.out.println("Agregando producto");
    catalogo.agregarProducto(producto1);
    catalogo.agregarProducto(producto2);
    catalogo.agregarProducto(producto3);

    System.out.println("CATALOGO DE PRODUCTOS===========================");
    System.out.println("Productos actuales: " + catalogo.numeroActualProductos);
    catalogo.mostrarProductos();

    catalogo.ordenarProductosPorNombre();

    System.out.println("CATALOGO DE PRODUCTOS===========================");
    System.out.println("Productos actuales: " + catalogo.numeroActualProductos);
    catalogo.mostrarProductos();
    /*
     * Producto productoSearch = catalogo.buscarProducto("Filtro");
     * 
     * System.out.println("Busqueda de producto: " + productoSearch);
     * 
     * System.out.println("ELIMINAR PRODUCTO");
     * catalogo.eliminarProducto("Balsa");
     * catalogo.mostrarProductos();
     * 
     */

  }
}
