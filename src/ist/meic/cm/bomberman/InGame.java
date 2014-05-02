package ist.meic.cm.bomberman;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import ist.meic.cm.bomberman.controller.MapController;
import ist.meic.cm.bomberman.controller.OperationCodes;
import ist.meic.cm.bomberman.gamelobby.GameLobby;
import ist.meic.cm.bomberman.model.Bomberman;
import ist.meic.cm.bomberman.multiplayerC.MPMainGamePanel;
import ist.meic.cm.bomberman.multiplayerC.SyncMap;
import ist.meic.cm.bomberman.settings.Settings;
import ist.meic.cm.bomberman.settings.SettingsActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView.CommaTokenizer;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InGame extends Activity {
	private static int height;
	private static int width;
	private static boolean multiplayerC;
	private static boolean prepared;
	private static boolean connected;
	private static AbsMainGamePanel gamePanel;

	private MediaPlayer player;
	private String levelName;
	private Thread timer;
	private String playerName;
	private static int playerId;
	private static Button quit, pause;
	private static int robotSpeed;
	private SharedPreferences prefs;
	private int time;

	private Intent intent;

	static final long INTERVAL = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		prepared = false;
		connected = false;
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		prefs = PreferenceManager.getDefaultSharedPreferences(InGame.this);

		robotSpeed = Integer.parseInt(prefs.getString(Settings.RS,
				Settings.RS_DEFAULT));

		levelName = prefs.getString(Settings.MAP, Settings.MAP_DEFAULT);

		intent = this.getIntent();

		if (intent.getStringExtra("game_mode").equals("singleplayer")) {
			Log.d("Debug", "Starting single player mode");
			playerId = 0;
			multiplayerC = false;
		}
		if (intent.getStringExtra("game_mode").equals("multiplayer")) {
			Log.d("Debug", "Starting multi player mode");
			multiplayerC = true;
		}

		Log.d("Debug", "lets play");
		setContentView(R.layout.activity_in_game);
		setKeyPad();
		prepareLayout();
		Log.d("Debug", "PREPARED");
	}

	@Override
	public void onResume() {
		super.onResume();

		if (multiplayerC)
			registerReceiver(broadcastReceiver, new IntentFilter(
					"your.custom.BROADCAST"));
	}

	@Override
	public void onPause() {
		super.onPause();

		if (multiplayerC)
			unregisterReceiver(broadcastReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.in_game, menu);
		return true;
	}

	// adds click listener to keyPad rows buttons
	private void setKeyPad() {
		final Button up = (Button) findViewById(R.id.up_button);
		up.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				gamePanel.moveUp();
				playSoundMove();
			}
		});
		final Button left = (Button) findViewById(R.id.left_button);
		left.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				gamePanel.moveLeft();
				playSoundMove();
			}
		});
		final Button right = (Button) findViewById(R.id.right_button);
		right.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				gamePanel.moveRight();
				playSoundMove();
			}
		});
		final Button down = (Button) findViewById(R.id.down_button);
		down.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click

				gamePanel.moveDown();
				playSoundMove();
			}
		});
		quit = (Button) findViewById(R.id.quit_button);
		quit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click

				if (timer != null) {
					timer.interrupt();

					gamePanel.getGhostThread().interrupt();
					gamePanel.getThread().interrupt();
					gamePanel.stopController();

					if (multiplayerC) {
						((MPMainGamePanel) gamePanel).endConnection();
						intent = new Intent(getBaseContext(), SyncMap.class);
						intent.putExtra("end", true);
						intent.putExtra("option", OperationCodes.MAP);
						startService(intent);
					}
				}
				finish();

				intent = new Intent(InGame.this,
						ist.meic.cm.bomberman.Menu.class);
				startActivity(intent);
			}
		});
		pause = (Button) findViewById(R.id.pause_button);
		pause.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click

				final Bomberman bomberman = gamePanel.getBomberman();

				bomberman.setIgnore();

				gamePanel.pauseGame();
			}
		});
		final Button bomb = (Button) findViewById(R.id.bomb_button);
		bomb.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				gamePanel.bomb();
			}
		});
	}

	public static void quit() {
		quit.performClick();
	}

	private void playSoundMove() {
		if (player != null)
			player.release();
		player = MediaPlayer.create(InGame.this, R.raw.move);
		player.setVolume(100, 100);
		player.start();
	}

	public void prepareLayout() {

		playerName = this.getIntent().getStringExtra("player_name");

		((TextView) findViewById(R.id.player_name)).setText("Name\n"
				+ playerName);
		((TextView) findViewById(R.id.player_score)).setText("Score\n0");
		time = Integer.parseInt(prefs.getString(Settings.DURATION,
				Settings.DURATION_DEFAULT));
		((TextView) findViewById(R.id.time_left))
				.setText("Time\n" + time + "s");
		((TextView) findViewById(R.id.number_of_players)).setText("Number\n1");

		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.in_game_screen);
		// sets the width and height of the game screen (pixels)
		rl.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@SuppressLint("NewApi")
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {

						// now we can retrieve the width and height
						width = rl.getWidth();
						height = rl.getHeight();
						prepared = true;

						// ...
						// do whatever you want with them
						// ...
						// this is an important step not to keep receiving
						// callbacks:
						// we should remove this listener
						// I use the function to remove it based on the api
						// level!

						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
							rl.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);
						else
							rl.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
					}
				});

		if (multiplayerC) {

			Intent i = new Intent(getApplicationContext(), GameLobby.class);

			i.putExtra("levelName", levelName);

			i.putExtra("playerName", playerName);

			gamePanel = new MPMainGamePanel(this, levelName);

			startActivityForResult(i, 0);

			connected = true;
		} else {
			gamePanel = new SPMainGamePanel(this, levelName);
			timerThread();
		}

		rl.addView(gamePanel);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 0) {
			playerId = data.getIntExtra("playerId", 0);

			timerThread();
			intent = new Intent(getBaseContext(), SyncMap.class);
			intent.putExtra("option", OperationCodes.MAP);
			startService(intent);
		} else if (resultCode == 1)
			quit();

	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("receive broadcast");
			updateUI(intent);
		}

	};

	private void updateUI(Intent intent) {
		System.out.println("updateUI");

		gamePanel.setMapController((MapController) intent
				.getSerializableExtra("mapController"));
	}

	public boolean updateTime() {

		if (time == 0)
			return false;

		time--;
		StringBuilder sb = new StringBuilder("Time\n");
		sb.append(time);
		sb.append("s");
		((TextView) findViewById(R.id.time_left)).setText(sb.toString());

		return true;
	}

	public static int getHeight() {
		return height;
	}

	public static int getWidth() {
		return width;
	}

	public static int getId() {
		return playerId;
	}

	public static boolean isPrepared() {
		return prepared;
	}

	private void timerThread() {
		final Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			boolean running = true;

			public void run() {
				while (!gamePanel.getThread().isInterrupted() && running) {
					try {
						Thread.sleep(INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					handler.post(new Runnable() {
						public void run() {
							running = updateTime();

							if (!running)
								gamePanel.gameOver(null);
						}
					});
				}
			}
		};
		timer = new Thread(runnable);
		timer.start();
	}

	@Override
	public void onBackPressed() {
		pause.performClick();
	}

	public static AbsMainGamePanel getGamePanel() {
		return gamePanel;
	}

	public static boolean isSinglePlayer() {
		return !multiplayerC;
	}

	public static int getRobotSpeed() {

		return robotSpeed;
	}
}
