package org.App;

import org.Catalogo.Producto;
import org.Catalogo.SharedScanner;
import org.Catalogo.Catalogo;
import org.Catalogo.Utils;
import java.util.Scanner;

public class Main {
  public static final Scanner sc = new Scanner(System.in);

  public static void main(String[] args) {
    System.out.println("products testing");
    System.out.println("Creando producto");
    Catalogo catalogo = new Catalogo(10);
    Producto producto2 = Utils.crearNuevoProducto();
    System.out.println("---------------");

    Producto producto1 = Utils.crearNuevoProducto();
    SharedScanner.getInstancia().closeScanner();
    System.out.println("Agregando producto");
    catalogo.agregarProducto(producto2);
    catalogo.agregarProducto(producto1);
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
