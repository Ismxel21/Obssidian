# Parte I: El Cubo — El Rey de la Geometría Tridimensional

Antes de sumergirnos en el código que controla la cámara y el escenario virtual, vale la pena detenernos a admirar al protagonista de tu escena: **el cubo** (también conocido formalmente como **hexaedro regular**).

Aunque hoy lo vemos en todos lados —desde un dado de juegos de mesa hasta los bloques de *Minecraft* o los píxeles volumétricos (*vóxeles*) de los gráficos por computadora—, para los matemáticos de la antigua Grecia el cubo era una figura casi sagrada.

---

## 1. Un Sólido Platónico con Historia
En el siglo IV a. C., el filósofo **Platón** estudió los únicos cinco poliedros convexos cuyas caras son polígonos regulares idénticos y en cuyos vértices concurre siempre el mismo número de caras. A estas figuras se les conoce hoy como los **Sólidos Platónicos**.

Platón asoció cada uno con un elemento de la naturaleza:
* El tetraedro con el fuego.
* El octaedro con el aire.
* El icosaedro con el agua.
* El dodecaedro con el cosmos.
* **El cubo con la Tierra**, porque entre los cinco es el más estable, firme y difícil de volcar.

---

## 2. Anatomía Matemática del Cubo
Si definimos un cubo de arista (lado) $a$ —en tu código, como va de $-0.5$ a $+0.5$, la arista mide exactamente $a = 1.0$ unidad—, sus propiedades fundamentales son:

* **Caras ($C$):** $6$ cuadrados congruentes.
* **Aristas ($A$):** $12$ segmentos de igual longitud $a$.
* **Vértices ($V$):** $8$ esquinas donde convergen $3$ caras y $3$ aristas perpendiculares entre sí ($90^\circ$).

### La Fórmula de Euler para Poliedros
Leonhard Euler demostró que todo poliedro convexo cumple una relación topológica invariante entre sus vértices, aristas y caras:

$$V - A + C = 2$$

Comprobémoslo con nuestro cubo:

$$8 - 12 + 6 = 2$$

No importa si estiras el cubo, si lo miras desde arriba o si lo proyectas en la pantalla de un celular: esa relación topológica jamás se rompe.

---

## 3. Métricas Fundamentales (Para una arista $a$)

1. **Área superficial total ($A_{\text{total}}$):** Como tiene $6$ caras cuadradas y cada una tiene área $a^2$:
   $$A_{\text{total}} = 6a^2$$
   *(Para tu cubo con $a = 1$, el área superficial es $6\text{ u}^2$).*

2. **Volumen ($V$):** El espacio tridimensional que encierra:
   $$V = a^3$$
   *(Para tu cubo con $a = 1$, el volumen es $1\text{ u}^3$).*

3. **Diagonal de una cara ($d_c$):** Aplicando el Teorema de Pitágoras en dos dimensiones sobre cualquiera de sus cuadrados:
   $$d_c = \sqrt{a^2 + a^2} = a\sqrt{2}$$

4. **Diagonal espacial o principal ($D$):** La línea recta que atraviesa el interior del cubo desde una esquina hasta la esquina completamente opuesta (por ejemplo, de $(-0.5, -0.5, -0.5)$ a $(+0.5, +0.5, +0.5)$). Aplicando Pitágoras en tres dimensiones:
   $$D = \sqrt{a^2 + a^2 + a^2} = a\sqrt{3} \approx 1.732a$$

---

## 4. Simetría y Por Qué la Computación Gráfica lo Ama
El cubo posee un altísimo grado de simetría (conocido en álgebra como el **grupo octaédrico**, con $48$ simetrías distintas entre rotaciones y reflexiones). Además, es el **único sólido platónico capaz de teselar (rellenar) el espacio tridimensional por sí solo** sin dejar huecos ni superponerse.

