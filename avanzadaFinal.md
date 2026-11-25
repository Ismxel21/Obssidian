# Guía ultra sencilla de `Cube.java` (Cubo 3D sólido)

Imagina que quieres dibujar un cubo de colores en la pantalla de tu celular usando OpenGL. Como OpenGL no entiende qué es un "cubo", tenemos que darle una lista de puntos, decirle cómo unirlos para formar triángulos y luego pintar cada lado.

---

## El Código Completo (Versión Sólida / Sin Transparencia)

```java
package com.example.cuerpos3d_amaguaya_ojeda;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cube {

    // 1. CAJAS DE MEMORIA PARA LA TARJETA GRÁFICA (GPU)
    // Java guarda los datos en una memoria, pero la tarjeta gráfica (GPU) usa otra memoria más rápida.
    // Estos "Buffers" son como cajas especiales para pasarle los datos del cubo a la GPU.
    private final FloatBuffer vertexBuffer; // Caja para guardar las 8 esquinas del cubo.
    private final ShortBuffer indexBuffer;  // Caja para guardar el orden en que se unen las esquinas.

    // 2. EL PROGRAMA DE PINTURA (SHADERS)
    // Es el ID del mini-programa que corre dentro de la tarjeta gráfica para dibujar el cubo.
    private final int mProgram;

    // 3. CONTROLES REMOTO (HANDLES)
    // Son como "enchufes" o etiquetas que nos permiten meter datos desde Java hacia el programa de la GPU.
    private int positionHandle;  // Enchufe para meter las coordenadas (esquinas).
    private int colorHandle;     // Enchufe para meter los colores.
    private int mvpMatrixHandle; // Enchufe para meter la posición de la cámara y perspectiva.

    // Cada esquina del cubo necesita 3 números: X (ancho), Y (alto), Z (profundidad).
    static final int COORDS_PER_VERTEX = 3;

    // 4. LAS 8 ESQUINAS DEL CUBO (VÉRTICES)
    // Un cubo tiene 8 puntas. El centro del cubo es (0, 0, 0).
    // Como mide 1 de ancho, va desde -0.5 hasta +0.5 en todas las direcciones.
    private static final float[] cubeCoords = {
            // 4 esquinas de la cara de adelante (Z = +0.5f, hacia ti)
            -0.5f,  0.5f,  0.5f, // Esquina 0: arriba - izquierda
            -0.5f, -0.5f,  0.5f, // Esquina 1: abajo  - izquierda
             0.5f, -0.5f,  0.5f, // Esquina 2: abajo  - derecha
             0.5f,  0.5f,  0.5f, // Esquina 3: arriba - derecha

            // 4 esquinas de la cara de atrás (Z = -0.5f, hacia el fondo)
            -0.5f,  0.5f, -0.5f, // Esquina 4: arriba - izquierda
            -0.5f, -0.5f, -0.5f, // Esquina 5: abajo  - izquierda
             0.5f, -0.5f, -0.5f, // Esquina 6: abajo  - derecha
             0.5f,  0.5f, -0.5f  // Esquina 7: arriba - derecha
    };

    // 5. CÓMO UNIR LOS PUNTOS (ÍNDICES)
    // OpenGL SOLO sabe dibujar triángulos. Un cuadrado se hace pegando 2 triángulos.
    // Un cubo tiene 6 caras x 2 triángulos = 12 triángulos = 36 puntos en total.
    // Aquí le decimos qué números de esquina unir para formar cada triángulo:
    private static final short[] drawOrder = {
            0, 1, 2,  0, 2, 3, // Cara 0 (Frontal): triángulo (0-1-2) y triángulo (0-2-3)
            3, 2, 6,  3, 7, 6, // Cara 1 (Derecha)
            4, 5, 6,  4, 6, 7, // Cara 2 (Trasera)
            0, 1, 5,  5, 4, 0, // Cara 3 (Izquierda)
            0, 3, 7,  7, 4, 0, // Cara 4 (Superior / Techo)
            1, 2, 6,  6, 5, 1  // Cara 5 (Inferior / Piso)
    };

    // 6. LOS COLORES DE CADA CARA (R, G, B, A)
    // Cada fila es un color: { Rojo, Verde, Azul, Opacidad }.
    // Los valores van de 0.0 (nada) a 1.0 (al máximo).
    // El 4to número (1.0f) significa 100% sólido (no transparente).
    private static final float[][] faceColors = {
            { 0.95f, 0.25f, 0.25f, 1.0f }, // Cara 0 (Frontal): Rojo
            { 0.20f, 0.85f, 0.35f, 1.0f }, // Cara 1 (Derecha): Verde
            { 0.25f, 0.55f, 1.00f, 1.0f }, // Cara 2 (Trasera): Azul
            { 1.00f, 0.75f, 0.15f, 1.0f }, // Cara 3 (Izquierda): Amarillo
            { 0.80f, 0.30f, 0.95f, 1.0f }, // Cara 4 (Superior): Morado
            { 0.15f, 0.85f, 0.85f, 1.0f }  // Cara 5 (Inferior): Cian (celeste)
    };

    // Colores originales translúcidos (Alpha = 0.45f) comentados por si quieres volver a activarlos:
    /*
    private static final float[][] faceColors = {
            { 0.95f, 0.25f, 0.25f, 0.45f }, // Frontal: Rojo transparente
            { 0.20f, 0.85f, 0.35f, 0.45f }, // Derecha: Verde transparente
            { 0.25f, 0.55f, 1.00f, 0.45f }, // Trasera: Azul transparente
            { 1.00f, 0.75f, 0.15f, 0.45f }, // Izquierda: Amarillo transparente
            { 0.80f, 0.30f, 0.95f, 0.45f }, // Superior: Morado transparente
            { 0.15f, 0.85f, 0.85f, 0.45f }  // Inferior: Cian transparente
    };
    */

    // Cuántos bytes ocupa cada esquina en memoria (3 números float x 4 bytes cada uno = 12 bytes).
    // Sirve para que OpenGL sepa cada cuánto empieza la siguiente esquina.
    private static final int vertexStride = COORDS_PER_VERTEX * 4;

    // 7. MATRICES (CALCULADORAS DE POSICIÓN 3D)
    // Son tablas de 16 números que guardan dónde está el cubo y cómo lo ve la cámara.
    private final float[] modelMatrix = new float[16]; // Posición/rotación propia del cubo.
    private final float[] mvpMatrix = new float[16];   // Posición final juntando Cubo + Cámara + Lente.

    // 8. LOS SHADERS (MINI-PROGRAMAS EN LENGUAJE DE TARJETA GRÁFICA)
    // Vertex Shader: Calcula en qué lugar exacto de la pantalla cae cada esquina del cubo.
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +       // Recibe la matriz final (Cámara + Posición).
            "attribute vec4 vPosition;" +      // Recibe una esquina del cubo.
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" + // Multiplica posición por cámara y lo manda a pantalla.
            "}";

    // Fragment Shader: Pinta de un color sólido cada píxel que está dentro de los triángulos.
    private final String fragmentShaderCode =
            "precision mediump float;" +       // Usa decimales normales.
            "uniform vec4 vColor;" +           // Recibe el color de la cara actual.
            "void main() {" +
            "  gl_FragColor = vColor;" +       // Pinta el píxel con ese color.
            "}";

    // 9. CONSTRUCTOR: SE EJECUTA UNA SOLA VEZ AL CREAR EL CUBO
    public Cube() {
        // Paso A: Metemos las 8 esquinas en la caja rápida (vertexBuffer).
        // Multiplicamos por 4 porque cada número float pesa 4 bytes.
        ByteBuffer bb = ByteBuffer.allocateDirect(cubeCoords.length * 4);
        bb.order(ByteOrder.nativeOrder()); // Usa el orden de bytes nativo del teléfono.
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubeCoords);      // Metemos los datos.
        vertexBuffer.position(0);          // Regresamos el dedo al inicio de la lista.

        // Paso B: Metemos el orden de unión en la otra caja rápida (indexBuffer).
        // Multiplicamos por 2 porque cada número short pesa 2 bytes.
        ByteBuffer ib = ByteBuffer.allocateDirect(drawOrder.length * 2);
        ib.order(ByteOrder.nativeOrder());
        indexBuffer = ib.asShortBuffer();
        indexBuffer.put(drawOrder);
        indexBuffer.position(0);

        // Paso C: Dejamos el cubo quieto en el centro (matriz identidad = no mover ni girar).
        Matrix.setIdentityM(modelMatrix, 0);

        // Paso D: Compilamos los dos mini-programas (shaders) y los pegamos en mProgram.
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // Crea el programa vacío.
        GLES20.glAttachShader(mProgram, vertexShader);   // Mete el shader de vértices.
        GLES20.glAttachShader(mProgram, fragmentShader); // Mete el shader de color.
        GLES20.glLinkProgram(mProgram);                  // Los une y los deja listos para usar.
    }

    // 10. MÉTODO DRAW: SE EJECUTA 60 VECES POR SEGUNDO PARA DIBUJAR EL CUBO EN PANTALLA
    public void draw(float[] vpMatrix) {
        // Le decimos a OpenGL: "Usa el programa de pintura de este cubo".
        GLES20.glUseProgram(mProgram);

        // Conectamos la caja de esquinas (vertexBuffer) con el enchufe "vPosition" del shader.
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(
                positionHandle,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
        );

        // Transparencia desactivada (comentada) para que el cubo se vea sólido:
        // GLES20.glEnable(GLES20.GL_BLEND);
        // GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        // GLES20.glDepthMask(false);

        // Multiplicamos la cámara (vpMatrix) por la posición del cubo (modelMatrix) -> mvpMatrix.
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);

        // Metemos esa matriz calculada en el enchufe "uMVPMatrix" del shader.
        mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        // Buscamos el enchufe "vColor" para ir cambiando de color en cada cara.
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Bucle que dibuja las 6 caras una por una:
        for (int i = 0; i < faceColors.length; i++) {
            // 1. Le pasamos al shader el color de la cara 'i'.
            GLES20.glUniform4fv(colorHandle, 1, faceColors[i], 0);

            // 2. Saltamos de 6 en 6 en la lista de índices (porque cada cara tiene 2 triángulos = 6 puntos).
            indexBuffer.position(i * 6);

            // 3. ¡Dibuja los 2 triángulos de esta cara!
            GLES20.glDrawElements(
                    GLES20.GL_TRIANGLES,
                    6,
                    GLES20.GL_UNSIGNED_SHORT,
                    indexBuffer
            );
        }

        // Rebobinamos la lista de índices al principio para el siguiente fotograma.
        indexBuffer.position(0);

        // Restauración de transparencia comentada porque no se usó:
        // GLES20.glDepthMask(true);
        // GLES20.glDisable(GLES20.GL_BLEND);

        // Desconectamos el enchufe de las esquinas para dejar todo limpio.
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
```

---

## Resumen en 4 pasos (Por si te preguntan cómo funciona)

1. **Los datos (`cubeCoords`, `drawOrder`, `faceColors`):** Son tres listas fijas. Una dice dónde están las 8 esquinas, otra dice cómo unirlas para hacer 12 triángulos (2 por cada una de las 6 caras) y la última dice de qué color sólido pintar cada cara.
2. **El constructor (`Cube()`):** Copia esas listas a una memoria ultra rápida (`FloatBuffer` y `ShortBuffer`) para que la tarjeta gráfica las lea sin lag, y compila dos mini-programas (*Shaders*).
3. **Los Shaders:** El *Vertex Shader* calcula en qué parte de la pantalla va cada esquina según la cámara; el *Fragment Shader* rellena de color sólido cada triángulo.
4. **El bucle de dibujo (`draw()`):** Se repite en cada fotograma. Recorre las 6 caras una por una, le cambia el color al shader y manda dibujar los 6 índices (2 triángulos) correspondientes a esa cara.
