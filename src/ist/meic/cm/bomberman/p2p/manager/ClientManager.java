package ist.meic.cm.bomberman.p2p.manager;

import ist.meic.cm.bomberman.multiplayerC.Message;
import ist.meic.cm.bomberman.p2p.WiFiServiceDiscoveryActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;

public class ClientManager implements Runnable, IManager {

	private Socket socket = null;

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Message toSend;
	private Message received;

	private String playerName;

	private CommTask commTask;

	private static WiFiGlobal global = WiFiGlobal.getInstance();

	public ClientManager(String playerName, Socket socket) {
		this.socket = socket;
		this.playerName = playerName;
		global.setSocket(socket);
		start();
	}

	@Override
	public void run() {

		communication();

	}

	private void communication() {
		try {
			toSend = new Message(Message.JOIN, playerName);

			output.writeObject(toSend);
			output.reset();

			received = (Message) input.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (received.getCode() == Message.SUCCESS) {
			global.setPlayerID(received.getPlayerID());
			global.setMap(received.getGameMap());
			global.setPrefs(received.getPrefs());
			global.setPlayerName(playerName);
			WiFiServiceDiscoveryActivity.setCanPlay(true);
			readyToPlay();

			if (received.canStart()) {
				WiFiServiceDiscoveryActivity.setAutoPlay();
				WiFiServiceDiscoveryActivity.setTime(received.getTime());
			}
		} else if (received.getCode() == Message.FAIL)
			askForName();
	}

	private void readyToPlay() {
		final Context context = global.getContext();
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {

				Toast.makeText(context, "Ready To Play!", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	private void start() {
		try {
			output = new ObjectOutputStream(socket.getOutputStream());

			input = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		global.setOutput(output);
		global.setInput(input);

	}

	private void askForName() {

		final Context context = global.getContext();
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {

				Toast.makeText(context,
						"You must provide a different player name!",
						Toast.LENGTH_SHORT).show();

				final String hint = "Not " + playerName;
				final AlertDialog.Builder alert = new AlertDialog.Builder(
						context).setTitle("Insert a different player name:");
				final EditText input = new EditText(context);
				input.setHint(hint);
				alert.setView(input);
				alert.setCancelable(false);

				alert.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								String value = hint;
								String tmp = input.getText().toString().trim();
								if (!tmp.equals(""))
									value = tmp;

								playerName = value;

								WiFiServiceDiscoveryActivity
										.setPlayerName(playerName);
								Toast.makeText(context,
										"Trying to Start Again!",
										Toast.LENGTH_SHORT).show();

								commTask = new CommTask();
								commTask.execute();
							}
						});

				alert.show();
			}
		});
	}

	public Socket getSocket() {
		return socket;
	}

	public ObjectOutputStream getOutput() {
		return output;
	}

	public ObjectInputStream getInput() {
		return input;
	}

	private class CommTask extends AsyncTask<Object, Void, Void> {

		@Override
		protected Void doInBackground(Object... objects) {

			communication();

			return null;
		}

	}
}
