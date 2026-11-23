package com.primitivas2D.ojeda_austin_deber1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private MyGLSurfaceView myGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myGLSurfaceView = new MyGLSurfaceView(this);

        setContentView(myGLSurfaceView);
    }
}