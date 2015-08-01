
package karthick.ram.cg;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
public class GameView extends View {

    private Marble gameMarble;
    private Maze gameMaze;
    private Activity gameActivity;
    private Canvas gameCanvas;
    private Paint canvasPaint;
    private Typeface gameFont = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
    private int fontTextPadding = 10;
    private int gameHudTextY = 440;
    private final static int BEFORE_BEGIN_STATE = -1;
    private final static int GAME_START = 0;
    private final static int GAME_RUNNING = 1;
    private final static int GAME_OVER = 2;
    private final static int GAME_COMPLETE = 3;
    private final static int GAME_LANDSCAPE = 4;
    private static int gameCurrentState = BEFORE_BEGIN_STATE;
    private final static int Game_LIVES = 0;
    private final static int Game_LEVEL = 1;
    private final static int Game_TIME = 2;
    private final static int Game_TAP_SCREEN = 3;
    private final static int Game_GAME_COMPLETE = 4;
    private final static int Game_GAME_OVER = 5;
    private final static int Game_TOTAL_TIME = 6;
    private final static int Game_GAME_OVER_MSG_A = 7;
    private final static int Game_GAME_OVER_MSG_B = 8;
    private final static int Game_RESTART = 9;
    private final static int Game_LANDSCAPE_MODE = 10;
    private static String gameStrings[];
    private boolean gameWarning = false;
    private int gameCanvasWidth = 0;
    private int gameCanvasHeight = 0;
    private int gameCanvasHalfWidth = 0;
    private int gameCanvasHalfHeight = 0;
    private boolean orientationPortrait;
    private int gamelevel = 1;
    private long gameTotalTime = 0;
    private long levelStartTime = 0;
    private long gameEndTime = 0;
    private SensorManager gameSensorManager;
    private float gameAccelX = 0;
    private float gameAccelY = 0;
    private float gameAccelZ = 0; 
    private float gameSensorBuffer = 0;
    private final SensorListener gameSensorAccelerometer = new SensorListener() {
        public void onSensorChanged(int sensor, float[] values) {
            gameAccelX = values[0];
            gameAccelY = values[1];
            gameAccelZ = values[2];
        }
        public void onAccuracyChanged(int sensor, int accuracy) {
        }
    };