Por esa razón exacta, los motores gráficos y OpenGL usan el cubo como la unidad básica para medir el espacio: antes de que la tarjeta gráfica dibuje tu escena en la pantalla, **aplasta todo el mundo visible dentro de un cubo imaginario** que va de $-1$ a $+1$ en los ejes $X$, $Y$ y $Z$ (llamado *Normalized Device Coordinates* o NDC).

---
---

# Parte II: Explicación Paso a Paso de `MyGLRenderer.java`

Si `Cube.java` era el actor que sabe cómo vestirse y qué forma tiene, **`MyGLRenderer.java` es el director de cine y el camarógrafo**.

Su trabajo es:
1. Preparar el escenario (limpiar la pantalla y encender las luces/profundidad).
2. Colocar la cámara en el lugar exacto y elegir el lente (perspectiva).
3. Llamar a los actores (`cubo.draw(...)`) 60 veces por segundo para grabar cada fotograma.

---

## 1. Paquete e Importaciones (Líneas 1 a 8)

```java
package com.example.cuerpos3d_amaguaya_ojeda;
```
* Indica la carpeta del proyecto donde vive este archivo, igual que en `Cube.java`.

```java
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
```
* `GLES20`: La caja de herramientas con las órdenes para la tarjeta gráfica (GPU).
* `GLSurfaceView`: Es el lienzo o pantalla especial de Android donde se pueden dibujar gráficos 3D acelerados por hardware. Aquí lo importamos para usar su interfaz `GLSurfaceView.Renderer`.
* `Matrix`: La calculadora de matrices de $4 \times 4$ que nos permite mover la cámara y crear efecto de profundidad 3D.

```java
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
```
* **¿Por qué dice `GL10` si usamos OpenGL ES 2.0?** Es pura herencia histórica de Android. Los métodos obligatorios de `Renderer` (`onSurfaceCreated`, `onSurfaceChanged`, `onDrawFrame`) nacieron cuando Android solo tenía OpenGL 1.0, así que conservaron el parámetro `GL10 gl` para no romper código antiguo. Nosotros lo ignoramos por completo y usamos siempre `GLES20`.

---

## 2. Declaración de la Clase y Variables (Líneas 10 a 17)

```java
public class MyGLRenderer implements GLSurfaceView.Renderer {
```
* `implements GLSurfaceView.Renderer`: Es un contrato obligatorio. Al firmar este contrato, Java te exige escribir **3 métodos específicos** que Android llamará automáticamente en momentos clave del ciclo de vida de la pantalla:
  1. `onSurfaceCreated` (al nacer la pantalla).
  2. `onSurfaceChanged` (al girar el celular o cambiar el tamaño de la ventana).
  3. `onDrawFrame` (en bucle infinito, unas 60 veces por segundo, para dibujar).

```java
    private Tetraedro tetraedro;
    private Cube cubo;
```
* Aquí declaras tus dos figuras 3D (actores). Todavía están vacías (`null`) porque aún no se ha creado el entorno gráfico de OpenGL.

```java
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
```
* Tres matrices de $4 \times 4$ ($16$ números `float` cada una):
  * `viewMatrix` (**Vista / Cámara**): Guarda dónde está parada tu cámara y hacia dónde está mirando.
  * `projectionMatrix` (**Proyección / Lente**): Guarda cómo se deforman los objetos según la distancia (hace que lo lejano se vea pequeño y lo cercano grande).
  * `vPMatrix` (**View-Projection**): Es la multiplicación de las dos anteriores ($\text{Projection} \times \text{View}$). Es el paquete final que le entregas a `cubo.draw(vPMatrix)`.

---

## 3. Método 1: `onSurfaceCreated` — Preparar el Escenario (Líneas 19 a 27)

Este método corre **una sola vez** cuando se abre la aplicación o se crea el lienzo 3D.

```java
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
```
* Android te avisa: *"Ya encendí la tarjeta gráfica, ¿qué quieres configurar antes de empezar?"*.

```java
        // Fondo negro
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
```
* Elige el color de la "pintura borrador" con la que se limpiará la pantalla antes de cada fotograma. Usa formato **RGBA** (`Rojo = 0`, `Verde = 0`, `Azul = 0`, `Opacidad = 1.0`), lo que da **negro puro totalmente opaco**.

