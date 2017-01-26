package com.colossal.zacharyhoffman.simplegame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ball {

    public BallColor color;
    private Paint paint;
    private float xPos;
    private float yPos;
    private float size;

    Ball(BallColor ballColor, int size){
        this.color = ballColor;
        this.xPos = 10;
        this.yPos = 10;
        this.size = size;
        this.color = ballColor;
        paint = new Paint();

        if(color == BallColor.RED)
            paint.setColor(Color.argb(255,  255, 0, 0));
        if(color == BallColor.GREEN)
            paint.setColor(Color.argb(255,  0, 255, 0));
        if(color == BallColor.BLUE)
            paint.setColor(Color.argb(255,  0, 0, 255));
    }

    public void setX(float xPos){
        this.xPos = xPos;
    }

    public void setY(float yPos){
        this.yPos = yPos;
    }

    public float getX(){
        return xPos;
    }

    public float getY(){
        return yPos;
    }

    public float getSize(){
        return size;
    }

    public Paint getPaint(){
        return paint;
    }

    public void draw(Canvas canvas){
        canvas.drawCircle(xPos, yPos, size, this.getPaint());
    }
}
