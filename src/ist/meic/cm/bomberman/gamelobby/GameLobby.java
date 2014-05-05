package ist.meic.cm.bomberman.gamelobby;

import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.R;
import ist.meic.cm.bomberman.multiplayerC.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class GameLobby extends Activity implements OnItemClickListener {

	private static boolean connected;
	private int playerId;
	private String value = "192.168.1.67";
	private ArrayList<String> playersList = new ArrayList<String>();
	private static final String NO_PLAYERS = "Not Connected!";
	private Context context;
	private boolean trying;
	private BindTask communication;
	private WaitTask waitToStart = new WaitTask();
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private CustomBaseAdapter adapter;
	private String playerName;

	private boolean starting;

	private static final String[] titles = new String[] { "Player 1",
			"Player 2", "Player 3" };

	private static final Integer[] images = { R.drawable.head,
			R.drawable.mario, R.drawable.luigi };

	private ListView listView;
	private List<RowItem> rowItems;

	private Button connect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_lobby);

		trying = false;

		playersList.add(NO_PLAYERS);

		rowItems = new ArrayList<RowItem>();
		updateList();

		listView = (ListView) findViewById(R.id.list);
		adapter = new CustomBaseAdapter(this, rowItems);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);

		context = getApplicationContext();

		connect = (Button) findViewById(R.id.Connect);
		connect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!trying) {
					trying = true;
					final Intent i = getIntent();

					final AlertDialog.Builder alert = new AlertDialog.Builder(
							GameLobby.this)
							.setTitle("Insert the host IP Address:");
					final EditText input = new EditText(GameLobby.this);
					alert.setView(input);
					alert.setCancelable(false);
					alert.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									Toast.makeText(context, "Connecting...",
											Toast.LENGTH_SHORT).show();

									String tmp = input.getText().toString()
											.trim();

									if (!tmp.equals(""))
										value = tmp;

									String levelName = i
											.getStringExtra("levelName");

									if (playerName == null)
										playerName = i
												.getStringExtra("playerName");

									communication = new BindTask();
									communication.execute(
											InGame.getGamePanel(), playerId,
											levelName, value, playerName,
											GameLobby.this, Integer
													.toString((InGame
															.getRobotSpeed())),
											getExplosionDetails(),
											getPointsDetails(), Integer
													.toString(InGame
															.getDuration()));

									playersList.remove(NO_PLAYERS);
									playersList.add(playerName);

									updateList();
								}

							});

					alert.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									trying = false;
									dialog.cancel();
								}
							});

					try {
						alert.show();
					} catch (Exception e) {
					}
				}
			}
		});
		Button start = (Button) findViewById(R.id.Start);
		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!starting && connected) {
					starting = true;
					waitToStart.execute();
				} else
					Toast.makeText(context,
							"You must be connected in order to play!",
							Toast.LENGTH_LONG).show();
			}

		});
		Button quit = (Button) findViewById(R.id.Quit);
		quit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(1, new Intent());
				finish();
			}

		});
		ImageButton refresh = (ImageButton) findViewById(R.id.Refresh);
		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (connected) {
					Toast.makeText(context, "Refreshing!", Toast.LENGTH_SHORT)
							.show();
					RefreshTask rt = new RefreshTask();
					rt.execute();
				} else
					Toast.makeText(context,
							"You must be connected in order to refresh!",
							Toast.LENGTH_SHORT).show();
			}

		});

	}

	void askForName() {

		Toast.makeText(context,
				"The name " + playerName + " is already in use!",
				Toast.LENGTH_SHORT).show();
		final AlertDialog.Builder alert = new AlertDialog.Builder(
				GameLobby.this).setTitle("Insert Player Name:");
		final EditText input = new EditText(this);
		input.setHint("Player");
		alert.setView(input);
		alert.setCancelable(false);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = "Player";
				String tmp = input.getText().toString().trim();
				if (!tmp.equals(""))
					value = tmp;

				playersList.remove(playerName);
				playersList.add(value);
				playerName = value;

				trying = false;
				connect.performClick();
			}
		});

		try {
			alert.show();
		} catch (Exception e) {
		}

	}

	private String getExplosionDetails() {

		StringBuilder sb = new StringBuilder();
		sb.append(InGame.getExplosionDuration());
		sb.append(" ");
		sb.append(InGame.getExplosionTimeout());
		sb.append(" ");
		sb.append(InGame.getExplosionRange());
		return sb.toString();
	}

	private String getPointsDetails() {

		StringBuilder sb = new StringBuilder();
		sb.append(InGame.getPointsRobot());
		sb.append(" ");
		sb.append(InGame.getPointsOpon());
		return sb.toString();
	}

	private void updateList() {
		rowItems.clear();
		for (int i = 0; i < playersList.size(); i++) {
			RowItem item = new RowItem(images[i], titles[i], playersList.get(i));
			rowItems.add(item);
		}
	}

	void setConnected(boolean con) {

		connected = con;
		Toast.makeText(GameLobby.this, "Connected!", Toast.LENGTH_SHORT).show();
	}

	public void setPlayers(ArrayList<String> players) {

		playersList.clear();

		playersList.addAll(players);

		updateList();

		adapter.notifyDataSetChanged();
	}

	private class RefreshTask extends AsyncTask<Object, Void, Void> {

		private Message toSend, received;
		private ArrayList<String> players;

		@Override
		protected Void doInBackground(Object... objects) {
			try {

				toSend = new Message(Message.REFRESH);

				output.writeObject(toSend);
				output.reset();

				received = (Message) input.readObject();

				if (received.getCode() == Message.SUCCESS)
					players = received.getPlayers();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			setPlayers(players);
		}
	}

	public void setOutput(ObjectOutputStream output) {
		this.output = output;

	}

	public void setInput(ObjectInputStream input) {
		this.input = input;

	}

	private class WaitTask extends AsyncTask<Object, Void, Void> {

		private Message toSend, received;
		private boolean started = true;

		@Override
		protected Void doInBackground(Object... objects) {
			try {

				toSend = new Message(Message.READY);

				output.writeObject(toSend);
				output.reset();

				received = (Message) input.readObject();

			} catch (IOException e) {
				started = false;
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			if (started)
				startGame(received);
			else
				notStarted();
		}

	}

	private void startGame(Message received) {
		if (received.getCode() == Message.SUCCESS) {
			Intent i = new Intent();

			i.putExtra("playerId", playerId);

			setResult(0, i);

			finish();
		} else
			Toast.makeText(context,
					"No other players are connected at the moment!",
					Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Toast.makeText(getApplicationContext(),
				rowItems.get(position).toString(), Toast.LENGTH_SHORT).show();
	}

	public void notConnected() {
		trying = false;

		Toast.makeText(getApplicationContext(),
				"TIMEOUT: Couldn't connect!\nTry again!", Toast.LENGTH_SHORT)
				.show();
		communication = new BindTask();
	}

	private void notStarted() {
		starting = false;
		Toast.makeText(getApplicationContext(),
				"TIMEOUT: Couldn't start game!\nTry again!", Toast.LENGTH_SHORT)
				.show();
		waitToStart = new WaitTask();
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;

	}
}
