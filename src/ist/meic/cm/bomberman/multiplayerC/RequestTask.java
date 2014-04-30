package ist.meic.cm.bomberman.multiplayerC;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import ist.meic.cm.bomberman.AbsMainGamePanel;
import ist.meic.cm.bomberman.controller.MapController;
import ist.meic.cm.bomberman.controller.OperationCodes;
import android.os.AsyncTask;

public class RequestTask extends AsyncTask<Object, Void, Void> {
	private Message toSend;
	private Socket client;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Message received;
	private MapController mapController;
	private boolean running;

	@Override
	protected Void doInBackground(Object... options) {
		running=true;
		MPMainGamePanel gamePanel = (MPMainGamePanel) options[0];
		client = gamePanel.getClient();
		try {
			input = new ObjectInputStream(client.getInputStream());
			output = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			if ((Boolean) options[1]) {
				toSend = new Message(Message.END);
				gamePanel.endConnection();
			} else {
				toSend = new Message(Message.REQUEST,
						(OperationCodes) options[2]);

				output.writeObject(toSend);

				received = (Message) input.readObject();

				if (received.getCode() == Message.SUCCESS) {
					mapController = received.getGameMap();
					gamePanel.setMapController(mapController);
				}
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		running=false;
		return null;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
