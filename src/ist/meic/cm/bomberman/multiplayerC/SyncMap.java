package ist.meic.cm.bomberman.multiplayerC;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.controller.MapController;
import ist.meic.cm.bomberman.controller.OperationCodes;
import ist.meic.cm.bomberman.status.BombermanStatus;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class SyncMap extends Service {
	private MPMainGamePanel gamePanel;
	private boolean end;
	private OperationCodes option;
	private boolean running;
	private MapController mapController;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		gamePanel = (MPMainGamePanel) InGame.getGamePanel();
		end = intent.getBooleanExtra("end", false);
		option = (OperationCodes) intent.getSerializableExtra("option");

		running = true;

		ThreadRefresh td = new ThreadRefresh();
		td.start();
		return super.onStartCommand(intent, flags, startId);
	}

	private class ThreadRefresh extends Thread {

		private Socket client;
		private ObjectInputStream input;
		private ObjectOutputStream output;
		private Message toSend;
		private Message received;
		private static final long REFRESH = 400;

		@Override
		public void run() {
			super.run();

			client = gamePanel.getClient();

			input = gamePanel.getInput();

			output = gamePanel.getOutput();

			try {

				if (end) {
					toSend = new Message(Message.END);
					sendToServer();
					gamePanel.endConnection();
					running = false;
					client.close();
				} else
					while (running) {
						toSend = new Message(Message.REQUEST, option);

						sendToServer();

						received = (Message) input.readObject();

						if (received.getCode() == Message.SUCCESS) {
							System.out.println("Received answer");

							mapController = received.getGameMap();

							handler.sendEmptyMessage(0);
						} else if (received.getCode() == Message.END) {
							running = false;
							handler.sendEmptyMessage(1);
						}

						sleep(REFRESH);
					}

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OptionalDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void sendToServer() throws IOException {
			synchronized (output) {
				output.writeObject(toSend);
				output.reset();
			}

		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent();
			intent.setAction("your.custom.BROADCAST");
			intent.setPackage("ist.meic.cm.bomberman");
			if (msg.what == 1) {
				intent.putExtra("mode", 1);

			} else {

				intent.putExtra("mode", 0);
				intent.putExtra("mapController", mapController);

			}
			sendBroadcast(intent);
		}
	};

}
