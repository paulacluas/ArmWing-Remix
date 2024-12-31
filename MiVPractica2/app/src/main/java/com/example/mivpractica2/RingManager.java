package com.example.mivpractica2;

import javax.microedition.khronos.opengles.GL10;
import java.util.Random;
import android.content.Context;

public class RingManager {
    private Object3D ring;
    private float[] ringPosition;
    private Context context;
    private Random random;
    private float spawnTimer = 0;


    public RingManager(Context context) {
        this.context = context;
        this.random = new Random();

        // Carga el modelo del anillo
        ring = new Object3D(context, R.raw.ring);
        resetRingPosition();
    }

    private void resetRingPosition() {
        // Posición inicial del anillo
        ringPosition = new float[]{
                random.nextFloat() * 4.0f - 2.0f, // X
                random.nextFloat() * 4.0f - 2.0f, // Y
                -30.0f // Z, empieza lejos
        };
    }

    public void updateAndDrawRings(GL10 gl, float deltaTime) {
        spawnTimer += deltaTime;

        // Mueve el anillo hacia la cámara
        ringPosition[2] += 0.05f; // Velocidad más lenta

        // Si el anillo pasa la cámara, reaparece en una nueva posición
        if (ringPosition[2] > 1.0f) {
            resetRingPosition();
        }

        // Habilitar la neblina si el anillo está lejos
        if (ringPosition[2] > -20.0f) {
            gl.glEnable(GL10.GL_FOG);
        } else {
            gl.glDisable(GL10.GL_FOG);
        }

        // Dibuja el anillo
        gl.glPushMatrix();
        gl.glTranslatef(ringPosition[0], ringPosition[1], ringPosition[2]); // Posiciona el anillo
        gl.glScalef(2.5f, 2.5f, 2.5f); // Escala el anillo (más grande)
        ring.draw(gl);
        gl.glPopMatrix();

        // Asegúrate de desactivar la neblina después del dibujo
        gl.glDisable(GL10.GL_FOG);
    }

    public boolean checkCollision(float planeX, float planeY, float planeZ) {
        float distanceX = ringPosition[0] - planeX;
        float distanceY = ringPosition[1] - planeY;
        float distanceZ = ringPosition[2] - planeZ; // Z del avión es `planeZ`
    
        // Radio de colisión del anillo y el avión
        float collisionRadius = 1.5f; // Ajustar según el tamaño del anillo y el avión
    
        // Calcular distancia al cuadrado
        float distanceSquared = distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;
    
        // Comprobar si la distancia está dentro del radio de colisión
        return distanceSquared < collisionRadius * collisionRadius;
    }
    

}