    public GameView(Context context, Activity activity) {
        super(context);

        gameActivity = activity;
        canvasPaint = new Paint();
        canvasPaint.setTextSize(14);
        canvasPaint.setTypeface(gameFont);
        canvasPaint.setAntiAlias(true);
        gameSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        gameSensorManager.registerListener(gameSensorAccelerometer, SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);
        gameMaze = new Maze(gameActivity);
        gameMarble = new Marble(this);
        gameStrings = getResources().getStringArray(R.array.gameStrings);
        changeState(GAME_START);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        gameCanvasWidth = w;
        gameCanvasHeight = h;
        gameCanvasHalfWidth = w / 2;
        gameCanvasHalfHeight = h / 2;
        if (gameCanvasHeight > gameCanvasWidth)
            orientationPortrait = true;
        else {
            orientationPortrait = false;
            changeState(GAME_LANDSCAPE);
        }
    }
    public void gameOnEachTick() {
        switch (gameCurrentState) {
        case GAME_START:
            startNewGame();
            changeState(GAME_RUNNING);

        case GAME_RUNNING:
            if (!gameWarning)
                updateMarblePosition();
            break;
        }
        invalidate();
    }
    public void startNewGame() {
        gameMarble.setLives(5);
        gameTotalTime = 0;
        gamelevel = 0;
        startLevel();
    }
    public void startLevel() {
        if (gamelevel < gameMaze.MAX_LEVELS) {
            
            gameWarning = true;
            gamelevel++;
            gameMaze.load(gameActivity, gamelevel);
            gameMarble.init();
        } else {
            
            changeState(GAME_COMPLETE);
        }
    }
    public void updateMarblePosition() {
        if (gameAccelX > gameSensorBuffer || gameAccelX < -gameSensorBuffer)
            gameMarble.updatePositionX(gameAccelX);
        if (gameAccelY > gameSensorBuffer || gameAccelY < -gameSensorBuffer)
            gameMarble.updatePositionY(gameAccelY);
        if (gameMaze.getCellType(gameMarble.getX(), gameMarble.getY()) == gameMaze.VOID_TILE) {
            if (gameMarble.getLives() > 0) {
                gameMarble.marbleDies();
                gameMarble.init();
                gameWarning = true;
            } else {
                gameEndTime = System.currentTimeMillis();
                gameTotalTime += gameEndTime - levelStartTime;
                changeState(GAME_OVER);
            }

        } else if (gameMaze.getCellType(gameMarble.getX(), gameMarble.getY()) == gameMaze.EXIT_TILE) {
            gameEndTime = System.currentTimeMillis();
            gameTotalTime += gameEndTime - levelStartTime;
            startLevel();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (gameCurrentState == GAME_OVER || gameCurrentState == GAME_COMPLETE) {
                gameCurrentState = GAME_START;
            } else if (gameCurrentState == GAME_RUNNING) {
                gameWarning = false;
                levelStartTime = System.currentTimeMillis();
            }
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            cleanUp();

        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        gameCanvas = canvas;
       canvasPaint.setColor(Color.BLACK);
       gameCanvas.drawRect(0, 0, gameCanvasWidth, gameCanvasHeight, canvasPaint);
        switch (gameCurrentState) {
        case GAME_RUNNING:
            gameMaze.draw(gameCanvas, canvasPaint);
            gameMarble.draw(gameCanvas, canvasPaint);
            drawMesseges();
            break;

        case GAME_OVER:
            drawGameOver();
            break;

        case GAME_COMPLETE:
            drawGameComplete();
            break;

        case GAME_LANDSCAPE:
            drawLandscapeMode();
            break;
        }

        gameOnEachTick();
    }
    public void drawMesseges() {
        canvasPaint.setColor(Color.GREEN);
        canvasPaint.setTextAlign(Paint.Align.LEFT);
        gameCanvas.drawText(gameStrings[Game_TIME] + ": " + (gameTotalTime / 1000), fontTextPadding, gameHudTextY,
                canvasPaint);
        canvasPaint.setTextAlign(Paint.Align.CENTER);
        gameCanvas.drawText(gameStrings[Game_LEVEL] + ": " + gamelevel, gameCanvasHalfWidth, gameHudTextY, canvasPaint);
        canvasPaint.setTextAlign(Paint.Align.RIGHT);
        gameCanvas.drawText(gameStrings[Game_LIVES] + ": " + gameMarble.getLives(), gameCanvasWidth - fontTextPadding,
                gameHudTextY, canvasPaint);
        if (gameWarning) {
            canvasPaint.setColor(Color.BLUE);
            gameCanvas
                    .drawRect(0, gameCanvasHalfHeight - 15, gameCanvasWidth, gameCanvasHalfHeight + 5,
                            canvasPaint);
            canvasPaint.setColor(Color.WHITE);
            canvasPaint.setTextAlign(Paint.Align.CENTER);
            gameCanvas.drawText(gameStrings[Game_TAP_SCREEN], gameCanvasHalfWidth, gameCanvasHalfHeight, canvasPaint);
        }
    }
    public void drawGameOver() {
        canvasPaint.setColor(Color.WHITE);
        canvasPaint.setTextAlign(Paint.Align.CENTER);

        gameCanvas.drawText(gameStrings[Game_GAME_OVER], gameCanvasHalfWidth, gameCanvasHalfHeight, canvasPaint);
        gameCanvas.drawText(gameStrings[Game_TOTAL_TIME] + ": " + (gameTotalTime / 1000) + "s",
                gameCanvasHalfWidth, gameCanvasHalfHeight + canvasPaint.getFontSpacing(), canvasPaint);
        gameCanvas.drawText(gameStrings[Game_GAME_OVER_MSG_A] + " " + (gamelevel - 1) + " "
                + gameStrings[Game_GAME_OVER_MSG_B], gameCanvasHalfWidth, gameCanvasHalfHeight
                + (canvasPaint.getFontSpacing() * 2), canvasPaint);
        
        gameCanvas.drawText(gameStrings[Game_RESTART], gameCanvasHalfWidth, gameCanvasHeight
                - (canvasPaint.getFontSpacing() * 3), canvasPaint);
        gameCanvas.translate(0, 50);
    }
    public void drawGameComplete() {
        canvasPaint.setColor(Color.WHITE);
        canvasPaint.setTextAlign(Paint.Align.CENTER);
        gameCanvas.drawText(gameStrings[GAME_COMPLETE], gameCanvasHalfWidth, gameCanvasHalfHeight, canvasPaint);
        gameCanvas.drawText(gameStrings[Game_TOTAL_TIME] + ": " + (gameTotalTime / 100) + "s",
                gameCanvasHalfWidth, gameCanvasHalfHeight + canvasPaint.getFontSpacing(), canvasPaint);
        gameCanvas.drawText(gameStrings[Game_RESTART], gameCanvasHalfWidth, gameCanvasHeight
                - (canvasPaint.getFontSpacing() * 3), canvasPaint);
    }
    public void drawLandscapeMode() {
        canvasPaint.setColor(Color.WHITE);
        canvasPaint.setTextAlign(Paint.Align.CENTER);
        gameCanvas.drawRect(0, 0, gameCanvasWidth, gameCanvasHeight, canvasPaint);
		canvasPaint.setColor(Color.BLACK);
		gameCanvas.drawText(gameStrings[Game_LANDSCAPE_MODE], gameCanvasHalfWidth,
				gameCanvasHalfHeight, canvasPaint);
	}

	public void changeState(int newState) {
		gameCurrentState = newState;
	}

	public void registerListener() {
		gameSensorManager.registerListener(gameSensorAccelerometer,
				SensorManager.SENSOR_ACCELEROMETER,
				SensorManager.SENSOR_DELAY_GAME);
	}

	public void unregisterListener() {
		gameSensorManager.unregisterListener(gameSensorAccelerometer);
	}

	public void cleanUp() {
		gameMarble = null;
		gameMaze = null;
		gameStrings = null;
		unregisterListener();
		gameActivity.finish();
	}
}