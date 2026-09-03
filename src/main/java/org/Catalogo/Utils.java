package org.Catalogo;

import java.util.Scanner;

public class Utils {
  public static void main(String[] args) {
    System.out.println("hola esta es una prueba ");
  }

  public static Producto crearNuevoProducto() {
    Scanner sc = SharedScanner.getInstancia().getScanner();
    Producto p = new Producto();



    // crear nombre
    System.out.println("Introduce el nombre de tu nuevo producto: ");
    p.setNombre(sc.nextLine());

    // crear SKU
    System.out.println("Introduce el SKU: ");
    p.setSKU(sc.nextLine());

    // crear familia
    System.out.println("Introduce la familia: ");
    p.setFamilia(sc.nextLine());

    // crear volumen
    System.out.println("Introduce el volumen: ");
    p.setVolumen(sc.nextDouble());

    // crear peso
    System.out.println("Introduce el peso: ");
    p.setPeso(sc.nextDouble());

    // crear rotación
    System.out.println("Introduce la rotación: ");
    p.setRotacion(sc.nextInt());
    sc.nextLine();
    // crear clasificacion
    System.out.println("Introduce la clasificacion");
    p.setClasificacion(sc.nextLine());

    // crear herramientaPicking
    System.out.println("Introduce la herramienta de picking");
    p.setHerramienta(sc.nextLine());

    return p;
  }
}
