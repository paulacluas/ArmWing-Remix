package com.example.mivpractica2;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

public class ParticleManager {
    private class Particle {
        float x, y, z; // Posición
        float dx, dy, dz; // Velocidad
        float life; // Vida restante
        float scale; // Tamaño de la partícula
    }

    private ArrayList<Particle> particles;
    private Random random;
    private int maxParticles = 100; // Número máximo de partículas visibles
    private FloatBuffer vertexBuffer; // Buffer para los vértices
    private FloatBuffer colorBuffer; // Buffer para los colores

    private float[] vertices = {
            -0.5f, -0.5f, 0.0f,  // Inferior izquierda
            0.5f, -0.5f, 0.0f,  // Inferior derecha
            -0.5f,  0.5f, 0.0f,  // Superior izquierda
            0.5f,  0.5f, 0.0f   // Superior derecha
    };

    private float[] colors = {
            1.0f, 1.0f, 1.0f, 1.0f, // Blanco con transparencia
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 0.5f,
            1.0f, 1.0f, 1.0f, 0.5f
    };

    public ParticleManager() {
        particles = new ArrayList<>();
        random = new Random();

        // Crear buffer de vértices
        ByteBuffer vb = ByteBuffer.allocateDirect(vertices.length * 4); // 4 bytes por float
        vb.order(ByteOrder.nativeOrder());
        vertexBuffer = vb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // Crear buffer de colores
        ByteBuffer cb = ByteBuffer.allocateDirect(colors.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);
    }

    public void spawnParticles(float originX, float originY, float originZ) {
        // Generar nuevas partículas alrededor del origen
        for (int i = 0; i < 5; i++) {
            if (particles.size() < maxParticles) {
                Particle p = new Particle();
                p.x = originX;
                p.y = originY;
                p.z = originZ;

                // Velocidad inicial aleatoria hacia atrás respecto al avión
                p.dx = (random.nextFloat() - 0.5f) * 0.1f; // Pequeño movimiento horizontal
                p.dy = -0.2f - random.nextFloat() * 0.1f;  // Movimiento hacia atrás (dirección Y)
                p.dz = (random.nextFloat() - 0.5f) * 0.1f; // Pequeño movimiento vertical

                // Vida y tamaño
                p.life = 1.0f; // 1 segundo de vida
                p.scale = 0.05f + random.nextFloat() * 0.05f;

                particles.add(p);
            }
        }
    }


    public void updateParticles(float deltaTime) {
        ArrayList<Particle> deadParticles = new ArrayList<>();

        // Actualizar posición y vida de cada partícula
        for (Particle p : particles) {
            p.x += p.dx;
            p.y += p.dy;
            p.z += p.dz;

            p.life -= deltaTime; // Reducir la vida útil
            if (p.life <= 0) {
                deadParticles.add(p); // Marcar para eliminar
            }
        }

        // Eliminar partículas muertas
        particles.removeAll(deadParticles);
    }

    public void drawParticles(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);

        for (Particle p : particles) {
            gl.glPushMatrix();
            gl.glTranslatef(p.x, p.y, p.z);
            gl.glScalef(p.scale, p.scale, p.scale);

            // Dibujar la partícula como un cuadrado
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

            gl.glPopMatrix();
        }

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }
}
