package ist.meic.cm.bomberman;

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
import ist.meic.cm.bomberman.controller.MapController;
import ist.meic.cm.bomberman.controller.OperationCodes;
import ist.meic.cm.bomberman.model.Bomberman;
import ist.meic.cm.bomberman.model.Creature;
import ist.meic.cm.bomberman.model.Map;

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
		synchronized(CHECKINGAMEOVER) {
			if(!gameEnded) {
				if (creature.checkCreature()
						|| (creature instanceof Bomberman && creature.isDestroyed())
						|| isDead()) {
					bomberman.setIgnore();
					Builder ad = new AlertDialog.Builder(getContext())
					.setTitle("Game Over!")
					.setMessage("You Lose!")
					.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
	
							InGame.quit();
						}
					}).setCancelable(false)
					.setIcon(R.drawable.ic_launcher);
					try {
						ad.show();
	
					} catch (Exception e) {
						e.printStackTrace();
					}
					gameEnded = true;
					return true;
				}
			}
		}
		return false;
	}

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
		map = new Map(InGame.getWidth(), InGame.getHeight());
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
		if (bomberman.moveDown()) {
			mapController.bombermanMove(playerId, OperationCodes.DOWN);
			gameOver(bomberman);

		} else
			bomberman.setBitmap(Bitmap.createScaledBitmap(BitmapFactory
					.decodeResource(getResources(), R.drawable.trydown), InGame
					.getWidth() / 21, InGame.getHeight() / 14, true));
	}

	public void moveLeft() {
		if (bomberman.moveLeft()) {
			mapController.bombermanMove(playerId, OperationCodes.LEFT);
			gameOver(bomberman);
		} else
			bomberman.setBitmap(Bitmap.createScaledBitmap(BitmapFactory
					.decodeResource(getResources(), R.drawable.tryleft), InGame
					.getWidth() / 21, InGame.getHeight() / 14, true));
	}

	public void moveRight() {
		if (bomberman.moveRight()) {
			mapController.bombermanMove(playerId, OperationCodes.RIGHT);
			gameOver(bomberman);
		} else
			bomberman.setBitmap(Bitmap.createScaledBitmap(BitmapFactory
					.decodeResource(getResources(), R.drawable.tryright),
					InGame.getWidth() / 21, InGame.getHeight() / 14, true));

	}

	public void moveUp() {
		if (bomberman.moveUp()) {
			mapController.bombermanMove(playerId, OperationCodes.UP);
			gameOver(bomberman);
		} else
			bomberman.setBitmap(Bitmap.createScaledBitmap(BitmapFactory
					.decodeResource(getResources(), R.drawable.tryup), InGame
					.getWidth() / 21, InGame.getHeight() / 14, true));

	}

	public void pauseGame() {
		Builder ad = new AlertDialog.Builder(getContext())
				.setTitle("Game is Paused!")
				.setMessage("Please Select an option.")
				.setPositiveButton("Resume",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								bomberman.setIgnore();
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
		if (bomberman != null)
			return gameOver(bomberman);

		return false;
	}

}
