# Listas enlazadas · recorrido, búsqueda y ordenamiento

Guía en lenguaje natural para explicar (o escribir a mano) las tres operaciones de la lista de pedidos de Slotify. Cada sección tiene la **lógica** (la idea, el porqué), el **algoritmo** (los pasos, tipo *mientras tal cosa…*) y la **complejidad** explicada con palabras.

---

## 0. La base de todo: cómo está hecha una lista enlazada

Una lista enlazada no guarda los datos uno pegado al otro, como hace un arreglo. Guarda **nodos sueltos por la memoria**, y cada nodo lleva dos cosas:

- el **dato** (en nuestro caso, un pedido),
- y la **referencia al siguiente nodo**.

La lista en sí solo conoce **un** nodo: la **cabeza**, que es el primero. Todos los demás se alcanzan pasando de un nodo a otro. El último nodo apunta a **null**, y eso es lo que marca el final de la lista.

```
cabeza → [PED-001] → [PED-002] → [PED-003] → null
```

De esa forma de estar construida salen las tres consecuencias que explican absolutamente todo lo demás:

1. **No hay índices.** No existe "el pedido número 3". Para llegar al cuarto nodo hay que pasar por los tres anteriores.
2. **La única forma de avanzar es seguir el enlace.** Desde un nodo solo se puede ir hacia adelante, y de uno en uno.
3. **Cambiar el orden es cambiar flechas, no mover datos.** Insertar o borrar en medio no obliga a correr nada, solo a reconectar dos referencias.

> **Frase para la expo:** "En un arreglo los datos están en fila y tienen dirección; en una lista enlazada los datos están dispersos y cada uno lleva escrito dónde está el siguiente."

---

## 1. Recorrido

### Lógica

Recorrer es **caminar la lista de principio a fin**, visitando cada nodo una sola vez.

Como la lista solo conoce la cabeza, todo recorrido empieza obligatoriamente ahí. Se usa una **variable auxiliar** —la llamamos `actual`— que hace de dedo señalando el nodo en el que estamos. Ese dedo se coloca primero en la cabeza y después va saltando al siguiente, una y otra vez, hasta que ya no hay siguiente.

La condición de parada es lo importante: **se sigue mientras `actual` no sea null**. Cuando `actual` vale null significa que nos pasamos del último nodo, o sea que la lista se acabó.

Lo interesante es que **este mismo esqueleto sirve para todo**: contar cuántos pedidos hay, sumar todas las cantidades, mostrar la tabla, filtrar los de un cliente. Lo único que cambia es qué se hace con el nodo mientras se está parado en él. Por eso conviene explicarlo una vez y decir "y todas las demás operaciones son esto mismo con otro trabajo dentro".

### Algoritmo — el recorrido base

```
actual ← cabeza

MIENTRAS actual ≠ nulo
    hacer algo con actual.dato
    actual ← actual.siguiente
FIN MIENTRAS
```

Leído en voz alta: *"pongo el dedo en la cabeza; mientras el dedo no se haya salido de la lista, proceso el dato que tengo delante y muevo el dedo al siguiente".*

### Algoritmo — variantes del mismo recorrido

**Contar los nodos** (comprobar cuántos hay de verdad, siguiendo enlaces):

```
total ← 0
actual ← cabeza

MIENTRAS actual ≠ nulo
    total ← total + 1
    actual ← actual.siguiente
FIN MIENTRAS

devolver total
```

**Sumar (recorrido acumulador)** — por ejemplo, cuántas unidades se han pedido en total:

```
total ← 0
actual ← cabeza

MIENTRAS actual ≠ nulo
    total ← total + actual.dato.cantidad
    actual ← actual.siguiente
FIN MIENTRAS

devolver total
```

**Llegar a la posición k** — aquí se ve la gran diferencia con el arreglo:

```
i ← 0
actual ← cabeza

MIENTRAS actual ≠ nulo
    SI i = k ENTONCES
        devolver actual.dato
    FIN SI
    i ← i + 1
    actual ← actual.siguiente
FIN MIENTRAS

devolver nulo        (esa posición no existe)
```

### Complejidad, explicada con palabras

- **Recorrido completo: O(n).** Se visita cada nodo exactamente una vez y lo que se hace en cada uno cuesta lo mismo siempre, así que el tiempo crece igual que el número de pedidos. Si hay el doble de pedidos, tarda el doble.
- **Llegar a la posición k: O(k).** No es que sea lento por capricho: es que físicamente hay que pasar por los k nodos anteriores. En un arreglo esto mismo sería **O(1)**, porque la posición se calcula con una cuenta.
- **Espacio: O(1).** Por muy larga que sea la lista, solo usamos una variable auxiliar (`actual`) y quizá un contador. No se crea ninguna estructura nueva.

