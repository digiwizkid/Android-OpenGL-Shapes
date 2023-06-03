package com.digiwizkid.OpenGlShapes;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

public class GLRenderer implements GLSurfaceView.Renderer {

    private Square square;

    public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = glCreateShader(type);

        // add the source code to the shader and compile it
        glShaderSource(shader, shaderCode);
        glCompileShader(shader);

        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0, 0, 0, 0);

        square = new Square();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        glViewport(0, 0, i, i1);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        square.draw();
    }
}
