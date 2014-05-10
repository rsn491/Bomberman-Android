package ist.meic.cm.bomberman;

import ist.meic.cm.bomberman.controller.MapController;
import ist.meic.cm.bomberman.controller.OperationCodes;
import ist.meic.cm.bomberman.model.Bomberman;
import ist.meic.cm.bomberman.model.Creature;
import ist.meic.cm.bomberman.model.Map;
import ist.meic.cm.bomberman.status.BombermanStatus;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class AbsMainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String CHECKINGAMEOVER = "CHECKINGAMEOVER";
	protected Map map;
	protected MapController mapController;
	protected Bomberman bomberman;
	protected MainThread thread;
	protected String levelName;
	protected int playerId;
	protected Socket client;
	protected ObjectOutputStream output;
	protected ObjectInputStream input;
	private boolean gameEnded;
	private boolean canContinue;
	private Thread continueThread;

	public AbsMainGamePanel(Context context) {
		super(context);
		getHolder().addCallback(this);
		// create the game loop thread
		thread = new MainThread(getHolder(), this, context);
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
		gameEnded = false;
	}

	public AbsMainGamePanel(Context context, String levelName) {
		super(context);
		getHolder().addCallback(this);

		this.levelName = levelName;

		// create the game loop thread
		thread = new MainThread(getHolder(), this, context);
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	public abstract void bomb();

	public boolean gameOver(Creature creature) {
		synchronized (CHECKINGAMEOVER) {
			if (!gameEnded) {
				BombermanStatus bS = mapController.getBombermansStatus().get(
						playerId);

				if (creature == null
						|| bS.isDead()
						|| creature.checkCreature(bS)
						|| (creature instanceof Bomberman && creature
								.isDestroyed()) || isDead()) {
					gameEnded = true;
					bS.setIgnore();

					showGameOver();
					return true;
				}
			}
		}
		return false;
	}

	protected void showGameOver() {
		AlertDialog.Builder ad = new AlertDialog.Builder(getContext())
				.setTitle("Game Over!").setMessage(checkScores())
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (!InGame.isSinglePlayer() && !InGame.Over()
						/* && canContinue */) {
							resumeWatching();
						} else
							InGame.quit();
					}
				}).setCancelable(false).setIcon(R.drawable.ic_launcher);
		try {

			ad.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void resumeWatching() {

		AlertDialog.Builder alert = new AlertDialog.Builder(getContext())
				.setTitle("Do you wish to continue watching?");
		alert.setPositiveButton("Continue",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// continueThread();
					}

				});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						InGame.quit();
					}
				});

		try {
			alert.show();
		} catch (Exception e) {
		}
	}

	/*
	 * private void continueThread() { final Handler handler = new Handler();
	 * Runnable runnable = new Runnable() { boolean running = true;
	 * 
	 * public void run() { while (!getThread().isInterrupted() && running) {
	 * 
	 * handler.post(new Runnable() { public void run() { if (InGame.Over() ||
	 * !continueToWatch()) { gameEnded = false; gameOver(null); running = false;
	 * } } }); } } }; continueThread = new Thread(runnable);
	 * continueThread.start(); }
	 */

	private String checkScores() {
		StringBuilder sb = new StringBuilder();
		sb.append("Your score: ");
		int tmp = mapController.getScore(playerId);
		sb.append(tmp);
		sb.append(" points");

		int max = 0, winner = playerId;
		if (!InGame.isSinglePlayer()) {
			for (int i = 0; i < mapController.getLastPlayerID(); i++)
				if (i != playerId) {
					max = mapController.getScore(i);
					winner = i;
				}

			if (!InGame.Over()/* canContinue = continueToWatch() */) {
				if (tmp == max)
					sb.append("\n\nIt currently is a draw!");
				else if (max > tmp) {
					sb.append("\n\nCurrent Winner:\nPlayer ");
					sb.append(winner + 1);
					sb.append(" with ");
					sb.append(max);
					sb.append(" points");
				} else
					sb.append("\n\nThe current winner is you!");
			} else if (tmp == max)
				sb.append("\n\nIt is a draw!");
			else if (max > tmp) {
				sb.append("\n\nThe Winner:\nPlayer ");
				sb.append(winner + 1);
				sb.append(" with ");
				sb.append(max);
				sb.append(" points");
			} else
				sb.append("\n\nThe winner is you!");
		}
		return sb.toString();
	}

	/*
	 * private boolean continueToWatch() {
	 * 
	 * int i = 0;
	 * 
	 * for (BombermanStatus current : mapController.getBombermansStatus()) { if
	 * (i != playerId && !current.isDead()) return true; i++; }
	 * 
	 * return false; }
	 */

	public MapController getMapController() {
		return mapController;
	}

	public void setMapController(MapController mapController) {
		this.mapController = mapController;
		if (map != null)
			map.setMapController(mapController);
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public Bomberman getBomberman() {
		return bomberman;
	}

	public void loadModels() {
		map = new Map(playerId, InGame.getWidth(), InGame.getHeight());
		map.setMapController(mapController);
		map.setWall(BitmapFactory.decodeResource(getResources(),
				R.drawable.wall));
		map.setObstacle(BitmapFactory.decodeResource(getResources(),
				R.drawable.obstacle));
		map.setFloor(BitmapFactory.decodeResource(getResources(),
				R.drawable.floor));
		map.setExplosion(BitmapFactory.decodeResource(getResources(),
				R.drawable.explosion));
		map.setBombermanU(BitmapFactory.decodeResource(getResources(),
				R.drawable.upb));
		map.setBombermanL(BitmapFactory.decodeResource(getResources(),
				R.drawable.leftb));
		map.setBombermanR(BitmapFactory.decodeResource(getResources(),
				R.drawable.rightb));
		map.setBombermanD(BitmapFactory.decodeResource(getResources(),
				R.drawable.downb));
		map.setBomb(BitmapFactory.decodeResource(getResources(),
				R.drawable.bomb));
		map.setGhost(BitmapFactory.decodeResource(getResources(),
				R.drawable.bghost));
	}

	public void moveDown() {
		if (bomberman != null)
			if (bomberman.moveDown()) {
				mapController.bombermanMove(playerId, OperationCodes.DOWN);
				gameOver(bomberman);

			} else
				bomberman.setBitmap(Bitmap.createScaledBitmap(BitmapFactory
						.decodeResource(getResources(), R.drawable.trydown),
						InGame.getWidth() / 21, InGame.getHeight() / 14, true));
		else
			gameOver(null);
	}

	public void moveLeft() {
		if (bomberman != null)
			if (bomberman.moveLeft()) {
				mapController.bombermanMove(playerId, OperationCodes.LEFT);
				gameOver(bomberman);
			} else
				bomberman.setBitmap(Bitmap.createScaledBitmap(BitmapFactory
						.decodeResource(getResources(), R.drawable.tryleft),
						InGame.getWidth() / 21, InGame.getHeight() / 14, true));
		else
			gameOver(null);
	}

	public void moveRight() {
		if (bomberman != null)
			if (bomberman.moveRight()) {
				mapController.bombermanMove(playerId, OperationCodes.RIGHT);
				gameOver(bomberman);
			} else
				bomberman.setBitmap(Bitmap.createScaledBitmap(BitmapFactory
						.decodeResource(getResources(), R.drawable.tryright),
						InGame.getWidth() / 21, InGame.getHeight() / 14, true));
		else
			gameOver(null);

	}

	public void moveUp() {
		if (bomberman != null)
			if (bomberman.moveUp()) {
				mapController.bombermanMove(playerId, OperationCodes.UP);
				gameOver(bomberman);
			} else
				bomberman.setBitmap(Bitmap.createScaledBitmap(BitmapFactory
						.decodeResource(getResources(), R.drawable.tryup),
						InGame.getWidth() / 21, InGame.getHeight() / 14, true));
		else
			gameOver(null);

	}

	public void pauseGame() {
		final BombermanStatus bS = mapController.getBombermansStatus().get(
				playerId);
		bS.setIgnore();

		Builder ad = new AlertDialog.Builder(getContext())
				.setTitle("Game is Paused!")
				.setMessage("Please Select an option.")
				.setPositiveButton("Resume",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								bS.setIgnore();
							}

						})
				.setNegativeButton("Quit",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								InGame.quit();
							}
						}).setCancelable(false).setIcon(R.drawable.ic_launcher);
		try {
			ad.show();
		} catch (Exception e) {

		}
	}

	public abstract void stopController();

	public Thread getThread() {
		return thread;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can load the unit models
		// and then safely start the game loop
		Log.d("Debug", "Loading models");
		loadModels();
		Log.d("Debug", "Loaded models");
		thread.setRunning(true);
		thread.start();
		Log.d("Debug", "started thread");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		boolean retry = true;

		thread.setRunning(false);

		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}

	}

	public Thread getGhostThread() {
		return mapController.getGhostThread();
	}

	@Override
	protected abstract void onDraw(Canvas canvas);

	public abstract boolean isDead();

	public void setSocket(Socket client) {
		this.client = client;
	}

	public void setOutput(ObjectOutputStream output) {
		this.output = output;

	}

	public void setInput(ObjectInputStream input) {
		this.input = input;
	}

	public boolean checkGhosts() {

		bomberman = map.getBomberman(playerId);

		return gameOver(bomberman);
	}
}
