package com.digiwizkid.OpenGlShapes;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;

public class Square {
    static final int COORDS_PER_VERTEX = 2;

//    static float[] squareCoords = {
//            0f, 1f,             // top triangle
//            -0.5f, 0.5f,      // top left
//            0.5f, 0.5f,        // top right
//            -1f,0f,              // left triangle
//            1f,0f,              // right triangle
//            -0.5f, -0.5f,     // bottom left
//            0.5f, -0.5f,      // bottom right
//            0f, -1f         // bottom triangle
//    };

    static float[] squareCoords = {
            -1f, 0f,    // left
            0f, 1f,     // top
            1f, 0f,     // right
            0f, -1f     // bottom
    };

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4;
    private final short[] drawOrder = {
            0, 1, 2,
            2, 0, 3,
            3, 0, 4,
            4, 0, 1,
    };
    private final int program;
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private int positionHandle;
    private int colorHandle;

    public Square() {
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);

        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        int vertexShader = GLRenderer.loadShader(GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode);

        checkGLError("TAG", "Before program");

        program = glCreateProgram();

        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);

        glLinkProgram(program);

        checkGLError("TAG", "After program");
    }

    public static void checkGLError(String tag, String label) {
        int lastError = GL_NO_ERROR;
        // Drain the queue of all errors.
        int error;
        while ((error = glGetError()) != GL_NO_ERROR) {
            Log.e(tag, label + ": glError " + error);
            lastError = error;
        }
        if (lastError != GL_NO_ERROR) {
            throw new RuntimeException(label + ": glError " + lastError);
        }
    }

    public void draw() {
        glUseProgram(program);
        positionHandle = glGetAttribLocation(program, "vPosition");
        glEnableVertexAttribArray(positionHandle);

        glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GL_FLOAT, false, vertexStride, vertexBuffer);

        colorHandle = glGetUniformLocation(program, "vColor");

        float[] color = {1f, 0f, 0f, 1.0f};

        glUniform4fv(colorHandle, 1, color, 0);

        // use glDrawArrays with GL_TRIANGLE_STRIP/ FAN ignoring draw order
//        glDrawArrays(GL_TRIANGLE_FAN, 0, vertexCount);

        // use glDrawElements with GL_TRIANGLES to utilise draw order
        glDrawElements(
                GL_TRIANGLES, drawOrder.length,
                GL_UNSIGNED_SHORT, drawListBuffer);

        glDisableVertexAttribArray(positionHandle);
    }
}
