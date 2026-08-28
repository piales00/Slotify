package org.Catalogo;

// SKU, familia/grupo de producto, volumen, peso, demanda/rotación, clasificación ABC, herramienta de picking requerida (manual, montacargas, grúa).
public class Producto {
  private String nombre;
  private String SKU;
  private String familia;
  private Double volumen;
  private Double peso;
  private int rotacion;
  private String clasificacion;
  private String herramientaPicking;
  public int posicion;

  public Producto(String nombre, String SKU, String familia, Double volumen, Double peso, int rotacion,
      String clasificacion,
      String herramientaPicking) {
    this.nombre = nombre;
    this.SKU = SKU;
    this.familia = familia;
    this.volumen = volumen;
    this.peso = peso;
    this.rotacion = rotacion;
    this.clasificacion = clasificacion;
    this.herramientaPicking = herramientaPicking;
  }

  // nombre encapsulacion
  public String getNombre() {
    return nombre;
  }

  public void setNombre(String newNombre) {
    this.nombre = newNombre;
  }

  // SKU encapsulación
  public String getSKU() {
    return SKU;
  }

  public void setSKU(String newSKU) {
    this.SKU = newSKU;
  }

  // familia encapsulación
  public String getFamilia() {
    return familia;
  }

  public void setFamilia(String newFamilia) {
    this.familia = newFamilia;
  }

  // volumen encapsulación
  public Double getVolumen() {
    return volumen;
  }

  public void setVolumen(Double newVolumen) {
    this.volumen = newVolumen;
  }

  // peso encapsulación
  public Double getPeso() {
    return peso;
  }

  public void setPeso(Double newPeso) {
    this.peso = newPeso;
  }

  // rotacion encapsulación
  public int getRotacion() {
    return rotacion;
  }

  public void setRotacion(int newRotacion) {
    this.rotacion = newRotacion;
  }

  // SKU clasificacion
  public String getClasificacion() {
    return clasificacion;
  }

  public void getClasificacion(String newClasificacion) {
    this.clasificacion = newClasificacion;
  }

  // herramientaPicking encapsulación
  public String getHerramienta() {
    return herramientaPicking;
  }

  public void setHerramienta(String newHerramienta) {
    this.herramientaPicking = newHerramienta;
  }

}
