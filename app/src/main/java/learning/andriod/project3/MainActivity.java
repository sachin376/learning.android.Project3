package learning.andriod.project3;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


enum GameStatus { NEW, PLAYING, PAUSED, END }

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GameView.UpdateGameStatListener{

    private static final String LOG_TAG = "sk";
    private static float screenWidth;
    private static float screenHeight;

    private GameView gameBoard;
    private static TextView score;
    private static TextView lives;
    private static Button pauseButton;
    private static Button newButton;

    private static final String BUTTON_LABEL_NEW = "New";
    private static final String BUTTON_LABEL_START = "Start";
    private static final String BUTTON_LABEL_END = "End";
    private static final String BUTTON_LABEL_PAUSE = "Pause";
    private static final String BUTTON_LABEL_RESUME = "Resume";


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

        gameBoard = findViewById(R.id.game_board);
        score = findViewById(R.id.score);
        lives = findViewById(R.id.lives);
        pauseButton = findViewById(R.id.pause);
        newButton = findViewById(R.id.newGame);

        pauseButton.setOnClickListener(this);
        newButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View button) {

        switch (button.getId()){
            case R.id.pause:
                switch (pauseButton.getText().toString()) {
                    case BUTTON_LABEL_PAUSE:
                        if(gameBoard.getGameStatus() == GameStatus.PLAYING){
                            pauseButton.setText(BUTTON_LABEL_RESUME);
                            gameBoard.setGameStatus(GameStatus.PAUSED);
                        }
                        try {
                            gameBoard.pause();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case BUTTON_LABEL_RESUME:
                        if(gameBoard.getGameStatus() == GameStatus.PAUSED){
                            pauseButton.setText(BUTTON_LABEL_PAUSE);
                            gameBoard.setGameStatus(GameStatus.PLAYING);
                            gameBoard.resume();
                        }
                        break;
                }
                break;
            case R.id.newGame:
                switch (newButton.getText().toString()) {
                    case BUTTON_LABEL_NEW:
                        pauseButton.setText(BUTTON_LABEL_PAUSE);
                        newButton.setText(BUTTON_LABEL_START);
                        gameBoard.setGameStatus(GameStatus.NEW);
                        gameBoard.newGame();
                        break;
                    case BUTTON_LABEL_START:
                        pauseButton.setText(BUTTON_LABEL_PAUSE);
                        newButton.setText(BUTTON_LABEL_END);
                        gameBoard.setGameStatus(GameStatus.PLAYING);
                        gameBoard.startGame();
                        break;
                    case BUTTON_LABEL_END:
                        pauseButton.setText(BUTTON_LABEL_PAUSE);
                        newButton.setText(BUTTON_LABEL_NEW);
                        gameBoard.setGameStatus(GameStatus.END);
                        gameBoard.endGame();
                        break;
                }
                break;
        }
    }

    @Override
    public void updateScore(int score) {
        this.score.setText(String.valueOf(score));

    }

    @Override
    public void updateLives(int lives) {
        this.lives.setText(String.valueOf(lives));

    }
}
