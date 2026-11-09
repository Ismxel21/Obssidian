# Explicación de la Clase Line en OpenGL ES (Android)

Este documento detalla el código completo y corregido de la clase `Line`, la cual se encarga de dibujar una línea en pantalla utilizando **OpenGL ES 2.0** en una aplicación de Android. 

---

## Código Completo de la Clase `Line`

```java
package com.prograavanzada.primerproyecto;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Line {

    /**
     * Clase que representa una línea geométrica en el espacio 2D para OpenGL ES.
     * Se encarga de:
     * 1. Reservar memoria nativa (fuera del control del Garbage Collector de Java) para la GPU.
     * 2. Cargar y compilar los shaders (Vertex Shader y Fragment Shader).
     * 3. Enlazar los shaders en un programa ejecutable de OpenGL (mProgram).
     * 4. Enviar las coordenadas y el color a la GPU para renderizar la línea en pantalla.
     */

    // Búfer en memoria nativa directa que almacena las coordenadas de los vértices.
    // La GPU no puede leer datos gestionados directamente por el Recolector de Basura de Java.
    private final FloatBuffer vertexBuffer;

    // Identificador único generado por OpenGL para el programa donde se enlazan los shaders.
    private final int mProgram;

    // Referencias ("ganchos") para conectar Java con las variables de los Shaders:
    // - positionHandle: Apunta a "vPosition" en el Vertex Shader (coordenadas).
    // - colorHandle: Apunta a "vColor" en el Fragment Shader (color).
    private int positionHandle, colorHandle;

    // Coordenadas cartesianas normalizadas de OpenGL (rango de -1.0 a 1.0).
    // Definimos una línea con dos puntos (Punto A y Punto B):
    private final float[] lineCoords = {
           -0.5f,  0.5f, // Vértice 1 (Inicio)
            0.5f,  0.0f  // Vértice 2 (Fin)
    };

    // Color asignado a la línea en formato RGBA (Red, Green, Blue, Alpha).
    // Valores entre 0.0f y 1.0f. (Naranja/Amarillo brillante).
    float[] color = { 1.0f, 1.0f, 0.0f, 1.0f };

    // A. El Vertex Shader (El geómetra):
    // Su único trabajo es tomar las coordenadas de cada punto y decidir exactamente 
    // en qué lugar de la pantalla deben colocarse.
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" + // Variable que recibe la posición (X, Y, Z, W) desde Java.
            "void main() {" +
            "  gl_Position = vPosition;" + // Variable obligatoria de OpenGL donde guardamos la posición final.
            "}";

    // B. El Fragment Shader (El pintor):
    // Una vez que la tarjeta gráfica sabe por dónde pasa la línea, 
    // este shader se encarga de pintar cada uno de los píxeles con el color enviado.
    private final String fragmentShaderCode =
            "precision mediump float;" + // Define una precisión matemática estándar para los decimales.
            "uniform vec4 vColor;" +      // Variable que recibe el color RGBA desde Java.
            "void main() {" +
            "  gl_FragColor = vColor;" +   // Variable obligatoria donde asignamos el color final visible.
            "}";

    /**
     * Constructor de la clase Line.
     * Inicializa la memoria nativa para la tarjeta gráfica y compila los shaders.
     */
    public Line() {
        // Paso 1: Reservamos espacio en la memoria nativa del sistema.
        // Como tenemos 4 valores en total (2 puntos * 2 coordenadas X/Y) 
        // y cada float ocupa 4 bytes, pedimos 4 * 4 = 16 bytes de memoria para la GPU.
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(lineCoords.length * 4);

        // Paso 2: Nos aseguramos de que los bytes se lean en el orden correcto 
        // según el procesador del dispositivo (arquitectura Little-Endian o Big-Endian).
        byteBuffer.order(ByteOrder.nativeOrder());

        // Paso 3: Convertimos ese espacio de bytes crudos en un búfer de números decimales (FloatBuffer).
        vertexBuffer = byteBuffer.asFloatBuffer();

        // Paso 4: Copiamos los números de nuestro arreglo de Java (`lineCoords`) hacia este nuevo búfer nativo.
        vertexBuffer.put(lineCoords);

        // Paso 5: Devolvemos el puntero del búfer al inicio (posición 0) 
        // para que la GPU empiece a leer desde el primer número.
        vertexBuffer.position(0);

        // Paso 6: Compilamos los dos shaders (el que posiciona y el que pinta).
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // Paso 7: Creamos un "programa" vacío dentro de la tarjeta gráfica como contenedor.
        mProgram = GLES20.glCreateProgram();

        // Paso 8: Adjuntamos los dos shaders compilados dentro de ese programa.
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        // Paso 9: Enlazamos el programa para verificar que no existan errores de comunicación.
        GLES20.glLinkProgram(mProgram);
    }

    /**
     * Carga y compila el código fuente escrito en GLSL dentro de la GPU.
     * 
     * @param type Tipo de shader (GLES20.GL_VERTEX_SHADER o GLES20.GL_FRAGMENT_SHADER).
     * @param shaderCode Cadena de texto con el código escrito en GLSL.
     * @return Retorna el identificador (ID) del shader en la GPU.
     */
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    /**
     * Método de dibujo (draw).
     * Se ejecuta continuamente en el ciclo de renderizado para proyectar la línea en pantalla.
     */
    public void draw() {
        // 1. Indica a OpenGL que use el programa ejecutable previamente enlazado (`mProgram`).
        GLES20.glUseProgram(mProgram);

        // 2. Obtiene la ubicación en la GPU de la variable `vPosition` definida en el Vertex Shader.
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // 3. Habilita el canal de datos en la GPU para el atributo de posición.
        GLES20.glEnableVertexAttribArray(positionHandle);

        // 4. Configura el puntero que indica a la GPU cómo leer los datos del búfer:
        // - positionHandle: Variable destino.
        // - 2: Cantidad de coordenadas por vértice (X e Y).
        // - GLES20.GL_FLOAT: Tipo de dato (número decimal).
        // - false: Datos sin normalizar.
        // - 0: Zancada (stride) en 0 porque los datos están ordenados de forma consecutiva.
        // - vertexBuffer: Origen de los datos.
        GLES20.glVertexAttribPointer(
                positionHandle,
                2,
                GLES20.GL_FLOAT,
                false,
                0,
                vertexBuffer
        );

        // 5. Obtiene la ubicación de la variable uniforme de color (`vColor`) en el Fragment Shader.
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // 6. Transfiere el arreglo de color hacia la variable uniforme de la GPU.
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // 7. Orden de renderizado: Dibuja líneas (`GLES20.GL_LINES`), 
        // empezando desde el vértice 0 y utilizando un total de 2 vértices.
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);

        // 8. Desactiva el canal del atributo de posición para mantener limpia la GPU.
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