```java
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
```
* **¡Muy importante!** Enciende el **Test de Profundidad (Z-Buffer)**.
* Sin esta línea, OpenGL dibujaría las cosas simplemente en el orden en que las llamas en el código (como pintar en una hoja de papel), haciendo que un objeto que está atrás se dibuje encima de uno que está adelante. Con `GL_DEPTH_TEST`, OpenGL mide la distancia $Z$ de cada píxel a la cámara y tapa correctamente lo que queda detrás.

```java
        tetraedro = new Tetraedro();
        cubo = new Cube();
    }
```
* Ahora que OpenGL ya está despierto, fabricamos nuestros objetos (`new Tetraedro()` y `new Cube()`), lo cual compila sus shaders y reserva sus búferes en memoria nativa. *(Si hicieras `new Cube()` antes de `onSurfaceCreated`, la app fallaría porque aún no existe el contexto de OpenGL).*

---

## 4. Método 2: `onSurfaceChanged` — Ajustar la Pantalla y el Lente (Líneas 29 a 35)

Este método corre **cuando aparece la pantalla por primera vez** y cada vez que **rotas el teléfono** (de vertical a horizontal o viceversa).

```java
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
```
* `glViewport(0, 0, width, height)`: Le dice a OpenGL que use toda la pantalla disponible desde la esquina $(0,0)$ hasta el ancho (`width`) y alto (`height`) en píxeles del celular.

```java
        float ratio = (float) width / Math.max(height, 1);
```
* Calcula la **relación de aspecto (aspect ratio)** de la pantalla (ancho dividido entre alto).
* **¿Por qué?** Porque las pantallas de celular son rectangulares (por ejemplo, $1080 \times 2400$, donde $\text{ratio} = 0.45$), no cuadradas. Si no corriges con `ratio`, tu cubo se vería aplastado como un ladrillo alargado.
* `Math.max(height, 1)` es un seguro de vida matemático: evita que si alguna vez `height` llega en `0` al minimizar la app, ocurra una división por cero (`ArithmeticException`) y se cierre el programa.

```java
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 2.0f, 10.0f);
    }
```
* **¿Qué es `frustumM`?** Un *frustum* es una **pirámide truncada** (una pirámide a la que le cortaron la punta). Representa el cono de visión de un ojo humano o una cámara real con perspectiva 3D:
  * `projectionMatrix, 0`: Dónde guardar el resultado.
  * `-ratio, ratio`: Bordes izquierdo y derecho de la visión (ajustados a la forma de tu pantalla para que el cubo no se deforme).
  * `-1.0f, 1.0f`: Bordes inferior y superior de la visión.
  * `2.0f` (**near / plano cercano**): Todo lo que esté más cerca de $2.0$ unidades de la cámara se corta y desaparece (como cuando te pegas un objeto justo en el ojo y ya no lo enfocas).
  * `10.0f` (**far / plano lejano**): Todo lo que esté más lejos de $10.0$ unidades de la cámara desaparece en el horizonte.

---

## 5. Método 3: `onDrawFrame` — El Bucle de Renderizado (Líneas 37 a 126)

Este método se ejecuta **continuamente** (decenas de veces por segundo) para dibujar cada fotograma de la animación.

```java
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
```
* Borra la pizarra antes de pintar el nuevo fotograma:
  * `GL_COLOR_BUFFER_BIT`: Limpia todos los colores pintados en el fotograma anterior (deja todo negro).
  * `GL_DEPTH_BUFFER_BIT`: Limpia las distancias guardadas en el búfer de profundidad para volver a calcular qué está delante y qué está detrás.

### Cómo Funciona `Matrix.setLookAtM(...)` (La Cámara Virtual)

En el bloque de perspectivas dejaste activa la **Opción 1** y comentadas (`/* ... */`) las opciones 2 a 8:

