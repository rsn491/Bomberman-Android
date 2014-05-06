package ist.meic.cm.bomberman.p2p;

import android.os.Handler;
import android.util.Log;

import ist.meic.cm.bomberman.p2p.Manager.Manager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientHandler extends Thread {

	private static final String TAG = "ClientSocketHandler";
	private Handler handler;
	private Manager manager;
	private InetAddress mAddress;

	public ClientHandler(Handler handler, InetAddress groupOwnerAddress) {
		this.handler = handler;
		this.mAddress = groupOwnerAddress;
	}

	@Override
	public void run() {
		Socket socket = new Socket();
		try {
			socket.bind(null);
			socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
					WiFiServiceDiscoveryActivity.SERVER_PORT), 5000);
			Log.d(TAG, "Launching the I/O handler");
			manager = new Manager(socket, handler);
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

	public Manager getManager() {
		return manager;
	}

}
