package learning.andriod.project3;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {

    private static final int ENEMY_BALL_INIT_FALLING_SPEED = 70;
    private float centerX;
    private float centerY;
    private float radius;
    private float fallingSpeed;
    private Paint paint;
    private int score;


    public Ball(float x, float y, float radius, Paint paint) {
        this.centerX = x;
        this.centerY = y;
        this.radius = radius;
        this.paint = paint;
        this.fallingSpeed = ENEMY_BALL_INIT_FALLING_SPEED;
        this.score = 1;
//        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    protected void drawOn(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    public void updateView(float x, float y) {
        this.centerX = x;
        this.centerY = y;
    }

    public void updateView(float radius) {
        this.radius = radius;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getFallingSpeed() {
        return fallingSpeed;
    }

    public void setFallingSpeed(float fallingSpeed) {
        this.fallingSpeed = fallingSpeed;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}