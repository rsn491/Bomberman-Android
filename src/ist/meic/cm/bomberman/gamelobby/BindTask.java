package ist.meic.cm.bomberman.gamelobby;

import ist.meic.cm.bomberman.AbsMainGamePanel;
import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.controller.MapController;
import ist.meic.cm.bomberman.multiplayerC.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import android.os.AsyncTask;

public class BindTask extends AsyncTask<Object, Void, Void> {
	private Socket client;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private static final int port = 4444;
	private Message toSend, received;
	private AbsMainGamePanel gamePanel;
	private MapController mapController;
	private ArrayList<String> players;
	private GameLobby gameLobby;
	private int playerId;
	private int duration;

	@Override
	protected Void doInBackground(Object... objects) {
		try {
			gameLobby = (GameLobby) objects[5];
			client = new Socket((String) objects[3], port);
			gamePanel = (AbsMainGamePanel) objects[0];
			output = new ObjectOutputStream(client.getOutputStream());
			input = new ObjectInputStream(client.getInputStream());

			toSend = new Message(Message.JOIN, getSettings(objects));

			output.writeObject(toSend);
			output.reset();

			received = (Message) input.readObject();

			if (received.getCode() == Message.SUCCESS) {
				objects[1] = received.getPlayerID();
				mapController = received.getGameMap();
				duration = received.getDuration();
				playerId = received.getPlayerID();
				gamePanel.setPlayerId(playerId);
				gamePanel.setMapController(mapController);
				gamePanel.setSocket(client);
				gamePanel.setOutput(output);
				gamePanel.setInput(input);
				players = received.getPlayers();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getSettings(Object... objects) {
		StringBuilder sb = new StringBuilder((String) objects[2]);
		sb.append(" ");
		sb.append((String) objects[4]);
		sb.append(" ");
		sb.append((String) objects[6]);
		sb.append(" ");
		sb.append((String) objects[7]);
		sb.append(" ");
		sb.append((String) objects[8]);
		sb.append(" ");
		sb.append((String) objects[9]);
		return sb.toString();
	}

	@Override
	protected void onPostExecute(Void v) {
		if (received == null || received.getCode() == Message.SUCCESS)
			try {
				gameLobby.setPlayers(players);
				gameLobby.setOutput(output);
				gameLobby.setInput(input);
				gameLobby.setPlayerId(playerId);
				if (playerId != 0)
					InGame.setDuration(duration);
				gameLobby.setConnected(true);
			} catch (Exception e) {
				gameLobby.notConnected();
			}
		else
			gameLobby.askForName();
	}

}
