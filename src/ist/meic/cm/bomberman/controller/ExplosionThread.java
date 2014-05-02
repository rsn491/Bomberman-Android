package ist.meic.cm.bomberman.controller;

import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.status.BombStatus;
import ist.meic.cm.bomberman.status.GhostStatus;
import ist.meic.cm.bomberman.status.Status;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ExplosionThread extends Thread implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -390850487009272286L;
	private static final int EXPLOSION_DURATION = InGame.getExplosionDuration();
	private static final int EXPLOSION_TIMEOUT = InGame.getExplosionTimeout();
	private static final int EXPLOSION_RANGE = InGame.getExplosionRange();
	private MapController mapController;
	private int position;
	private BombStatus bombStatus;
	protected final static int OTHER_LINE_STEP = 21;

	public ExplosionThread(int position, BombStatus bombStatus,
			MapController mapController) {
		this.mapController = mapController;
		this.position = position;
		this.bombStatus = bombStatus;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(EXPLOSION_DURATION);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		deleteBomb();
		ExplodingThread et = new ExplodingThread(bombStatus, mapController,
				position);
		et.start();

		try {
			Thread.sleep(EXPLOSION_TIMEOUT);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		et.setRunning(false);
		exploded();
	}

	//
	public void bombExplode() {
		char[] mapArray = mapController.getMap().toCharArray();
		mapArray[position] = 'E';

		if (mapArray[position - 1] != 'W')
			mapArray[position - 1] = 'E';

		if (mapArray[position + 1] != 'W')
			mapArray[position + 1] = 'E';

		if (mapArray[position - OTHER_LINE_STEP] != 'W')
			mapArray[position - OTHER_LINE_STEP] = 'E';

		if (mapArray[position + OTHER_LINE_STEP] != 'W')
			mapArray[position + OTHER_LINE_STEP] = 'E';

		mapController.setMap(new String(mapArray));
	}

	//
	public void deleteBomb() {
		LinkedList<GhostStatus> ghostsStatus = mapController.getGhostsStatus();
		//
		bombStatus.die();
		bombStatus.getBomberman().setCanBomb(true);
		//
		bombExplode();

		for (Status ghost : ghostsStatus)
			if (checkDeathPos(ghost.getI(), position)) {
				ghost.die(); // remove this ghost from the list of ghosts
								// Statuses, no longer exists
				bombStatus.getBomberman().increaseScore(1); // TODO
			}
	}

	//
	private boolean checkDeathPos(int currentPos, int position) {
		return currentPos == position || currentPos == position - 1
				|| currentPos == position + 1
				|| currentPos == position - OTHER_LINE_STEP
				|| currentPos == position + OTHER_LINE_STEP;
	}

	public void exploded() {
		char[] mapArray = mapController.getMap().toCharArray();

		mapArray[position] = '-';

		if (mapArray[position - 1] == 'E')
			mapArray[position - 1] = '-';

		if (mapArray[position + 1] == 'E')
			mapArray[position + 1] = '-';

		if (mapArray[position - OTHER_LINE_STEP] == 'E')
			mapArray[position - OTHER_LINE_STEP] = '-';

		if (mapArray[position + OTHER_LINE_STEP] == 'E')
			mapArray[position + OTHER_LINE_STEP] = '-';

		mapController.setMap(new String(mapArray));
	}

}
