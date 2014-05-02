package ist.meic.cm.bomberman.controller;

import ist.meic.cm.bomberman.status.BombStatus;
import ist.meic.cm.bomberman.status.BombermanStatus;
import ist.meic.cm.bomberman.status.GhostStatus;
import ist.meic.cm.bomberman.status.Status;

import java.util.LinkedList;
import java.util.List;

public class ExplodingThread extends Thread {
	protected final static int OTHER_LINE_STEP = 21;
	private BombStatus bombStatus;
	private MapController mapController;
	private boolean running;
	private char[] array;

	public ExplodingThread(BombStatus bombStatus, MapController mapController,
			char[] array) {
		this.bombStatus = bombStatus;
		this.array = array;
		this.mapController = mapController;
		running = true;
	}

	@Override
	public void run() {
		LinkedList<GhostStatus> ghostsStatus = mapController.getGhostsStatus();
		LinkedList<BombermanStatus> bombermanStatus = mapController
				.getBombermansStatus();

		while (running) {
			for (Status ghost : ghostsStatus)
				if (checkDeathPos(ghost.getI())) {
					ghost.die();
					bombStatus.getBomberman().increaseScore(1); // TODO
				}
			for (Status bomberman : bombermanStatus)
				if (checkDeathPos(bomberman.getI())) {
					bomberman.die();
					bombStatus.getBomberman().increaseScore(1);
				}
		}

	}

	private boolean checkDeathPos(int currentPos) {
		return array[currentPos] == 'E';
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
