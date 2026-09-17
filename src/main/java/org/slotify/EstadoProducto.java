package org.slotify;

public enum EstadoProducto {
  VACIO, OCUPADO, PARED, PASILLO;

  public boolean esTransitable() {
    if (this == VACIO || this == PASILLO) {
      return true;
    }
    return false;
  }
}
