package org.slotify;

//Lista enlazada de pedidos, a diferencia de catalogo este no es estático, crece y decrece con los dos
public class ListaPedidos {

  private Nodo cabeza;
  private int numeroActualPedidos;

  public ListaPedidos() {
    this.cabeza = null;
    this.numeroActualPedidos = 0;
  }

  public int getNumeroActualPedidos() {
    return numeroActualPedidos;
  }

  public boolean estaVacia() {
    return cabeza == null;
  }

  // Inserta al principio: el pedido nuevo pasa a ser la cabeza y apunta a la
  // cabeza anterior.
  public void insertarAlInicio(Pedido pedido) {
    Nodo nuevo = new Nodo(pedido);
    nuevo.setSiguiente(cabeza);
    cabeza = nuevo;
    numeroActualPedidos++;
  }

  public void insertarAlFinal(Pedido pedido) {
    Nodo nuevo = new Nodo(pedido);
    if (cabeza == null) {
      cabeza = nuevo;
      numeroActualPedidos++;
      return;
    }

    Nodo actual = cabeza;
    while (actual.getSiguiente() != null) {
      actual = actual.getSiguiente();
    }
    actual.setSiguiente(nuevo);
    numeroActualPedidos++;
  }

  // Elimina el pedido con ese código. Se guarda el nodo anterior para poder
  // saltarse el nodo eliminado (anterior.siguiente = eliminado.siguiente).
  public Pedido eliminarPorCodigo(String codigo) {
    if (cabeza == null || codigo == null) {
      return null;
    }

    String buscado = codigo.trim().toUpperCase();
    Nodo anterior = null;
    Nodo actual = cabeza;

    while (actual != null) {
      if (actual.getDato().getCodigo().equalsIgnoreCase(buscado)) {
        if (anterior == null) {
          cabeza = actual.getSiguiente();
        } else {
          anterior.setSiguiente(actual.getSiguiente());
        }
        actual.setSiguiente(null);
        numeroActualPedidos--;
        return actual.getDato();
      }
      anterior = actual;
      actual = actual.getSiguiente();
    }

    return null;
  }

  // Comprueba si ya hay un pedido con ese código, para no repetirlos.
  public boolean existeCodigo(String codigo) {
    if (codigo == null) {
      return false;
    }

    Nodo actual = cabeza;
    while (actual != null) {
      if (actual.getDato().getCodigo().equalsIgnoreCase(codigo.trim())) {
        return true;
      }
      actual = actual.getSiguiente();
    }
    return false;
  }

  public void mostrarTabla() {
    if (estaVacia()) {
      System.out.println("  (no hay pedidos)");
      return;
    }

    String formato = "  %-3s %-9s %-20s %-9s %9s%n";
    String cabecera = String.format(formato, "#", "Código", "Cliente", "SKU", "Cantidad");
    System.out.print(cabecera);
    System.out.println("  " + "─".repeat(cabecera.length() - 3));

    int i = 1;
    Nodo actual = cabeza;
    while (actual != null) {
      Pedido pedido = actual.getDato();
      System.out.printf(formato, i, pedido.getCodigo(), pedido.getCliente(), pedido.getSKU(), pedido.getCantidad());
      i++;
      actual = actual.getSiguiente();
    }
  }

  // Dibuja los enlaces entre nodos para ver cómo está armada la lista.
  public void mostrarEnlaces() {
    System.out.print("  cabeza → ");
    Nodo actual = cabeza;
    while (actual != null) {
      System.out.print("[" + actual.getDato().getCodigo() + "] → ");
      actual = actual.getSiguiente();
    }
    System.out.println("null");
  }
}
