package ist.meic.cm.bomberman.controller;

import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.status.GhostStatus;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class GhostThread extends Thread implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1627860422696618221L;
	private static final int INTERVAL = 1000;
	private MapController mapController;
	private boolean running;
	private LinkedList<GhostStatus> ghostsIndex;
	private int ROBOT_SPEED;

	public GhostThread(MapController mapController) {
		this.mapController = mapController;
		ROBOT_SPEED = InGame.getRobotSpeed();
	}

	@Override
	public void run() {

		int updateTime = INTERVAL / ROBOT_SPEED;

		while (running) {
			try {
				Thread.sleep(updateTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			ghostsIndex = mapController.getGhostsStatus();
			for (GhostStatus ghost : ghostsIndex) {
				ghost.randomMove();
			}
		} // end finally
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
