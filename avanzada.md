# Guía Definitiva (y Sin Tecnicismos Raros) de `Cube.java` en OpenGL ES 2.0

Imagina que **tu teléfono tiene dos cerebros**:
1. **La CPU (el procesador normal):** Es muy listo para pensar, hacer cuentas de Java, leer botones y organizar datos, pero es **lentísimo** dibujando millones de píxeles 60 veces por segundo.
2. **La GPU (la tarjeta gráfica):** Es como un ejército de miles de pintores enanos. No saben tomar decisiones complejas, pero si les das una lista de puntos y colores, pintan la pantalla a la velocidad de la luz.

El problema es que **la CPU y la GPU no hablan el mismo idioma ni comparten la misma memoria**. Toda esta clase `Cube.java` es básicamente un **traductor y mensajero** que prepara las medidas de un cubo en Java (CPU) y se las empaqueta bien bonito a la tarjeta gráfica (GPU) usando OpenGL ES 2.0.

---

## 1. El Paquete y las Importaciones (Líneas 1 a 8)

```java
package com.example.cuerpos3d_amaguaya_ojeda;
```
* **¿Qué hace?** Le dice a Java en qué carpeta/dirección vive este archivo dentro de tu proyecto de Android. Es como la dirección de tu casa para que otras clases (como `MyGLRenderer`) puedan encontrar a `Cube`.

```java
import android.opengl.GLES20;
import android.opengl.Matrix;
```
* `import android.opengl.GLES20;`: Trae la caja de herramientas de **OpenGL ES 2.0**. Cada vez que veas `GLES20.algo(...)`, es Java dándole una orden directa a la tarjeta gráfica.
* `import android.opengl.Matrix;`: Trae una calculadora matemática especializada en matrices de $4 \times 4$. En 3D, mover, girar, agrandar o proyectar un objeto en la pantalla se hace multiplicando matrices.

```java
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
```
* **¿Por qué importamos `Buffer`?** Java guarda sus arreglos (`float[]`, `short[]`) en una zona llamada *Heap* (memoria administrada por Java). La GPU **no puede leer** ahí porque Java mueve cosas de sitio cuando limpia la basura (*Garbage Collector*).
* Estas clases (`ByteBuffer`, `FloatBuffer`, `ShortBuffer`) sirven para crear **memoria nativa**, es decir, un espacio fijo en la RAM donde la GPU sí puede meter la nariz sin problemas.

---

## 2. Declaración de la Clase y sus Variables Globales (Líneas 10 a 26)

```java
public class Cube {
```
* Abre el molde llamado `Cube`. Cada vez que hagas `new Cube()`, se fabricará un cubo 3D siguiendo estas instrucciones.

```java
    private final FloatBuffer vertexBuffer;
```
* **¿Qué es?** Es la caja en memoria nativa donde guardaremos las coordenadas $(X, Y, Z)$ de las 8 esquinas del cubo. Usa `FloatBuffer` porque las coordenadas tienen decimales (`-0.5f`, `0.5f`).
* `private`: Solo `Cube` puede tocar esta variable.
* `final`: Una vez que se crea esa caja en el constructor, no se vuelve a cambiar por otra caja distinta.

```java
    private final ShortBuffer indexBuffer;
```
* **¿Qué es?** Es otra caja en memoria nativa donde guardaremos el **orden en que se unen los puntos** para formar triángulos. Usa `ShortBuffer` porque son números enteros pequeños (`0, 1, 2...`), y un `short` ocupa la mitad de memoria que un `int`.

```java
    private final int mProgram;
```
* **¿Qué es?** Es un número de identificación (un ID) que OpenGL le asigna al **programa gráfico** ya compilado. Un programa en OpenGL es la unión de dos mini-programas llamados *Shaders* (uno coloca los puntos y otro los pinta).

```java
    private int positionHandle, colorHandle, mvpMatrixHandle;
```
* **¿Qué son?** Son "controles remotos" o "enchufes" (números enteros) para conectarnos desde Java hacia las variables que viven dentro de la tarjeta gráfica:
  * `positionHandle`: El enchufe para mandarle las posiciones de los vértices.
  * `colorHandle`: El enchufe para mandarle el color de cada cara.
  * `mvpMatrixHandle`: El enchufe para mandarle la posición de la cámara y perspectiva.

