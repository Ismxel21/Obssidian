# Documentación de la clase `Point` (OpenGL ES 2.0 en Android)

Este archivo contiene la implementación de la clase `Point` estructurada para OpenGL ES, con explicaciones detalladas en cada línea sobre el funcionamiento de la memoria nativa y el renderizado gráfico.

```java
package com.example.primitivas2d;

// Importaciones necesarias para manejar búferes de memoria nativa fuera de la JVM (Java Virtual Machine)
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Clase que representa un punto geométrico en el espacio 3D/2D para OpenGL ES.
 * Se encarga de almacenar las coordenadas espaciales, el color y reservar
 * la memoria nativa (RAM del sistema/GPU) para que la GPU procese la figura.
 */
public class Point {

    // Búfer que almacena las coordenadas de los vértices en memoria nativa
    // (indispensable porque la GPU no puede acceder directamente a los arreglos de Java).
    private final FloatBuffer vertexBuffer;

    // Identificadores de las variables 'attribute' y 'uniform' dentro de los Shaders de OpenGL.
    // - positionHandle: Referencia al atributo de posición (aPosition) en el Vertex Shader.
    // - colorHandle: Referencia al uniforme de color (uColor) en el Fragment Shader.
    private int positionHandle, colorHandle;

    // Define cuántas componentes numéricas tiene cada vértice (X, Y, Z = 3 coordenadas).
    static final int coords_per_vertex = 3;

    // Arreglo de Java con las coordenadas cartesianas del punto en el sistema normalizado de OpenGL (-1.0 a 1.0).
    // Representa un punto ubicado en X = 0.25, Y = -0.25, Z = 0.0.
    float[] pointCoords = { 0.25f, -0.25f, 0.0f };

    // Calcula cuántos vértices contiene la figura dividiendo el total de elementos entre las coordenadas por vértice.
    // Para este caso: 3 elementos / 3 coordenadas = 1 vértice.
    private final int vertexCount = pointCoords.length / coords_per_vertex;

    // Define la "zancada" (stride) en bytes. Indica el tamaño total de memoria que ocupa un único vértice.
    // En Java/C++, un dato tipo float ocupa 4 bytes.
    // Cálculo: 3 coordenadas * 4 bytes = 12 bytes por vértice.
    private final int vertexStride = coords_per_vertex * 4; 
    // Por lo general, cada vértice simple de 3 coordenadas debe pesar exactamente 12 bytes.

    // Arreglo con el color en formato RGBA (Rojo, Verde, Azul, Alfa/Transparencia).
    // Los valores van de 0.0f a 1.0f. Este código representa un color naranja/amarillo opaco.
    float[] color = { 1.0f, 0.75f, 0.25f, 1.0f };

    /**
     * Constructor de la clase Point.
     * Reserva, inicializa y llena la memoria nativa accesible por la GPU.
     */
    public Point() {
        // 1. Reserva un bloque de memoria nativa directa no gestionado por el recolector de basura de Java.
        // Se calcula en bytes: número de elementos en el arreglo (3) * 4 bytes por float = 12 bytes.
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(pointCoords.length * 4);

        // 2. Establece el orden de lectura de los bytes según el procesador del dispositivo (Little-Endian o Big-Endian).
        byteBuffer.order(ByteOrder.nativeOrder());

        // 3. Convierte el búfer de bytes en un búfer de números decimales (FloatBuffer) para trabajar con floats.
        vertexBuffer = byteBuffer.asFloatBuffer();

        // 4. Copia las coordenadas definidas en el arreglo de Java (`pointCoords`) hacia la memoria nativa.
        vertexBuffer.put(pointCoords);

        // 5. Mueve el puntero de lectura del búfer a la posición inicial (índice 0).
        // Sin esto, OpenGL intentaría leer desde el final del búfer y arrojaría un error o no dibujaría nada.
        vertexBuffer.position(0);
    }
}
```
