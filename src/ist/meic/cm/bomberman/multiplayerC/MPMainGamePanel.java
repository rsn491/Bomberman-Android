package ist.meic.cm.bomberman.multiplayerC;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import ist.meic.cm.bomberman.AbsMainGamePanel;
import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.R;
import ist.meic.cm.bomberman.SPMainGamePanel;
import ist.meic.cm.bomberman.controller.MapController;
import ist.meic.cm.bomberman.controller.OperationCodes;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.widget.SpinnerAdapter;

public class MPMainGamePanel extends AbsMainGamePanel {

	public static final long CANBOMBAGAININTERVAL = 6000;
	private static final Object BOMBLOCK = new Object();
	private boolean connected;
	private boolean canBomb;
	private boolean exploded;

	public MPMainGamePanel(Context context) {
		super(context);
	}

	public MPMainGamePanel(Context context, String levelName) {
		super(context, levelName);
		canBomb = true;
		connected = true;
		exploded = true;
	}

	@Override
	public void bomb() {
		if(canBomb) {
			synchronized (BOMBLOCK) {
				if(exploded) {
					BombTask bt = new BombTask();
					bt.execute();
					canBomb = false;
					exploded = false;
				}
			}
		}
	}

	@Override
	public void moveDown() {
		super.moveDown();
		sendBombermanStatus();
	}

	private void sendBombermanStatus() {
		synchronized (output) {
			MoveTask mt = new MoveTask();
			mt.execute();
		}
	}

	@Override
	public void moveLeft() {
		super.moveLeft();
		sendBombermanStatus();

	}

	@Override
	public void moveRight() {
		super.moveRight();
		sendBombermanStatus();

	}

	@Override
	public void moveUp() {
		super.moveUp();
		sendBombermanStatus();

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
	public boolean isDead() {
		return map.isDead(playerId);
	}

	public Socket getClient() {
		return client;
	}

	public boolean getState() {

		return connected;
	}

	public void endConnection() {
		connected = false;
	}

	public ObjectInputStream getInput() {
		return input;
	}

	public ObjectOutputStream getOutput() {
		return output;
	}

	@Override
	public void loadModels() {
		super.loadModels();

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

	private class MoveTask extends AsyncTask<Object, Void, Void> {

		private Message toSend;

		@Override
		protected Void doInBackground(Object... objects) {
			try {

				toSend = new Message(Message.REQUEST, OperationCodes.MOVE,
						mapController.getBombermansStatus().get(playerId));
				synchronized (output) {
					output.writeObject(toSend);
					output.reset();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
	
	private class BombTask extends AsyncTask<Object, Void, Void> {
		private Message toSend;

		@Override
		protected Void doInBackground(Object... objects) {
			try {

				toSend = new Message(Message.REQUEST, OperationCodes.BOMB,
						mapController.getBombermansStatus().get(playerId));
				synchronized (output) {
					output.writeObject(toSend);
					output.reset();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(CANBOMBAGAININTERVAL);
				canBomb = true;
				exploded = true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
	}
}
