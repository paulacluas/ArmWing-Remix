package com.example.mivpractica2;

import android.content.Context;
import javax.microedition.khronos.opengles.GL10;
import java.util.Random;

public class BombManager {
    private Object3D[] bombs;
    private float[] bombX;
    private float[] bombY;
    private float[] bombZ;
    private float[] bombSpeed;
    private float[] bombRotation;
    private float[] bombRotationSpeed;
    private float[] bombScale;
    private Random random;
    private Context context;
    private int numBombs;

    public BombManager(Context context, int numBombs) {
        this.context = context;
        this.numBombs = numBombs;
        this.bombs = new Object3D[numBombs];
        this.bombX = new float[numBombs];
        this.bombY = new float[numBombs];
        this.bombZ = new float[numBombs];
        this.bombSpeed = new float[numBombs];
        this.bombRotation = new float[numBombs];
        this.bombRotationSpeed = new float[numBombs];
        this.bombScale = new float[numBombs];
        this.random = new Random();
    }

    public void initializeBombs(GL10 gl) {
        for (int i = 0; i < numBombs; i++) {
            bombs[i] = new Object3D(context, R.raw.bomba);
            bombs[i].loadTexture(gl, context, R.raw.texturillabomba3);
            bombX[i] = getRandomPositionX();
            bombY[i] = getRandomPositionY();
            bombZ[i] = -100.0f - i * 50; // Más lejos que los asteroides
            bombSpeed[i] = 0.5f + random.nextFloat() * 0.5f; // Velocidad alta
            bombRotation[i] = random.nextFloat() * 360.0f;
            bombRotationSpeed[i] = random.nextFloat() * 2.0f - 1.0f;
            bombScale[i] = 1.5f + random.nextFloat() * 0.5f; // Tamaño más grande que antes
        }
    }


    public void updateAndDrawBombs(GL10 gl) {
        for (int i = 0; i < numBombs; i++) {
            bombZ[i] += bombSpeed[i];
            if (bombZ[i] > 8.0f) {
                bombZ[i] = -120.0f; // Reiniciar en el fondo
                bombX[i] = getRandomPositionX();
                bombY[i] = getRandomPositionY();
                bombSpeed[i] = 0.5f + random.nextFloat() * 0.5f; // Velocidad alta
            }

            bombRotation[i] += bombRotationSpeed[i];
            if (bombRotation[i] > 360.0f) bombRotation[i] -= 360.0f;

            gl.glPushMatrix();
            gl.glTranslatef(bombX[i], bombY[i], bombZ[i]);
            gl.glRotatef(bombRotation[i], 0.0f, 1.0f, 0.0f);
            gl.glScalef(bombScale[i], bombScale[i], bombScale[i]);
            bombs[i].draw(gl);
            gl.glPopMatrix();
        }
    }

    private float getRandomPositionX() {
        return random.nextFloat() * 25.0f - 12.5f;
    }

    private float getRandomPositionY() {
        return random.nextFloat() * 32.0f - 16.0f;
    }

    public int getNumBombs() {
        return numBombs;
    }

    public float getBombX(int index) {
        return bombX[index];
    }

    public float getBombY(int index) {
        return bombY[index];
    }

    public float getBombZ(int index) {
        return bombZ[index];
    }
}
