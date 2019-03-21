package learning.andriod.project3;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {
    private float centerX;
    private float centerY;
    private float radius;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Ball(float x, float y, float radius, Paint paint) {
        this.centerX = x;
        this.centerY = y;
        this.radius = radius;
        this.paint = paint;
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
}