package learning.andriod.project3;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String LOG_TAG = "sk";
    private static final int PLAYER_BALL_HEIGHT = 1500;
    private static final int PLAYER_BALL_SPEED = 50;

    private float screenWidth;
    private float screenHeight;
    private static Paint blackFill;
    private static Paint redFill;

    private FrameLayout gameBoard;
    private TextView score;
    private TextView lives;
    private View pauseButton;
    private View newButton;
    private ArrayList<Ball> enemyBalls;
    private static Ball playerBall;


    private float gameBoardWidth;
    private float gameBoardHeight;

    private float playerBallCurrentX = 0;


    static {
        blackFill = new Paint();
        blackFill.setColor(Color.BLACK);
        redFill = new Paint();
        redFill.setColor(Color.RED);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        screenWidth = screenSize.x;
        screenHeight = screenSize.y;

        Log.i(LOG_TAG, "screenWidth" + screenWidth);
        Log.i(LOG_TAG, "screenHeight" + screenHeight);

//        gameBoard = findViewById(R.id.game_board);
        score = findViewById(R.id.score);
        lives = findViewById(R.id.lives);
        pauseButton = findViewById(R.id.pause);
        newButton = findViewById(R.id.newGame);


    }

    public void onNewGame(View v) {

        gameBoard = findViewById(R.id.game_board);
        gameBoard.setOnTouchListener(this);
        newButton.setOnTouchListener(this);
        pauseButton.setOnTouchListener(this);

        // todo : somehow if I use screenWidth, ball shows complete and if I use gameBoardWidth, balss shows half
        playerBall = new Ball(this, screenWidth/2, PLAYER_BALL_HEIGHT,40, blackFill);
//        playerBall = new Ball(this, 300, PLAYER_BALL_HEIGHT,40, blackFill);
        gameBoard.addView(playerBall);

        View enemyBall = new Ball(this, 200,200,100, redFill);
        gameBoard.addView(enemyBall);
//        enemyBall.setOnTouchListener(this);

        //        gameBoard.addView();
//        canvas.drawCircle(40, 30, 20, blackFill);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        gameBoard = findViewById(R.id.game_board);

        // We need to get frame width and height here because the UI has not been set on the screen in onCreate method.
        gameBoardWidth = gameBoard.getWidth();
        gameBoardHeight = gameBoard.getHeight();

        playerBallCurrentX = gameBoardWidth/2;

        Log.i(LOG_TAG, event.toString());
        Log.i(LOG_TAG, "view detail"+ v.toString());
        logTouchType(event);

        Log.i(LOG_TAG, "number of touches; " + event.getPointerCount());
        Log.i(LOG_TAG, "x; " + event.getX() + " y: " + event.getY());
        for (int k = 1; k < event.getPointerCount();k++ )
            Log.i(LOG_TAG, "x; " + event.getX(k) + " y: " + event.getY(k));

        return true;
    }

    private void logTouchType(MotionEvent event) {
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;

        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(event);
                Log.i(LOG_TAG, "down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(LOG_TAG, "move " + event.getHistorySize() );
                break;
            case MotionEvent.ACTION_UP:
                Log.i(LOG_TAG, "UP");
                break;
            default:
                Log.i(LOG_TAG,"other action " + event.getAction());
        }
    }

    private boolean handleActionDown(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        Log.i(LOG_TAG,"before ball x " + playerBall.getX());
        Log.i(LOG_TAG," before  ball getCenterX  " + playerBall.getCenterX());
//
        move(x);
        Log.i(LOG_TAG,"ball x " + playerBall.getX());
        Log.i(LOG_TAG,"ball getCenterX  " + playerBall.getCenterX());
//        Log.i(LOG_TAG,"playerBallCurrentX" + playerBallCurrentX);
        Log.i(LOG_TAG,"gameBoardWidth " + gameBoardWidth);
//        playerBall.setX(playerBallCurrentX);

        return true;
    }



    private void move(float x) {

        if(isRightScreenTouch(x)){
//            playerBallCurrentX += PLAYER_BALL_SPEED;
            playerBall.setX(playerBall.getX() + PLAYER_BALL_SPEED);
//            playerBall.setX(playerBall.getCenterX());
        }else{
//            playerBallCurrentX -= PLAYER_BALL_SPEED;
            playerBall.setX(playerBall.getX() - PLAYER_BALL_SPEED);
//            playerBall.setX(playerBall.getCenterX());
        }

        boundaryCheck();
    }

    private boolean isRightScreenTouch(float x) {
        return (x > screenWidth/2);
    }


    //        Boundary check condition for Player Ball
    private void boundaryCheck() {
        if(playerBall.getX() > gameBoardWidth/2){
            playerBall.setX(gameBoardWidth/2);
        }
        if(playerBall.getX() < -gameBoardWidth/2){
            playerBall.setX(-gameBoardWidth/2);
        }
    }
}
