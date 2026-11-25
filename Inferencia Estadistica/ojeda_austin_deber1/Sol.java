package com.primitivas2D.ojeda_austin_deber1;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class Sol {

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle, colorHandle, mvpMatrixHandle;
    static final int coords_per_vertex = 3;
    float[] color = {1.0f, 0.85f, 0.0f, 1.0f};
    private final int vertexCount;

    private float[] createCircleCoords(float radius, int numPoints, float cx, float cy, float cz) {
        List<Float> coords = new ArrayList<>();

        coords.add(cx);
        coords.add(cy);
        coords.add(cz);

        double angle = 2.0 * Math.PI / numPoints;
        for (int i = 0; i <= numPoints; i++) {
            double angle2 = angle * i;
            coords.add(cx + (float) (radius * Math.cos(angle2)));
            coords.add(cy + (float) (radius * Math.sin(angle2)));
            coords.add(cz);
        }

        float[] arrayVertex = new float[coords.size()];
        for (int i = 0; i < coords.size(); i++) {
            arrayVertex[i] = coords.get(i);
        }

        return arrayVertex;
    }

    public Sol(float radius, int numPoints, float cx, float cy, float cz) {
        float[] sunCoords = createCircleCoords(radius, numPoints, cx, cy, cz);
        vertexCount = sunCoords.length / coords_per_vertex;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(sunCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(sunCoords);
        vertexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main(){" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main(){" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public void draw(float[] mVPMatrix) {
        GLES20.glUseProgram(mProgram);
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(
                positionHandle,
                coords_per_vertex,
                GLES20.GL_FLOAT,
                false,
                0,
                vertexBuffer
        );

        mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mVPMatrix, 0);

        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}