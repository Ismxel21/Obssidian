# Explicación Detallada de la Clase `Point` (OpenGL ES 2.0 en Java/Android)

Este documento contiene la versión final corregida del código con comentarios explicativos claros y detallados para defender la implementación ante preguntas teóricas.

---

## Código Java Completo (`Point.java`)

```java
package com.prograavanzada.primerproyecto;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Clase que representa un punto geométrico en el espacio 3D/2D para OpenGL ES.
 * Se encarga de:
 * 1. Reservar memoria nativa (RAM fuera de la JVM) para enviarla a la GPU.
 * 2. Cargar y compilar los shaders (Vertex Shader y Fragment Shader).
 * 3. Enlazar los shaders en un programa ejecutable de OpenGL (`mProgram`).
 * 4. Enviar las coordenadas y el color a la GPU para renderizar la figura.
 */
public class Point {

    // Búfer en memoria nativa directa que almacena las coordenadas de los vértices.
    // La GPU no puede leer datos gestionados por el Recolector de Basura (Garbage Collector) de Java.
    private final FloatBuffer vertexBuffer;

    // Identificador único generado por OpenGL para el programa de shaders enlazado.
    private final int mProgram;

    // Puentes (handles) para conectar variables de Java con las variables del código GLSL:
    // - positionHandle: Apunta a "vPosition" en el Vertex Shader (tipo attribute).
    // - colorHandle: Apunta a "vColor" en el Fragment Shader (tipo uniform).
    private int positionHandle, colorHandle;

    // Número de componentes numéricas por vértice en el espacio cartesiano (X, Y, Z).
    static final int coords_per_vertex = 3;

    // Coordenadas cartesianas normalizadas de OpenGL (rango de -1.0 a 1.0).
    // En este caso, representa un único punto en X = 0.25, Y = -0.25, Z = 0.0.
    float[] pointCoords = { 0.25f, -0.25f, 0.0f };

    // Total de vértices a procesar. Se calcula dividiendo el número total de datos float entre las coordenadas por vértice.
    // Para pointCoords.length = 3 -> 3 / 3 = 1 vértice.
    private final int vertexCount = pointCoords.length / coords_per_vertex;

    // Tamaño en bytes entre vértices consecutivos (Stride / Zancada).
    // Cada dato 'float' en Java ocupa 4 bytes.
    // Cálculo: 3 coordenadas * 4 bytes/float = 12 bytes por vértice.
    private final int vertexStride = coords_per_vertex * 4;

    // Color asignado al punto en formato RGBA (Red, Green, Blue, Alpha).
    // Valores entre 0.0f y 1.0f. (1.0f, 0.75f, 0.25f, 1.0f -> Naranja/Amarillo brillante).
    float[] color = { 1.0f, 0.75f, 0.25f, 1.0f };

    // ----------------------------------------------------------------------------------
    // CÓDIGO GLSL (Lenguaje de programación para la Tarjeta Gráfica GPU)
    // ----------------------------------------------------------------------------------

    // 1. VERTEX SHADER: Define la posición espacial (dónde se ubica el punto).
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" + // Variable de entrada que recibe las coordenadas (X,Y,Z,W) desde Java.
            "void main() {" +
            "  gl_Position = vPosition;" + // Variable reservada de OpenGL donde se guarda la posición final en pantalla.
            "  gl_PointSize = 10.0;" +     // Tamaño en píxeles del punto dibujado.
            "}";

    // 2. FRAGMENT SHADER: Define la apariencia visual y el color del pixel/punto.
    private final String fragmentShaderCode =
            "precision mediump float;" + // Establece la precisión matemática de los números decimales en la GPU.
            "uniform vec4 vColor;" +      // Variable global que recibe el color RGBA desde Java.
            "void main() {" +
            "  gl_FragColor = vColor;" +   // Variable reservada de OpenGL donde se asigna el color final.
            "}";

    /**
     * CONSTRUCTOR DE LA CLASE POINT:
     * - Inicializa la memoria nativa para la tarjeta gráfica.
     * - Carga, compila y enlaza los shaders en la GPU.
     */
    public Point() {
        // 1. Reserva memoria nativa sin gestión del Garbage Collector.
        // Tamaño en bytes: (número de floats en pointCoords) * 4 bytes/float.
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(pointCoords.length * 4);

        // 2. Configura el orden de lectura de los bytes según el hardware del dispositivo (Little-Endian o Big-Endian).
        byteBuffer.order(ByteOrder.nativeOrder());

        // 3. Modifica la interpretación del búfer de bytes crudos a un búfer de floats (FloatBuffer).
        vertexBuffer = byteBuffer.asFloatBuffer();

        // 4. Copia los datos del arreglo de Java (`pointCoords`) a la memoria nativa accesible por la GPU.
        vertexBuffer.put(pointCoords);

        // 5. Restablece el puntero de lectura del búfer a la posición inicial (índice 0).
        // Si no se reposiciona, la GPU intentará leer después del último elemento cargado.
        vertexBuffer.position(0);

        // 6. Carga y compila individualmente los dos shaders requeridos.
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // 7. Crea un "Programa de OpenGL" (un contenedor vacío en la GPU).
        mProgram = GLES20.glCreateProgram();

        // 8. Adjunta los dos shaders compilados al programa recién creado.
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        // 9. Enlaza los shaders dentro del programa para comprobar compatibilidad entre las entradas y salidas de ambos.
        GLES20.glLinkProgram(mProgram);
    }

    /**
     * Carga y compila el código fuente escrito en GLSL dentro de la GPU.
     * @param type Tipo de shader (GLES20.GL_VERTEX_SHADER o GLES20.GL_FRAGMENT_SHADER).
     * @param shaderCode Cadena de texto con el código escrito en GLSL.
     * @return Retorna el identificador (ID) del shader en la GPU.
     */
    private int loadShader(int type, String shaderCode) {
        // Asigna un espacio o casillero en la GPU para el shader según su tipo.
        int shader = GLES20.glCreateShader(type);

        // Asocia el código fuente de texto al casillero creado en la GPU.
        GLES20.glShaderSource(shader, shaderCode);

        // Compila el código traduciéndolo al lenguaje máquina ejecutable por la tarjeta gráfica.
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * MÉTODO DE DIBUJO (`drawn`):
     * Se ejecuta continuamente en el ciclo de renderizado para proyectar el punto en pantalla.
     */
    public void drawn() {
        // 1. Indica a OpenGL que use el programa ejecutable previamente enlazado (`mProgram`).
        GLES20.glUseProgram(mProgram);

        // 2. Obtiene la ubicación en la GPU de la variable `vPosition` definida en el Vertex Shader.
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // 3. Habilita el canal de transmisión de datos para el atributo de posición.
        GLES20.glEnableVertexAttribArray(positionHandle);

        // 4. Configura el puntero que indica a la GPU cómo interpretar la estructura del `vertexBuffer`:
        //    - positionHandle: Dónde entregar los datos en el shader.
        //    - coords_per_vertex: Cuántos floats representan 1 vértice (3: X, Y, Z).
        //    - GLES20.GL_FLOAT: Tipo de dato numérico.
        //    - false: Sin normalizar los valores.
        //    - vertexStride: Tamaño total en bytes de cada elemento (12 bytes).
        //    - vertexBuffer: Búfer de memoria nativa donde residen las coordenadas.
        GLES20.glVertexAttribPointer(
                positionHandle,
                coords_per_vertex,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
        );

        // 5. Obtiene la ubicación en la GPU de la variable uniforme `vColor` en el Fragment Shader.
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // 6. Transfiere los 4 datos flotantes del arreglo de Java `color` hacia la variable uniforme de la GPU.
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // 7. ORDEN DE RENDERIZADO:
        //    - GLES20.GL_POINTS: Tipo de primitiva gráfica a dibujar (puntos).
        //    - 0: Posición inicial del arreglo a leer.
        //    - vertexCount: Cantidad total de vértices a dibujar (1 vértice).
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount);

        // 8. Desactiva el canal del atributo de posición para mantener un estado limpio en la GPU.
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
```

