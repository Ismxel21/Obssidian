package com.primitivas2D.ojeda_austin_deber1;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Camiseta {
    private final FloatBuffer vertexBuffer;
    private final ShortBuffer indexBuffer;
    private final int mProgram;
    private int positionHandle;
    static final int COORDS_POR_VERTEX = 3;

    static float squareCoords[] = {
            // cuerpo y manga izquierda
            -0.13f, -0.24f, 0.0f,
            -0.142f, -0.05f, 0.0f,
            -0.31f, -0.02f, 0.0f,
            -0.31f, 0.04f, 0.0f,
            -0.2f, 0.08f, 0.0f,

            // cuello y hombros
            -0.09f, 0.08f, 0.0f,
            0.016f, 0.049f, 0.0f,
            0.19f, 0.12f, 0.0f,
            0.38f, 0.11f, 0.0f,

            // manga derecha y cuepro inferior
            0.54f, 0.1f, 0.0f,
            0.63f, 0.0f, 0.0f,
            0.55f, -0.03f, 0.0f,
            0.431f, -0.086f, 0.0f,
            0.455f, -0.245f, 0.0f,
            0.32f, -0.26f, 0.0f
    };

    private final short index[] = {
            // manga izquierda
            1,2,3,
            1,3,4,

            // torso izquierdo y cuello
            0,1,4,
            0,4,5,
            0,5,6,

            //cuerpo cen-derecho
            0,6,14,
            6,14,12,
            6,12,8,
            6,8,7,

            //cuerpo derecha y manga derecha
            14,13,12,
            12,8,9,
            12,9,11,
            9,10,11
    };

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "void main() {" +
                    "  gl_FragColor = vec4(0.9, 0.9, 0.9, 1.0);" +
                    "}";

    public Camiseta() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(squareCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        ByteBuffer sb = ByteBuffer.allocateDirect(index.length * 2);
        sb.order(ByteOrder.nativeOrder());
        indexBuffer = sb.asShortBuffer();
        indexBuffer.put(index);
        indexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw() {
        GLES20.glUseProgram(mProgram);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(
                positionHandle,
                COORDS_POR_VERTEX,
                GLES20.GL_FLOAT,
                false,
                0,
                vertexBuffer
        );

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                index.length,
                GLES20.GL_UNSIGNED_SHORT,
                indexBuffer
        );

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}