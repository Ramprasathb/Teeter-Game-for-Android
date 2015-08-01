package karthick.ram.cg;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class Maze {

	private final static int TILE_SIZE = 16;
	private final static int MAZE_COLUMNS = 20;
	private final static int MAZE_ROWS = 26;
	public final static int PATH_TILE = 0;
	public final static int VOID_TILE = 1;
	public final static int EXIT_TILE = 2;
	private final static int VOID_COLOR = Color.LTGRAY;
	private static int[] MazeArray;
	public final static int MAX_LEVELS = 10;
	private Rect mazeRectangle = new Rect();
	private int mazeRow;
	private int mazeColumn;
	private int mazeScreenX;
	private int mazeScreenY;

	Maze(Activity activity) {
	}

	void load(Activity activity, int newLevel) {
		String mLevel = "level" + newLevel + ".txt";
		InputStream is = null;
		try {

			MazeArray = new int[MAZE_ROWS * MAZE_COLUMNS];

			is = activity.getAssets().open(mLevel);
			for (int i = 0; i < MazeArray.length; i++) {

				MazeArray[i] = Character.getNumericValue(is.read());
				is.read();
				is.read();
			}
		} catch (Exception e) {
			
		} finally {
			closeStream(is);
		}

	}

	public void draw(Canvas canvas, Paint paint) {
		for (int i = 0; i < MazeArray.length; i++) {
			mazeRow = i / MAZE_COLUMNS;
			mazeColumn = i % MAZE_COLUMNS;
			mazeScreenX = mazeColumn * TILE_SIZE;
			mazeScreenY = mazeRow * TILE_SIZE;
			paint.setColor(Color.YELLOW);
			if (MazeArray[i] == PATH_TILE) {
				canvas.drawRect(mazeScreenX, mazeScreenY, mazeScreenX + 15, mazeScreenY + 15, paint);
			} else if (MazeArray[i] == EXIT_TILE) {
				paint.setColor(Color.RED);
				canvas.drawRect(mazeScreenX, mazeScreenY, mazeScreenX + 15, mazeScreenY + 15, paint);
				paint.setColor(Color.YELLOW);
			} else if (MazeArray[i] == VOID_TILE) {

				mazeRectangle.left = mazeScreenX;
				mazeRectangle.top = mazeScreenY;
				mazeRectangle.right = mazeScreenX + TILE_SIZE;
				mazeRectangle.bottom = mazeScreenY + TILE_SIZE;

				paint.setColor(VOID_COLOR);
				canvas.drawRect(mazeRectangle, paint);
			}
		}

	}

	public int getCellType(int x, int y) {

		int mCellCol = x / TILE_SIZE;
		int mCellRow = y / TILE_SIZE;
		int mLocation = 0;
		if (mCellRow > 0)
			mLocation = mCellRow * MAZE_COLUMNS;
		mLocation += mCellCol;
		return MazeArray[mLocation];
	}

	private static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}
}