```java
    static final int COORDS_PER_VERTEX = 3;
```
* **¿Qué significa?** Como estamos en **3D**, cada esquina (vértice) necesita **3 números**: cuánto a la izquierda/derecha ($X$), cuánto arriba/abajo ($Y$) y cuánto adelante/atrás ($Z$).
* `static final`: Es una constante fija que vale `3` para todos los cubos y nunca cambia.

---

## 3. Las Coordenadas del Cubo: `cubeCoords` (Líneas 29 a 42)

```java
    private static final float[] cubeCoords = {
            // Cara frontal (Z = +0.5f)
            -0.5f,  0.5f,  0.5f, // 0: sup-izq
            -0.5f, -0.5f,  0.5f, // 1: inf-izq
             0.5f, -0.5f,  0.5f, // 2: inf-der
             0.5f,  0.5f,  0.5f, // 3: sup-der

            // Cara trasera (Z = -0.5f)
            -0.5f,  0.5f, -0.5f, // 4: sup-izq
            -0.5f, -0.5f, -0.5f, // 5: inf-izq
             0.5f, -0.5f, -0.5f, // 6: inf-der
             0.5f,  0.5f, -0.5f  // 7: sup-der
    };
```

### ¿Cómo funciona el sistema de coordenadas en OpenGL?
Imagina que el centro exacto de tu universo 3D es el punto $(0, 0, 0)$:
* **Eje $X$:** Negativo (`-0.5f`) es a la **izquierda**; positivo (`+0.5f`) es a la **derecha**.
* **Eje $Y$:** Negativo (`-0.5f`) es **abajo**; positivo (`+0.5f`) es **arriba**.
* **Eje $Z$:** Positivo (`+0.5f`) viene **hacia tu cara** (adelante); negativo (`-0.5f`) se mete **hacia el fondo de la pantalla** (atrás).

Como cada esquina va desde `-0.5` hasta `+0.5`, la distancia total de lado a lado es:
$$0.5 - (-0.5) = 1.0$$
Es decir, es un cubo de $1 \times 1 \times 1$ centrado justo en el origen $(0,0,0)$.

Cada fila de 3 números tiene un **número de esquina (índice)** del `0` al `7`:
* `0`: Adelante, arriba, izquierda.
* `1`: Adelante, abajo, izquierda.
* `2`: Adelante, abajo, derecha.
* `3`: Adelante, arriba, derecha.
* `4`: Atrás, arriba, izquierda.
* `5`: Atrás, abajo, izquierda.
* `6`: Atrás, abajo, derecha.
* `7`: Atrás, arriba, derecha.

---

## 4. El Orden de Dibujo: `drawOrder` (Líneas 45 a 52)

```java
    private static final short[] drawOrder = {
            0, 1, 2,  0, 2, 3, // Cara 0: Frontal
            3, 2, 6,  3, 6, 7, // Cara 1: Derecha
            7, 6, 5,  7, 5, 4, // Cara 2: Trasera
            4, 5, 1,  4, 1, 0, // Cara 3: Izquierda
            4, 0, 3,  4, 3, 7, // Cara 4: Superior
            1, 5, 6,  1, 6, 2  // Cara 5: Inferior
    };
```

### ¿Por qué hay 36 números si un cubo solo tiene 8 esquinas?
Porque **OpenGL NO sabe dibujar cuadrados**. OpenGL solo sabe dibujar **triángulos** (es la figura más simple posible y siempre es plana).
* Un cuadrado (una cara del cubo) se parte en diagonal para formar **2 triángulos**.
* Cada triángulo necesita **3 esquinas**.
* Entonces, **1 cara = 2 triángulos = 6 números**.
* Como un cubo tiene **6 caras**: $6 \text{ caras} \times 6 \text{ números} = 36 \text{ índices}$.

Por ejemplo, en la **Cara 0 (Frontal)**:
* Primer triángulo: une las esquinas `0 -> 1 -> 2` (la mitad inferior-izquierda del cuadrado frontal).
* Segundo triángulo: une las esquinas `0 -> 2 -> 3` (la mitad superior-derecha del cuadrado frontal).
* Juntos forman el cuadrado completo sin tener que repetir las coordenadas $(X, Y, Z)$ en `cubeCoords` (eso ahorra mucha memoria).

