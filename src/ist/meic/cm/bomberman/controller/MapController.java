package ist.meic.cm.bomberman.controller;

import ist.meic.cm.bomberman.maps.MapModels;
import ist.meic.cm.bomberman.status.BombStatus;
import ist.meic.cm.bomberman.status.BombermanStatus;
import ist.meic.cm.bomberman.status.GhostStatus;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class MapController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7243935250677800655L;
	private LinkedList<BombermanStatus> bombermansStatus;
	private LinkedList<GhostStatus> ghostsStatus;
	private LinkedList<BombStatus> bombsStatus;
	private GhostThread ghostThread;
	private int numberOfPlayers;
	private String levelName;
	private MapModels mapModel;
	private String map;

	public MapController(String levelName) {
		numberOfPlayers = 0;
		bombermansStatus = new LinkedList<BombermanStatus>();
		ghostsStatus = new LinkedList<GhostStatus>();
		bombsStatus = new LinkedList<BombStatus>();
		this.levelName = levelName;
		mapModel = new MapModels(levelName);
		map = mapModel.getMap();
		loadGhosts();
		ghostThread = new GhostThread(this);
		ghostThread.setRunning(true);
		ghostThread.start();
	}

	public int joinBomberman() {
		char[] mapArray = map.toCharArray();
		int x = 0;
		int y = 0;
		int playerId = numberOfPlayers;

		char id = Character.forDigit(playerId + 1, 10);

		if (numberOfPlayers < 3) {
			for (int i = 0; i < mapArray.length; i++)
				if (mapArray[i] == id) {
					System.out.println("x:" + x);
					BombermanStatus status = new BombermanStatus(i, x, y,
							map.toCharArray());
					bombermansStatus.add(status);
					break;
				} else if (mapArray[i] == 'n') {
					x = 0;
					y += 1;
				} else
					x += 1;

			numberOfPlayers++;

			return playerId;
		}
		return -1;
	}

	private void loadGhosts() {
		ghostsStatus = new LinkedList<GhostStatus>();

		char[] mapArray = map.toCharArray();

		int x = 0;
		int y = 0;

		for (int i = 0; i < mapArray.length; i++)
			if (mapArray[i] == 'G') {
				GhostStatus status = new GhostStatus(i, x, y, map.toCharArray());
				ghostsStatus.add(status);
			} else if (mapArray[i] == 'n') {
				x = 0;
				y += 1;
			} else
				x += 1;
	}

	public void bombermanMove(int playerId, OperationCodes where) {
		switch (where) {
		case UP:
			bombermansStatus.get(playerId).up();
			break;
		case LEFT:
			bombermansStatus.get(playerId).left();
			break;
		case RIGHT:
			bombermansStatus.get(playerId).right();
			break;
		case DOWN:
			bombermansStatus.get(playerId).down();
			break;
		default:
			break;
		}
	}

	public void newBomb(int playerId) {
		BombermanStatus bombermanStatus = bombermansStatus.get(playerId);
		if (bombermanStatus.isCanBomb()) {
			BombStatus bombStatus = new BombStatus(bombermanStatus,
					map.toCharArray());
			bombsStatus.add(bombStatus);
			bombStatus.getBomberman().setCanBomb(false);
			explode(bombStatus);
		}
	}

	private void explode(BombStatus bombStatus) {
		new ExplosionThread(bombStatus.getI(), bombStatus, this).start();
	}

	public boolean isDead(int playerId) {
		return bombermansStatus.get(playerId).isDead();
	}

	// Getters and Setters
	//

	public void setBombermanOrientation(PossibleMoves orientation) {// BUG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		bombermansStatus.get(0).setOrientation(orientation);
	}

	public LinkedList<BombermanStatus> getBombermansStatus() {
		return bombermansStatus;
	}

	public void setBombermansStatus(LinkedList<BombermanStatus> bombermansStatus) {
		this.bombermansStatus = bombermansStatus;
	}

	public LinkedList<GhostStatus> getGhostsStatus() {
		return ghostsStatus;
	}

	public LinkedList<BombStatus> getBombsStatus() {
		return bombsStatus;
	}

	public void setGhostsStatus(LinkedList<GhostStatus> ghostsStatus) {
		this.ghostsStatus = ghostsStatus;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public GhostThread getGhostThread() {
		return ghostThread;
	}

	public String getLevelName() {

		return levelName;
	}

	public int getLastPlayerID() {

		return numberOfPlayers;
	}

	public String getMap() {
		return map;
	}
	//
}
