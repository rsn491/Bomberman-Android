package ist.meic.cm.bomberman.model;

import android.graphics.Bitmap;

public class Ghost extends Creature {

	public Ghost(Map map, Bitmap bitmap, int currentPos) {
		super(map, bitmap, currentPos);
	}

	@Override
	public boolean moveUp() {
		y -= verticalStep;
		currentPos -= OTHER_LINE_STEP;
		return true;
	}

	@Override
	public boolean moveDown() {
		y += verticalStep;
		currentPos += OTHER_LINE_STEP;
		return true;
	}

	@Override
	public boolean moveLeft() {
		x -= horizontalStep;
		currentPos--;
		return true;
	}

	@Override
	public boolean moveRight() {
		x += horizontalStep;
		currentPos++;
		return true;
	}

	@Override
	public boolean checkCreature() {
		return map.checkBomberman(currentPos);
	}

	public void setMapArray(char[] mapArray) {
		this.mapArray = mapArray;

	}

}