---

## 5. Los Colores Transparentes: `faceColors` (Líneas 55 a 62)

```java
    private static final float[][] faceColors = {
            { 0.95f, 0.25f, 0.25f, 0.45f }, // Frontal: Rojo transparente
            { 0.20f, 0.85f, 0.35f, 0.45f }, // Derecha: Verde transparente
            { 0.25f, 0.55f, 1.00f, 0.45f }, // Trasera: Azul transparente
            { 1.00f, 0.75f, 0.15f, 0.45f }, // Izquierda: Amarillo transparente
            { 0.80f, 0.30f, 0.95f, 0.45f }, // Superior: Morado transparente
            { 0.15f, 0.85f, 0.85f, 0.45f }  // Inferior: Cian transparente
    };
```
* Es una tabla (matriz) de 6 filas (una por cada cara) y 4 columnas.
* En OpenGL los colores no van de `0` a `255`, sino de **`0.0f` (nada) a `1.0f` (máximo)** en formato **RGBA**:
  1. **R (Red / Rojo)**
  2. **G (Green / Verde)**
  3. **B (Blue / Azul)**
  4. **A (Alpha / Opacidad):** `1.0f` es totalmente sólido (como una pared), `0.0f` es invisible, y **`0.45f`** es translúcido (como vidrio polarizado o gelatina, por eso puedes ver las caras de atrás a través de las de adelante).

---

## 6. Tamaño de Salto y Matrices (Líneas 65 a 69)

```java
    private static final int vertexStride = COORDS_PER_VERTEX * 4;
```
* **¿Qué es `vertexStride` (zancada)?** Cuando la GPU lee `vertexBuffer`, lee una lista gigante seguida de números. Necesita saber **cuántos bytes debe saltar** para pasar del inicio de un vértice al inicio del siguiente.
* Cada vértice tiene `3` floats (`COORDS_PER_VERTEX`), y en Java cada `float` ocupa `4` bytes en memoria:
  $$3 \times 4 = 12 \text{ bytes}$$

```java
    private final float[] modelMatrix = new float[16];
    private final float[] mvpMatrix = new float[16];
```
* En gráficos 3D, las transformaciones usan matrices de $4 \times 4$, que tienen $4 \times 4 = 16$ números. Por eso son arreglos de 16 `float`:
  * `modelMatrix`: Guarda la posición, rotación y escala **del propio cubo** en el mundo.
  * `mvpMatrix`: Significa **Model-View-Projection**. Es la matriz final que combina dónde está el cubo (`Model`), dónde está la cámara (`View`) y el lente/perspectiva 3D (`Projection`).

---

## 7. Los Shaders: El Código que Corre Dentro de la GPU (Líneas 72 a 85)

Estos dos `String` **NO son código Java**. Están escritos en un lenguaje llamado **GLSL** (*OpenGL Shading Language*), que se parece a C y se ejecuta directamente dentro de la tarjeta gráfica.

### A) El Vertex Shader (El arquitecto que coloca puntos)
```java
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";
```
* `uniform mat4 uMVPMatrix;`: Recibe la matriz final de cámara/perspectiva. Se llama `uniform` porque es **igual para todos** los vértices del cubo en ese fotograma.
* `attribute vec4 vPosition;`: Recibe la coordenada de **un vértice** a la vez desde `vertexBuffer`. Se llama `attribute` porque cambia en cada esquina.
* `void main() { gl_Position = uMVPMatrix * vPosition; }`: Multiplica la posición del punto 3D por la matriz de la cámara y guarda el resultado en `gl_Position`, que le dice a la pantalla en qué lugar exacto cae esa esquina. Este código corre **8 veces** (una por esquina).

### B) El Fragment Shader (El pintor que rellena píxeles)
```java
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
```
* `precision mediump float;`: Le dice a la GPU del celular: *"usa precisión media para los decimales, así no gastas tanta batería ni calientas el teléfono"*.
* `uniform vec4 vColor;`: Recibe el color RGBA de la cara actual desde Java.
* `void main() { gl_FragColor = vColor; }`: Pinta cada píxel (fragmento) que cae dentro de los triángulos con ese color. Este código corre **miles de veces** por segundo (una vez por cada píxel pintado).

