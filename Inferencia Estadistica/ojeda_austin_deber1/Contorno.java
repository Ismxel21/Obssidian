package com.primitivas2D.ojeda_austin_deber1;

import static com.primitivas2D.ojeda_austin_deber1.MyGLRenderer.loadShader;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Contorno {

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle, colorHandle;

    static final int COORDS_PER_VERTEX = 2;

    static float[] lineCoords = {
            //cuello
            -0.12f, 0.078f,
            -0.1f, 0.078f,
            0.016f, 0.025f,
            0.165f, 0.082f,

            //manga izquierda
            -0.198f, 0.081f,
            -0.311f, 0.035f,
            -0.311f, -0.022f,
            -0.16f, -0.051f,

            //manga derecha
            0.4f, 0.08f,
            0.538f, 0.102f,
            0.62f, 0.00f,
            0.55f, -0.03f,
            0.431f, -0.086f,

            //torso
            -0.16f, -0.051f,
            -0.129f, -0.245f,
            0.318f, -0.265f,
            0.455f, -0.245f,
            0.431f, -0.086f,

            //cara
            -0.186f, 0.074f,
            0.437f, 0.086f,
            0.399f, 0.275f,
            -0.255f, 0.272f,

            //brazo izquierdo
            -0.311f, -0.022f,
            -0.16f, -0.051f,
            -0.22f, -0.145f,
            -0.255f, -0.235f,
            -0.335f, -0.23f,
            -0.355f, -0.175f,

            //brazo derecho
            0.431f, -0.086f,
            0.62f, 0.0f,
            0.70f, -0.125f,
            0.665f, -0.215f,
            0.575f, -0.22f,
            0.535f, -0.155f,
            0.48f, -0.125f,

            //boca
            -0.045f, 0.114f,
            0.035f, 0.104f,
            0.11f, 0.118f,

            //pierna izq
            -0.255f, -0.295f,
            -0.255f, -0.405f,
            -0.006f, -0.408f,
            -0.01f, -0.295f,

            //pierna derecha
            0.325f, -0.34f,
            0.535f, -0.31f,
            0.57f, -0.44f,
            0.345f, -0.475f,

            //polin izquierdo
            -0.255f, -0.405f,
            -0.005f, -0.408f,
            -0.005f, -0.505f,
            -0.24f, -0.498f,

            //polin derecho
            0.345f, -0.475f,
            0.585f, -0.44f,
            0.645f, -0.485f,
            0.605f, -0.535f,
            0.465f, -0.54f,

            //manos
            -0.34f, -0.22f,
            -0.29f, -0.205f,
            -0.32f, -0.23f,
            -0.28f, -0.215f,
            -0.29f, -0.204f,
            -0.265f, -0.235f,
            0.555f, -0.19f,
            0.63f, -0.17f,
            0.56f, -0.215f,
            0.625f, -0.2f,

            //pierna izquierda extra
            -0.255f, -0.295f,
            -0.2f, -0.24f
    };

    private final int vertexStride = COORDS_PER_VERTEX * 4;
    float[] color = {0.0f, 0.0f, 0.0f, 1.0f};

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public Contorno() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(lineCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(lineCoords);
        vertexBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

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
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
        );

        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        GLES20.glLineWidth(12.0f);

        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 4);
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 4, 4);
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 8, 5);

        GLES20.glLineWidth(9.0f);

        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 13, 5);
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 18, 4);
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 22, 6);
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 28, 7);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 35, 3);

        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 38, 4);
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 42, 4);
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 46, 4);
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 50, 5);
        GLES20.glLineWidth(8.0f);
        GLES20.glDrawArrays(GLES20.GL_LINES, 55, 14);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}