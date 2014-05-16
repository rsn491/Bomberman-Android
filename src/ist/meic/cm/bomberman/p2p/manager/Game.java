package ist.meic.cm.bomberman.p2p.manager;

import java.util.ArrayList;

import ist.meic.cm.bomberman.controller.MapController;

public class Game {

	private static final long INTERVAL = 1000;
	private MapController mapController;
	private ArrayList<String> players;
	private boolean[] ready;
	private int maxNumPlayers;
	private int duration;
	private Thread timer;
	private int tmp;

	public Game(MapController mapController, int duration) {
		this.mapController = mapController;
		players = new ArrayList<String>();
		ready = new boolean[4];
		this.duration = duration;
		this.tmp = this.duration;
	}

	public void setReady(int playerId) {
		ready[playerId] = true;
	}

	public MapController getMapController() {
		return mapController;
	}

	public void setMapController(MapController mapController) {
		this.mapController = mapController;
	}

	public ArrayList<String> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<String> players) {
		this.players = players;
		maxNumPlayers++;
	}

	public int getMaxNumPlayers() {
		return maxNumPlayers;
	}

	public boolean[] getReady() {
		return ready;
	}

	public int removePlayer() {
		return --maxNumPlayers;
	}

	public int getDuration() {
		return duration;
	}

	public boolean is4Players() {
		return mapController.is4Players();
	}

	public void timerThread() {

		Runnable runnable = new Runnable() {
			boolean running = true;

			public void run() {
				while (running) {
					try {
						Thread.sleep(INTERVAL);
					} catch (InterruptedException e) {

					}

					if (running)
						tmp--;

					if (tmp <= 0)
						running = false;
				}
			}
		};
		timer = new Thread(runnable);
		timer.start();
	}

	public Thread getTimer() {
		return timer;
	}

	public int getTmp() {

		return tmp;
	}
}