---

## 8. El Constructor `public Cube()` (Líneas 87 a 112)

El constructor se ejecuta **una sola vez** cuando nace el cubo, para dejar todo preparado antes de empezar a dibujar fotogramas.

### Paso 1: Empaquetar los vértices en memoria nativa
```java
        ByteBuffer bb = ByteBuffer.allocateDirect(cubeCoords.length * 4);
```
* Pide un bloque de memoria directa (fuera de la basura de Java). Como `cubeCoords` tiene 24 floats y cada float pesa 4 bytes, reserva $24 \times 4 = 96$ bytes exactos.

```java
        bb.order(ByteOrder.nativeOrder());
```
* **¿Qué es esto?** Distintos procesadores leen los bytes de izquierda a derecha (*Big Endian*) o de derecha a izquierda (*Little Endian*, como los celulares ARM). Esta línea le dice: *"ordena los bytes exactamente como le gusta al procesador de este teléfono"*.

```java
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubeCoords);
        vertexBuffer.position(0);
```
* `bb.asFloatBuffer()`: Convierte esa caja de bytes crudos en una vista que entiende números decimales (`float`).
* `vertexBuffer.put(cubeCoords)`: Copia todos los números de `cubeCoords` adentro de la memoria nativa.
* `vertexBuffer.position(0)`: Al meter los datos, el cursor interno quedó al final de la lista. `position(0)` regresa el cursor al **principio** para que cuando OpenGL empiece a leer, lea desde el primer número y no desde el vacío.

### Paso 2: Empaquetar los índices en memoria nativa
```java
        ByteBuffer ib = ByteBuffer.allocateDirect(drawOrder.length * 2);
        ib.order(ByteOrder.nativeOrder());
        indexBuffer = ib.asShortBuffer();
        indexBuffer.put(drawOrder);
        indexBuffer.position(0);
```
* Hace exactamente lo mismo que el Paso 1, pero para `drawOrder`. Como son 36 números `short` y cada `short` ocupa **2 bytes**, reserva $36 \times 2 = 72$ bytes, los ordena, los mete y rebobina el cursor a `0`.

### Paso 3: Dejar la matriz del cubo quieta
```java
        Matrix.setIdentityM(modelMatrix, 0);
```
* Convierte `modelMatrix` en una **matriz identidad** (como multiplicar por `1`). Esto significa: *"no muevas el cubo, no lo gires y no lo deformes; déjalo quieto en el centro $(0,0,0)$"*.

### Paso 4: Compilar y unir los Shaders
```java
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
```
* Toma los textos `vertexShaderCode` y `fragmentShaderCode` y los compila para que la GPU los entienda (usando tu método auxiliar `loadShader` de `MyGLRenderer`).

```java
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
```
* `glCreateProgram()`: Crea un contenedor vacío en la GPU.
* `glAttachShader(...)`: Mete ambos shaders compilados dentro del contenedor.
* `glLinkProgram(mProgram)`: Los "suelda" juntos para que la salida del Vertex Shader conecte con la entrada del Fragment Shader. Listo, ya tenemos nuestro programa gráfico guardado en `mProgram`.

---

## 9. El Método `draw(float[] vpMatrix)` (Líneas 114 a 157)

Este método es el que se ejecuta **60 veces por segundo** (en cada fotograma de la pantalla) para pintar el cubo. Recibe `vpMatrix`, que es la matriz de **Vista + Proyección** (la cámara) que viene desde `MyGLRenderer`.

```java
        GLES20.glUseProgram(mProgram);
```
* Le dice a OpenGL: *"Oye, para lo que voy a dibujar ahorita mismo, usa los shaders de este cubo (`mProgram`)"*.

```java
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
```
* Busca dónde quedó la variable `vPosition` dentro del shader compilado y enciende ese canal para poder enviarle coordenadas.

