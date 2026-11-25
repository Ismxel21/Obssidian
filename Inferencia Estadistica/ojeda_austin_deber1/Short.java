package com.primitivas2D.ojeda_austin_deber1;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Short {
    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle;
    static final int COORDS_POR_VERTEX = 3;

    private final float[] shapeVertices = {
            -0.129f, -0.212f, 0.0f,
            -0.27f, -0.285f, 0.0f,
            -0.04f,-.31f, 0.0f,
            0.054f, -0.359f, 0.0f,
            0.14f,-0.28f, 0.0f,
            0.24f, -0.38f, 0.0f,
            0.44f, -0.38f, 0.0f,
            0.56f,-0.32f, 0.0f,
            0.42f, -0.23f, 0.0f,
            0.14f, -0.23f, 0.0f
    };

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "void main() {" +
                    "  gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);" +
                    "}";

    public Short() {
        ByteBuffer bb = ByteBuffer.allocateDirect(shapeVertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(shapeVertices);
        vertexBuffer.position(0);

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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 10);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}