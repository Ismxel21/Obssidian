package com.primitivas2D.ojeda_austin_deber1;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Nubes {

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle;

    static float[] stripData = {
            -0.96f,  0.48f,
            -0.96f,  0.56f,
            -0.72f,  0.48f,
            -0.72f,  0.62f,
            -0.48f,  0.50f,
            -0.48f,  0.56f,

            -0.30f,  0.62f,
            -0.30f,  0.70f,
            -0.04f,  0.64f,
            -0.04f,  0.77f,
            0.22f,  0.62f,
            0.22f,  0.74f,

            0.38f,  0.50f,
            0.38f,  0.58f,
            0.62f,  0.50f,
            0.62f,  0.65f,
            0.90f,  0.55f,
            0.90f,  0.60f,
    };

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "void main() {" +
                    "  gl_FragColor = vec4(.90, .90, .90, 1.0);" +
                    "}";

    public Nubes() {
        ByteBuffer bb = ByteBuffer.allocateDirect(stripData.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(stripData);
        vertexBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 6);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 6, 6);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 12, 6);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}