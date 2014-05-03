package ist.meic.cm.bomberman;

import ist.meic.cm.bomberman.controller.MapController;
import ist.meic.cm.bomberman.controller.OperationCodes;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class SPMainGamePanel extends AbsMainGamePanel {

	public SPMainGamePanel(Context context) {
		super(context);
	}

	public SPMainGamePanel(Context context, String levelName) {

		super(context, levelName);

		mapController = new MapController(levelName);
		mapController.joinBomberman();

		playerId = 0;
	}

	@Override
	public void bomb() {
		map.bomb(0);
	}

	@Override
	public void stopController() {
		mapController.getGhostThread().setRunning(false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (canvas != null) {
			map.draw(canvas);
			bomberman = map.getBomberman(0);
		}
	}

	@Override
	public boolean isDead() {
		return map.isDead(playerId);
	}

}
