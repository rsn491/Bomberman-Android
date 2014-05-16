package ist.meic.cm.bomberman.p2p;

import ist.meic.cm.bomberman.AbsMainGamePanel;
import ist.meic.cm.bomberman.R;
import ist.meic.cm.bomberman.p2p.manager.Client;

import java.net.ServerSocket;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

public class MPDMainGamePanel extends AbsMainGamePanel {

	private ServerSocket serverSocket;
	private ArrayList<Client> clients;
	private Channel channel;
	private WifiP2pManager manager;
	private boolean connected;

	public MPDMainGamePanel(Context context) {
		super(context);
	}

	public MPDMainGamePanel(Context context, String levelName) {
		super(context, levelName);
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public void setClients(ArrayList<Client> clients) {
		this.clients = clients;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void setManager(WifiP2pManager manager) {
		this.manager = manager;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public ArrayList<Client> getClients() {
		return clients;
	}

	public Channel getChannel() {
		return channel;
	}

	public WifiP2pManager getManager() {
		return manager;
	}

	@Override
	public void bomb() {
		if (bomberman != null)
			map.bomb(playerId);
		else
			gameOver(null);
	}

	@Override
	public void stopController() {
		mapController.getGhostThread().setRunning(false);

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

	public void endConnection() {
		connected = false;
	}

	public void setConnected() {
		connected = true;
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

}
