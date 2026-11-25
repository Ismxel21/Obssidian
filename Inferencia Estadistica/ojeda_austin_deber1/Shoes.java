package com.primitivas2D.ojeda_austin_deber1;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Shoes {
    private final FloatBuffer vertexBuffer;
    private final ShortBuffer indexBuffer;
    private final int mProgram;
    private int positionHandle;
    static final int COORDS_POR_VERTEX = 3;

    static float squareCoords[] = {
            -0.16f, -0.475f, 0.0f,
            0.028f, -0.487f, 0.0f,
            0.022f, -0.555f, 0.0f,
            -0.211f, -0.581f, 0.0f,
            -0.293f, -0.531f, 0.0f,

            0.655f, -0.495f, 0.0f,
            0.35f, -0.525f, 0.0f,
            0.69f, -0.52f, 0.0f,
            0.64f, -0.58f, 0.0f,
            0.45f, -0.59f, 0.0f
    };

    private final short index[] = {
            0,1,2,
            0,2,3,
            0,3,4,

            5,6,9,
            5,9,8,
            5,8,7,
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

    public Shoes() {
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