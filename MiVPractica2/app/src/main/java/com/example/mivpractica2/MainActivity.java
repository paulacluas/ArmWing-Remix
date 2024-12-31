package com.example.mivpractica2;

import android.app.Activity;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.security.Key;

public class MainActivity extends Activity {

    private GLSurfaceView glView;   // Use GLSurfaceView
    private MyGLRenderer myGLRenderer;
    private MediaPlayer mediaPlayer;


    // Call back when the activity is started, to initialize the view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glView = new GLSurfaceView(this);           // Allocate a GLSurfaceView
        glView.setRenderer(myGLRenderer = new MyGLRenderer(this)); // Use a custom renderer
        this.setContentView(glView);                // This activity sets to GLSurfaceView
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
    }

    // Call back when the activity is going into the background
    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    // Call back after onPause()
    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        if (y > myGLRenderer.getHeight() / 2) {
            myGLRenderer.setZ(myGLRenderer.getZ() + 0.1f);
        } else {
            myGLRenderer.setZ(myGLRenderer.getZ() - 0.1f);
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                myGLRenderer.movePlane(0, 0.2f);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                myGLRenderer.movePlane(0, -0.2f);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                myGLRenderer.movePlane(-0.2f, 0);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                myGLRenderer.movePlane(0.5f, 0);
                break;
            case KeyEvent.KEYCODE_SPACE:
                myGLRenderer.toggleCameraView();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            myGLRenderer.onKeyReleased(); // Indica al renderizador que se soltó la tecla
        }
        return super.onKeyUp(keyCode, event);
    }
}