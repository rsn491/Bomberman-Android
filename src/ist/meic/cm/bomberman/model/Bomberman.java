package ist.meic.cm.bomberman.model;

import android.graphics.Bitmap;
import android.util.Log;

public class Bomberman extends Creature {
	private boolean ignore;

	public Bomberman(Map map, Bitmap bitmap, int currentPos, int x, int y) {
		super(map, bitmap, currentPos);

		setX(x);
		setY(y);

		ignore = false;
	}

	public boolean ignore() {
		return ignore;
	}

	@Override
	public boolean moveUp() {

		Log.d("Debug", "called move Up");
		if (checkUP()) {
			y -= verticalStep;
			currentPos -= OTHER_LINE_STEP;
			return true;
		}

		return false;
	}

	@Override
	public boolean moveLeft() {

		Log.d("Debug", "called move Left");
		if (checkLEFT()) {
			x -= horizontalStep;
			currentPos--;
			return true;
		}

		return false;
	}

	@Override
	public boolean moveRight() {

		Log.d("Debug", "called move Right");
		if (checkRIGHT()) {
			x += horizontalStep;
			currentPos++;
			return true;
		}

		return false;
	}

	@Override
	public boolean moveDown() {

		Log.d("Debug", "called move Down");
		if (checkDOWN()) {
			y += verticalStep;
			currentPos += OTHER_LINE_STEP;
			return true;
		}

		return false;
	}

	public boolean checkCreature() {
		return map.checkGhost(currentPos);
	}

	public void setIgnore() {

		ignore = !ignore;

	}

}
