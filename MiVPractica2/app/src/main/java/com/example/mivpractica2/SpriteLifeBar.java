package com.example.mivpractica2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class SpriteLifeBar {
    private int spriteTexture;
    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;
    private int totalFrames;
    private float maxLife;
    private float currentLife;
    private Context context;

    public SpriteLifeBar(Context context, int maxLife, int totalFrames) {
        this.context = context;
        this.maxLife = maxLife;
        this.currentLife = maxLife;
        this.totalFrames = totalFrames;
    }

    public void loadTexture(GL10 gl) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.raw.spritevida);

        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        spriteTexture = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, spriteTexture);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }

    public void draw(GL10 gl) {
        // Determina el frame basado en la vida restante
        int frame = (int) ((currentLife / maxLife) * (totalFrames - 1));
        float frameHeight = 1.0f / totalFrames; // Altura de cada frame
        float texTop = 1.0f - frame * frameHeight;
        float texBottom = texTop - frameHeight;

        // Actualiza las coordenadas de textura
        float[] texCoords = {
                0.0f, texTop,     // Top-left
                1.0f, texTop,     // Top-right
                0.0f, texBottom,  // Bottom-left
                1.0f, texBottom   // Bottom-right
        };

        texCoordBuffer.clear();
        texCoordBuffer.put(texCoords);
        texCoordBuffer.position(0);

        gl.glPushMatrix();
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, spriteTexture);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordBuffer);

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glPopMatrix();
    }
}
