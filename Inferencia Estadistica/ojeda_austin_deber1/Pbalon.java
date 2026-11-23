package com.primitivas2D.ojeda_austin_deber1;

import static com.primitivas2D.ojeda_austin_deber1.MyGLRenderer.loadShader;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Pbalon {

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle, colorHandle;

    static final int COORDS_PER_VERTEX = 3;

    float[] pointCoords = {
            -0.40f, -0.415f, 0.0f,
            -0.62f, -0.415f, 0.0f,
            -0.50f, -0.475f, 0.0f,
            -0.62f, -0.525f, 0.0f,
            -0.4f, -0.525f, 0.0f,

    };

    private final int vertexCount = pointCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    float[] color = { 0.05f, 0.05f, 0.05f, 1.0f };

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "  gl_PointSize = 45.0;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public Pbalon() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(pointCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(pointCoords);
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

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}