> **Frase para la expo:** "El recorrido es O(n) y no hay forma de mejorarlo, porque la lista no da atajos: para ver el último hay que haber visto todos los anteriores."

---

## 2. Búsqueda

### Lógica

Buscar en una lista enlazada es **un recorrido que se detiene antes de tiempo**: se camina la lista comparando el dato de cada nodo con lo que buscamos, y en cuanto coincide, se devuelve y se corta. Si se llega al final sin coincidencia, es que no está.

Esto es la **búsqueda lineal** (o secuencial), y en una lista enlazada simple es **la única posible**. Este es el punto fuerte para defender ante el profesor, así que conviene explicarlo bien:

**¿Por qué no se puede hacer búsqueda binaria?** La búsqueda binaria funciona porque puede plantarse en el elemento del medio de golpe, comparar, y tirar la mitad que sobra. Eso exige poder saltar a cualquier posición al instante. En una lista enlazada, **llegar al elemento del medio ya cuesta recorrer media lista**. Así que haríamos `log n` saltos y cada salto costaría recorrer un montón de nodos: saldría **peor** que la búsqueda lineal de toda la vida.

De ahí sale una conclusión que sorprende y queda muy bien decirla: **ordenar una lista enlazada no hace que buscar sea más rápido.** En un arreglo sí (porque desbloquea la binaria); en una lista, no sirve de nada para buscar.

En nuestro proyecto la búsqueda además **va contando las comparaciones** que hace, para poder enseñar el número real en pantalla, y devuelve también **en qué posición estaba** el nodo.

### Algoritmo — búsqueda lineal con conteo

```
comparaciones ← 0
posicion ← 0
actual ← cabeza

MIENTRAS actual ≠ nulo
    comparaciones ← comparaciones + 1
    SI actual.dato.codigo = buscado ENTONCES
        devolver (actual.dato, comparaciones, posicion)
    FIN SI
    posicion ← posicion + 1
    actual ← actual.siguiente
FIN MIENTRAS

devolver (nulo, comparaciones, −1)
```

Leído en voz alta: *"empiezo en la cabeza; mientras no se acabe la lista, comparo el nodo actual con lo que busco, y si es ese lo devuelvo junto con lo que me costó encontrarlo; si no, avanzo. Si termino el bucle es que no estaba."*

La misma estructura sirve para buscar por SKU, para contar los pedidos de un cliente (en vez de devolver, se suma y se sigue) o para comprobar si un código ya existe.

### Complejidad, explicada con palabras

- **Mejor caso: O(1).** Lo que buscamos es el primer nodo, la cabeza: una sola comparación.
- **Caso promedio: O(n).** De media hay que revisar la mitad de la lista, `n/2`. Eso sigue siendo O(n), porque en esta notación las constantes no cuentan: lo que importa es que **crece en proporción** al tamaño, no el factor exacto.
- **Peor caso: O(n).** El elemento está al final, o directamente no existe: se recorre la lista entera.
- **Espacio: O(1).** Solo las variables auxiliares.
- **Búsqueda binaria: no aplicable**, por lo explicado arriba.

> **Frase para la expo:** "En la lista la búsqueda es siempre lineal. Y como no se puede hacer binaria, ordenarla no sirve para buscar más rápido: solo sirve para presentarla ordenada."

---

## 3. Ordenamiento

### Lógica general: hay dos formas de ordenar una lista

Antes de entrar en los algoritmos hay que explicar esta decisión, porque es lo que diferencia ordenar una lista de ordenar un arreglo:

**Forma A — reenlazar nodos (cambiar las flechas).**
Los nodos se quedan donde están en la memoria, pero se cambia a quién apunta cada uno. Los datos **no se tocan en ningún momento**. Es la forma "propia" de una lista: mover una referencia cuesta siempre lo mismo, da igual que el dato sea un número o un objeto enorme.

**Forma B — intercambiar datos (cambiar el contenido).**
Los nodos se quedan quietos y enlazados igual que antes; lo que se cambia es el dato que lleva cada uno dentro. Es mucho más fácil de escribir y no hay riesgo de romper la cadena, pero copia contenido en vez de referencias. Además tiene un efecto secundario: si alguien tuviera guardada una referencia a un nodo concreto, tras el intercambio esa referencia estaría apuntando a otro pedido distinto.

En el proyecto usamos **las dos** a propósito, para poder compararlas.

