package ist.meic.cm.bomberman.gamelobby;

import ist.meic.cm.bomberman.AbsMainGamePanel;
import ist.meic.cm.bomberman.controller.MapController;
import ist.meic.cm.bomberman.multiplayerC.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.widget.Toast;

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

	@Override
	protected Void doInBackground(Object... objects) {
		try {
			gameLobby = (GameLobby) objects[5];
			client = new Socket((String) objects[3], port);
			gamePanel = (AbsMainGamePanel) objects[0];
			output = new ObjectOutputStream(client.getOutputStream());
			input = new ObjectInputStream(client.getInputStream());

			StringBuilder sb = new StringBuilder((String) objects[2]);
			sb.append(" ");
			sb.append((String) objects[4]);
			sb.append(" ");
			sb.append((String) objects[6]);
			sb.append(" ");
			sb.append((String) objects[7]);
			
			toSend = new Message(Message.JOIN, sb.toString());

			output.writeObject(toSend);
			output.reset();

			received = (Message) input.readObject();

			if (received.getCode() == Message.SUCCESS) {
				objects[1] = received.getPlayerID();
				mapController = received.getGameMap();
				gamePanel.setPlayerId(received.getPlayerID());
				gamePanel.setMapController(mapController);
				gamePanel.setSocket(client);
				gamePanel.setOutput(output);
				gamePanel.setInput(input);
				GameLobby.setConnected(true);
				players = received.getPlayers();
				System.out.println(players.size());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void v) {
		gameLobby.setPlayers(players);
		gameLobby.setOutput(output);
		gameLobby.setInput(input);
	}

}
