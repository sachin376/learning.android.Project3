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

    private GameView gameBoard;
    private static float gameBoardWidth;
    private GameStatus gameStatus = GameStatus.NEW;
    private static final int BALL_FALLING_HEIGHT = 1500;
    private static int scoreValue = 0;
    private static int livesValue = 3;

    private static Ball playerBall;
    private static final int PLAYER_BALL_RADIUS = 60;
    private static final int PLAYER_BALL_SPEED = 50;
    private static final int BASE_LINE_HEIGHT = BALL_FALLING_HEIGHT + PLAYER_BALL_RADIUS;

    private ArrayList<Ball> enemyBalls;
    private static final int ENEMY_BALL_INIT_RADIUS = 20;       // Initial radius of circle from where ball growing animation starts
    private static final int ENEMY_BALL_RADIUS_INCREMENT = 20;       // Ball growing increment value
    private static final float ENEMY_BALL_FALLING_SPEED_INCREMENT_PERCENTAGE = 1.25f;

    private static final Paint blackFill;
    private static final Paint redFill;
    private static final Paint bottomLinePaint;

    private GestureDetector gestureDetector;
    private TimerTask growBallSizeTask;
    private TimerTask enemyBallFallingTask;

    static {
        blackFill = new Paint();
        blackFill.setColor(Color.BLACK);
        redFill = new Paint();
        redFill.setColor(Color.RED);
        bottomLinePaint = new Paint();
        bottomLinePaint.setColor(Color.rgb(255, 152, 0));
        bottomLinePaint.setStrokeWidth(10f);
    }

    public interface UpdateGameStatListener {
        void updateScore(int score);

        void updateLives(int lives);
    }

    public GameView(Context context, AttributeSet xmlAttributes) {
        super(context, xmlAttributes);
        gestureDetector = new GestureDetector(context, this);
    }

    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        this.canvas = canvas;
        canvas.drawLine(0.0f, (float) BASE_LINE_HEIGHT, gameBoardWidth, (float) BASE_LINE_HEIGHT, bottomLinePaint);
        if (playerBall != null) {
            playerBall.drawOn(canvas);
        }
        if (enemyBalls != null) {
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
        playerBall = new Ball(gameBoardWidth / 2, BALL_FALLING_HEIGHT, PLAYER_BALL_RADIUS, blackFill);

        this.scoreValue = 0;
        this.livesValue = 3;
        updateGameStats();
    }

    public void startGame() {
        gameBoard = findViewById(R.id.game_board);
        fallEnemyBalls();
    }

    private void fallEnemyBalls() {
        Log.d("testing", "fall enemy balls");
        enemyBallFallingTask = new TimerTask() {
            @Override
            public void run() {
                Log.d("testing", "Ball is falling");
                if (enemyBalls != null) {
                    for (Ball enemyBall : enemyBalls) {
                        changeFallingBallPosition(enemyBall);
                    }
                }
            }
        };
        Timer everySecond = new Timer();
        everySecond.scheduleAtFixedRate(enemyBallFallingTask, 100, 500);
    }


    public void changeFallingBallPosition(Ball enemyBall) {
        float newY;
        newY = enemyBall.getCenterY() + enemyBall.getFallingSpeed();
        float ballEndPosition = BASE_LINE_HEIGHT - enemyBall.getRadius();

        if (checkCollision(enemyBall)) {
            Log.d("testing", "Collision Happened");
            livesValue--;
            newY = ballNextRound(enemyBall, 0);
            if (livesValue == 0) {
                gameStatus = GameStatus.END;
                try {
                    pause();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if ((newY - enemyBall.getFallingSpeed()) == ballEndPosition) {
            Log.d("testing", " inside lastPostion");
            newY = ballNextRound(enemyBall, enemyBall.getScore());
        }

        if (newY > BASE_LINE_HEIGHT - enemyBall.getRadius()) {
            Log.d("testing", " Out of boundary ");
            newY = ballEndPosition;
        }
        enemyBall.setCenterY(newY);
        invalidate();
    }

    private float ballNextRound(Ball enemyBall, int scoreToAdd) {
        float newY = 0 - enemyBall.getRadius();
        scoreValue += scoreToAdd;
        updateGameStats();
//        if(enemyBall.getFallingSpeed()*ENEMY_BALL_FALLING_SPEED_INCREMENT_PERCENTAGE > this.BASE_LINE_HEIGHT ){
//            Log.d("testing", " Max speed reached. Successfully completed the game");
//            gameBoard.setGameStatus(GameStatus.END);
//            endGame();
//        }

//        Setting Max limit of the ball to the 80% of the height of the game. After reaching this limit, speed won't increase.
        if ((enemyBall.getFallingSpeed() * ENEMY_BALL_FALLING_SPEED_INCREMENT_PERCENTAGE) < (0.8 * this.BASE_LINE_HEIGHT)) {
            enemyBall.setFallingSpeed(enemyBall.getFallingSpeed() * ENEMY_BALL_FALLING_SPEED_INCREMENT_PERCENTAGE);
        }
        enemyBall.setScore(enemyBall.getScore() + 1);
        return newY;

    }

    private boolean checkCollision(Ball enemyBall) {
        double distanceBetweenEnemyBallAndPlayerBall = calculateDistanceBetweenEnemyBallAndPlayerBall(enemyBall);
        return (distanceBetweenEnemyBallAndPlayerBall > (playerBall.getRadius() + enemyBall.getRadius())) ? false : true;
    }

    private double calculateDistanceBetweenEnemyBallAndPlayerBall(Ball enemyBall) {
        float deltaX = enemyBall.getCenterX() - playerBall.getCenterX();
        float deltaY = enemyBall.getCenterY() - playerBall.getCenterY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return distance;

    }

    public void endGame() {
        gameBoard = findViewById(R.id.game_board);
        gameBoard.setOnTouchListener(this);
        if (enemyBallFallingTask != null) {
            enemyBallFallingTask.cancel();
        }
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
        Ball enemyBall = new Ball(motionEvent.getX(), motionEvent.getY(), ENEMY_BALL_INIT_RADIUS, redFill);
        if (this.enemyBalls == null) {
            enemyBalls = new ArrayList<>();
        }
        this.enemyBalls.add(enemyBall);
        return enemyBall;
    }

    private void growBallSizeThread(MotionEvent event, final Ball ball) {
        Log.d("testing", "growBallSizeThread enemy balls");
        growBallSizeTask = new TimerTask() {

            int enemyBallRadius = ENEMY_BALL_INIT_RADIUS;

            @Override
            public void run() {
                Log.d("testing", "Ball is growing - enemyBallRadius " + enemyBallRadius);
                enemyBallRadius += ENEMY_BALL_RADIUS_INCREMENT;
                ball.updateView(enemyBallRadius);
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

    public void pause() throws InterruptedException {
        gameBoard.setGameStatus(GameStatus.PAUSED);
        if (enemyBallFallingTask != null) {
            enemyBallFallingTask.cancel();
        }
        enemyBallFallingTask = null;
    }


    public void resume() {
        gameBoard.setGameStatus(GameStatus.PLAYING);
        fallEnemyBalls();
    }

    private void updateGameStats() {
        UpdateGameStatListener listener = (UpdateGameStatListener) getContext();
        listener.updateScore(this.scoreValue);
        listener.updateLives(this.livesValue);
        invalidate();
    }
}