Otra cosa que conviene decir: en una lista **no usamos merge, quick ni heap**. Quick y heap necesitan saltar a posiciones concretas (el pivote, los hijos del montículo), y en una lista eso cuesta recorrer. Merge sort sí sería viable y daría O(n log n), pero para la cantidad de pedidos que manejamos no compensa la complicación.

---

### 3.1 Ordenar por código · **insertion sort reenlazando nodos**

#### Lógica

Es como ordenar cartas en la mano. Se construye **una segunda lista, que siempre está ordenada**, y se van sacando nodos de la lista original de uno en uno para **engancharlos en su sitio** dentro de esa segunda lista.

Para cada nodo que sacamos hay dos situaciones:
- o es **menor que el primero** de la lista ordenada, y entonces va al principio del todo,
- o hay que **buscar tras qué nodo** colocarlo, avanzando por la lista ordenada mientras los que vamos viendo sigan siendo menores o iguales.

Cuando encontramos el sitio, se hace la conexión en dos pasos: el nodo nuevo apunta a lo que venía después, y el nodo anterior pasa a apuntar al nodo nuevo.

**El detalle crítico —y el error clásico—:** antes de tocar el enlace del nodo que estamos moviendo, hay que **guardar en una variable cuál era su siguiente**. Si no, en el momento en que lo reenganchamos a la lista ordenada, perdemos la referencia a todo el resto de la lista original y se pierden los datos.

Al terminar, la cabeza de la lista pasa a ser la cabeza de la lista ordenada.

#### Algoritmo

```
ordenada ← nulo
actual ← cabeza

MIENTRAS actual ≠ nulo

    siguiente ← actual.siguiente        ← guardar ANTES de romper el enlace

    SI ordenada = nulo O el código de ordenada > el código de actual ENTONCES
        actual.siguiente ← ordenada     (va al principio)
        ordenada ← actual
    SI NO
        busca ← ordenada
        MIENTRAS busca.siguiente ≠ nulo Y el código de busca.siguiente ≤ el de actual
            busca ← busca.siguiente     (sigo avanzando: aún no es su sitio)
        FIN MIENTRAS
        actual.siguiente ← busca.siguiente
        busca.siguiente ← actual        (queda enganchado entre los dos)
    FIN SI

    actual ← siguiente
FIN MIENTRAS

cabeza ← ordenada
```

#### Complejidad

- **Peor caso y promedio: O(n²).** Por cada uno de los `n` nodos puede haber que recorrer toda la parte ya ordenada para encontrarle sitio.
- **Mejor caso: O(n).** Si los códigos ya vienen ordenados, cada nodo se engancha al primer intento y el bucle interno no llega a entrar.
- **Espacio: O(1).** Este es el punto que hay que destacar: **no se crea ni un solo nodo nuevo**, solo se cambian flechas. Un merge sort gastaría O(n) de memoria extra.
- **Es estable:** al avanzar usamos "menor o igual", así que un nodo nuevo nunca se cuela por delante de uno que ya estaba con el mismo valor.

---

### 3.2 Ordenar por cantidad · **selection sort intercambiando datos**

#### Lógica

Por cada nodo, se busca en **todo lo que queda por detrás** el pedido con mayor cantidad, y se intercambian los datos de los dos nodos. Así, en la primera vuelta el pedido más grande queda el primero; en la segunda vuelta el segundo más grande queda el segundo; y así hasta el final.

Lo característico de este algoritmo es que **compara muchísimo pero mueve poquísimo**: por cada posición hace un solo intercambio, como máximo. Por eso es el indicado cuando mover datos sale caro.

Aquí los **enlaces no se tocan en ningún momento**: la cadena de nodos sigue siendo exactamente la misma, lo que cambia es el contenido de cada uno.

#### Algoritmo

```
i ← cabeza

MIENTRAS i ≠ nulo

    mayor ← i
    j ← i.siguiente

    MIENTRAS j ≠ nulo                     ← busca el mayor en lo que falta
        SI j.dato.cantidad > mayor.dato.cantidad ENTONCES
            mayor ← j
        FIN SI
        j ← j.siguiente
    FIN MIENTRAS

    SI mayor ≠ i ENTONCES                 ← intercambia los DATOS, no los enlaces
        temporal ← i.dato
        i.dato ← mayor.dato
        mayor.dato ← temporal
    FIN SI

    i ← i.siguiente
FIN MIENTRAS
```

#### Complejidad

- **O(n²) en todos los casos**, incluso si la lista ya está ordenada: siempre recorre hasta el final lo que queda para asegurarse de cuál es el mayor. No tiene forma de "darse cuenta" de que ya estaba bien.
- El número de comparaciones sale de sumar `(n−1) + (n−2) + … + 1`, que es `n(n−1)/2`.
- **Espacio: O(1)** (una variable temporal para el intercambio).
- **No es estable:** el intercambio se hace entre nodos que pueden estar muy lejos, y eso puede adelantar a un pedido con la misma cantidad que ya estaba antes.

