package org.App;

import org.Catalogo.Catalogo;
import org.Catalogo.Producto;

// Productos de ejemplo compartidos por Main y MainGuiado
public class DatosIniciales {

  public static void cargarProductos(Catalogo catalogo) {
    catalogo.agregarProducto(new Producto("Filtro de aceite", "SKU-001", "Filtros", 0.02, 0.5, 120, "A", "Manual"));
    catalogo.agregarProducto(new Producto("Motor eléctrico", "SKU-002", "Motores", 0.40, 35.0, 15, "B", "Montacargas"));
    catalogo.agregarProducto(new Producto("Tornillos M8 (caja)", "SKU-003", "Ferretería", 0.01, 2.0, 200, "A", "Manual"));
    catalogo.agregarProducto(new Producto("Batería 12V", "SKU-004", "Eléctrico", 0.03, 18.5, 60, "B", "Manual"));
    catalogo.agregarProducto(new Producto("Neumático", "SKU-005", "Ruedas", 0.15, 12.0, 45, "B", "Manual"));
    catalogo.agregarProducto(new Producto("Bomba hidráulica", "SKU-006", "Hidráulica", 0.25, 48.0, 8, "C", "Montacargas"));
    catalogo.agregarProducto(new Producto("Bobina de cable", "SKU-007", "Eléctrico", 0.10, 25.0, 30, "B", "Montacargas"));
    catalogo.agregarProducto(new Producto("Aceite de motor 5L", "SKU-008", "Lubricantes", 0.006, 4.5, 150, "A", "Manual"));
    catalogo.agregarProducto(new Producto("Compresor industrial", "SKU-009", "Neumática", 1.20, 180.0, 3, "C", "Grúa"));
    catalogo.agregarProducto(new Producto("Guantes de trabajo", "SKU-010", "EPP", 0.002, 0.2, 180, "A", "Manual"));
    catalogo.agregarProducto(new Producto("Rodamientos", "SKU-011", "Ferretería", 0.004, 1.2, 90, "A", "Manual"));
    catalogo.agregarProducto(new Producto("Transformador", "SKU-012", "Eléctrico", 0.80, 320.0, 2, "C", "Grúa"));
  }
}
