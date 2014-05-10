package ist.meic.cm.bomberman.p2p.handler;

import android.util.Log;

import ist.meic.cm.bomberman.p2p.WiFiServiceDiscoveryActivity;
import ist.meic.cm.bomberman.p2p.manager.ClientManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientHandler extends Thread {

	private static final String TAG = "ClientSocketHandler";
	private ClientManager manager;
	private InetAddress mAddress;
	private String playerName;

	public ClientHandler(String playerName,InetAddress groupOwnerAddress) {
		this.mAddress = groupOwnerAddress;
		this.playerName=playerName;
	}

	@Override
	public void run() {
		Socket socket = new Socket();
		try {
			socket.bind(null);
			socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
					WiFiServiceDiscoveryActivity.SERVER_PORT), 5000);
			Log.d(TAG, "Launching the I/O handler");
			manager = new ClientManager(playerName,socket);
			new Thread(manager).start();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
	}

	public ClientManager getManager() {
		return manager;
	}

}
