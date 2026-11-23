package com.primitivas2D.ojeda_austin_deber1;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

    private MyGLRenderer myGLRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        myGLRenderer = new MyGLRenderer();

        setRenderer(myGLRenderer);
    }
}