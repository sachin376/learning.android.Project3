package learning.andriod.project3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class GameView extends View implements GestureDetector.OnGestureListener, View.OnTouchListener {

    private static final String LOG_TAG = "sk";

    private static final int PLAYER_BALL_HEIGHT = 1500;
    private static final int PLAYER_BALL_SPEED = 50;
    private static final Paint blackFill;
    private static final Paint redFill;
    private static float gameBoardWidth;
    private static float gameBoardHeight;

    private GameStatus gameStatus = GameStatus.NEW;
    private GameView gameBoard;
    private static Ball playerBall;
    private ArrayList<Ball> enemyBalls;

    private static int scoreValue = 0;
    private static int livesValue = 3;

    private GestureDetector gestureDetector;
    private TimerTask growBallSizeTask;
//    private Canvas canvas;

    static {
        blackFill = new Paint();
        blackFill.setColor(Color.BLACK);
        redFill = new Paint();
        redFill.setColor(Color.RED);
    }

    public GameView(Context context, AttributeSet xmlAttributes) {
        super(context, xmlAttributes);
        gestureDetector = new GestureDetector(context, this);
    }

    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        this.canvas = canvas;
        if (playerBall != null) {
            playerBall.drawOn(canvas);
        }
        if(enemyBalls != null){
            for (Ball enemyBall : enemyBalls) {
                enemyBall.drawOn(canvas);
            }
        }
        invalidate();
    }

    public void newGame() {
        gameBoard = findViewById(R.id.game_board);
        gameBoard.setOnTouchListener(this);

        gameBoardWidth = gameBoard.getWidth();
        gameBoardHeight = gameBoard.getHeight();
        playerBall = new Ball(gameBoardWidth / 2, PLAYER_BALL_HEIGHT, 40, blackFill);
    }

    public void startGame() {
        gameBoard = findViewById(R.id.game_board);
        updateGameStats(0, 3);
    }


    public void updateGameStats(int scoreValue, int livesValue) {
        this.scoreValue = scoreValue;
        this.livesValue = livesValue;

        // todo : need to update labels
//        score.setText(String.valueOf(scoreValue));
//        lives.setText(String.valueOf(livesValue));
    }

    public void endGame() {

        gameBoard = findViewById(R.id.game_board);
        gameBoard.setOnTouchListener(this);
        gameBoard.clearAnimation();
        this.enemyBalls = null;
//        gameBoard.removeAllViews();

    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {

        Log.d(LOG_TAG, "onSingleTapUp: called.");
        if (this.gameStatus != GameStatus.PLAYING) {
            return true;
        }

        Log.i(LOG_TAG, event.toString());
        moveBall(event);

        Log.i(LOG_TAG, "number of touches; " + event.getPointerCount());
        Log.i(LOG_TAG, "x; " + event.getX() + " y: " + event.getY());
        for (int k = 1; k < event.getPointerCount(); k++)
            Log.i(LOG_TAG, "x; " + event.getX(k) + " y: " + event.getY(k));

        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        gestureDetector.onTouchEvent(event);
        handleAction(event);
        return true;
    }

    private void handleAction(MotionEvent event) {

        Ball enemyBall = null;
        if (this.gameStatus != GameStatus.NEW) {
            return;
        }

        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                enemyBall = createEnemyBalls(event);
                growBallSizeThread(event, enemyBall);
                Log.i(LOG_TAG, "down");
                Log.d("testing", "Ball output from growing method " + enemyBall.toString());
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(LOG_TAG, "changePosition " + event.getHistorySize());
                break;
            case MotionEvent.ACTION_UP:
                Log.i(LOG_TAG, "UP");
                stopBallSizeThread(event);
                break;
            default:
                Log.i(LOG_TAG, "other action " + event.getAction());
        }
        invalidate();
    }

    private boolean moveBall(MotionEvent event) {

//        Log.i(LOG_TAG,"before ball x " + playerBall.getX());
        Log.i(LOG_TAG, " before  ball getCenterX  " + playerBall.getCenterX());
        changePosition(event.getX());
        Log.i(LOG_TAG, "after ball getCenterX  " + playerBall.getCenterX());
        Log.i(LOG_TAG, "gameBoardWidth " + gameBoardWidth);
        return true;
    }


    private void changePosition(float x) {

        if (isRightScreenTouch(x)) {
            playerBall.setCenterX(playerBall.getCenterX() + PLAYER_BALL_SPEED);
        } else {
            playerBall.setCenterX(playerBall.getCenterX() - PLAYER_BALL_SPEED);
        }
        boundaryCheck();
        invalidate();
    }

    private boolean isRightScreenTouch(float x) {
        return (x > gameBoardWidth / 2);
    }


    //        Boundary check condition for Player Ball
    private void boundaryCheck() {
        if (playerBall.getCenterX() > gameBoardWidth - playerBall.getRadius()) {
            playerBall.setCenterX(gameBoardWidth - playerBall.getRadius());
        }
        if (playerBall.getCenterX() < 0 + playerBall.getRadius()) {
            playerBall.setCenterX(0 + playerBall.getRadius());
        }
    }

    private Ball createEnemyBalls(MotionEvent motionEvent) {
        Log.d("testing", "Creating enemy balls");
        Ball enemyBall = new Ball(motionEvent.getX(), motionEvent.getY(), 20, redFill);
        if(this.enemyBalls == null){
            enemyBalls = new ArrayList<>();
        }
        this.enemyBalls.add(enemyBall);
//        invalidate();
        return enemyBall;
    }

    private void growBallSizeThread(MotionEvent event, final Ball ball) {
        Log.d("testing", "growBallSizeThread enemy balls");
        growBallSizeTask = new TimerTask() {
            int baseRadius = 20; // This is the initial radius of circle from where growing animation starts

            @Override
            public void run() {
                Log.d("testing", "Ball is growing - baseRadius " + baseRadius);
                baseRadius += 20;
                ball.updateView(baseRadius);
                invalidate();
            }
        };
        Timer everySecond = new Timer();
        everySecond.scheduleAtFixedRate(growBallSizeTask, 100, 500);
    }


    private void stopBallSizeThread(MotionEvent event) {
        Log.d("testing", "stopBallSizeThread enemy balls");
        this.growBallSizeTask.cancel();
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

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }
}