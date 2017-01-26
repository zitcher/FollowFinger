package com.colossal.zacharyhoffman.simplegame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/**
 * My first go at android studio. Made a ball that follows pointer!
 */


public class SimpleGameEngine extends Activity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        setContentView(gameView);

    }

    class GameView extends SurfaceView implements Runnable {

        // This is our thread
        Thread gameThread = null;

        //allows us to lock and unlock canvas
        private  SurfaceHolder ourHolder;

        // A boolean holding whether the game is running
        volatile boolean playing;

        // A Canvas and a Paint object
        private Canvas canvas;
        private Paint paint;

        // This variable tracks the game frame rate
        private long fps;

        // Current time since the start
        private long timeThisFrame;

        // Our Ball that will follow the cursor
        private Ball redBall = new Ball(BallColor.RED, 100);

        //movement variables
        boolean fingerDown = false;
        private float fingerX;
        private float fingerY;
        private final static float walkSpeedPerSecond = 150;

        public GameView(Context context) {
            super(context);

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

        }

        @Override
        public void run() {
            while (playing) {

                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();

                // Update the frame
                update();

                // Draw the frame
                draw();

                // Calculate the fps this frame
                // We can then use the result to
                // time animations and more.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        private float pythagorean(float x1, float y1, float x2, float y2) {
            return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        }

        private void moveBall(){
            float distToFinger = pythagorean(
                    redBall.getX(), redBall.getY(), this.fingerX, this.fingerY);

            float xUnitVector = (this.fingerX - redBall.getX()) / distToFinger;
            float yUnitVector = (this.fingerY - redBall.getY()) / distToFinger;
            float accelration = (float) Math.pow(distToFinger, .5);
            float moveSpeed = (walkSpeedPerSecond * accelration/ fps);

            //move x pos towards finger
            redBall.setX(redBall.getX() + xUnitVector * moveSpeed);

            //move y pos towards finger
            redBall.setY(redBall.getY() + yUnitVector * moveSpeed);
        }

        public void update() {
            // If ball
            if(fingerDown){
                moveBall();
            }
        }

        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                // Make the drawing surface our canvas object
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                canvas.drawColor(Color.argb(255,  0, 0, 0));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255,  255, 255, 255));

                // Make the text a bit bigger
                paint.setTextSize(45);

                // Display the current fps on the screen
                canvas.drawText("FPS:" + fps, 20, 40, paint);

                // Draw the circle
                redBall.draw(canvas);

                // Draw everything to the screen
                // and unlock the drawing surface
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        // If SimpleGameEngine Activity is paused/stopped
        // shutdown our thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        // If SimpleGameEngine Activity is started then
        // start our thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:

                    // Set fingerDown to true so the ball moves and get the finger's xy
                    fingerDown = true;
                    fingerX = motionEvent.getX();
                    fingerY = motionEvent.getY();

                    break;

                case MotionEvent.ACTION_MOVE:
                    fingerX = motionEvent.getX();
                    fingerY = motionEvent.getY();

                    break;

                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:

                    // Set fingerDown so ball does not move
                    fingerDown = false;

                    break;
            }
            return true;
        }
    }
    // More SimpleGameEngine methods will go here

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        gameView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        gameView.pause();
    }
}