---

## Puntos Clave para Preguntas del Profesor

### 1. ¿Por qué usamos `ByteBuffer.allocateDirect` en lugar de un arreglo normal de Java?
Java administra la memoria mediante el **Garbage Collector** (Recolector de Basura), el cual puede mover los objetos de lugar en la memoria RAM en cualquier momento. La GPU (tarjeta gráfica) necesita acceder a la RAM directamente en una dirección fija. `ByteBuffer.allocateDirect` reserva **memoria nativa** fuera de la máquina virtual de Java a la que la GPU puede acceder rápidamente sin interferencias.

### 2. Diferencia entre un atributo (`attribute`) y un uniforme (`uniform`):
* **`attribute`** (`vPosition`): Cambia por cada vértice que procesa la GPU. Se usa en el **Vertex Shader** para enviar coordenadas.
* **`uniform`** (`vColor`): Es una variable constante para todos los vértices de una misma llamada de dibujo. Se usa habitualmente en el **Fragment Shader** para asignar colores o matrices de transformación.

### 3. Fases del pipeline de OpenGL en la clase:
1. **Creación del búfer:** Preparación de datos de Java a memoria nativa.
2. **Compilación de Shaders:** `glCreateShader`, `glShaderSource`, `glCompileShader`.
3. **Enlace (Linking):** `glCreateProgram`, `glAttachShader`, `glLinkProgram`.
4. **Carga de variables y renderizado:** `glUseProgram`, `glGetAttribLocation`, `glVertexAttribPointer`, `glDrawArrays`.
