package ist.meic.cm.bomberman.p2p;

import java.util.ArrayList;

import ist.meic.cm.bomberman.AbsMainGamePanel;
import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.R;
import ist.meic.cm.bomberman.SPMainGamePanel;
import ist.meic.cm.bomberman.controller.OperationCodes;
import ist.meic.cm.bomberman.gamelobby.GameLobby;
import ist.meic.cm.bomberman.multiplayerC.MPMainGamePanel;
import ist.meic.cm.bomberman.multiplayerC.SyncMap;
import ist.meic.cm.bomberman.p2p.manager.ClientManager;
import ist.meic.cm.bomberman.p2p.manager.IManager;
import ist.meic.cm.bomberman.settings.Settings;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InGameP2P extends Activity {

	private IManager manager;
	private Intent intent;
	private boolean prepared;
	private boolean isClient;
	private SharedPreferences prefs;
	private static int time;
	private static int robotSpeed;
	private static Context InGame_context;
	private String levelName;
	private static int explosionDuration;
	private static int explosionTimeout;
	private static int explosionRange;
	private static int pointsRobot;
	private static int pointsOpon;

	private MPDMainGamePanel gamePanel;
	private static Button quit;
	private static MediaPlayer bomb_player;
	private Button pause;
	private MediaPlayer player;
	private String playerName;
	private static int width;
	private static int height;
	private static boolean over;
	private Thread timer;
	private int playerId;
	private ArrayList<String> playersList;

	static final long INTERVAL = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		prepared = false;
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		intent = this.getIntent();

	/*	globalVariable = (GlobalClass) getApplicationContext();
		
		manager = globalVariable.getManager();*/

		if (intent.getBooleanExtra("isClient", true)) {
			isClient = true;
		/*	genPrefs(((ClientManager) manager).getPrefs());
			playerId = ((ClientManager) manager).getPlayerID();*/
		} else {
			isClient = false;
			loadPrefs();
		}

		setContentView(R.layout.activity_in_game);
		setKeyPad();
		prepareLayout();

		InGame_context = (Context) InGameP2P.this;
	}

	private void genPrefs(String settings) {
		prefs = PreferenceManager.getDefaultSharedPreferences(InGameP2P.this);

		String[] setts = settings.split(" ");

		time = Integer.parseInt(setts[0]);

		robotSpeed = Integer.parseInt(setts[1]);

		levelName = setts[3];

		explosionDuration = Integer.parseInt(setts[4]);
		explosionTimeout = Integer.parseInt(setts[5]);
		explosionRange = Integer.parseInt(setts[6]);

		pointsRobot = Integer.parseInt(setts[7]);
		pointsOpon = Integer.parseInt(setts[8]);
	}

	private void loadPrefs() {
		prefs = PreferenceManager.getDefaultSharedPreferences(InGameP2P.this);

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

				// To DO

				intent = new Intent(InGameP2P.this,
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
		player = MediaPlayer.create(InGame_context, R.raw.move);
		player.setVolume(100, 100);
		player.start();
	}

	public static void playSoundBomb() {
		if (bomb_player != null)
			bomb_player.release();
		bomb_player = MediaPlayer.create(InGame_context, R.raw.bomb);
		bomb_player.setVolume(100, 100);
		bomb_player.start();
	}

	public void prepareLayout() {

		playerName = this.getIntent().getStringExtra("player_name");

		((TextView) findViewById(R.id.player_name)).setText("Name\n"
				+ playerName);
		((TextView) findViewById(R.id.player_score)).setText("Score\n0");

		((TextView) findViewById(R.id.time_left)).setText("Time\n" + "--s");
		((TextView) findViewById(R.id.number_of_players)).setText("Number\n-");

		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.in_game_screen);
		// sets the width and height of the game screen (pixels)
		rl.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
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

		gamePanel = new MPDMainGamePanel(this, levelName);

		/*if (isClient)
			gamePanel.setMapController(((ClientManager) manager)
					.getMapController());*/

		timerThread();

		rl.addView(gamePanel);
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
					if (running)
						handler.post(new Runnable() {

							public void run() {

								running = updateTime();

								if (!running) {

									over = true;
									gamePanel.showGameOver();
								}
							}
						});
				}
			}
		};
		timer = new Thread(runnable);
		timer.start();
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

	public static boolean Over() {
		return over;
	}

	public static int getDuration() {
		return time;
	}

	public static void setDuration(int duration) {
		time = duration;
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		// TODO Auto-generated method stub
		return height;
	}
}
