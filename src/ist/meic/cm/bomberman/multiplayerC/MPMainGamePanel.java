package ist.meic.cm.bomberman.multiplayerC;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import ist.meic.cm.bomberman.AbsMainGamePanel;
import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.SPMainGamePanel;
import ist.meic.cm.bomberman.controller.MapController;
import ist.meic.cm.bomberman.controller.OperationCodes;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.widget.SpinnerAdapter;

public class MPMainGamePanel extends AbsMainGamePanel {

	private boolean connected;

	public MPMainGamePanel(Context context) {
		super(context);
	}

	public MPMainGamePanel(Context context, String levelName) {
		super(context, levelName);

		connected = true;
	}

	@Override
	public void bomb() {

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
		return false;
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
}
