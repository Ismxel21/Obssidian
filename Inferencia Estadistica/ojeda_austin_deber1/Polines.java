package com.primitivas2D.ojeda_austin_deber1;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Polines {
    private final FloatBuffer vertexBuffer;
    private final ShortBuffer indexBuffer;
    private final int mProgram;
    private int positionHandle;
    static final int COORDS_POR_VERTEX = 3;

    static float squareCoords[] = {

            -0.255f, -0.405f, 0.0f,
            -0.005f, -0.408f, 0.0f,
            -0.005f, -0.505f, 0.0f,
            -0.24f, -0.498f, 0.0f,

            0.345f, -0.475f, 0.0f,
            0.52f, -0.468f, 0.0f,
            0.585f, -0.44f, 0.0f,
            0.645f, -0.485f, 0.0f,
            0.605f, -0.535f, 0.0f,
            0.465f, -0.54f, 0.0f
    };

    private final short index[] = {
            0,1,2,
            0,2,3,

            6,7,8,
            6,8,9,
            6,9,4,
            6,4,5
    };

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "void main() {" +
                    "  gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);" +
                    "}";

    public Polines() {
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