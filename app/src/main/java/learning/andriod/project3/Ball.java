package learning.andriod.project3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class Ball extends View {
    private float centerX;
    private float centerY;
    private float radius;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//    private SurfaceHolder surfaceHolder;

    public Ball(Context context, float x, float y, int radius, Paint paint) {
        super(context);
//        surfaceHolder = getHolder();
        this.centerX = x;
        this.centerY = y;
        this.radius = radius;
        this.paint = paint;
//        paint.setColor(0xFFFF0000);
    }

    public Ball(Context context) {
        super(context);
//        surfaceHolder = getHolder();
    }

    public Ball(Context context, AttributeSet xmlAttributes) {
        super(context, xmlAttributes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        this.ball.draw(canvas);
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, radius, paint);
//        invalidate();
    }

    public void updateView(float radius) {
        this.radius = radius;
        invalidate();
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if(event.getAction() == MotionEvent.ACTION_DOWN) {
//            if (surfaceHolder.getSurface().isValid()) {
//                Canvas canvas = surfaceHolder.lockCanvas();
//                canvas.drawColor(Color.RED);
//                canvas.drawCircle(event.getX(), event.getY(), 50, paint);
//                surfaceHolder.unlockCanvasAndPost(canvas);
//            }
//        }
//        return false;
//    }

    public void updateView(float x, float y) {
        this.centerX = x;
        this.centerY = y;
        invalidate();
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
}