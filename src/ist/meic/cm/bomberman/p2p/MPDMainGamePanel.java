package ist.meic.cm.bomberman.p2p;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import ist.meic.cm.bomberman.AbsMainGamePanel;
import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.R;
import ist.meic.cm.bomberman.model.Creature;
import ist.meic.cm.bomberman.model.Map;

public class MPDMainGamePanel extends AbsMainGamePanel {

	private boolean connected;

	public MPDMainGamePanel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MPDMainGamePanel(Context context, String levelName) {
		super(context, levelName);
		
		connected = true;
	}
	
	@Override
	public void bomb() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopController() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (connected && mapController != null) {

			if (canvas != null) {
				map.draw(canvas);
				bomberman = map.getBomberman(playerId);
			}
		}

	}
	
	@Override
	public void loadModels() {
		map = new Map(playerId, InGameP2P.getWidth(), InGameP2P.getHeight());
		map.setMapController(mapController);

		map.setBomberman2U(BitmapFactory.decodeResource(getResources(),
				R.drawable.up2));
		map.setBomberman2L(BitmapFactory.decodeResource(getResources(),
				R.drawable.left2));
		map.setBomberman2R(BitmapFactory.decodeResource(getResources(),
				R.drawable.right2));
		map.setBomberman2D(BitmapFactory.decodeResource(getResources(),
				R.drawable.down2));
		map.setBomberman3U(BitmapFactory.decodeResource(getResources(),
				R.drawable.up3));
		map.setBomberman3L(BitmapFactory.decodeResource(getResources(),
				R.drawable.left3));
		map.setBomberman3R(BitmapFactory.decodeResource(getResources(),
				R.drawable.right3));
		map.setBomberman3D(BitmapFactory.decodeResource(getResources(),
				R.drawable.down3));
	}

	@Override
	public boolean isDead() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean gameOver(Creature creature) {
		return false;

	}

	@Override
	protected void showGameOver() {
	}

}
