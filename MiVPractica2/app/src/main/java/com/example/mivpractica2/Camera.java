package com.example.mivpractica2;

import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLU;

public class Camera {
    private GL10 gl;
    private Vector4 eye;
    private Vector4 center;
    private Vector4 up;

    public Camera(GL10 gl, Vector4 eye, Vector4 center, Vector4 up) {
        this.gl = gl;
        this.eye = eye;
        this.center = center;
        this.up = up;
    }

    public void update() {
        GLU.gluLookAt(gl,
                eye.get(0), eye.get(1), eye.get(2),
                center.get(0), center.get(1), center.get(2),
                up.get(0), up.get(1), up.get(2));
    }

    public void move(Vector4 direction) {
        eye = eye.add(direction);
        center = center.add(direction);
    }

    public void rotate(float angle, Vector4 axis) {
        // Implementar la rotación de la cámara alrededor de un eje
        // Esto puede requerir cálculos de matrices de rotación y transformación de vectores
    }

    public Vector4 getEye() {
        return eye;
    }

    public Vector4 getCenter() {
        return center;
    }

    public Vector4 getUp() {
        return up;
    }
}