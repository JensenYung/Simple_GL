package com.coo.simple.gles.activity;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;

import com.coo.simple.gles.R;
import com.coo.simple.gles.base.BaseActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RenderActivity extends BaseActivity {

    GLSurfaceView gsvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render);
        gsvContent = (GLSurfaceView) findViewById(R.id.gsvContent);
        gsvContent.setRenderer(new MyRender());
        gsvContent.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gsvContent.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gsvContent.onPause();
    }

    class MyRender implements GLSurfaceView.Renderer {

        private float[] points, triangles;
        private FloatBuffer pointsBuffer, trianglesBuffer;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            /*//设置背景颜色
            gl.glClearColor(0.f, 0.f, 0.f, 0.9f);
            //开启 smooth shading
            gl.glShadeModel(GL10.GL_SMOOTH);
            //设置 depth buffer
            gl.glClearDepthf(0.f);
            //开启depth 测试
            gl.glEnable(GL10.GL_DEPTH_TEST);
            //The type of depth testing to do
            gl.glDepthFunc(GL10.GL_LEQUAL);
            //Really nice perspective calculations
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);*/

            //初始化点坐标(上、中、下、左、右)
            points = new float[]{
                    0.0f, 0.9f, 0.0f,
                    0.0f, 0.5f, 0.0f,
                    0.0f, 0.1f, 0.0f,
                    -0.4f, 0.5f, 0.0f,
                    0.4f, 0.5f, 0.0f,
            };
            pointsBuffer = ByteBuffer.allocateDirect(points.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            pointsBuffer.put(points);
            pointsBuffer.position(0);

            //初始化点坐标(上、左、中、右、下)
            triangles = new float[]{
                    -0.4f, -0.1f, 0.0f,//
                    0.0f, -0.1f, 0.0f,
                    -0.4f, -0.5f, 0.0f,
                    0.0f, -0.5f, 0.0f,
                    0.4f, -0.5f, 0.0f,
                    0.0f, -0.9f, 0.0f,

            };
            trianglesBuffer = ByteBuffer.allocateDirect(triangles.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            trianglesBuffer.put(triangles);
            trianglesBuffer.position(0);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置当前view port 一个新的尺寸
            gl.glViewport(0, 0, width, height);
            //设置 projection matrix mode
            gl.glMatrixMode(GL10.GL_PROJECTION);
            //reset
            gl.glLoadIdentity();
            //calculate the aspect ratio of the window
            GLU.gluPerspective(gl, 45.f, width / (float) height, 0.1f, 100.f);
            //设置 model view matrix
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            //reset
            gl.glLoadIdentity();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glLoadIdentity();
            gl.glTranslatef(0.f, 0.f, -4.f);

            gl.glClearColor(0.f, 0.f, 0.f, 0.f);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            //
            gl.glColor4f(1.f, 1.f, 1.f, 0.5f);
            gl.glPointSize(20.f);

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, pointsBuffer);

            int mode = (int) (System.currentTimeMillis() / 1000 % 10);
            /**
             *
             */
            gl.glDrawArrays(GL10.GL_POINTS, 0, mode % (points.length / 3) + 1);

            /**
             * GL_LINES:两点一线(相邻顶点不共用)
             * GL_LINE_STRIP:两点一线(相邻顶点共用)
             * GL_LINE_LOOP:两点一线(相邻顶点共用，首尾相连)
             */
            switch (mode) {
                case 0:
                case 1:
                case 2:
                    gl.glColor4f(1.f, 0.f, 0.f, 1.f);
                    gl.glDrawArrays(GL10.GL_LINES, 0, points.length / 3);
                    break;
                case 3:
                case 4:
                case 5:
                    gl.glColor4f(0.f, 1.f, 0.f, 1.f);
                    gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, points.length / 3);
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                    gl.glColor4f(0.f, 0.f, 1.f, 1.f);
                    gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, points.length / 3);
                    break;
            }

            //
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, trianglesBuffer);
            gl.glColor4f(1.f, 1.f, 1.f, 0.5f);
            gl.glDrawArrays(GL10.GL_POINTS, 0, triangles.length / 3);
            /**
             * GL_TRIANGLES:每隔三个点点组成一个三角形
             * GL_TRIANGLE_STRIP:每相邻三个顶点组成一个三角形
             * GL_TRIANGLE_FAN:以一个点为三角形公共顶点，组成一系列相邻的三角形
             */
            switch (mode) {
                case 0:
                case 1:
                case 2:
                    gl.glColor4f(1.f, 0.f, 0.f, 1.f);
                    gl.glDrawArrays(GL10.GL_TRIANGLES, 0, triangles.length / 3);
                    break;
                case 3:
                case 4:
                case 5:
                    gl.glColor4f(0.f, 1.f, 0.f, 1.f);
                    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, triangles.length / 3);
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                    gl.glColor4f(0.f, 0.f, 1.f, 1.f);
                    gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, triangles.length / 3);
                    break;
            }

            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }
    }
}
