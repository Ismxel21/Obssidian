package com.primitivas2D.ojeda_austin_deber1;

import static com.primitivas2D.ojeda_austin_deber1.MyGLRenderer.loadShader;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Cejas {

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle;

    static float[] stripData = {
            //ceja izq
            -0.095f, 0.222f,
            -0.095f, 0.236f,
            -0.015f, 0.226f,
            -0.015f, 0.24f,

            //ceja der
            0.105f, 0.226f,
            0.105f, 0.24f,
            0.185f, 0.222f,
            0.185f, 0.236f
    };

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "void main() {" +
                    "  gl_FragColor = vec4(0.15, 0.09, 0.05, 1.0);" +
                    "}";

    public Cejas() {
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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 4, 4);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}