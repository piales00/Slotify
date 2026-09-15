package org.Catalogo;

import java.util.Scanner;

public class SharedScanner {
  private static class Propietario {
    private static final SharedScanner INSTANCIA = new SharedScanner();
  }

  private Scanner sc;

  private SharedScanner() {
    sc = new Scanner(System.in);
  }

  public static SharedScanner getInstancia() {
    return Propietario.INSTANCIA;
  }

  public Scanner getScanner() {
    return sc;
  }

  public void closeScanner() {
    if (sc != null) {
      sc.close();
    }
  }
}
