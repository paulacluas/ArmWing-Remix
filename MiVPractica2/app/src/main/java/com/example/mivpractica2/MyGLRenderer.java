package com.example.mivpractica2;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

/**
 * OpenGL Custom renderer used with GLSurfaceView
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private float width, height;
    private Light light;
    private int[] backgroundTexture = new int[1];
    private FloatBuffer backgroundVertices;
    private FloatBuffer backgroundTexCoords;
    private float lastCollisionTime = 0; // Tiempo de la última colisión
    private final float collisionCooldown = 3.0f; // Tiempo de inmunidad en segundos
    private float currentTime = 0; // Tiempo actual del juego
    private int currentLife = 5;
    private int[] lifeTextures = new int[6];
    private boolean isFirstPersonView = false;


    private Random random;

    // POSICIONS DE L'AVIÓ
    private float planeX = 0.0f;
    private float planeY = 0.0f;
    private float planeTiltAngle = 0.0f;
    private boolean isKeyPressed = false;

    // BACKGROUND CONFIG
    private int[] backgroundResources = {R.raw.background3, R.raw.background1};
    private int currentBackgroundIndex = 0;
    private float backgroundChangeTimer = 0.0f; // Temporizador para cambiar el fondo

    private int spriteVidaTexture;
    private FloatBuffer spriteVerticesBuffer;
    private FloatBuffer spriteTexCoordsBuffer;
    Context context;   // Application's context
    Object3D object3D;



    int angle = 0;
    float Z = 1;

    private RingManager ringManager; // Añadir RingManager
    private AsteroidManager asteroidManager; // Añadir AsteroidManager
    private StarManager starManager;
    private SpriteLifeBar spriteLifeBar;
    private ExplosionManager explosionManager;
    private BombManager bombManager;
    private HealthManager healthManager;
    private ParticleManager particleManager;
    private Camera camera;

    // Constructor with global application context
    public MyGLRenderer(Context context) {
        this.context = context;
        this.object3D = new Object3D(context, R.raw.avion);
        this.ringManager = new RingManager(context); // Inicializar RingManager
        this.asteroidManager = new AsteroidManager(context, 20); // Inicializar AsteroidManager
        this.bombManager = new BombManager(context, 2);
        this.starManager = new StarManager();
        this.spriteLifeBar = new SpriteLifeBar(context, 100, 6);
        this.explosionManager = new ExplosionManager(context);
        this.healthManager = new HealthManager(context, 1);
        this.particleManager = new ParticleManager();


        // asignem posició inicial a l'avió
        this.planeX = 0.0f;
        this.planeY = -2.5f;

        setupBackgroundBuffers();
    }

    // Initialize the buffers for the background quad
    private void setupBackgroundBuffers() {
        float[] vertices = {
                -1.0f, -1.0f, -1.0f, // Bottom-left
                1.0f, -1.0f, -1.0f, // Bottom-right
                -1.0f,  1.0f, -1.0f, // Top-left
                1.0f,  1.0f, -1.0f  // Top-right
        };
    
        float[] texCoords = {
                0.0f, 1.0f, // Bottom-left
                1.0f, 1.0f, // Bottom-right
                0.0f, 0.0f, // Top-left
                1.0f, 0.0f  // Top-right
        };
    
        // Buffer for vertices
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        backgroundVertices = vbb.asFloatBuffer();
        backgroundVertices.put(vertices);
        backgroundVertices.position(0);
    
        // Buffer for texture coordinates
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        backgroundTexCoords = tbb.asFloatBuffer();
        backgroundTexCoords.put(texCoords);
        backgroundTexCoords.position(0);
    }

    private void setupSpriteVidaBuffers() {
        // Coordenadas para que la imagen ocupe toda la pantalla
        float[] vertices = {
                -0.5f, 0.5f, 0.0f,  // Top-left
                0.5f, 0.5f, 0.0f,   // Top-right
                -0.5f, -0.5f, 0.0f, // Bottom-left
                0.5f, -0.5f, 0.0f   // Bottom-right
        };

        float[] texCoords = {
                0.0f, 0.0f,  // Top-left
                1.0f, 0.0f,  // Top-right
                0.0f, 1.0f,  // Bottom-left
                1.0f, 1.0f   // Bottom-right
        };

        // Crear buffer para los vértices
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        spriteVerticesBuffer = vbb.asFloatBuffer();
        spriteVerticesBuffer.put(vertices);
        spriteVerticesBuffer.position(0);

        // Crear buffer para las coordenadas de textura
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        spriteTexCoordsBuffer = tbb.asFloatBuffer();
        spriteTexCoordsBuffer.put(texCoords);
        spriteTexCoordsBuffer.position(0);
    }

    private void loadLifeTextures(GL10 gl) {
        int[] resourceIds = {
                R.raw.spritevida1,
                R.raw.spritevida2,
                R.raw.spritevida3,
                R.raw.spritevida4,
                R.raw.spritevida5,
                R.raw.spritevida6
        };

        for (int i = 0; i < resourceIds.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceIds[i]);

            int[] textureIds = new int[1];
            gl.glGenTextures(1, textureIds, 0);
            lifeTextures[i] = textureIds[0];

            gl.glBindTexture(GL10.GL_TEXTURE_2D, lifeTextures[i]);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
    }

    private void drawSpriteVida(GL10 gl) {
        gl.glPushMatrix();

        // Cambia a proyección ortográfica para renderizar en 2D
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrthof(0, width, 0, height, -1, 1); // Proyección ortográfica basada en las dimensiones de la pantalla
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        // Selecciona la textura actual de vida
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, lifeTextures[5 - currentLife]); // El índice depende de la vida restante

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        // Ajusta los vértices a las dimensiones de la pantalla
        float spriteWidth = 2200;  // Anchura del sprite en píxeles
        float spriteHeight = 200; // Altura del sprite en píxeles
        float[] vertices = {
                0, height - spriteHeight, 0.0f,  // Top-left
                spriteWidth, height - spriteHeight, 0.0f,  // Top-right
                0, height, 0.0f,  // Bottom-left
                spriteWidth, height, 0.0f   // Bottom-right
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, spriteTexCoordsBuffer);

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);

        // Restaura la proyección anterior
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        gl.glPopMatrix();
    }


    // Load the texture for the background
    private void loadBackgroundTexture(GL10 gl, int resourceId) {
        // Carga la textura del fondo dada una ID de recurso
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        gl.glGenTextures(1, backgroundTexture, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, backgroundTexture[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }

    private void updateBackgroundTexture(GL10 gl, float deltaTime) {
        // Actualiza el temporizador
        backgroundChangeTimer += deltaTime;

        // Cambia el fondo cada 20 segundos
        if (backgroundChangeTimer >= 20.0f) {
            backgroundChangeTimer = 0.0f; // Reinicia el temporizador
            currentBackgroundIndex = (currentBackgroundIndex + 1) % backgroundResources.length; // Avanza al siguiente fondo
            loadBackgroundTexture(gl, backgroundResources[currentBackgroundIndex]); // Carga el nuevo fondo
        }
    }

    // Draw the background
    private void drawBackground(GL10 gl) {
        gl.glDisable(GL10.GL_DEPTH_TEST); // Disable depth test to ensure background is drawn first
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, backgroundTexture[0]);
    
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    
        // Ajustar las coordenadas de textura en función de la posición del avión
        float offsetX = planeX * 0.01f; // Ajusta el factor de escala según sea necesario
        float offsetY = -planeY * 0.01f; // Invertir el movimiento vertical
    
        float[] texCoords = {
                0.0f + offsetX, 1.0f + offsetY, // Bottom-left
                1.0f + offsetX, 1.0f + offsetY, // Bottom-right
                0.0f + offsetX, 0.0f + offsetY, // Top-left
                1.0f + offsetX, 0.0f + offsetY  // Top-right
        };
    
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        FloatBuffer texCoordBuffer = tbb.asFloatBuffer();
        texCoordBuffer.put(texCoords);
        texCoordBuffer.position(0);
    
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, backgroundVertices);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordBuffer);
    
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
    
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_DEPTH_TEST); // Re-enable depth test
    }


    // Call back when the surface is first created or re-created
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);  // Set color's clear-value to black
        gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
        gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL10.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // Dibuja solo píxeles con alfa > 0.1


        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
        gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
        gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance

        gl.glEnable(GL10.GL_LIGHTING);     // Enable lighting
        gl.glEnable(GL10.GL_NORMALIZE);    // Enable normalization
        configureFog(gl);
        object3D.loadTexture(gl, context, R.raw.avion_textura);
        spriteLifeBar.loadTexture(gl);
        asteroidManager.initializeAsteroids(gl); // Inicializar los asteroides
        bombManager.initializeBombs(gl);
        healthManager.initializeHealthObjects(gl);
        explosionManager.loadExplosionTexture(gl);
        setupSpriteVidaBuffers();
        loadLifeTextures(gl);

        light = new Light(gl, GL10.GL_LIGHT0);
        light.setPosition(new float[]{0.0f, 0f, 1, 0.0f});
        light.setAmbientColor(new float[]{0.1f, 0.1f, 0.1f});
        light.setDiffuseColor(new float[]{1, 1, 1});
        
        // Inicializamos la cámara en una posición relativa al avión
        camera = new Camera(
            gl,
            new Vector4(0.0f, -2.0f, 10.0f), // Ojo (posición inicial de la cámara)
            new Vector4(0.0f, -2.0f, 0.0f), // Centro (apuntando al avión)
            new Vector4(0.0f, 1.0f, 0.0f)   // Arriba (eje Y)
        );

        loadBackgroundTexture(gl, R.raw.background3);         // Load the background texture
    }

    // Call back after onSurfaceCreated() or whenever the window's size changes
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) height = 1; // Para prevenir división por cero
        this.width = width;
        this.height = height;

        float aspect = (float) width / height;
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 60, aspect, 0.1f, 100.f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }


    // Call back to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        float deltaTime = 0.016f; // Aproximadamente 60 FPS
        currentTime += deltaTime;

        updateBackgroundTexture(gl, deltaTime);

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        drawBackground(gl);       // Renderizar el fondo
        drawSpriteVida(gl);       // Renderizar el sprite de vida encima del fondo

        starManager.drawStars(gl); // Dibujar las estrellas
        ringManager.updateAndDrawRings(gl, deltaTime);
        light.setPosition(new float[]{this.getZ(), 0f, 5, 0});
        //GLU.gluLookAt(gl, 0, 0, 10, 0f, 0f, 0f, 0f, 1f, 0f);

        if (isFirstPersonView) {
            GLU.gluLookAt(gl,
                planeX, planeY, 0.0f,
                planeX, planeY, -5.0f,
                0.0f, 1.0f, 0.0f);
        } else {
            camera.update();
        }

        asteroidManager.updateAndDrawAsteroids(gl); // Dibujar asteroides
        bombManager.updateAndDrawBombs(gl);
        healthManager.updateAndDrawHealthObjects(gl);
        explosionManager.updateAndDrawExplosions(gl, deltaTime);
        checkCollisions();

        // Actualizar partículas
        particleManager.spawnParticles(planeX - 1.0f, planeY, 0.0f); // Ala izquierda
        particleManager.spawnParticles(planeX + 1.0f, planeY, 0.0f); // Ala derecha
        particleManager.updateParticles(deltaTime);
        particleManager.drawParticles(gl); // Dibujar partículas

        gl.glPushMatrix();
        gl.glTranslatef(planeX, planeY, 0);
        gl.glRotatef(planeTiltAngle, 0, 0, 1);
        gl.glRotatef(180, 0, 1, 0);
        gl.glScalef(0.5f, 0.5f, 0.5f);
        object3D.draw(gl);        // Dibujar el avión
        gl.glPopMatrix();

        smoothResetTilt();        // Suavizar el retorno de inclinación
        angle = (++angle) % 360;
    }

    public void toggleCameraView() {
        isFirstPersonView = !isFirstPersonView;
    }


    public void movePlane(float dx, float dy) {
        this.planeX += dx;
        this.planeY += dy;
        isKeyPressed = true; // Marca que una tecla está siendo presionada
    
        if (dx > 0) {
            planeTiltAngle = -15.0f; // Inclinación a la derecha
        } else if (dx < 0) {
            planeTiltAngle = 15.0f; // Inclinación a la izquierda
        }
    }

    public void onKeyReleased() {
        isKeyPressed = false; // Marca que no hay teclas presionadas
    }

    // Método de retorn a la posicio original
    private void smoothResetTilt() {
        if (!isKeyPressed) { // Solo aplica el retorno si no hay teclas presionadas
            if (planeTiltAngle > 0) {
                planeTiltAngle -= 2.0f; // Reducir la inclinación hacia la derecha
                if (planeTiltAngle < 0) planeTiltAngle = 0.0f; // Evitar valores negativos
            } else if (planeTiltAngle < 0) {
                planeTiltAngle += 2.0f; // Reducir la inclinación hacia la izquierda
                if (planeTiltAngle > 0) planeTiltAngle = 0.0f; // Evitar valores positivos
            }
        }
    }

    public float getHeight() {
        return this.height;
    }

    public float getWidth() {
        return this.width;
    }

    public float getZ() {
        return Z;
    }

    public void setZ(float z) {
        this.Z = z;
    }
    public void configureFog(GL10 gl) {
        float[] fogColor = {0.5f, 0.5f, 0.5f, 1.0f}; // Color de la neblina
        gl.glEnable(GL10.GL_FOG);                    // Habilita la neblina
        gl.glFogfv(GL10.GL_FOG_COLOR, fogColor, 0);  // Configura el color de la neblina
        gl.glFogf(GL10.GL_FOG_MODE, GL10.GL_LINEAR); // Tipo de neblina (lineal)
        gl.glFogf(GL10.GL_FOG_START, 10.0f);         // Distancia de inicio de la neblina
        gl.glFogf(GL10.GL_FOG_END, 50.0f);           // Distancia final de la neblina
    }

    private void checkCollisions() {
        if (currentTime - lastCollisionTime < collisionCooldown) {
            return; // Aún en periodo de inmunidad
        }

        for (int i = 0; i < asteroidManager.getNumAsteroids(); i++) {
            if (checkAsteroidCollision(asteroidManager.getAsteroidX(i), asteroidManager.getAsteroidY(i), asteroidManager.getAsteroidZ(i))) {
                reduceLife(1);
                explosionManager.addExplosion(
                        asteroidManager.getAsteroidX(i),
                        asteroidManager.getAsteroidY(i),
                        asteroidManager.getAsteroidZ(i)
                );
                lastCollisionTime = currentTime;
                return;
            }
        }

        for (int i = 0; i < bombManager.getNumBombs(); i++) {
            if (checkAsteroidCollision(bombManager.getBombX(i), bombManager.getBombY(i), bombManager.getBombZ(i))) {
                reduceLife(2);
                explosionManager.addExplosion(
                        bombManager.getBombX(i),
                        bombManager.getBombY(i),
                        bombManager.getBombZ(i)
                );
                lastCollisionTime = currentTime;
                return;
            }
        }

        for (int i = 0; i < healthManager.getNumHealthObjects(); i++) {
            if (checkAsteroidCollision(healthManager.getHealthX(i), healthManager.getHealthY(i), healthManager.getHealthZ(i))) {
                increaseLife(); // Incrementa la vida
                healthManager.getHealthZ(i); // Reinicia la posición del objeto de vida
                lastCollisionTime = currentTime;
                return;
            }
        }

        if (ringManager.checkCollision(planeX, planeY, 0)) {
            increaseLife();
            lastCollisionTime = currentTime;
        }
    }



    private void reduceLife(int damage) {
        currentLife -= damage;
        if (currentLife <= 0) {
            currentLife = 0;
            // Lógica para el fin del juego
        }
    }
    private void increaseLife() {
        if (currentLife < 5) { // Asegurarse de que la vida no exceda el límite
            currentLife++;
        }
    }

    private boolean checkAsteroidCollision(float asteroidX, float asteroidY, float asteroidZ) {
        float distanceX = asteroidX - planeX;
        float distanceY = asteroidY - planeY;
        float distanceZ = asteroidZ - 0;

        float collisionRadius = 3.0f;

        float distanceSquared = distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;

        return distanceSquared < collisionRadius * collisionRadius;
    }
}

