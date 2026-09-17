# Slotify · Documentación técnica

Slotify es una aplicación de consola en Java que simula la gestión de un **almacén** (una matriz de celdas con paredes, pasillos y estanterías) y de un **catálogo de productos** (un arreglo). El programa guía al usuario paso a paso para crear el almacén, llenarlo, agregar, actualizar, ordenar, buscar, retirar y eliminar productos.

---

## Índice

1. [Requisitos y ejecución](#1-requisitos-y-ejecución)
2. [Estructura del proyecto](#2-estructura-del-proyecto)
3. [Recorrido guiado (flujo del programa)](#3-recorrido-guiado-flujo-del-programa)
4. [Modelo del almacén](#4-modelo-del-almacén)
5. [Notación de complejidad](#5-notación-de-complejidad)
6. [Documentación por clase y método](#6-documentación-por-clase-y-método)
   - [EstadoProducto](#61-estadoproducto)
   - [Producto](#62-producto)
   - [Celda](#63-celda)
   - [ResultadoBusqueda](#64-resultadobusqueda)
   - [SharedScanner](#65-sharedscanner)
   - [Utils](#66-utils)
   - [Catalogo](#67-catalogo)
   - [Almacen](#68-almacen)
   - [DatosIniciales](#69-datosiniciales)
   - [MainGuiado](#610-mainguiado)
7. [Resumen de algoritmos de ordenamiento](#7-resumen-de-algoritmos-de-ordenamiento)
8. [Resumen de algoritmos de búsqueda](#8-resumen-de-algoritmos-de-búsqueda)
9. [Patrones de diseño utilizados](#9-patrones-de-diseño-utilizados)
10. [Decisiones y limitaciones conocidas](#10-decisiones-y-limitaciones-conocidas)

---

## 1. Requisitos y ejecución

| Requisito | Valor |
|---|---|
| Versión mínima de Java | **Java 14** (usa `switch` con flechas `->` y `String.repeat`) |
| Versión configurada en `pom.xml` | Java 21 |
| Dependencias externas | Ninguna |
| Codificación de archivos | UTF-8 (el programa usa caracteres como `█`, `▲`, `═`, `✔`) |
| Clase principal | `org.slotify.MainGuiado` |

**Ejecución desde un IDE (NetBeans, IntelliJ, Eclipse):**
1. Copiar los 10 archivos `.java` dentro de un mismo paquete.
2. Si el paquete no se llama `org.slotify`, cambiar la primera línea `package ...;` de cada archivo por el nombre del paquete destino.
3. Ejecutar `MainGuiado`.

**Ejecución desde la terminal:**
```bash
javac -d out src/main/java/org/slotify/*.java
java -cp out org.slotify.MainGuiado
```

---

## 2. Estructura del proyecto

Todas las clases están en un único paquete: `org.slotify`.

```
src/main/java/org/slotify/
├── MainGuiado.java         Punto de entrada: recorrido guiado de 12 pasos
├── DatosIniciales.java     Carga 12 productos de ejemplo
├── Catalogo.java           Arreglo de productos: CRUD, búsquedas y 7 ordenamientos
├── Producto.java           Entidad producto (SKU, familia, peso, rotación, ABC...)
├── ResultadoBusqueda.java  Producto encontrado + número de comparaciones
├── Almacen.java            Matriz 2D de celdas: pasillos, estanterías, ubicación
├── Celda.java              Una posición de la matriz (estado, producto, pasillo)
├── EstadoProducto.java     Enum: VACIO, OCUPADO, PARED, PASILLO
├── Utils.java              Lectura validada de datos y mensajes por consola
└── SharedScanner.java      Scanner único compartido (Singleton)
```

**Relaciones entre clases:**

```
MainGuiado ──usa──► Catalogo ──contiene──► Producto[]
     │                  │
     │                  └──devuelve──► ResultadoBusqueda
     │
     ├──usa──► Almacen ──contiene──► Celda[][] ──tiene──► Producto
     │                                    │
     │                                    └──tiene──► EstadoProducto
     │
     ├──usa──► DatosIniciales ──llena──► Catalogo
     └──usa──► Utils ──usa──► SharedScanner
```

---

## 3. Recorrido guiado (flujo del programa)

`MainGuiado` ejecuta los pasos en este orden:

| Paso | Nombre | Qué hace | Métodos principales |
|---|---|---|---|
| 1 | Crear tu almacén | Pide filas (5-20) y columnas (5-30) y genera la matriz | `Almacen.crearAlmacen`, `Almacen.mostrarAlmacen` |
| 2 | Catálogo de ejemplo | Carga 12 productos y los muestra en tabla | `DatosIniciales.cargarProductos`, `Catalogo.mostrarTabla` |
| 3 | Llenar el almacén | Coloca los productos: más rotación = más cerca de la entrada | `Almacen.ubicarProductos` |
| 4 | Productos por pasillo | Lista qué hay en cada pasillo | `Almacen.mostrarProductosPorPasillo` |
| 5 | Agregar y colocar producto | Registra un producto en el catálogo, lo ubica en una celda libre de la matriz y muestra el almacén antes y después | `Utils.crearNuevoProducto`, `Catalogo.agregarProducto`, `Almacen.agregarProducto` |
| 6 | Actualizar un producto | Reemplaza los datos de un producto (por SKU) en el catálogo y en su celda. Muestra la tabla antes y después | `Catalogo.actualizarProducto`, `Almacen.eliminarProducto`, `Almacen.agregarProducto` |
| 7 | Ordenar el catálogo | El usuario elige uno de los 7 algoritmos. Muestra la tabla antes y después; se puede repetir | `ordenarProductosPor...` |
| 8 | Buscar un producto | El usuario elige búsqueda lineal o binaria; se muestra el número de comparaciones | `buscarProductoLineal`, `buscarProductoBinario` |
| 9 | Buscar ubicación | Dado un SKU, indica pasillo, fila y columna | `Almacen.buscarUbicacion`, `Almacen.describirUbicacion` |
| 10 | Retirar de su ubicación | Vacía una celda; el producto sigue en el catálogo | `Almacen.eliminarProducto` |
| 11 | Eliminar del catálogo | Elimina por nombre y lo retira también del almacén. Muestra la tabla antes y después | `Catalogo.eliminarProducto`, `Almacen.eliminarProducto` |
| 12 | Estado final | Muestra el catálogo y el almacén resultantes | `mostrarTabla`, `mostrarAlmacen` |

---

## 4. Modelo del almacén

El almacén es una matriz `Celda[filas][columnas]`. Cada celda tiene un `EstadoProducto`:

| Estado | Símbolo | Significado |
|---|---|---|
| `PARED` | `███` | Borde exterior de la matriz |
| `PASILLO` | ` · ` | Zona de circulación, no almacena productos |
| `VACIO` | `[ ]` | Estantería libre |
| `OCUPADO` | `[A]`, `[B]`, `[C]` | Estantería con un producto (se muestra su clasificación ABC) |
| Entrada | ` ▲ ` | Celda de la fila inferior, columna 1 (es un `PASILLO`) |

**Reglas de construcción (`llenarAlmacen`):**
- Fila 0, última fila, columna 0 y última columna → `PARED`.
- Fila 1 y penúltima fila → `PASILLO` horizontal.
- En las filas interiores, con `k = columna - 1`, el patrón se repite cada 3 columnas:
  - `k % 3 == 0` → `PASILLO` vertical (pasillo número `k / 3 + 1`).
  - `k % 3 == 1` → estantería del pasillo de su izquierda.
  - `k % 3 == 2` → estantería del pasillo de su derecha (o pasillo extra si es la última columna interior).
- La celda `(filas - 1, 1)` se abre como entrada.

Ejemplo de un almacén de 8×13 lleno:

```
    0  1  2  3  4  5  6  7  8  9  10 11 12
       P1       P2       P3       P4
 0  ███████████████████████████████████████
 1  ███ ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  · ███
 2  ███ · [B][B] · [ ][ ] · [ ][ ] · [ ]███
 3  ███ · [A][B] · [C][ ] · [ ][ ] · [ ]███
 4  ███ · [A][A] · [C][ ] · [ ][ ] · [ ]███
 5  ███ · [A][A] · [B][C] · [ ][ ] · [ ]███
 6  ███ ·  ·  ·  ·  ·  ·  ·  ·  ·  ·  · ███
 7  ███ ▲ █████████████████████████████████
```

---

## 5. Notación de complejidad

| Símbolo | Significado |
|---|---|
| `n` | Número actual de productos en el catálogo (`numeroActualProductos`) |
| `T` | Capacidad máxima del catálogo (`totalProductos`, 50 en el recorrido) |
| `F`, `C` | Filas y columnas del almacén |
| `F·C` | Número total de celdas de la matriz |
| `L` | Número de estanterías libres (`L ≤ F·C`) |
| `P` | Número de pasillos (`P ≈ C / 3`) |
| `m` | Longitud de un texto |

- **Complejidad temporal**: cuántas operaciones hace el método según el tamaño de la entrada.
- **Complejidad espacial**: memoria **adicional** que usa el método (sin contar los datos que ya existían).
- Las comparaciones de `String` (`compareToIgnoreCase`, `equalsIgnoreCase`) cuestan `O(m)`. Como los nombres y SKU son cortos, se consideran `O(1)` en el análisis.
- Los métodos que leen datos del teclado dependen de cuántas veces el usuario escriba un valor inválido; ese tiempo no se cuenta.

---

## 6. Documentación por clase y método

### 6.1 `EstadoProducto`

Enumeración con los cuatro estados posibles de una celda: `VACIO`, `OCUPADO`, `PARED`, `PASILLO`.

| Método | Descripción | Tiempo | Espacio | Por qué |
|---|---|---|---|---|
| `esTransitable()` | Devuelve `true` si el estado es `VACIO` o `PASILLO` (se puede pasar por la celda) | O(1) | O(1) | Solo hace dos comparaciones de referencia. No se usa en el recorrido actual; queda disponible para calcular rutas |

---

### 6.2 `Producto`

Entidad que representa un artículo del almacén.

**Atributos:**

| Atributo | Tipo | Descripción |
|---|---|---|
| `nombre` | `String` | Nombre descriptivo |
| `SKU` | `String` | Código único del producto (ej. `SKU-001`) |
| `familia` | `String` | Grupo o categoría (Filtros, Motores, EPP...) |
| `volumen` | `Double` | Volumen en m³ |
| `peso` | `Double` | Peso en kg |
| `rotacion` | `int` | Demanda en unidades por mes |
| `clasificacion` | `String` | Clasificación ABC (A = alta rotación, C = baja) |
| `herramientaPicking` | `String` | Manual, Montacargas o Grúa |
| `posicion` | `int` (público) | Índice actual del producto dentro del arreglo del catálogo |

**Métodos:**

| Método | Descripción | Tiempo | Espacio | Por qué |
|---|---|---|---|---|
| `Producto()` | Constructor vacío; los datos se asignan con setters | O(1) | O(1) | Solo reserva el objeto |
| `Producto(String nombre)` | Crea un producto solo con nombre | O(1) | O(1) | Una asignación |
| `Producto(nombre, SKU, familia, volumen, peso, rotacion, clasificacion, herramientaPicking)` | Crea un producto con todos sus datos | O(1) | O(1) | Número fijo de asignaciones |
| `getNombre()` / `setNombre()` | Lee o cambia el nombre | O(1) | O(1) | Acceso directo a un atributo |
| `getSKU()` / `setSKU()` | Lee o cambia el SKU | O(1) | O(1) | Acceso directo |
| `getFamilia()` / `setFamilia()` | Lee o cambia la familia | O(1) | O(1) | Acceso directo |
| `getVolumen()` / `setVolumen()` | Lee o cambia el volumen | O(1) | O(1) | Acceso directo |
| `getPeso()` / `setPeso()` | Lee o cambia el peso | O(1) | O(1) | Acceso directo |
| `getRotacion()` / `setRotacion()` | Lee o cambia la rotación | O(1) | O(1) | Acceso directo |
| `getClasificacion()` / `setClasificacion()` | Lee o cambia la clasificación ABC | O(1) | O(1) | Acceso directo |
| `getHerramienta()` / `setHerramienta()` | Lee o cambia la herramienta de picking | O(1) | O(1) | Acceso directo |
| `toString()` | Devuelve `"SKU - nombre (posicion: i)"` | O(m) | O(m) | Concatena textos de longitud `m`; con textos cortos es prácticamente O(1) |

---

### 6.3 `Celda`

Una posición de la matriz del almacén. Guarda su estado, el producto (si lo hay) y el número de pasillo al que pertenece.

| Método | Descripción | Tiempo | Espacio | Por qué |
|---|---|---|---|---|
| `Celda(EstadoProducto estado)` | Crea una celda con un estado y sin pasillo (paredes, pasillos horizontales) | O(1) | O(1) | Una asignación |
| `Celda(EstadoProducto estado, int pasillo)` | Crea una celda con estado y número de pasillo | O(1) | O(1) | Dos asignaciones |
| `Celda(Producto producto, EstadoProducto estado)` | Crea una celda que ya contiene un producto. No se usa en el recorrido | O(1) | O(1) | Dos asignaciones |
| `getProducto()` | Devuelve el producto guardado o `null` | O(1) | O(1) | Acceso directo |
| `getEstado()` | Devuelve el estado de la celda | O(1) | O(1) | Acceso directo |
| `getPasillo()` | Devuelve el número de pasillo (0 si no tiene) | O(1) | O(1) | Acceso directo |
| `setEstado(EstadoProducto)` | Cambia el estado | O(1) | O(1) | Asignación |
| `setProducto(Producto)` | Cambia el producto guardado | O(1) | O(1) | Asignación |

---

### 6.4 `ResultadoBusqueda`

Objeto que devuelven las búsquedas del catálogo para informar **qué** se encontró y **cuánto costó** encontrarlo.

| Método | Descripción | Tiempo | Espacio | Por qué |
|---|---|---|---|---|
| `ResultadoBusqueda(Producto, int)` | Guarda el producto encontrado (o `null`) y el número de comparaciones | O(1) | O(1) | Dos asignaciones |
| `getProducto()` | Devuelve el producto encontrado o `null` | O(1) | O(1) | Acceso directo |
| `getComparaciones()` | Devuelve cuántas comparaciones se hicieron | O(1) | O(1) | Acceso directo |

---

### 6.5 `SharedScanner`

Garantiza que todo el programa use **un único** `Scanner` sobre `System.in`. Si se crearan varios `Scanner` y uno se cerrara, se cerraría la entrada estándar para todos.

Usa el patrón **Singleton con clase interna** (*Initialization-on-demand holder*): la instancia se crea la primera vez que se accede a `Propietario.INSTANCIA`, y la JVM garantiza que esa inicialización ocurre una sola vez, incluso con varios hilos.

| Método | Descripción | Tiempo | Espacio | Por qué |
|---|---|---|---|---|
| `SharedScanner()` (privado) | Crea el `Scanner` sobre `System.in` | O(1) | O(1) | Es privado para que nadie más pueda instanciarlo |
| `getInstancia()` | Devuelve la única instancia | O(1) | O(1) | Lectura de un campo estático ya inicializado |
| `getScanner()` | Devuelve el `Scanner` compartido | O(1) | O(1) | Acceso directo |
| `closeScanner()` | Cierra el `Scanner` (se llama al terminar el programa) | O(1) | O(1) | Una sola operación de cierre |

---

### 6.6 `Utils`

Métodos estáticos de apoyo: lectura de datos **validada** desde el teclado y mensajes formateados.

Todos los métodos `leer...` repiten la pregunta hasta que el valor sea válido, así el programa nunca se cae por una entrada incorrecta. Su costo real depende de cuántas veces el usuario se equivoque (`k` intentos → `O(k)`); por intento es `O(m)` por leer una línea de longitud `m`.

| Método | Descripción | Tiempo | Espacio | Por qué |
|---|---|---|---|---|
| `crearNuevoProducto()` | Pide todos los datos de un producto (nombre, SKU en mayúsculas, familia, volumen, peso, rotación, ABC y herramienta) y devuelve el objeto | O(1) por dato | O(1) | Hace un número fijo de lecturas (8), cada una validada |
| `esPar(int)` | Devuelve `true` si el número es par. No se usa en el recorrido | O(1) | O(1) | Una operación de módulo |
| `leerTexto(String)` | Muestra el mensaje y lee una línea no vacía (sin espacios al inicio ni al final) | O(k·m) | O(m) | Repite hasta recibir texto; guarda una línea |
| `leerEntero(String, min, max)` | Lee un entero dentro de `[min, max]`; captura `NumberFormatException` | O(k·m) | O(m) | Usa `leerTexto` y convierte con `Integer.parseInt` |
| `leerDecimal(String, min)` | Lee un decimal `≥ min`; acepta coma o punto como separador | O(k·m) | O(m) | Reemplaza `,` por `.` y convierte con `Double.parseDouble` |
| `leerClasificacion()` | Lee `A`, `B` o `C` (sin importar mayúsculas) | O(k) | O(1) | Tres comparaciones por intento |
| `confirmar(String)` | Pregunta sí/no; acepta `s`, `si`, `sí`, `n`, `no` | O(k) | O(1) | Cinco comparaciones por intento |
| `pausa()` | Espera a que el usuario presione ENTER | O(1) | O(1) | Una lectura de línea |
| `titulo(String)` | Dibuja un recuadro `╔═╗` alrededor del texto | O(m) | O(m) | `repeat` construye una línea del largo del texto |
| `exito(String)` | Imprime `✔ texto` | O(m) | O(1) | Imprime el texto |
| `error(String)` | Imprime `✘ texto` | O(m) | O(1) | Imprime el texto |
| `info(String)` | Imprime `ℹ texto` | O(m) | O(1) | Imprime el texto |

---

### 6.7 `Catalogo`

Lista de productos implementada con un **arreglo de tamaño fijo** (`Producto[] productos`) y un contador (`numeroActualProductos`). Los productos siempre ocupan las posiciones `0` a `n - 1`, sin huecos.

**Atributos:**

| Atributo | Descripción |
|---|---|
| `totalProductos` | Capacidad máxima del arreglo |
| `productos` | Arreglo con los productos |
| `numeroActualProductos` (público) | Cantidad de productos cargados (`n`) |
| `ordenadoPorNombre` | Indica si el arreglo está ordenado por nombre; lo necesita la búsqueda binaria |

#### 6.7.1 Operaciones básicas (CRUD)

| Método | Descripción | Tiempo | Espacio | Por qué |
|---|---|---|---|---|
| `Catalogo(int totalProductos)` | Crea el arreglo con la capacidad indicada | O(T) | O(T) | Java reserva e inicializa en `null` las `T` posiciones |
| `conseguirTamano(Producto[] lista)` | Cuenta los elementos no nulos de un arreglo. No se usa en el recorrido | O(longitud) | O(1) | Recorre el arreglo completo una vez |
| `getProducto(int indice)` | Devuelve el producto en esa posición o `null` si el índice no es válido | O(1) | O(1) | Acceso directo por índice en un arreglo |
| `agregarProducto(Producto)` | Inserta al final, asigna su `posicion` y marca el catálogo como no ordenado. Si está lleno, devuelve `false` | O(1) | O(1) | Escribe directamente en `productos[n]`; no desplaza nada |
| `eliminarProducto(String nombre)` | Busca por nombre, desplaza a la izquierda los productos siguientes y actualiza posiciones | O(n) | O(1) | Búsqueda lineal O(n) + desplazamiento O(n) + `actualizarPosiciones` O(n) = O(3n) = O(n). Mantener el arreglo sin huecos obliga a mover elementos |
| `actualizarProducto(Producto original, Producto nuevo)` | Reemplaza al producto en su misma posición del arreglo | O(1) | O(1) | Usa `original.posicion` para ir directo al índice, sin buscar |
| `mostrarProductos()` | Imprime cada producto con `toString()`. No se usa en el recorrido | O(T) | O(1) | Recorre todo el arreglo, incluidas las posiciones vacías |
| `mostrarTabla()` | Imprime una tabla alineada con todas las columnas del producto | O(n) | O(1) | Una fila por producto; el formato de cada fila es de costo constante |
| `copiarCatalogo()` | Crea un catálogo nuevo con copias de todos los productos | O(T + n) | O(T + n) | Reserva un arreglo de capacidad `T` y crea `n` objetos nuevos |
| `actualizarPosiciones()` (privado) | Hace que `productos[i].posicion = i` para todos | O(n) | O(1) | Una pasada por el arreglo. Se llama después de cualquier cambio de orden |
| `recortar(String, int)` (privado) | Acorta un texto largo y le agrega `…` | O(m) | O(m) | `substring` copia hasta `m` caracteres |

#### 6.7.2 Búsquedas

| Método | Descripción | Mejor | Promedio | Peor | Espacio |
|---|---|---|---|---|---|
| `buscarProducto(String nombre)` | Búsqueda lineal por nombre; devuelve el `Producto` o `null`. La usa `eliminarProducto` | O(1) | O(n) | O(n) | O(1) |
| `buscarProductoPorSKU(String sku)` | Búsqueda lineal por SKU | O(1) | O(n) | O(n) | O(1) |
| `buscarProductoLineal(String nombre)` | Búsqueda lineal por nombre que además **cuenta comparaciones** y devuelve un `ResultadoBusqueda` | O(1) | O(n) | O(n) | O(1) |
| `buscarProductoBinario(String nombre)` | Búsqueda binaria por nombre. Si el catálogo no está ordenado por nombre, primero lo ordena con Bubble sort | O(1) | O(log n) | O(log n) si ya está ordenado; O(n²) si debe ordenar antes | O(1) |

**Por qué la búsqueda lineal es O(n):** revisa los productos uno por uno desde el índice 0. En el mejor caso el producto está primero (1 comparación); en el peor caso está último o no existe (`n` comparaciones). En promedio revisa la mitad, `n/2`, que sigue siendo O(n).

**Por qué la búsqueda binaria es O(log n):** en cada paso compara con el elemento del medio y descarta la mitad del rango (`inicio = medio + 1` o `fin = medio - 1`). El rango pasa de `n` a `n/2`, `n/4`, ..., `1`, así que como máximo hace `⌊log₂ n⌋ + 1` comparaciones. Con 13 productos, como mucho 4 comparaciones, frente a las 13 de la lineal.

**Por qué necesita ordenar:** la binaria solo funciona si el arreglo está ordenado por el mismo criterio que busca (nombre). El atributo `ordenadoPorNombre` evita reordenar si ya lo está. Cualquier otro ordenamiento, `agregarProducto` o `actualizarProducto` lo vuelve `false`.

#### 6.7.3 Ordenamientos

Todos los ordenamientos trabajan **sobre el mismo arreglo** (`productos`, posiciones `0` a `n - 1`), terminan llamando a `actualizarPosiciones()` y ajustan `ordenadoPorNombre`.

##### `ordenarProductosPorNombre()` · Bubble sort (burbuja)

- **Criterio:** nombre, de la A a la Z (sin distinguir mayúsculas).
- **Funcionamiento:** recorre el arreglo comparando cada par de vecinos `j` y `j+1`; si están en desorden los intercambia. Tras la pasada `i`, el elemento mayor queda al final (“sube como una burbuja”), por eso cada pasada revisa un elemento menos (`n - 1 - i`).
- **Complejidad temporal:** O(n²) en **todos** los casos.
- **Por qué:** hay dos ciclos anidados; el externo hace `n - 1` pasadas y el interno `n - 1 - i` comparaciones. En total `(n-1) + (n-2) + ... + 1 = n(n-1)/2` comparaciones. Esta implementación no detiene el ciclo si una pasada no hizo intercambios, por eso incluso con el arreglo ya ordenado hace las `n(n-1)/2` comparaciones.
- **Complejidad espacial:** O(1): solo usa una variable temporal para intercambiar.
- **Estable:** sí (solo intercambia si `comparacion > 0`, así que los iguales no cambian de orden).
- Deja `ordenadoPorNombre = true`.

##### `ordenarProductosPorRotacion()` · Selection sort (selección)

- **Criterio:** rotación de **mayor a menor** (primero lo que más se vende).
- **Funcionamiento:** para cada posición `i`, busca en el resto del arreglo (`i+1` a `n-1`) el producto con mayor rotación y lo intercambia con el de la posición `i`.
- **Complejidad temporal:** O(n²) en todos los casos.
- **Por qué:** para cada `i` siempre recorre todo lo que falta para encontrar el mayor, sin importar si el arreglo ya estaba ordenado: `(n-1) + (n-2) + ... + 1 = n(n-1)/2` comparaciones. Su ventaja es que hace como máximo `n - 1` intercambios.
- **Complejidad espacial:** O(1).
- **Estable:** no (el intercambio a distancia puede cambiar el orden de productos con la misma rotación).

##### `ordenarProductosPorClasificacion()` · Insertion sort (inserción)

- **Criterio:** clasificación ABC (`A`, luego `B`, luego `C`).
- **Funcionamiento:** toma el producto de la posición `i` y lo va desplazando hacia la izquierda mientras el anterior sea mayor, hasta insertarlo en su lugar. La parte izquierda (`0` a `i-1`) siempre está ordenada.
- **Complejidad temporal:**
  - Mejor caso **O(n)**: si ya está ordenado, el `while` interno no entra nunca y solo se hace una comparación por elemento.
  - Promedio y peor caso **O(n²)**: si está en orden inverso, cada elemento se desplaza hasta el inicio: `1 + 2 + ... + (n-1) = n(n-1)/2` movimientos.
- **Complejidad espacial:** O(1).
- **Estable:** sí (usa `> 0`, así que no pasa por delante de elementos iguales). Por eso es buena opción para ABC, donde hay muchos valores repetidos.

##### `ordenarProductosPorPeso()` · Shell sort

- **Criterio:** peso de menor a mayor.
- **Funcionamiento:** es una mejora de la inserción. En lugar de comparar vecinos, compara elementos separados por un salto (`gap`) que empieza en `n/2` y se divide a la mitad en cada ronda (`n/2`, `n/4`, ..., `1`). Los saltos grandes mueven rápido los elementos que están lejos de su lugar; la última ronda (`gap = 1`) es una inserción normal sobre un arreglo casi ordenado.
- **Complejidad temporal:**
  - Mejor caso **O(n log n)**: hay `log n` rondas de `gap` y, si ya está ordenado, cada ronda hace una comparación por elemento.
  - Promedio: aproximadamente **O(n^1.5)** con la secuencia de saltos `n/2, n/4, ...` (depende de la secuencia elegida).
  - Peor caso **O(n²)**: con esta secuencia de saltos existen entradas donde las rondas grandes no ayudan y la última ronda hace casi todo el trabajo.
- **Complejidad espacial:** O(1).
- **Estable:** no (los saltos pueden adelantar un elemento sobre otro igual).

##### `ordenarProductosPorSKU()` · Merge sort (mezcla)

- **Criterio:** SKU en orden alfabético.
- **Funcionamiento (divide y vencerás):**
  1. `mergeSort(auxiliar, inicio, fin)` divide el rango en dos mitades y se llama recursivamente para cada una hasta llegar a rangos de 1 elemento.
  2. `mezclar(auxiliar, inicio, medio, fin)` copia el rango al arreglo auxiliar y lo vuelve a escribir en `productos` tomando siempre el menor de los dos frentes (izquierda y derecha).
- **Complejidad temporal:** **O(n log n)** en todos los casos.
- **Por qué:** el arreglo se divide a la mitad hasta llegar a tamaño 1, lo que da `log₂ n` niveles de recursión. En cada nivel, las mezclas recorren en total los `n` elementos. `log n` niveles × `n` trabajo por nivel = `n log n`. No depende del orden inicial de los datos.
- **Complejidad espacial:** **O(n)**: el arreglo `auxiliar` de tamaño `n` (se crea una sola vez y se reutiliza) más `O(log n)` de la pila de recursión.
- **Estable:** sí (en empate usa `<= 0` y toma primero el de la izquierda).

| Método auxiliar | Descripción | Tiempo | Espacio |
|---|---|---|---|
| `mergeSort(Producto[] auxiliar, int inicio, int fin)` (privado) | Divide recursivamente el rango | O(k log k) para un rango de tamaño `k` | O(log k) de pila |
| `mezclar(Producto[] auxiliar, int inicio, int medio, int fin)` (privado) | Une dos mitades ordenadas | O(k) | O(1) extra (usa el auxiliar ya creado) |

##### `ordenarProductosPorVolumen()` · Quick sort (rápido)

- **Criterio:** volumen de menor a mayor.
- **Funcionamiento (divide y vencerás):**
  1. `particionar(inicio, fin)` toma como **pivote** el volumen del último elemento. Recorre el rango y pasa a la izquierda todo lo que sea `≤ pivote`; al final coloca el pivote justo después. El pivote queda en su posición definitiva (esquema de Lomuto).
  2. `quickSort` se llama recursivamente para la parte izquierda y la derecha del pivote.
- **Complejidad temporal:**
  - Mejor y promedio **O(n log n)**: si el pivote divide el rango en partes parecidas, hay `log n` niveles y cada nivel recorre `n` elementos en total.
  - Peor caso **O(n²)**: si el arreglo ya está ordenado (o todos los volúmenes son iguales), el último elemento siempre es el mayor, una de las partes queda vacía y la otra tiene `k - 1` elementos: `(n-1) + (n-2) + ... + 1 = n(n-1)/2`.
- **Complejidad espacial:** O(log n) promedio por la pila de recursión; O(n) en el peor caso. No usa arreglos auxiliares.
- **Estable:** no.

| Método auxiliar | Descripción | Tiempo | Espacio |
|---|---|---|---|
| `quickSort(int inicio, int fin)` (privado) | Ordena recursivamente el rango | O(k log k) promedio, O(k²) peor | O(log k) promedio |
| `particionar(int inicio, int fin)` (privado) | Ubica el pivote y separa menores y mayores; devuelve el índice del pivote | O(k) | O(1) |

##### `ordenarProductosPorFamilia()` · Heap sort (montículo)

- **Criterio:** familia en orden alfabético.
- **Funcionamiento:** ve el arreglo como un árbol binario completo (hijos de `i` en `2i+1` y `2i+2`).
  1. **Construir el montículo máximo:** llama a `hundir` desde el último nodo con hijos (`n/2 - 1`) hasta la raíz. Así, cada padre queda mayor o igual que sus hijos, y la raíz (`productos[0]`) es la familia mayor.
  2. **Extraer:** intercambia la raíz con el último elemento del montículo (que queda en su lugar definitivo), reduce el tamaño en 1 y vuelve a `hundir` la nueva raíz. Repite hasta que queda un solo elemento.
- **Complejidad temporal:** **O(n log n)** en todos los casos.
- **Por qué:** la altura del árbol es `log₂ n`, así que `hundir` cuesta como máximo O(log n). Construir el montículo cuesta O(n) (la mayoría de nodos están cerca de las hojas y bajan poco). Luego se hacen `n - 1` extracciones de O(log n) cada una: O(n log n). Total O(n + n log n) = O(n log n), sin importar el orden inicial.
- **Complejidad espacial:** O(1) de memoria extra para los datos; `hundir` es recursivo, así que usa O(log n) de pila.
- **Estable:** no.

| Método auxiliar | Descripción | Tiempo | Espacio |
|---|---|---|---|
| `hundir(int tamano, int raiz)` (privado) | Baja un nodo intercambiándolo con su hijo mayor hasta que cumpla la propiedad de montículo | O(log n) | O(log n) de pila |
| `compararFamilia(Producto a, Producto b)` (privado) | Compara familias sin distinguir mayúsculas | O(m) ≈ O(1) | O(1) |
| `intercambiar(int i, int j)` (privado) | Intercambia dos posiciones del arreglo. Lo usan Quick sort y Heap sort | O(1) | O(1) |

---

### 6.8 `Almacen`

Representa el almacén físico como una matriz `Celda[filas][columnas]`. Usa el patrón **Singleton**: existe una sola instancia (`INSTANCIA`) y la mayoría de métodos son estáticos y trabajan sobre ella.

**Atributos:**

| Atributo | Descripción |
|---|---|
| `MIN_FILAS`, `MAX_FILAS` | Límites de filas: 5 a 20 |
| `MIN_COLUMNAS`, `MAX_COLUMNAS` | Límites de columnas: 5 a 30 |
| `INSTANCIA` | Única instancia del almacén |
| `filas`, `columnas` | Tamaño de la matriz |
| `almacen` | La matriz de celdas |
| `pasillos` | Número de pasillos verticales generados |
| `entradaFila`, `entradaColumna` | Posición de la entrada: `(filas - 1, 1)` |

#### 6.8.1 Creación e instancia

| Método | Descripción | Tiempo | Espacio | Por qué |
|---|---|---|---|---|
| `Almacen(int filas, int columnas)` (privado) | Reserva la matriz y define la entrada | O(F·C) | O(F·C) | Java reserva e inicializa en `null` las `F·C` referencias. Es privado por el Singleton |
| `getInstancia(int filas, int columnas)` | Devuelve la instancia; si no existe la crea (sin llenarla). No se usa en el recorrido | O(1) si existe; O(F·C) si la crea | O(F·C) | Depende de si llama al constructor |
| `getInstancia()` | Devuelve la instancia o avisa si todavía no se creó | O(1) | O(1) | Lectura de un campo estático |
| `existe()` | Indica si el almacén ya fue creado. No se usa en el recorrido | O(1) | O(1) | Una comparación con `null` |
| `crearAlmacen(int filas, int columnas)` | Crea (o reemplaza) la instancia y la llena con paredes, pasillos y estanterías | O(F·C) | O(F·C) | Constructor O(F·C) + `llenarAlmacen` O(F·C) |
| `llenarAlmacen()` | Recorre la matriz y asigna a cada celda su estado y pasillo según las reglas de la [sección 4](#4-modelo-del-almacén) | O(F·C) | O(F·C) | Dos ciclos anidados (`F` × `C`) con trabajo constante por celda; crea un objeto `Celda` por posición |

#### 6.8.2 Operaciones sobre productos

| Método | Descripción | Tiempo | Espacio | Por qué |
|---|---|---|---|---|
| `agregarProducto(Producto, int fila, int columna)` | Coloca un producto en una celda. Falla si la posición está fuera del almacén o la celda no está `VACIO` | O(1) | O(1) | Acceso directo `almacen[fila][columna]`, sin recorrer la matriz |
| `eliminarProducto(int fila, int columna)` | Vacía una celda `OCUPADO` y devuelve el producto que tenía | O(1) | O(1) | Acceso directo por índices |
| `buscarUbicacion(String sku)` | Recorre la matriz buscando el SKU y devuelve `{fila, columna}` o `null` | Mejor O(1), peor O(F·C) | O(1) | Búsqueda lineal sobre la matriz: en el peor caso revisa todas las celdas |
| `describirUbicacion(int fila, int columna)` | Devuelve `"Pasillo P · fila f · columna c"` | O(1) | O(1) | Acceso directo y concatenación corta |
| `ubicarProductos(Catalogo)` | Llena el almacén automáticamente: los productos de mayor rotación van a las estanterías más cercanas a la entrada | O((F·C)² + n² + n·F·C) | O(F·C + T + n) | Ver explicación abajo |

**Explicación de `ubicarProductos`:**
1. **Lista de estanterías libres ordenadas por distancia a la entrada.** Recorre la matriz (`F·C`) y, cada vez que encuentra una celda `VACIO`, la inserta en su lugar dentro del arreglo `libres` (inserción ordenada por distancia Manhattan). Insertar la celda número `L` puede desplazar hasta `L` elementos, así que en el peor caso cuesta `1 + 2 + ... + L = O(L²)`, y como `L ≤ F·C`, esto es **O((F·C)²)**. Espacio: arreglo de `F·C` posiciones.
2. **Copia del catálogo y orden por rotación.** `copiarCatalogo()` O(T + n) y `ordenarProductosPorRotacion()` (Selection sort) **O(n²)**. Se ordena una copia para no alterar el orden del catálogo original.
3. **Colocación.** Por cada producto (`n`) llama a `buscarUbicacion` para no duplicarlo, que cuesta O(F·C): **O(n·F·C)**. Luego lo asigna a la siguiente estantería libre en O(1).

Con los tamaños del programa (máximo 20×30 = 600 celdas y 50 productos) se ejecuta al instante.

> Nota: como se ordena una copia, las celdas guardan **copias** de los productos del catálogo. Por eso la relación entre catálogo y almacén se hace siempre por **SKU** (`buscarUbicacion`), y al actualizar o eliminar un producto también se actualiza su celda.

#### 6.8.3 Visualización y conteo

| Método | Descripción | Tiempo | Espacio | Por qué |
|---|---|---|---|---|
| `mostrarAlmacen()` | Dibuja la matriz con índices de filas y columnas, etiquetas de pasillo, leyenda y porcentaje de ocupación | O(F·C) | O(F·C) | Recorre la matriz una vez para dibujarla y llama 3 veces a `contarEstado` (O(F·C) cada una): O(4·F·C) = O(F·C). Acumula el dibujo en un `StringBuilder` |
| `mostrarProductosPorPasillo()` | Para cada pasillo, lista los productos (posición, SKU, nombre) | O(P·F·C) ≈ O(F·C²) | O(1) | Por cada uno de los `P` pasillos recorre toda la matriz. Como `P ≈ C/3`, queda O(F·C²) |
| `dibujarCelda(int fila, int columna)` (privado) | Devuelve el símbolo de una celda (`███`, ` · `, `[ ]`, `[A]`, ` ▲ `) | O(1) | O(1) | Un `switch` sobre el estado |
| `estantesLibres()` | Cuenta las celdas `VACIO` | O(F·C) | O(1) | Llama a `contarEstado` |
| `estantesOcupados()` | Cuenta las celdas `OCUPADO` | O(F·C) | O(1) | Llama a `contarEstado` |
| `contarEstado(EstadoProducto)` (privado) | Cuenta cuántas celdas tienen un estado | O(F·C) | O(1) | Recorre todas las celdas una vez |
| `distanciaEntrada(int[] celda)` (privado) | Distancia Manhattan `|f - fe| + |c - ce|` a la entrada | O(1) | O(1) | Dos restas y dos valores absolutos |
| `posicionValida(int fila, int columna)` (privado) | Verifica que la posición esté dentro de la matriz | O(1) | O(1) | Cuatro comparaciones |
| `getFilas()` | Devuelve el número de filas | O(1) | O(1) | Acceso directo |
| `getColumnas()` | Devuelve el número de columnas | O(1) | O(1) | Acceso directo |
| `getPasillos()` | Devuelve el número de pasillos. No se usa en el recorrido | O(1) | O(1) | Acceso directo |

---

### 6.9 `DatosIniciales`

| Método | Descripción | Tiempo | Espacio | Por qué |
|---|---|---|---|---|
| `cargarProductos(Catalogo)` | Agrega 12 productos de ejemplo con distintas familias, pesos, rotaciones, clasificaciones y herramientas | O(1) | O(1) | Siempre son 12 inserciones de O(1); no depende de ninguna entrada |

---

### 6.10 `MainGuiado`

Punto de entrada. Contiene el `main` y un método privado por cada paso del recorrido. Usa un `Catalogo` con capacidad para 50 productos.

La complejidad de cada paso es la de los métodos que llama (más el tiempo de espera del usuario, que no se cuenta).

| Método | Descripción | Complejidad dominante |
|---|---|---|
| `main(String[])` | Muestra la bienvenida, ejecuta los 12 pasos en orden y cierra el `Scanner` | Suma de todos los pasos |
| `pasoCrearAlmacen()` | Pide filas y columnas, crea el almacén y lo muestra vacío | O(F·C) |
| `pasoCargarCatalogo()` | Carga los productos de ejemplo y muestra la tabla | O(n) |
| `pasoLlenarAlmacen()` | Ubica automáticamente los productos y muestra el almacén | O((F·C)² + n² + n·F·C) por `ubicarProductos` |
| `pasoProductosPorPasillo()` | Muestra los productos de cada pasillo | O(F·C²) |
| `pasoAgregarProducto()` | Opcional. Crea un producto, valida que el SKU no se repita, lo agrega al catálogo pide la fila y columna donde colocarlo en la matriz (repite si la celda no es válida) y muestra el almacén después, con el producto ya colocado | O(n) por la validación de SKU + O(F·C) por mostrar el almacén |
| `pasoActualizarProducto()` | Opcional. Muestra la tabla, pide el SKU del producto, pide los nuevos datos, valida que el nuevo SKU no pertenezca a otro producto, lo reemplaza en el catálogo y en su celda del almacén, y muestra la tabla actualizada | O(n + F·C): búsquedas por SKU O(n) y `buscarUbicacion` O(F·C) |
| `pasoOrdenarCatalogo()` | Muestra la tabla, ofrece los 7 algoritmos, ordena y vuelve a mostrar la tabla. Se puede repetir para probar otros | Del algoritmo elegido: O(n²) o O(n log n) |
| `pasoBuscarProducto()` | Pregunta si usar búsqueda lineal o binaria, busca por nombre e informa cuántas comparaciones hizo. Se puede repetir | Lineal O(n); binaria O(log n) (+ O(n²) si antes debe ordenar) |
| `pasoBuscarUbicacion()` | Pide un SKU y muestra su pasillo, fila y columna. Se puede repetir | O(F·C) |
| `pasoRetirarProducto()` | Muestra el almacén, pide una celda ocupada y la vacía (el producto sigue en el catálogo). Muestra el almacén después | O(F·C) por mostrar la matriz; retirar es O(1) |
| `pasoEliminarProducto()` | Muestra la tabla, pide un nombre, lo elimina del catálogo y de su celda si estaba ubicado, y muestra la tabla después | O(n + F·C) |
| `pasoResumenFinal()` | Muestra el catálogo y el almacén finales | O(n + F·C) |
| `encabezado(int paso, String titulo)` (privado) | Imprime el título `PASO x/12 · ...` | O(m) |

---

## 7. Resumen de algoritmos de ordenamiento

| # | Algoritmo | Método | Criterio | Mejor | Promedio | Peor | Espacio | Estable |
|---|---|---|---|---|---|---|---|---|
| 1 | Bubble sort | `ordenarProductosPorNombre` | Nombre (A→Z) | O(n²) | O(n²) | O(n²) | O(1) | Sí |
| 2 | Selection sort | `ordenarProductosPorRotacion` | Rotación (mayor→menor) | O(n²) | O(n²) | O(n²) | O(1) | No |
| 3 | Insertion sort | `ordenarProductosPorClasificacion` | ABC (A→C) | O(n) | O(n²) | O(n²) | O(1) | Sí |
| 4 | Shell sort | `ordenarProductosPorPeso` | Peso (menor→mayor) | O(n log n) | ≈ O(n^1.5) | O(n²) | O(1) | No |
| 5 | Merge sort | `ordenarProductosPorSKU` | SKU (A→Z) | O(n log n) | O(n log n) | O(n log n) | O(n) | Sí |
| 6 | Quick sort | `ordenarProductosPorVolumen` | Volumen (menor→mayor) | O(n log n) | O(n log n) | O(n²) | O(log n) | No |
| 7 | Heap sort | `ordenarProductosPorFamilia` | Familia (A→Z) | O(n log n) | O(n log n) | O(n log n) | O(1)* | No |

\* Heap sort no usa arreglos auxiliares; la recursión de `hundir` ocupa O(log n) de pila.

**Estable** significa que dos productos con el mismo valor del criterio conservan el orden relativo que tenían antes de ordenar.

**Comparación práctica:**
- Los algoritmos **O(n²)** (1 a 3) son simples y funcionan bien con pocos datos; Insertion sort es muy rápido si el arreglo ya está casi ordenado.
- **Shell sort** mejora a Insertion sort sin usar memoria extra.
- **Merge sort** garantiza O(n log n) siempre, a cambio de O(n) de memoria.
- **Quick sort** suele ser el más rápido en la práctica, pero se degrada a O(n²) con datos ya ordenados usando el último elemento como pivote.
- **Heap sort** garantiza O(n log n) sin memoria auxiliar, aunque no es estable.

---

## 8. Resumen de algoritmos de búsqueda

| Búsqueda | Método | Requisito | Mejor | Promedio | Peor | Espacio |
|---|---|---|---|---|---|---|
| Lineal por nombre | `buscarProductoLineal` / `buscarProducto` | Ninguno | O(1) | O(n) | O(n) | O(1) |
| Lineal por SKU | `buscarProductoPorSKU` | Ninguno | O(1) | O(n) | O(n) | O(1) |
| Binaria por nombre | `buscarProductoBinario` | Arreglo ordenado por nombre (lo ordena si hace falta) | O(1) | O(log n) | O(log n) | O(1) |
| Ubicación por SKU en la matriz | `Almacen.buscarUbicacion` | Ninguno | O(1) | O(F·C) | O(F·C) | O(1) |

Ejemplo real del programa con 13 productos:
- Búsqueda lineal de `Casco` → 5 comparaciones.
- Búsqueda binaria de `Tornillos M10 (caja)` → 3 comparaciones.
- La binaria nunca necesita más de `⌊log₂ 13⌋ + 1 = 4` comparaciones; la lineal puede necesitar 13.

---

## 9. Patrones de diseño utilizados

| Patrón | Dónde | Para qué |
|---|---|---|
| **Singleton** | `Almacen` | Garantiza que exista un único almacén compartido por todo el programa |
| **Singleton (holder idiom)** | `SharedScanner` | Un único `Scanner` sobre `System.in`, creado de forma perezosa y segura para hilos |
| **Objeto de resultado** | `ResultadoBusqueda` | Devolver dos valores (producto y comparaciones) desde un método |
| **Enumeración de estados** | `EstadoProducto` | Limitar los estados de una celda a valores válidos y usarlos en `switch` |
| **Clase utilitaria** | `Utils` | Centralizar la lectura validada y los mensajes para no repetir código |

---

## 10. Decisiones y limitaciones conocidas

- **Arreglo de tamaño fijo en `Catalogo`:** la capacidad es 50. Si se llena, `agregarProducto` avisa y devuelve `false`.
- **Relación catálogo–almacén por SKU:** el almacén guarda copias de los productos, por eso el SKU debe ser único. El programa valida que no se repita al agregar y al actualizar.
- **`eliminarProducto` del catálogo busca por nombre:** si hubiera dos productos con el mismo nombre, se elimina el primero que encuentre.
- **La búsqueda binaria cambia el orden del catálogo:** si no está ordenado por nombre, lo ordena antes de buscar, y ese orden se mantiene.
- **Quick sort con datos ya ordenados:** al usar el último elemento como pivote, cae en su peor caso O(n²). Con 50 productos como máximo no se nota.
- **Métodos no usados en el recorrido:** `EstadoProducto.esTransitable`, `Utils.esPar`, `Catalogo.conseguirTamano`, `Catalogo.mostrarProductos`, `Almacen.getInstancia(int, int)`, `Almacen.existe`, `Almacen.getPasillos` y el constructor `Celda(Producto, EstadoProducto)`. Están disponibles pero el recorrido guiado no los necesita.
- **Caracteres especiales:** la interfaz usa símbolos Unicode (`█`, `▲`, `═`, `✔`, `✘`, `ℹ`). Si la consola no usa UTF-8, pueden verse como `?` sin afectar el funcionamiento.
