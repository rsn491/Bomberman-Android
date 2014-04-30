package ist.meic.cm.bomberman.multiplayerC;

import ist.meic.cm.bomberman.AbsMainGamePanel;
import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.R;
import ist.meic.cm.bomberman.controller.MapController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class GameLobby extends Activity { // FALTA ATUALIZAR LISTA DE
											// JOGADORES!!

	private static boolean connected;
	private int playerId;
	private String value = "192.168.1.83";
	private ArrayList<String> playersList = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private static final String NO_PLAYERS = "No Players Yet!";
	private Context context;
	private boolean trying;
	private BindTask communication = new BindTask();
	private ObjectOutputStream output;
	private ObjectInputStream input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_lobby);

		ListView myListView = (ListView) findViewById(R.id.listView1);

		trying = false;

		playersList.add(NO_PLAYERS);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, playersList);
		myListView.setAdapter(adapter);

		context = getApplicationContext();

		Button connect = (Button) findViewById(R.id.Connect);
		connect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!trying) {
					trying = true;
					final Intent i = getIntent();

					final AlertDialog.Builder alert = new AlertDialog.Builder(
							GameLobby.this).setTitle("Insert IP Address:");
					final EditText input = new EditText(GameLobby.this);
					alert.setView(input);
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

									String playerName = i
											.getStringExtra("playerName");

									communication.execute(
											InGame.getGamePanel(), playerId,
											levelName, value, playerName,
											GameLobby.this);

									playersList.remove(NO_PLAYERS);
									playersList.add(playerName);
									adapter.notifyDataSetChanged();
								}
							});

					alert.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
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

				if (connected) {
					Intent i = new Intent();

					i.putExtra("playerId", playerId);

					setResult(0, i);

					finish();
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
		Button refresh = (Button) findViewById(R.id.Refresh);
		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RefreshTask rt = new RefreshTask();
				rt.execute();
			}

		});

	}

	static void setConnected(boolean con) {

		connected = con;
	}

	public void setPlayers(ArrayList<String> players) {

		playersList.clear();

		playersList.addAll(players);

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
}
