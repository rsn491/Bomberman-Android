package ist.meic.cm.bomberman.multiplayerC;

import ist.meic.cm.bomberman.AbsMainGamePanel;
import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.R;
import ist.meic.cm.bomberman.controller.OperationCodes;
import ist.meic.cm.bomberman.model.Creature;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;

public class MPMainGamePanel extends AbsMainGamePanel {

	private long CAN_BOMB_AGAIN_INTERVAL;
	private static final long ADJUST = 1000;
	private static final Object BOMBLOCK = new Object();
	private boolean connected;
	private boolean canBomb;
	private boolean exploded;
	private BombTask bt;
	private MoveTask mt;

	public MPMainGamePanel(Context context) {
		super(context);
	}

	public MPMainGamePanel(Context context, String levelName) {
		super(context, levelName);
		canBomb = true;
		connected = true;
		exploded = true;
		CAN_BOMB_AGAIN_INTERVAL = (long) (InGame.getExplosionTimeout() * ADJUST);
	}

	@Override
	public void bomb() {
		if (bomberman != null) {
			if (canBomb) {
				synchronized (BOMBLOCK) {
					if (exploded) {
						bt = new BombTask();
						bt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						canBomb = false;
						exploded = false;
					}
				}
			}
		} else
			gameOver(null);
	}

	@Override
	public void moveDown() {
		super.moveDown();
		sendBombermanStatus();
	}

	private void sendBombermanStatus() {
		if (bomberman != null)
			synchronized (output) {
				mt = new MoveTask();
				mt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		else
			gameOver(null);
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
		
		map.setBomberman4U(BitmapFactory.decodeResource(getResources(),
				R.drawable.up4));
		map.setBomberman4L(BitmapFactory.decodeResource(getResources(),
				R.drawable.left4));
		map.setBomberman4R(BitmapFactory.decodeResource(getResources(),
				R.drawable.right4));
		map.setBomberman4D(BitmapFactory.decodeResource(getResources(),
				R.drawable.down4));
	}

	@Override
	public void pauseGame() {

		super.pauseGame();

		sendBombermanStatus();
	}

	@Override
	public boolean gameOver(Creature creature) {
		boolean over = super.gameOver(creature);
		if (over) {
			mapController.getBombermansStatus().get(playerId).die();
			synchronized (output) {
				mt = new MoveTask();
				mt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}

		return over;
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

				toSend = new Message(Message.REQUEST, OperationCodes.BOMB);
				synchronized (output) {
					output.writeObject(toSend);
					output.reset();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(CAN_BOMB_AGAIN_INTERVAL);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			InGame.playSoundBomb();
			canBomb = true;
			exploded = true;
			return null;
		}

	}
}