```java
        GLES20.glVertexAttribPointer(
                positionHandle,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
        );
```
* Esta es la conexión principal de los vértices. Le dice a la GPU:
  1. Manda los datos al enchufe `positionHandle`.
  2. Agarra números de **3 en 3** (`COORDS_PER_VERTEX`).
  3. Son de tipo decimal (`GLES20.GL_FLOAT`).
  4. `false`: No los normalices (no les cambies la escala).
  5. Avanza **12 bytes** (`vertexStride`) para leer el siguiente vértice.
  6. Sácalos de la caja `vertexBuffer`.

### Activar la transparencia (Blending)
```java
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glDepthMask(false);
```
* `glEnable(GLES20.GL_BLEND)`: Activa la **mezcla de colores**. Sin esto, OpenGL ignora el cuarto número (`0.45f`) y pinta todo sólido.
* `glBlendFunc(...)`: Define la fórmula matemática de transparencia estándar: toma el color nuevo multiplicado por su transparencia (`SRC_ALPHA`, o sea $45\%$) y mézclalo con el color que ya estaba dibujado al fondo multiplicado por lo que falta ($1 - 0.45 = 55\%$).
* `glDepthMask(false)`: Desactiva temporalmente la escritura en el búfer de profundidad (*Z-Buffer*). **¿Por qué?** Porque si dibujas primero la cara frontal y marca que ahí ya hay algo delante, OpenGL descartaría las caras traseras por estar "tapadas", y no podrías verlas a través del cristal transparente.

### Calcular la posición final en pantalla (Matriz MVP)
```java
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);
        mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
```
* `Matrix.multiplyMM(...)`: Multiplica $\text{MVP} = \text{VP} \times \text{Model}$. Combina la cámara con la posición del cubo y guarda el resultado en `mvpMatrix`.
* `glGetUniformLocation(...)`: Busca la variable `uMVPMatrix` en el Vertex Shader.
* `glUniformMatrix4fv(...)`: Le envía los 16 floats de `mvpMatrix` a la GPU.

### El Bucle que Pinta las 6 Caras (Una de cada color)
```java
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
```
* Busca la variable `vColor` en el Fragment Shader antes de entrar al bucle.

```java
        for (int i = 0; i < faceColors.length; i++) {
            GLES20.glUniform4fv(colorHandle, 1, faceColors[i], 0);
            indexBuffer.position(i * 6);
            GLES20.glDrawElements(
                    GLES20.GL_TRIANGLES,
                    6,
                    GLES20.GL_UNSIGNED_SHORT,
                    indexBuffer
            );
        }
```
Este bucle da **6 vueltas** (`i` va de `0` a `5`, una por cada cara del cubo):
1. `glUniform4fv(colorHandle, 1, faceColors[i], 0)`: Cambia la pintura de la brocha al color `i` (en la vuelta `0` pone rojo, en la `1` pone verde, en la `2` azul, etc.).
2. `indexBuffer.position(i * 6)`: Mueve el cursor de `indexBuffer` al inicio de la cara correspondiente:
   * Cara `0`: empieza en el índice `0`
   * Cara `1`: empieza en el índice `6`
   * Cara `2`: empieza en el índice `12`
   * ...y así de 6 en 6.
3. `glDrawElements(...)`: **¡La orden de pintar!** Le dice a la GPU:
   * Dibuja triángulos (`GL_TRIANGLES`).
   * Lee **6 índices** a partir de donde dejé el cursor (eso dibuja los 2 triángulos de esa cara).
   * Los índices son enteros cortos sin signo (`GL_UNSIGNED_SHORT`).
   * Usa la lista `indexBuffer`.

### Limpieza Final del Fotograma
```java
        indexBuffer.position(0);
        GLES20.glDepthMask(true);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
```
Una vez pintadas las 6 caras, dejamos todo ordenado como lo encontramos para que si luego dibujas otra figura (una pirámide, una esfera o un cilindro), no herede configuraciones rotas:
* `indexBuffer.position(0)`: Rebobina el cursor de índices al inicio para el próximo fotograma.
* `glDepthMask(true)`: Vuelve a activar la escritura de profundidad normal.
* `glDisable(GLES20.GL_BLEND)`: Apaga la transparencia.
* `glDisableVertexAttribArray(positionHandle)`: Apaga el canal de posiciones de vértices.
