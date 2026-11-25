package com.primitivas2D.ojeda_austin_deber1;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Campo1 {
    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle;
    static final int COORDS_POR_VERTEX=3;
    static float squareCoords []={
            -1.0f, -0.105f, 0.0f,
            -0.601f, -0.105f, 0.0f,
            -0.998f, -0.414f, 0.0f,

            -0.275f, -0.099f, 0.0f,
            0.027f, -0.093f, 0.0f,
            0.015f, -1.0f, 0.0f,
            -0.853f, -1.0f, 0.0f,

            0.8f, -1.0f, 0.0f,
            0.512f, -0.093f, 0.0f,
            0.996f, -0.096f, 0.0f,
            1.0f, -1.0f, 0.0f,
    };

    private final short index []={
            0,1,2,
            3,4,5,
            3,5,6,
            7,8,9,
            7,10,9
    };
    private final ShortBuffer indexBuffer;

    public Campo1 (){
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
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vec4(0.071, 0.49, 0.165, 1.0);" +
                    "}";
    public void draw(){
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