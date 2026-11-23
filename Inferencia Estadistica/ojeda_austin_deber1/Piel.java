package com.primitivas2D.ojeda_austin_deber1;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Piel {
    private final FloatBuffer vertexBuffer;
    private final ShortBuffer indexBuffer;
    private final int mProgram;
    private int positionHandle;
    static final int COORDS_POR_VERTEX = 3;

    static float squareCoords[] = {
            //pierna izquierda
            -0.2f, -0.245f, 0.0f,
            -0.255f, -0.295f, 0.0f,
            -0.245f, -0.425f, 0.0f,
            -0.015f, -0.425f, 0.0f,
            -0.01f, -0.295f, 0.0f,

            //pierna derecha
            0.325f, -0.34f, 0.0f,
            0.365f, -0.495f, 0.0f,
            0.535f, -0.31f, 0.0f,
            0.575f, -0.485f, 0.0f,

            //brazo izquierdo
            -0.311f, -0.022f,0.0f,
            -0.142f, -0.051f,0.0f,
            -0.355f,-0.175f, 0.0f,
            -0.335f,-0.23f, 0.0f,
            -0.255f,-.235f,0.0f,
            -0.22f, -.145f, 0.0f,

            //brazo derecho
            0.431f, -0.086f, 0.0f,
            0.62f,.02f, 0.0f,
            0.7f, -0.125f,0.0f,
            0.665f, -0.215f, 0.0f,
            0.575f,-0.22f, 0.0f,
            0.535f,-0.155f, 0.0f,
            0.48f, -0.125f, 0.0f
    };

    private final short index[] = {
            0,1,2,
            0,2,3,
            0,3,4,

            5,6,8,
            5,8,7,

            9,11,14,
            9,14,10,
            11,12,14,
            12,13,14,

            15,21,16,
            16,21,17,
            21,20,17,
            20,18,17,
            20,19,18,
    };

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "void main() {" +
                    "  gl_FragColor = vec4(0.87, 0.65, 0.49, 1.0);" +
                    "}";

    public Piel() {
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