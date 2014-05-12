package ist.meic.cm.bomberman.p2p.manager;

import ist.meic.cm.bomberman.controller.MapController;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

public class WiFiGlobal {

	private static WiFiGlobal WiFiGlobal_Instance;
	private Context context;
	private Game game;
	private WifiP2pManager manager;
	private Channel channel;
	private Socket socket;
	private ServerSocket serverSocket;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ArrayList<Client> clients;
	private int playerID;
	private MapController map;
	private String prefs;
	private String playerName;

	private WiFiGlobal() {
		game = null;
	}

	public static WiFiGlobal getInstance() {
		synchronized (WiFiGlobal.class) {

			if (WiFiGlobal_Instance == null)
				WiFiGlobal_Instance = new WiFiGlobal();
		}
		return WiFiGlobal_Instance;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setManager(WifiP2pManager manager) {
		this.manager = manager;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public WifiP2pManager getManager() {
		return manager;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setServerSocket(ServerSocket socket) {

		this.serverSocket = socket;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setOutput(ObjectOutputStream output) {
		this.output = output;
	}

	public void setInput(ObjectInputStream input) {
		this.input = input;
	}

	public ObjectOutputStream getOutput() {
		return output;
	}

	public ObjectInputStream getInput() {
		return input;
	}

	public ArrayList<Client> getClients() {
		synchronized (WiFiGlobal.class) {

			if (clients == null)
				clients = new ArrayList<Client>();
		}
		return clients;
	}

	public void setClients(ArrayList<Client> clients) {
		this.clients = clients;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;

	}

	public void setMap(MapController gameMap) {
		this.map = gameMap;

	}

	public int getPlayerID() {
		return playerID;
	}

	public MapController getMap() {
		return map;
	}

	public String getPrefs() {
		return prefs;
	}

	public void setPrefs(String prefs) {
		this.prefs = prefs;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;

	}

	public String getPlayerName() {
		return playerName;
	}

	public synchronized void clear() {
		WiFiGlobal_Instance = null;
	}
}