```java
        Matrix.setLookAtM(
                viewMatrix, 0,
                1.1f, 0.9f, 3.2f,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f
        );
```
`setLookAtM` coloca un trípode virtual en el espacio 3D usando **3 vectores de 3 números cada uno**:

1. **Posición del Ojo / Cámara (`eyeX, eyeY, eyeZ`):** `1.1f, 0.9f, 3.2f`
   * Te mueves $1.1$ unidades a la derecha ($+X$).
   * Subes $0.9$ unidades hacia arriba ($+Y$).
   * Retrocedes $3.2$ unidades hacia ti ($+Z$).
   * Desde ese punto elevado y a la derecha puedes ver **tres caras del cubo al mismo tiempo** (frontal, derecha y superior), logrando una vista isométrica 3D.

2. **Punto al que Miras (`centerX, centerY, centerZ`):** `0.0f, 0.0f, 0.0f`
   * La cámara apunta directo al origen $(0, 0, 0)$, que es justo el centro donde está tu cubo.

3. **Vector "Arriba" (`upX, upY, upZ`):** `0.0f, 1.0f, 0.0f`
   * Le dice a la cámara dónde queda el cielo (hacia $+Y$). Si pusieras `0.0f, -1.0f, 0.0f`, la cámara quedaría boca abajo como un murciélago.

*(Las opciones 2 a 8 están entre `/* ... */` para que Java las ignore; si descomentas alguna y comentas la 1, simplemente cambias las coordenadas `(eyeX, eyeY, eyeZ)` desde donde miras el cubo).*

### Combinar Cámara + Lente y Dibujar el Cubo

```java
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
```
* Multiplica matrices:
  $$\text{vPMatrix} = \text{projectionMatrix} \times \text{viewMatrix}$$
* **Ojo con el orden:** En álgebra lineal, el orden de multiplicación de matrices importa ($A \times B \neq B \times A$). Primero se aplica la vista (`viewMatrix`, a la derecha) y luego la perspectiva (`projectionMatrix`, a la izquierda), guardando el resultado en `vPMatrix`.

```java
        //tetraedro.draw(vPMatrix);
         cubo.draw(vPMatrix);
    }
```
* `//tetraedro.draw(vPMatrix);`: Está comentado con `//`, así que el tetraedro no se dibuja.
* `cubo.draw(vPMatrix);`: Llama al método `draw` de tu clase `Cube.java` y le pasa la matriz `vPMatrix` para que el cubo sepa desde qué ángulo y distancia debe dibujarse en este fotograma.

---

## 6. El Método Auxiliar `loadShader` (Líneas 128 a 133)

Este método es `public static`, lo que significa que cualquier figura (`Cube`, `Tetraedro`, etc.) puede llamarlo directamente sin necesidad de crear un `new MyGLRenderer()`. Su función es traducir el texto GLSL de tus shaders a código máquina de la tarjeta gráfica:

```java
    public static int loadShader(int type, String shaderCode) {
```
* Recibe dos cosas:
  * `type`: Qué tipo de shader es (`GLES20.GL_VERTEX_SHADER` o `GLES20.GL_FRAGMENT_SHADER`).
  * `shaderCode`: El `String` con el código GLSL escrito en `Cube.java`.

```java
        int shader = GLES20.glCreateShader(type);
```
* Le pide a OpenGL que cree un espacio vacío en la GPU para ese tipo de shader y devuelve su número identificador (`shader`).

```java
        GLES20.glShaderSource(shader, shaderCode);
```
* Mete el texto de tu código GLSL (`shaderCode`) dentro de ese contenedor en la GPU.

```java
        GLES20.glCompileShader(shader);
```
* **Compila** el shader (igual que cuando compilas Java, pero dentro de la tarjeta gráfica) para convertirlo en instrucciones ejecutables por los núcleos gráficos.

```java
        return shader;
    }
}
```
* Devuelve el número identificador (`int`) del shader ya compilado para que `Cube` pueda enchufarlo en `mProgram` mediante `GLES20.glAttachShader(...)`.
