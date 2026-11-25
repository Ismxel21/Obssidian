package com.primitivas2D.ojeda_austin_deber1;

import static com.primitivas2D.ojeda_austin_deber1.MyGLRenderer.loadShader;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Cara {

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle;

    static float[] coords = {

            -0.186f, 0.074f,
            0.437f, 0.086f,
            0.399f, 0.275f,
            -0.255f, 0.272f,


            -0.1f, 0.078f,
            0.165f, 0.082f,
            0.016f, 0.025f
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

    public Cara() {
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(coords);
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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 4, 3);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}