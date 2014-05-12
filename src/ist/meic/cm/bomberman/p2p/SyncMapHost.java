package ist.meic.cm.bomberman.p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.controller.MapController;
import ist.meic.cm.bomberman.controller.OperationCodes;
import ist.meic.cm.bomberman.multiplayerC.Message;
import ist.meic.cm.bomberman.p2p.manager.Client;
import ist.meic.cm.bomberman.p2p.manager.WiFiGlobal;
import ist.meic.cm.bomberman.status.BombermanStatus;
import android.app.Service;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class SyncMapHost extends Service {
	private MPDMainGamePanel gamePanel;
	private boolean end;
	private boolean running;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		gamePanel = (MPDMainGamePanel) InGame.getGamePanel();
		end = intent.getBooleanExtra("end", false);
		running = true;
		ThreadRefresh td = new ThreadRefresh();
		td.start();
		return super.onStartCommand(intent, flags, startId);
	}

	private class ThreadRefresh extends Thread {

		private ArrayList<Client> clients;
		private ObjectInputStream input;
		private ObjectOutputStream output;
		private Message toSend;
		private Message received;
		private ServerSocket mySocket;
		private static final long REFRESH = 100;

		@Override
		public void run() {
			super.run();

			clients = gamePanel.getClients();

			mySocket = gamePanel.getServerSocket();

			try {

				if (end) {

					for (Client current : clients) {
						output = current.getOut();
						input = current.getIn();
						toSend = new Message(Message.END);
						sendToClient();
					}
					gamePanel.endConnection();
					mySocket.close();
					running = false;

					WiFiGlobal global = WiFiGlobal.getInstance();

					Channel channel = global.getChannel();
					WifiP2pManager manager = global.getManager();

					if (manager != null && channel != null) {
						manager.removeGroup(channel, new ActionListener() {

							@Override
							public void onFailure(int reasonCode) {
								Log.d("QUIT", "Disconnect failed. Reason :"
										+ reasonCode);
							}

							@Override
							public void onSuccess() {
							}

						});
					}
					global.clear();

				} else {
					Client current;
					while (running) {

						for (int i = 0; i < clients.size(); i++) {

							current = clients.get(i);
							input = current.getIn();
							output = current.getOut();
							received = (Message) input.readObject();

							int code = received.getCode();

							if (code == Message.REQUEST) {
								OperationCodes request = received.getRequest();
								if (request.equals(OperationCodes.BOMB))
									gamePanel.getMapController().newBomb(
											current.getPlayerID());
								else if (request.equals(OperationCodes.MOVE)) {
									LinkedList<BombermanStatus> bombermansStatus = gamePanel
											.getMapController()
											.getBombermansStatus();
									bombermansStatus.set(current.getPlayerID(),
											received.getBombermanStatus());
									gamePanel.getMapController()
											.setBombermansStatus(
													bombermansStatus);
								} else if (request.equals(OperationCodes.MAP)) {
									toSend = new Message(Message.SUCCESS,
											gamePanel.getMapController());
									sendToClient();
								}
							} else if (code == Message.END) {

								clients.get(i).getSocket().close();

								clients.remove(i);

								gamePanel.getMapController()
										.getBombermansStatus()
										.get(current.getPlayerID()).die();
								break;
							}

						}

						sleep(REFRESH);
					}
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

		private void sendToClient() throws IOException {
			synchronized (output) {
				output.writeObject(toSend);
				output.reset();
			}

		}
	}

}
