package com.example.mivpractica2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ExplosionManager {

    private class Explosion {
        float x, y, z;
        float remainingTime;

        Explosion(float x, float y, float z, float remainingTime) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.remainingTime = remainingTime;
        }
    }

    private List<Explosion> explosions;
    private int[] explosionTexture = new int[1];
    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;
    private Context context;

    public ExplosionManager(Context context) {
        this.context = context;
        this.explosions = new ArrayList<>();
        setupBuffers();
    }

    // Configurar los buffers para los vértices y las coordenadas de textura
    private void setupBuffers() {
        float[] vertices = {
                -1.0f, -1.0f, 0.0f, // Bottom-left
                1.0f, -1.0f, 0.0f,  // Bottom-right
                -1.0f, 1.0f, 0.0f,  // Top-left
                1.0f, 1.0f, 0.0f    // Top-right
        };

        float[] texCoords = {
                0.0f, 1.0f, // Bottom-left
                1.0f, 1.0f, // Bottom-right
                0.0f, 0.0f, // Top-left
                1.0f, 0.0f  // Top-right
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        texCoordBuffer = tbb.asFloatBuffer();
        texCoordBuffer.put(texCoords);
        texCoordBuffer.position(0);
    }

    // Cargar la textura de explosión
    public void loadExplosionTexture(GL10 gl) {
        // Carga la imagen de la explosión con transparencia
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false; // Evita que Android escale la textura
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.raw.explosion, options);

        // Asegura que el bitmap tenga formato ARGB_8888 (con canal alfa)
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        gl.glGenTextures(1, explosionTexture, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, explosionTexture[0]);

        // Configura los parámetros de la textura
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        // Carga la textura con soporte para el canal alfa
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

        // Libera la memoria del bitmap
        bitmap.recycle();
    }





    // Agregar una nueva explosión
    public void addExplosion(float x, float y, float z) {
        explosions.add(new Explosion(x, y, z, 0.2f)); // Duración de la explosión
    }

    // Dibujar y actualizar las explosiones
    public void updateAndDrawExplosions(GL10 gl, float deltaTime) {
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, explosionTexture[0]);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        // Habilitar blending para manejar transparencias
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        List<Explosion> toRemove = new ArrayList<>();

        for (Explosion explosion : explosions) {
            gl.glPushMatrix();
            gl.glTranslatef(explosion.x, explosion.y, explosion.z);
            gl.glScalef(2.0f, 2.0f, 2.0f);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordBuffer);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
            gl.glPopMatrix();

            explosion.remainingTime -= deltaTime;
            if (explosion.remainingTime <= 0) {
                toRemove.add(explosion);
            }
        }

        explosions.removeAll(toRemove);

        // Deshabilitar blending después de dibujar
        gl.glDisable(GL10.GL_BLEND);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
    }

}
