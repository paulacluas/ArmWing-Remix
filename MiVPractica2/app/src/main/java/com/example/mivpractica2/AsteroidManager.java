package com.example.mivpractica2;

import android.content.Context;
import javax.microedition.khronos.opengles.GL10;
import java.util.Random;

public class AsteroidManager {
    private Object3D[] asteroids;
    private float[] asteroidX;
    private float[] asteroidY;
    private float[] asteroidZ;
    private float[] asteroidSpeed;
    private float[] asteroidRotation;
    private float[] asteroidRotationSpeed;
    private float[] asteroidScale;
    private Random random;
    private Context context;
    private int numAsteroids;

    public AsteroidManager(Context context, int numAsteroids) {
        this.context = context;
        this.numAsteroids = numAsteroids;
        this.asteroids = new Object3D[numAsteroids];
        this.asteroidX = new float[numAsteroids];
        this.asteroidY = new float[numAsteroids];
        this.asteroidZ = new float[numAsteroids];
        this.asteroidSpeed = new float[numAsteroids];
        this.asteroidRotation = new float[numAsteroids];
        this.asteroidRotationSpeed = new float[numAsteroids];
        this.asteroidScale = new float[numAsteroids];
        this.random = new Random();
    }

    public void initializeAsteroids(GL10 gl) {
        for (int i = 0; i < numAsteroids; i++) {
            asteroids[i] = new Object3D(context, R.raw.asteroid);
            asteroids[i].loadTexture(gl, context, R.raw.meteorito_textura);
            asteroidX[i] = getRandomPositionX();
            asteroidY[i] = getRandomPositionY();
            asteroidZ[i] = -50.0f - i * 10;
            asteroidSpeed[i] = 0.1f + random.nextFloat() * 0.1f;
            asteroidRotation[i] = random.nextFloat() * 360.0f;
            asteroidRotationSpeed[i] = random.nextFloat() * 2.0f - 1.0f;
            asteroidScale[i] = 0.5f + random.nextFloat() * 0.5f;
        }
    }

    public void updateAndDrawAsteroids(GL10 gl) {
        for (int i = 0; i < numAsteroids; i++) {
            asteroidZ[i] += asteroidSpeed[i];
            if (asteroidZ[i] > 8.0f) {
                asteroidZ[i] = -70.0f;
                asteroidX[i] = getRandomPositionX();
                asteroidY[i] = getRandomPositionY();
                asteroidSpeed[i] = 0.1f + random.nextFloat() * 0.1f;
            }

            asteroidRotation[i] += asteroidRotationSpeed[i];
            if (asteroidRotation[i] > 360.0f) asteroidRotation[i] -= 360.0f;

            gl.glPushMatrix();
            gl.glTranslatef(asteroidX[i], asteroidY[i], asteroidZ[i]);
            gl.glRotatef(asteroidRotation[i], 0.0f, 1.0f, 0.0f);
            gl.glScalef(asteroidScale[i], asteroidScale[i], asteroidScale[i]);
            asteroids[i].draw(gl);
            gl.glPopMatrix();
        }
    }

    private float getRandomPositionX() {
        return random.nextFloat() * 32.0f - 16.0f;
    }

    private float getRandomPositionY() {
        return random.nextFloat() * 32.0f - 16.0f;
    }

    public int getNumAsteroids() {
        return numAsteroids;
    }

    public float getAsteroidX(int index) {
        return asteroidX[index];
    }

    public float getAsteroidY(int index) {
        return asteroidY[index];
    }

    public float getAsteroidZ(int index) {
        return asteroidZ[index];
    }
}