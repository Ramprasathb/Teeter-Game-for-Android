package karthick.ram.cg;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Marble {

	private View gameView;
	private int marbleX = 0;
	private int marbleY = 0;
	private int marbleRadius = 8;
	private int marbleColor = Color.CYAN;
	private int gameLives = 5;

	public Marble(View view) {
		this.gameView = view;
		init();
	}

	public void init() {
		marbleX = marbleRadius * 6;
		marbleY = marbleRadius * 6;
	}

	public void draw(Canvas canvas, Paint paint) {
		paint.setColor(marbleColor);
		canvas.drawCircle(marbleX, marbleY, marbleRadius, paint);
	}

	public void updatePositionX(float newX) {
		marbleX += newX;
		if (marbleX + marbleRadius >= gameView.getWidth())
			marbleX = gameView.getWidth() - marbleRadius;
		else if (marbleX - marbleRadius < 0)
			marbleX = marbleRadius;
	}

	public void updatePositionY(float newY) {
		marbleY -= newY;
		if (marbleY + marbleRadius >= gameView.getHeight())
			marbleY = gameView.getHeight() - marbleRadius;
		else if (marbleY - marbleRadius < 0)
			marbleY = marbleRadius;
	}

	public void marbleDies() {
		gameLives--;
	}

	public void setLives(int val) {
		gameLives = val;
	}

	public int getLives() {
		return gameLives;
	}

	public int getX() {
		return marbleX;
	}

	public int getY() {
		return marbleY;
	}
}