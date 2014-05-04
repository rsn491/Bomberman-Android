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
import android.widget.ImageView;
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
	private static int pointsRobot, pointsOpon;
	private static int explosionDuration;
	private static int explosionTimeout;
	private static int explosionRange;

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

		loadPrefs();

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

	private void loadPrefs() {
		prefs = PreferenceManager.getDefaultSharedPreferences(InGame.this);

		time = Integer.parseInt(prefs.getString(Settings.DURATION,
				Settings.DURATION_DEFAULT));

		robotSpeed = Integer.parseInt(prefs.getString(Settings.RS,
				Settings.RS_DEFAULT));

		levelName = prefs.getString(Settings.MAP, Settings.MAP_DEFAULT);

		explosionDuration = Integer.parseInt(prefs.getString(Settings.ED,
				Settings.ED_DEFAULT));
		explosionTimeout = Integer.parseInt(prefs.getString(Settings.ET,
				Settings.ET_DEFAULT));
		explosionRange = Integer.parseInt(prefs.getString(Settings.ER,
				Settings.ER_DEFAULT));

		pointsRobot = Integer.parseInt(prefs.getString(Settings.PR,
				Settings.PR_DEFAULT));
		pointsOpon = Integer.parseInt(prefs.getString(Settings.PO,
				Settings.PO_DEFAULT));
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

		if (multiplayerC)
			unregisterReceiver(broadcastReceiver);

		super.onPause();
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

				gamePanel.pauseGame();
			}
		});
		final Button bomb = (Button) findViewById(R.id.bombButton);
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

			if (playerId != 0)
				chooseHead();

			timerThread();
			intent = new Intent(getBaseContext(), SyncMap.class);
			intent.putExtra("option", OperationCodes.MAP);
			startService(intent);
			StringBuilder sb = new StringBuilder("Number\n");
			sb.append(gamePanel.getMapController().getLastPlayerID());
			((TextView) findViewById(R.id.number_of_players)).setText(sb
					.toString());
		} else if (resultCode == 1)
			quit();

	}

	private void chooseHead() {
		ImageView image = (ImageView) findViewById(R.id.head);
		if (playerId == 1) {
			image.setImageResource(R.drawable.mario);
		} else
			image.setImageResource(R.drawable.luigi);

	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(intent);
		}

	};

	private void updateUI(Intent intent) {

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

	public static int getExplosionDuration() {
		return explosionDuration;
	}

	public static int getExplosionTimeout() {
		return explosionTimeout;
	}

	public static int getExplosionRange() {
		return explosionRange;
	}

	public static int getPointsRobot() {
		return pointsRobot;
	}

	public static int getPointsOpon() {
		return pointsOpon;
	}
}
