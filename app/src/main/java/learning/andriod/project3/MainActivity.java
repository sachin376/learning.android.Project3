package learning.andriod.project3;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


enum GameStatus { NEW, PLAYING, PAUSED, END }

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, GestureDetector.OnGestureListener{

    private static final String LOG_TAG = "sk";
    private static final int PLAYER_BALL_HEIGHT = 1500;
    private static final int PLAYER_BALL_SPEED = 50;
    private static final String BUTTON_LABEL_NEW = "New";
    private static final String BUTTON_LABEL_START = "Start";
    private static final String BUTTON_LABEL_END = "End";
    private static final String BUTTON_LABEL_PAUSE = "Pause";
    private static final String BUTTON_LABEL_RESUME = "Resume";
    private GameStatus gameStatus = GameStatus.NEW;

    private static float screenWidth;
    private static float screenHeight;
    private static final Paint blackFill;
    private static final Paint redFill;

    private FrameLayout gameBoard;
    private static TextView score;
    private static TextView lives;
    private static Button pauseButton;
    private static Button newButton;
    private ArrayList<Ball> enemyBalls = new ArrayList<Ball>();
    private static Ball playerBall;

    private static int scoreValue = 0;
    private static int livesValue = 3;

    private GestureDetector gestureDetector;
    private TimerTask growBallSizeTask;


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

        pauseButton.setOnClickListener(this);
        newButton.setOnClickListener(this);

        gestureDetector = new GestureDetector(this, this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.pause:
                switch (pauseButton.getText().toString()) {
                    case BUTTON_LABEL_PAUSE:
                         gameStatus = GameStatus.PAUSED;
                         pauseButton.setText(BUTTON_LABEL_RESUME);
                        break;
                    case BUTTON_LABEL_RESUME:
                         gameStatus = GameStatus.PLAYING;
                         pauseButton.setText(BUTTON_LABEL_PAUSE);
                        break;
                }
                break;
            case R.id.newGame:
                switch (newButton.getText().toString()) {
                    case BUTTON_LABEL_NEW:
                         gameStatus = GameStatus.NEW;
                         newButton.setText(BUTTON_LABEL_START);
                         newGame();
                        break;
                    case BUTTON_LABEL_START:
                         gameStatus = GameStatus.PLAYING;
                         newButton.setText(BUTTON_LABEL_END);
                         startGame();
                        break;
                    case BUTTON_LABEL_END:
                         gameStatus = GameStatus.END;
                         newButton.setText(BUTTON_LABEL_NEW);
                         endGame();
                        break;
                }
                break;
        }
    }


    private void newGame() {

        gameBoard = findViewById(R.id.game_board);
//        gameBoard.setOnLongClickListener(this);
        gameBoard.setOnTouchListener(this);

        // todo : somehow if I use screenWidth, ball shows complete and if I use gameBoardWidth, balss shows half
        playerBall = new Ball(this, screenWidth/2, PLAYER_BALL_HEIGHT,40, blackFill);
//        playerBall = new Ball(this, 300, PLAYER_BALL_HEIGHT,40, blackFill);
//        playerBall.setX(screenWidth/2);
//        playerBall.setY(PLAYER_BALL_HEIGHT);
        gameBoard.addView(playerBall);

//        View enemyBall = new Ball(this, 200,200,100, redFill);
//        gameBoard.addView(enemyBall);

    }

    private void startGame() {
        gameBoard = findViewById(R.id.game_board);
        updateGameStats(0,3);
    }

    private void updateGameStats(int scoreValue, int livesValue) {
        this.scoreValue = scoreValue;
        this.livesValue = livesValue;
        score.setText(String.valueOf(scoreValue));
        lives.setText(String.valueOf(livesValue));
    }

    private void endGame() {

        gameBoard = findViewById(R.id.game_board);
        gameBoard.setOnTouchListener(this);
        gameBoard.removeAllViews();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        gestureDetector.onTouchEvent(event);
        handleAction(event);
        return true;
    }

    private void handleAction(MotionEvent event) {

        if(gameStatus != GameStatus.NEW){
            return;
        }

        Ball enemyBall = createEnemyBalls(event);
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;


        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                growBallSizeThread(event, enemyBall);
                Log.i(LOG_TAG, "down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(LOG_TAG, "changePosition " + event.getHistorySize() );
                break;
            case MotionEvent.ACTION_UP:
                Log.i(LOG_TAG, "UP");
                stopBallSizeThread(event, enemyBall);
                break;
            default:
                Log.i(LOG_TAG,"other action " + event.getAction());
        }
    }

    private boolean moveBall(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        Log.i(LOG_TAG,"before ball x " + playerBall.getX());
        Log.i(LOG_TAG," before  ball getCenterX  " + playerBall.getCenterX());

        int location[] = new int[2];
        playerBall.getLocationOnScreen(location);

        Log.i(LOG_TAG," before  ball getCenterX getLocationOnScreen " + location[0]);
        Log.i(LOG_TAG," before  ball getCenterY getLocationOnScreen " + location[1]);

//
        changePosition(x);
        Log.i(LOG_TAG,"ball x " + playerBall.getX());
        Log.i(LOG_TAG,"ball getCenterX  " + playerBall.getCenterX());
//        Log.i(LOG_TAG,"playerBallCurrentX" + playerBallCurrentX);
        Log.i(LOG_TAG,"gameBoardWidth " + gameBoardWidth);
//        playerBall.setX(playerBallCurrentX);

        playerBall.getLocationOnScreen(location);

        Log.i(LOG_TAG," after  ball getCenterX getLocationOnScreen " + location[0]);
        Log.i(LOG_TAG," after  ball getCenterY getLocationOnScreen " + location[1]);


        return true;
    }



    private void changePosition(float x) {

        if(isRightScreenTouch(x)){
//            playerBallCurrentX += PLAYER_BALL_SPEED;
            playerBall.setX(playerBall.getX() + PLAYER_BALL_SPEED);
//            playerBall.setX(playerBall.getCenterX());
//            playerBall.updateView(playerBall.getX() + PLAYER_BALL_SPEED, playerBall.getY());
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


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Log.d(LOG_TAG, "onDown: called.");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        Log.d(LOG_TAG, "onShowPress: called.");

    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(LOG_TAG, "onSingleTapUp: called.");

        if(gameStatus != GameStatus.PLAYING){
            return true;
        }

//        gameBoard = findViewById(R.id.game_board);

        // We need to get frame width and height here because the UI has not been set on the screen in onCreate method.
        gameBoardWidth = gameBoard.getWidth();
        gameBoardHeight = gameBoard.getHeight();

        playerBallCurrentX = gameBoardWidth/2;

        Log.i(LOG_TAG, event.toString());
//        Log.i(LOG_TAG, "view detail"+ v.toString());
//        handleAction(event);
        moveBall(event);

        Log.i(LOG_TAG, "number of touches; " + event.getPointerCount());
        Log.i(LOG_TAG, "x; " + event.getX() + " y: " + event.getY());
        for (int k = 1; k < event.getPointerCount();k++ )
            Log.i(LOG_TAG, "x; " + event.getX(k) + " y: " + event.getY(k));

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Log.d(LOG_TAG, "onScroll: called.");
        return false;
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Log.d(LOG_TAG, "onFling: called.");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Log.d(LOG_TAG, "onLongPress: called.");
    }

    private Ball createEnemyBalls(MotionEvent motionEvent) {
        Log.d("testing", "Creating enemy balls");
//        View enemyBall = new Ball(this, 200,200,100, redFill);
        Ball enemyBall = new Ball(this, motionEvent.getX(),motionEvent.getY(),100, redFill);
        gameBoard.addView(enemyBall);
//        enemyBall.setOnTouchListener(this);
//        growCircle(enemyBall);

        this.enemyBalls.add(enemyBall);
        return enemyBall;
    }


    public void growCircle(final Ball ball) {
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                if (i < 70) { // Please change '70' according to how long you want to go
                    runOnUiThread(new Runnable() {
                        int baseRadius=20; // base radius is basic radius of circle from which to start animation
                        @Override
                        public void run() {
                            ball.updateView(baseRadius++);
                        }
                    });
//                } else {
//                    i = 0;
//                }
            }
        }, 0, 500); // change '500' to milliseconds for how frequent you want to update radius
    }

    private void growBallSizeThread(MotionEvent event, final Ball ball) {
        Log.d("testing", "growBallSizeThread enemy balls");

        growBallSizeTask = new TimerTask() {
            int baseRadius=10; // base radius is basic radius of circle from which to start animation
            @Override
            public void run() {
                Log.d("testing","Ball is growing ");
                ball.updateView(baseRadius++);
            }
        };
        Timer everySecond = new Timer();
        everySecond.scheduleAtFixedRate(growBallSizeTask, 100, 500);
    }


    private void stopBallSizeThread(MotionEvent event, Ball ball) {
        Log.d("testing", "stopBallSizeThread enemy balls");

        this.growBallSizeTask.cancel();
    }
}
