package ist.meic.cm.bomberman;

import ist.meic.cm.bomberman.controller.MapController;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.TextView;

public class MainThread extends Thread {

	private static final String TAG = MainThread.class.getSimpleName();

	// Surface holder that can access the physical surface
	private SurfaceHolder surfaceHolder;
	// The actual view that handles inputs
	// and draws to the surface
	private AbsMainGamePanel gamePanel;

	// flag to hold game state
	private boolean running;

	private Activity context;

	private boolean ended;
	private int score;

	private int playerID;
	private MapController mapController;

	public void setRunning(boolean running) {
		this.running = running;
	}

	public MainThread(SurfaceHolder surfaceHolder, AbsMainGamePanel gamePanel,
			Context context) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
		this.context = (Activity) context;
		this.playerID = InGame.getId();
		Log.d("Debug", "created thread");
		ended = false;
	}

	@SuppressLint("WrongCall")
	@Override
	public void run() {
		Canvas canvas;
		Log.d(TAG, "Starting game loop");
		this.playerID = InGame.getId();
		while (running) {

			canvas = null;
			// try locking the canvas for exclusive pixel editing
			// in the surface
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {

					// update game state
					// render state to the screen
					// draws the canvas on the panel
					this.gamePanel.onDraw(canvas);

					if (!ended)
						context.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								ended = gamePanel.checkGhosts();

								mapController = gamePanel.getMapController();
								if (mapController != null) {
									score = mapController.getScore(playerID);

									StringBuilder sb = new StringBuilder(
											"Score\n");
									sb.append(score);
									((TextView) context
											.findViewById(R.id.player_score))
											.setText(sb.toString());
								}
							}
						});
				}
			} finally {
				// in case of an exception the surface is not left in
				// an inconsistent state
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			} // end finally

		}
	}

}