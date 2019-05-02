package com.opengl.study;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    GLSurfaceView mGLSurfaceView;

    static final float[] verties = new float[]{
      0, 0.5f, 0,
      -0.5f, -0.5f, 0,
      0.5f, -0.5f, 0,

    };

    static final float[] color = new float[]{255f, 0f, 0f, 1.0f};

    static final String VertexSource =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main(){" +
            "  gl_Position = uMVPMatrix  * vPosition;" +
            "}";

    static final String FragSource =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main(){" +
            "  gl_FragColor = vColor;" +
            "}";

    FloatBuffer vertexBuffer;

    int program;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private int mMVPMatrixHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = findViewById(R.id.glsurface_view);

        mGLSurfaceView.setEGLContextClientVersion(2);

        mGLSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
                Log.d("surface", "onSurfaceCreated");
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

                int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
                GLES20.glShaderSource(vertexShader, VertexSource);
                GLES20.glCompileShader(vertexShader);

                int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
                GLES20.glShaderSource(fragmentShader, FragSource);
                GLES20.glCompileShader(fragmentShader);

                program = GLES20.glCreateProgram();
                GLES20.glAttachShader(program, vertexShader);
                GLES20.glAttachShader(program, fragmentShader);
                GLES20.glLinkProgram(program);

                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(verties.length * 4);
                byteBuffer.order(ByteOrder.nativeOrder());
                vertexBuffer = byteBuffer.asFloatBuffer();
                vertexBuffer.put(verties);
                vertexBuffer.position(0);

            }

            @Override
            public void onSurfaceChanged(GL10 unused, int width, int height) {
                Log.d("surface", "onSurfaceChanged");
                GLES20.glViewport(0, 0, width, height);
                float radio = (float)width / height;
                Matrix.frustumM(mProjectMatrix, 0, -radio, radio, -1, 1, 3, 7);

            }

            @Override
            public void onDrawFrame(GL10 gl) {
                Log.d("surface", "onDrawFrame");



                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                GLES20.glUseProgram(program);

                Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0, 0, 0, 0, 1, 0);
                Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

                mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
                GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);


                int vertexAttributeIndex = GLES20.glGetAttribLocation(program, "vPosition");
                GLES20.glEnableVertexAttribArray(vertexAttributeIndex);
                GLES20.glVertexAttribPointer(vertexAttributeIndex, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);
                int colorHandle = GLES20.glGetUniformLocation(program, "vColor");
                GLES20.glUniform4fv(colorHandle, 1, color, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
                GLES20.glDisableVertexAttribArray(vertexAttributeIndex);

            }
        });
    }
}
