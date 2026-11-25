package com.primitivas2D.ojeda_austin_deber1;

import static com.primitivas2D.ojeda_austin_deber1.MyGLRenderer.loadShader;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class FranjasCamiseta {

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle, colorHandle;

    private final float[] stripCoords = {

            -0.085f, 0.068f,
            -0.025f, 0.042f,
            -0.075f, -0.245f,
            -0.015f, -0.247f,

            0.045f, 0.035f,
            0.105f, 0.057f,
            0.055f, -0.252f,
            0.115f, -0.254f,

            0.175f, 0.08f,
            0.235f, 0.08f,
            0.185f, -0.256f,
            0.245f, -0.258f,

            0.305f, 0.08f,
            0.365f, 0.08f,
            0.315f, -0.26f,
            0.375f, -0.26f
    };

    float[] color = {0.28f, 0.8f, 0.9f, 1.0f};

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

    public FranjasCamiseta() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(stripCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(stripCoords);
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
                2,
                GLES20.GL_FLOAT,
                false,
                0,
                vertexBuffer
        );

        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 4, 4);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 8, 4);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 12, 4);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}