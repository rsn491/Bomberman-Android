package ist.meic.cm.bomberman.model;

import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.status.BombermanStatus;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public abstract class Creature {
	private Bitmap bitmap; // the actual bitmap
	protected int x; // the X coordinate
	protected int y; // the Y coordinate
	protected float verticalStep;
	protected float horizontalStep;
	protected Map map;
	protected char[] mapArray;
	protected int currentPos;
	protected boolean destroy;
	protected final static int OTHER_LINE_STEP = 21;

	public Creature(Map map, Bitmap bitmap, int currentPos) {
		this.verticalStep = InGame.getHeight() / 13;
		this.horizontalStep = InGame.getWidth() / 20;
		this.x += horizontalStep;
		this.y += verticalStep;
		this.bitmap = Bitmap.createScaledBitmap(bitmap, InGame.getWidth()
				/ OTHER_LINE_STEP, InGame.getHeight() / 14, true);
		this.currentPos = currentPos;
		this.map = map;
		this.mapArray = map.getMap().toCharArray();
		this.destroy = false;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public int getCurrentPos() {
		return currentPos;
	}

	public float getHorizontalStep() {
		return horizontalStep;
	}

	public float getVerticalStep() {
		return verticalStep;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x * (int) horizontalStep;
	}

	public void setY(int y) {
		this.y = y * (int) verticalStep;
	}

	public abstract boolean moveUp();

	public abstract boolean moveDown();

	public abstract boolean moveLeft();

	public abstract boolean moveRight();

	public boolean checkCreature(BombermanStatus bS) {
		return map.checkGhost(bS, currentPos);
	}

	protected void destroy() {
		this.destroy = true;
	}

	public boolean isDestroyed() {

		return destroy;
	}

	protected boolean checkUP() {

		if (mapArray[currentPos - OTHER_LINE_STEP] == 'E')
			this.destroy();

		return mapArray[currentPos - OTHER_LINE_STEP] != 'W'
				&& mapArray[currentPos - OTHER_LINE_STEP] != 'O';
	}

	protected boolean checkLEFT() {
		if (mapArray[currentPos - 1] == 'E')
			this.destroy();

		return mapArray[currentPos - 1] != 'W'
				&& mapArray[currentPos - 1] != 'O';
	}

	protected boolean checkRIGHT() {
		if (mapArray[currentPos + 1] == 'E')
			this.destroy();

		return mapArray[currentPos + 1] != 'W'
				&& mapArray[currentPos + 1] != 'O';
	}

	protected boolean checkDOWN() {
		if (mapArray[currentPos + OTHER_LINE_STEP] == 'E')
			this.destroy();

		return mapArray[currentPos + OTHER_LINE_STEP] != 'W'
				&& mapArray[currentPos + OTHER_LINE_STEP] != 'O';
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, x, y, null);
	}
}
