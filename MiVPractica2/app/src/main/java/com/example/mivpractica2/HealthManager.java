package com.example.mivpractica2;

import android.content.Context;
import javax.microedition.khronos.opengles.GL10;
import java.util.Random;

public class HealthManager {
    private Object3D[] healthObjects;
    private float[] healthX;
    private float[] healthY;
    private float[] healthZ;
    private float[] healthSpeed;
    private float[] healthScale;
    private Random random;
    private Context context;
    private int numHealthObjects;

    public HealthManager(Context context, int numHealthObjects) {
        this.context = context;
        this.numHealthObjects = numHealthObjects;
        this.healthObjects = new Object3D[numHealthObjects];
        this.healthX = new float[numHealthObjects];
        this.healthY = new float[numHealthObjects];
        this.healthZ = new float[numHealthObjects];
        this.healthSpeed = new float[numHealthObjects];
        this.healthScale = new float[numHealthObjects];
        this.random = new Random();
    }

    public void initializeHealthObjects(GL10 gl) {
        for (int i = 0; i < numHealthObjects; i++) {
            healthObjects[i] = new Object3D(context, R.raw.salud);
            healthObjects[i].loadTexture(gl, context, R.raw.texturasalud);
            healthX[i] = getRandomPositionX();
            healthY[i] = getRandomPositionY();
            healthZ[i] = -100.0f - i * 50;
            healthSpeed[i] = 0.2f + random.nextFloat() * 0.2f;
            healthScale[i] = 1.0f + random.nextFloat() * 0.5f;
        }
    }

    public void updateAndDrawHealthObjects(GL10 gl) {
        for (int i = 0; i < numHealthObjects; i++) {
            healthZ[i] += healthSpeed[i];
            if (healthZ[i] > 8.0f) {
                healthZ[i] = -120.0f;
                healthX[i] = getRandomPositionX();
                healthY[i] = getRandomPositionY();
            }

            gl.glPushMatrix();
            gl.glTranslatef(healthX[i], healthY[i], healthZ[i]);
            gl.glScalef(healthScale[i], healthScale[i], healthScale[i]);
            healthObjects[i].draw(gl);
            gl.glPopMatrix();
        }
    }

    private float getRandomPositionX() {
        return random.nextFloat() * 15.0f - 12.5f;
    }

    private float getRandomPositionY() {
        return random.nextFloat() * 15.0f - 12.5f;
    }

    public int getNumHealthObjects() {
        return numHealthObjects;
    }

    public float getHealthX(int index) {
        return healthX[index];
    }

    public float getHealthY(int index) {
        return healthY[index];
    }

    public float getHealthZ(int index) {
        return healthZ[index];
    }
}
