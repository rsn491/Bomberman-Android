package ist.meic.cm.bomberman.controller;

import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.status.GhostStatus;

import java.io.Serializable;
import java.util.LinkedList;

public class GhostThread extends Thread implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1627860422696618221L;
	private static final long INTERVAL = 1000;
	private MapController mapController;
	private boolean running;
	private LinkedList<GhostStatus> ghostsIndex;
	private double ROBOT_SPEED;

	public GhostThread(MapController mapController) {
		this.mapController = mapController;
		ROBOT_SPEED = InGame.getRobotSpeed();
	}

	@Override
	public void run() {

		long updateTime = (long) (INTERVAL * ROBOT_SPEED);

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