---

### 3.3 Ordenar por cliente · **bubble sort intercambiando datos, con bandera**

#### Lógica

Se recorre la lista comparando **cada nodo con su siguiente**, y si están en desorden se intercambian los datos. Se repiten pasadas hasta que la lista queda ordenada. En cada pasada, el mayor va "subiendo" hacia el final como una burbuja, que es de donde viene el nombre.

La mejora importante de esta versión es la **bandera**: una variable que empieza en falso al principio de cada pasada y se pone en verdadero si hubo algún intercambio. Si termina una pasada entera **sin ningún intercambio**, significa que ya está todo ordenado y se corta ahí mismo, sin hacer las pasadas restantes.

Vale la pena señalar que el bubble sort del **catálogo** (el del arreglo, que ordena por nombre) **no** tiene esta bandera, así que ese hace siempre todas las pasadas aunque el arreglo ya esté ordenado. Comparar los dos queda muy bien en la exposición.

#### Algoritmo

```
SI cabeza = nulo ENTONCES salir          (lista vacía)

huboIntercambio ← verdadero

MIENTRAS huboIntercambio

    huboIntercambio ← falso
    actual ← cabeza

    MIENTRAS actual.siguiente ≠ nulo
        SI el cliente de actual > el cliente de actual.siguiente ENTONCES
            intercambiar los datos de actual y actual.siguiente
            huboIntercambio ← verdadero
        FIN SI
        actual ← actual.siguiente
    FIN MIENTRAS

FIN MIENTRAS
```

#### Complejidad

- **Mejor caso: O(n).** Si la lista ya está ordenada, se hace una única pasada, no se intercambia nada, la bandera se queda en falso y se termina.
- **Promedio y peor caso: O(n²).** En el peor caso (lista al revés) hacen falta `n−1` pasadas de `n` comparaciones.
- **Espacio: O(1).**
- **Es estable:** solo se intercambia cuando uno es estrictamente mayor que el otro, así que dos pedidos del mismo cliente nunca se adelantan entre sí.

---

## 4. Resumen de complejidades

| Operación | Mejor | Promedio | Peor | Espacio |
|---|---|---|---|---|
| Recorrido completo | O(n) | O(n) | O(n) | O(1) |
| Llegar a la posición k | O(1) | O(k) | O(n) | O(1) |
| Búsqueda lineal | O(1) | O(n) | O(n) | O(1) |
| Búsqueda binaria | — no aplicable — | | | |
| Insertion sort (reenlazando) | O(n) | O(n²) | O(n²) | O(1) |
| Selection sort (datos) | O(n²) | O(n²) | O(n²) | O(1) |
| Bubble sort con bandera (datos) | O(n) | O(n²) | O(n²) | O(1) |

Los tres ordenamientos son **O(1) de espacio**: ninguno crea nodos nuevos ni estructuras auxiliares.

---

## 5. Comparación con el arreglo (el cierre de la exposición)

| Operación | Arreglo (catálogo) | Lista enlazada (pedidos) |
|---|---|---|
| Acceder a la posición k | **O(1)** — cuenta directa | O(k) — hay que caminar |
| Insertar al inicio | O(n) — hay que desplazar todo | **O(1)** — dos flechas |
| Insertar al final | **O(1)** — se escribe al final | O(n) — hay que llegar al último |
| Buscar | O(n), o **O(log n)** si está ordenado | O(n) siempre |
| Eliminar (ya localizado) | O(n) — hay que desplazar | **O(1)** — solo reenlaza |
| Tamaño | Fijo, puede llenarse | **Dinámico**, sin límite |

### Las tres ideas con las que cerrar

1. **El arreglo gana en lectura; la lista gana en modificación.** El arreglo tiene direcciones y por eso puede saltar a cualquier sitio y puede usar búsqueda binaria. La lista no tiene direcciones, pero como nada está pegado a nada, insertar y borrar no obliga a mover el resto.

2. **Por eso el catálogo es un arreglo y los pedidos son una lista.** El catálogo se consulta y se ordena constantemente y el número de productos es conocido y acotado. Los pedidos entran y se cancelan todo el tiempo y no se sabe cuántos habrá.

3. **No existe una estructura mejor, existe la estructura adecuada** a las operaciones que más se repiten. Si mañana hubiera que acceder al pedido número k todo el rato, el arreglo volvería a ser el candidato correcto.
