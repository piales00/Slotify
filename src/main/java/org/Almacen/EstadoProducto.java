package org.Almacen;

public enum EstadoProducto {
  VACIO, OCUPADO, PARED, ESTANTE;

  public boolean esTransitable() {
    if (this == VACIO) {
      return true;
    }
    return false;
  }
}
