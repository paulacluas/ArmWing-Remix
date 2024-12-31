package com.example.mivpractica2;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

public class StarManager {
    private static final int NUM_STARS = 200;
    private FloatBuffer starBuffer;
    private float[][] stars;
    private Random random;

    public StarManager() {
        stars = new float[NUM_STARS][3]; // [x, y, z] for each star
        random = new Random();
        initStars();
    }

    // Initialize star positions
    private void initStars() {
        float[] starPositions = new float[NUM_STARS * 3];
        for (int i = 0; i < NUM_STARS; i++) {
            stars[i][0] = random.nextFloat() * 4.0f - 2.0f; // X position (-2 to 2)
            stars[i][1] = random.nextFloat() * 4.0f - 2.0f; // Y position (-2 to 2)
            stars[i][2] = random.nextFloat() * 10.0f + 1.0f; // Z position (1 to 10)

            starPositions[i * 3] = stars[i][0];
            starPositions[i * 3 + 1] = stars[i][1];
            starPositions[i * 3 + 2] = stars[i][2];
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(starPositions.length * 4);
        bb.order(ByteOrder.nativeOrder());
        starBuffer = bb.asFloatBuffer();
        starBuffer.put(starPositions);
        starBuffer.position(0);
    }

    public void drawStars(GL10 gl) {
        gl.glPointSize(6.0f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        for (int i = 0; i < NUM_STARS; i++) {
            stars[i][2] -= 0.1f;
            if (stars[i][2] < 0.1f) { // Reset star if it goes out of view
                stars[i][0] = random.nextFloat() * 4.0f - 2.0f;
                stars[i][1] = random.nextFloat() * 4.0f - 2.0f;
                stars[i][2] = random.nextFloat() * 10.0f + 1.0f;
            }
        }
        // Update star buffer
        float[] starPositions = new float[NUM_STARS * 3];
        for (int i = 0; i < NUM_STARS; i++) {
            starPositions[i * 3] = stars[i][0];
            starPositions[i * 3 + 1] = stars[i][1];
            starPositions[i * 3 + 2] = -stars[i][2];
        }
        starBuffer.clear();
        starBuffer.put(starPositions);
        starBuffer.position(0);

        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, starBuffer);
        gl.glDrawArrays(GL10.GL_POINTS, 0, NUM_STARS);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}