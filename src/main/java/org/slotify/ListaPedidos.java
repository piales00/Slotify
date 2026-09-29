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

  // ---------------------------------------------------------------
  // RECORRIDO
  // En una lista enlazada no hay índices: para llegar a cualquier nodo hay que
  // empezar en la cabeza y seguir los enlaces uno por uno. Todos los recorridos
  // usan el mismo patrón: actual = cabeza; while (actual != null) actual = actual.siguiente;
  // ---------------------------------------------------------------

  // Recorrido completo mostrando cada paso, para ver cómo se camina la lista.
  public void recorrer() {
    if (estaVacia()) {
      System.out.println("  (no hay pedidos que recorrer)");
      return;
    }

    int posicion = 0;
    Nodo actual = cabeza;
    while (actual != null) {
      Pedido pedido = actual.getDato();
      String enlace = actual.getSiguiente() == null
          ? "null (fin de la lista)"
          : actual.getSiguiente().getDato().getCodigo();
      System.out.printf("  paso %d → nodo[%d] %s  ·  siguiente: %s%n",
          posicion + 1, posicion, pedido.getCodigo(), enlace);
      posicion++;
      actual = actual.getSiguiente();
    }
    System.out.println("  Recorrido terminado: " + posicion + " nodo(s) visitado(s).");
  }

  // Cuenta los nodos siguiendo los enlaces, sin mirar el contador.
  public int contarNodos() {
    int total = 0;
    Nodo actual = cabeza;
    while (actual != null) {
      total++;
      actual = actual.getSiguiente();
    }
    return total;
  }

  // Recorrido acumulador: suma las cantidades de todos los pedidos.
  public int totalUnidades() {
    int total = 0;
    Nodo actual = cabeza;
    while (actual != null) {
      total += actual.getDato().getCantidad();
      actual = actual.getSiguiente();
    }
    return total;
  }

  // Aquí se ve la diferencia con el arreglo: para llegar a la posición k hay que
  // caminar los k nodos anteriores, no existe el acceso directo productos[k].
  public Pedido obtenerPorPosicion(int posicion) {
    if (posicion < 0) {
      return null;
    }

    int i = 0;
    Nodo actual = cabeza;
    while (actual != null) {
      if (i == posicion) {
        return actual.getDato();
      }
      i++;
      actual = actual.getSiguiente();
    }
    return null;
  }

  // ---------------------------------------------------------------
  // BÚSQUEDA
  // Solo puede ser lineal: la búsqueda binaria necesita saltar al elemento del
  // medio, y en una lista enlazada llegar al medio ya cuesta recorrer media lista.
  // ---------------------------------------------------------------

  // Búsqueda lineal por código que además cuenta las comparaciones hechas.
  public ResultadoBusquedaPedido buscarPorCodigo(String codigo) {
    int comparaciones = 0;
    if (codigo == null) {
      return new ResultadoBusquedaPedido(null, comparaciones, -1);
    }

    String buscado = codigo.trim();
    int posicion = 0;
    Nodo actual = cabeza;

    while (actual != null) {
      comparaciones++;
      if (actual.getDato().getCodigo().equalsIgnoreCase(buscado)) {
        return new ResultadoBusquedaPedido(actual.getDato(), comparaciones, posicion);
      }
      posicion++;
      actual = actual.getSiguiente();
    }

    return new ResultadoBusquedaPedido(null, comparaciones, -1);
  }

  // Búsqueda lineal por SKU: devuelve el primer pedido que pide ese producto.
  public Pedido buscarPorSKU(String sku) {
    if (sku == null) {
      return null;
    }

    Nodo actual = cabeza;
    while (actual != null) {
      if (actual.getDato().getSKU().equalsIgnoreCase(sku.trim())) {
        return actual.getDato();
      }
      actual = actual.getSiguiente();
    }
    return null;
  }

  // Cuenta cuántos pedidos ha hecho un cliente (recorrido con filtro).
  public int contarPedidosDeCliente(String cliente) {
    if (cliente == null) {
      return 0;
    }

    int total = 0;
    Nodo actual = cabeza;
    while (actual != null) {
      if (actual.getDato().getCliente().equalsIgnoreCase(cliente.trim())) {
        total++;
      }
      actual = actual.getSiguiente();
    }
    return total;
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

  // ---------------------------------------------------------------
  // ORDENAMIENTO
  // Hay dos formas de ordenar una lista enlazada:
  //   a) reenlazando nodos: se cambian las referencias, los datos no se tocan.
  //   b) intercambiando datos: los nodos quedan donde están y se cambia el
  //      contenido. Es más simple, pero pierde la ventaja de la lista.
  // Aquí se usan las dos para poder compararlas.
  // ---------------------------------------------------------------

  // Insertion sort REENLAZANDO NODOS: se van sacando nodos de la lista original y
  // se insertan en su lugar dentro de una lista nueva que siempre está ordenada.
  // Tiempo O(n^2), espacio O(1): no se crea ni un solo nodo nuevo.
  public void ordenarPorCodigo() {
    Nodo ordenada = null;
    Nodo actual = cabeza;

    while (actual != null) {
      Nodo siguiente = actual.getSiguiente(); // se guarda antes de romper el enlace

      if (ordenada == null
          || ordenada.getDato().getCodigo().compareToIgnoreCase(actual.getDato().getCodigo()) > 0) {
        // va al principio de la lista ordenada
        actual.setSiguiente(ordenada);
        ordenada = actual;
      } else {
        // se busca el nodo tras el cual hay que engancharlo
        Nodo busca = ordenada;
        while (busca.getSiguiente() != null
            && busca.getSiguiente().getDato().getCodigo()
                .compareToIgnoreCase(actual.getDato().getCodigo()) <= 0) {
          busca = busca.getSiguiente();
        }
        actual.setSiguiente(busca.getSiguiente());
        busca.setSiguiente(actual);
      }

      actual = siguiente;
    }

    cabeza = ordenada;
  }

  // Selection sort INTERCAMBIANDO DATOS: por cada nodo i busca en el resto el
  // pedido con mayor cantidad y cambia los datos de sitio. Los enlaces no se tocan.
  // Tiempo O(n^2), espacio O(1). No es estable.
  public void ordenarPorCantidad() {
    Nodo i = cabeza;
    while (i != null) {
      Nodo mayor = i;
      Nodo j = i.getSiguiente();

      while (j != null) {
        if (j.getDato().getCantidad() > mayor.getDato().getCantidad()) {
          mayor = j;
        }
        j = j.getSiguiente();
      }

      if (mayor != i) {
        Pedido temp = i.getDato();
        i.setDato(mayor.getDato());
        mayor.setDato(temp);
      }

      i = i.getSiguiente();
    }
  }

  // Bubble sort INTERCAMBIANDO DATOS, con bandera: si una pasada no hace ningún
  // intercambio la lista ya está ordenada y se corta.
  // Mejor caso O(n), promedio y peor O(n^2), espacio O(1). Es estable.
  public void ordenarPorCliente() {
    if (cabeza == null) {
      return;
    }

    boolean huboIntercambio = true;
    while (huboIntercambio) {
      huboIntercambio = false;
      Nodo actual = cabeza;

      while (actual.getSiguiente() != null) {
        Pedido a = actual.getDato();
        Pedido b = actual.getSiguiente().getDato();

        if (a.getCliente().compareToIgnoreCase(b.getCliente()) > 0) {
          actual.setDato(b);
          actual.getSiguiente().setDato(a);
          huboIntercambio = true;
        }

        actual = actual.getSiguiente();
      }
    }
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
