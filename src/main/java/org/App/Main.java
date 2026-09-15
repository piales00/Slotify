package org.App;

import java.util.Scanner;

import org.Catalogo.Catalogo;
import org.Catalogo.Producto;

public class Main {
  public static final Scanner sc = new Scanner(System.in);

  public static void main(String[] args) {
    System.out.println("""

         ██████╗ ██╗      ██████╗ ████████╗██╗███████╗██╗   ██╗
         ██╔════╝ ██║     ██╔═══██╗╚══██╔══╝██║██╔════╝╚██╗ ██╔╝
         ╚█████╗  ██║     ██║   ██║   ██║   ██║█████╗   ╚████╔╝ 
          ╚═══██╗ ██║     ██║   ██║   ██║   ██║██╔══╝    ╚██╔╝  
         ██████╔╝ ███████╗╚██████╔╝   ██║   ██║██║        ██║   
         ╚═════╝  ╚══════╝ ╚═════╝    ╚═╝   ╚═╝╚═╝        ╚═╝   

                    Sistema de gestión de almacenes
            =========================================

        """);

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

    System.out.println("PRUEBA DE ALMACEN");

    System.out.println("DIME CUAL QUIERES QUE SEA LAS FILAS DE TU ALMACEN:");

    int filas = sc.nextInt();

    System.out.println();

    System.out.println("DIME CUÁNTAS COLUMNAS QUIERES QUE TENGA TU ALMACEN:");

    int columnas = sc.nextInt();

  }
}
