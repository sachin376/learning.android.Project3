package learning.andriod.project3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Ball extends View {
    private float centerX;
    private float centerY;
    private int r;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Ball(Context context, float x, float y, int r, Paint paint) {
        super(context);
        this.centerX = x;
        this.centerY = y;
        this.r = r;
        this.paint = paint;
//        paint.setColor(0xFFFF0000);
    }

    public Ball(Context context) {
        super(context);
    }

    public Ball(Context context, AttributeSet xmlAttributes) {
        super(context, xmlAttributes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        this.ball.draw(canvas);
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, r, paint);
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

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}