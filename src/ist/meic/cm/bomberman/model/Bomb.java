package ist.meic.cm.bomberman.model;

import ist.meic.cm.bomberman.InGame;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bomb {

	private Bitmap bitmap;
	private char[] mapArray;
	private int x;
	private int y;
	private int currentPos;
	private Map map;
	int verticalStep = InGame.getHeight() / 13;
	int horizontalStep = InGame.getWidth() / 20;

	public Bomb(Bitmap bombBit, int x, int y,
			int currentPos) {

		this.bitmap = Bitmap.createScaledBitmap(bombBit,
				InGame.getWidth() / 21, InGame.getHeight() / 14, true);
		this.x = x*(int)horizontalStep;

		this.y = y*(int)verticalStep;
		
		this.currentPos = currentPos;
	}

	char[] getMapArray() {
		return mapArray;
	}

	int getX() {
		return x;
	}

	int getY() {
		return y;
	}

	int getCurrentPos() {
		return currentPos;
	}

	void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void draw(Canvas canvas) {
		if (bitmap != null)
			canvas.drawBitmap(bitmap, x, y, null);
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

}