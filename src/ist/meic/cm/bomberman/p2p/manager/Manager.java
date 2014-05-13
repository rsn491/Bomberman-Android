package ist.meic.cm.bomberman.p2p.manager;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import ist.meic.cm.bomberman.controller.MapController;
import ist.meic.cm.bomberman.multiplayerC.Message;
import ist.meic.cm.bomberman.p2p.WiFiServiceDiscoveryActivity;
import ist.meic.cm.bomberman.p2p.handler.GroupOwnerHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Handles reading and writing of messages with socket buffers. Uses a Handler
 * to post messages to UI thread for UI updates.
 */
public class Manager implements Runnable, IManager {// To do

	private Socket socket = null;

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Message fromClient;
	private int playerID;
	private MapController currentMap;
	private Game game;
	private Message toSend;

	private String prefs;

	private String playerName;

	private int duration;

	private String levelName;

	private static WiFiGlobal global = WiFiGlobal.getInstance();
	private static final String TAG = "ChatHandler";

	public Manager(String playerName, Socket socket, String prefs) {
		this.socket = socket;
		this.prefs = prefs;
		this.playerName = playerName;
		System.out.println(playerName);
		genPrefs();
		start();
	}

	@Override
	public void run() {
		try {

			fromClient = (Message) input.readObject();

			int request = fromClient.getCode();
			if (request == Message.JOIN)
				while (!join(fromClient))
					fromClient = (Message) input.readObject();

		} catch (IOException e) {
			Log.e(TAG, "disconnected", e);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void start() {
		playerID = 0;

		if (global.getGame() == null) {
			currentMap = new MapController(levelName, false);
			currentMap.joinBomberman();
			this.game = new Game(currentMap, duration);
			addPlayer(playerName, game);
			global.setPlayerName(playerName);
			global.setPlayerID(0);
		} else {
			game = global.getGame();
			currentMap = game.getMapController();
		}

		try {
			this.output = new ObjectOutputStream(socket.getOutputStream());

			this.input = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void genPrefs() {

		String[] setts = prefs.split(" ");

		duration = Integer.parseInt(setts[1]);

		levelName = setts[0];
	}

	private ArrayList<String> addPlayer(String playerName, Game game) {
		ArrayList<String> players = game.getPlayers();
		players.add(playerName);
		game.setPlayers(players);
		return players;
	}

	private boolean join(Message fromClient) {

		String details = fromClient.getSimpleDetails();
		boolean success = true;

		if (game.getPlayers().contains(details)) {
			toSend = new Message(Message.FAIL);
			success = false;
		} else {
			playerID = currentMap.joinBomberman();
			if (playerID != -1) {

				GroupOwnerHandler handler = global.getHandler();

				if (handler != null && handler.canStart())
					toSend = new Message(Message.SUCCESS, playerID, currentMap,
							addPlayer(details, game), prefs, true);
				else
					toSend = new Message(Message.SUCCESS, playerID, currentMap,
							addPlayer(details, game), prefs);

				synchronized (global) {
					ArrayList<Client> clients = global.getClients();
					clients.add(new Client(socket, output, input, playerID));
					global.setClients(clients);
					global.setGame(game);
					global.setMap(currentMap);
				}
			}

		}

		return sendToPlayer(toSend) && success;
	}

	private boolean sendToPlayer(Message toSend) {
		try {
			output.writeObject(toSend);
			output.reset();
		} catch (IOException e) {
			e.printStackTrace();
			return false;

		}
		return true;
	}

}
