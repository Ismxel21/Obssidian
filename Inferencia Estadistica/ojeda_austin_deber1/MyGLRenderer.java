package com.primitivas2D.ojeda_austin_deber1;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    //Matrices fundamentales, conclusion de la transformacion 3d a coords de pantalla
    private final float[] mProjectionMatrix = new float[16]; // proyeccion como se vizualiza los 3d en pantalla || calculo
    private final float[] mViewMatrix = new float[16]; //vista: donde y hacia donde esta observando los objetos en 3D || config de la camara
    private final float[] mVPMatrix = new float[16]; //Vista-Proyeccion: sale la MVP=PxV
    private Campo1 vOscuro;
    private Campo2 vClaro;
    private Shoes shoes;
    private Lineas lineas;
    private Polines polines;
    private Piel piel;
    private Short pantalon;
    private Camiseta camiseta;
    private Cara cara;
    private Pelo pelo;
    private Ojos ojos;
    private Cejas cejas;
    private Contorno contorno;
    private FranjasCamiseta cami;
    private Balon balon;
    private Pbalon tBalon;
    private Sol sol;
    private Nubes nubes;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.412f, 0.725f, 0.871f, 1.0f);

        vOscuro = new Campo1();
        vClaro = new Campo2();
        lineas = new Lineas();
        shoes = new Shoes();
        piel = new Piel();
        polines = new Polines();
        pantalon = new Short();
        camiseta = new Camiseta();
        cara = new Cara();
        pelo = new Pelo();
        ojos = new Ojos();
        cejas = new Cejas();
        contorno = new Contorno();
        cami = new FranjasCamiseta();
        balon = new Balon(0.3f, 36, -0.7f, -1.4f, 0.0f);
        tBalon = new Pbalon();
        sol = new Sol(0.25f,36,-0.9f,2.0f , 0.0f);
        nubes= new Nubes();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        //Proyección a utilizar
        float radio = (float) width/height; //radio de aspecto: calcula la relacion en donde se dibuja el objeto
        Matrix.frustumM(mProjectionMatrix,0,
                -radio,radio,-1,1,1,10); //Proyeccion en prespectiva

/*        Matrix.orthoM(mProjectionMatrix,0,
                -radio,radio,-1,1,1,100); //Proyeccion Ortografica*/

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT  )  ;

        //CÁMARA
        Matrix.setLookAtM(mViewMatrix,0,
                0.0f,0.0f,3.0f,
                0.0f,0.0f,0.0f,
                0.0f,1.0f,0.0f);

        //Multi de mVP
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0 ,mViewMatrix, 0);


        //DIBUJOS
        vOscuro.draw();
        vClaro.draw();
        lineas.draw();
        piel.draw();
        polines.draw();
        shoes.draw();
        pantalon.draw();
        camiseta.draw();
        cami.draw();
        cara.draw();
        ojos.draw();
        cejas.draw();
        contorno.draw();
        pelo.draw();
        balon.draw(mVPMatrix);
        tBalon.draw();
        sol.draw(mVPMatrix);
        nubes.draw();
    }
    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}