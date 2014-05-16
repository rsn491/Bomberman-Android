package ist.meic.cm.bomberman.model;

import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.controller.MapController;
import ist.meic.cm.bomberman.status.BombStatus;
import ist.meic.cm.bomberman.status.BombermanStatus;
import ist.meic.cm.bomberman.status.GhostStatus;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Map {

	private int width;
	private int height;
	private List<BombermanStatus> bombermansPos;
	private List<GhostStatus> ghostsPos;
	private List<BombStatus> bombsPos;
	private MapController mapController;
	private List<Bomberman> bombermanObj;
	private Bitmap wall;
	private Bitmap obstacle;
	private Bitmap ghost;
	private Bitmap bombermanR;
	private Bitmap bombermanL;
	private Bitmap bombermanU;
	private Bitmap bombermanD;
	private Bitmap bomb;
	private Bitmap floor;
	private Bitmap explosion;
	private List<Ghost> ghosts;
	private BombermanStatus myStatus;
	private Bitmap bomberman2D;
	private Bitmap bomberman2U;
	private Bitmap bomberman2L;
	private Bitmap bomberman2R;
	private Bitmap bomberman3L;
	private Bitmap bomberman3R;
	private Bitmap bomberman3U;
	private Bitmap bomberman3D;
	private Object bombermanLock;
	private int playerId;
	private boolean hasDied;
	private Bitmap bomberman4R;
	private Bitmap bomberman4L;
	private Bitmap bomberman4U;
	private Bitmap bomberman4D;

	private final static int H_STEP = 20;
	private static final int OTHER_LINE_STEP = 21;

	public Map(int playerId, int width, int height) {
		this.width = width;
		this.height = height;
		this.playerId = playerId;
		this.bombermanLock = new Object();
	}

	public void draw(Canvas canvas) {
		char[] mapArray = mapController.getMap().toCharArray();

		float x = 0, y = 0;

		// Draws map
		for (int i = 0; i < mapArray.length; i++)
			switch (mapArray[i]) {
			case 'n':
				x = 0;
				y += height / 13;
				break;
			case 'W':
				canvas.drawBitmap(wall, x, y, null);
				x += width / H_STEP;
				break;
			case 'O':
				canvas.drawBitmap(obstacle, x, y, null);
				x += width / H_STEP;
				break;
			case 'E':
				canvas.drawBitmap(explosion, x, y, null);
				x += width / H_STEP;
				break;
			default:
				canvas.drawBitmap(floor, x, y, null);
				x += width / H_STEP;
				break;
			}
		//
		ghostsPos = mapController.getGhostsStatus();
		ghosts = new LinkedList<Ghost>();
		// Draw ghosts
		for (GhostStatus ghostStatus : ghostsPos) {
			if (!ghostStatus.isDead()) {
				Ghost current = new Ghost(this, ghost, ghostStatus.getI());
				ghosts.add(current);
				current.setX(ghostStatus.getX());
				current.setY(ghostStatus.getY());
				current.draw(canvas);
			}
		}
		//

		boolean multiplayer = !InGame.isSinglePlayer();

		// Draws Bomberman(s)
		if (multiplayer && bombermansPos != null) {
			myStatus = bombermansPos.get(playerId);
			bombermansPos = mapController.getBombermansStatus();
			if (bombermansPos.get(playerId).isDead() || myStatus.isDead())
				hasDied = true;

			if (hasDied)
				myStatus.die();
			bombermansPos.set(playerId, myStatus);
		} else
			bombermansPos = mapController.getBombermansStatus();

		synchronized (bombermanLock) {

			bombermanObj = new LinkedList<Bomberman>();

			int i = 0, a, b;
			for (BombermanStatus bombermanPos : bombermansPos) {
				if (bombermanPos.isDead()) {
					bombermanObj.add(null);

				} else {
					a = bombermanPos.getX();
					b = bombermanPos.getY();
					switch (bombermanPos.getOrientation()) {
					case UP:
						if (i != 0 && multiplayer) {
							if (i == 1)
								bombermanObj
										.add(new Bomberman(this, bomberman2U,
												bombermanPos.getI(), a, b));
							else if (i == 2)
								bombermanObj
										.add(new Bomberman(this, bomberman3U,
												bombermanPos.getI(), a, b));
							else if (i == 3)
								bombermanObj
										.add(new Bomberman(this, bomberman4U,
												bombermanPos.getI(), a, b));
						} else
							bombermanObj.add(new Bomberman(this, bombermanU,
									bombermanPos.getI(), a, b));
						break;
					case LEFT:
						if (i != 0 && multiplayer) {
							if (i == 1)
								bombermanObj
										.add(new Bomberman(this, bomberman2L,
												bombermanPos.getI(), a, b));
							else if (i == 2)
								bombermanObj
										.add(new Bomberman(this, bomberman3L,
												bombermanPos.getI(), a, b));
							else if (i == 3)
								bombermanObj
										.add(new Bomberman(this, bomberman4L,
												bombermanPos.getI(), a, b));
						} else
							bombermanObj.add(new Bomberman(this, bombermanL,
									bombermanPos.getI(), a, b));
						break;
					case RIGHT:
						if (i != 0 && multiplayer) {
							if (i == 1)
								bombermanObj
										.add(new Bomberman(this, bomberman2R,
												bombermanPos.getI(), a, b));
							else if (i == 2)
								bombermanObj
										.add(new Bomberman(this, bomberman3R,
												bombermanPos.getI(), a, b));
							else if (i == 3)
								bombermanObj
										.add(new Bomberman(this, bomberman4R,
												bombermanPos.getI(), a, b));
						} else
							bombermanObj.add(new Bomberman(this, bombermanR,
									bombermanPos.getI(), a, b));
						break;
					case DOWN:
						if (i != 0 && multiplayer) {
							if (i == 1)
								bombermanObj
										.add(new Bomberman(this, bomberman2D,
												bombermanPos.getI(), a, b));
							else if (i == 2)
								bombermanObj
										.add(new Bomberman(this, bomberman3D,
												bombermanPos.getI(), a, b));
							else if (i == 3)
								bombermanObj
										.add(new Bomberman(this, bomberman4D,
												bombermanPos.getI(), a, b));
						} else
							bombermanObj.add(new Bomberman(this, bombermanD,
									bombermanPos.getI(), a, b));
						break;
					default:
						break;
					}
					Bomberman tmp = bombermanObj.get(i);
					if (tmp != null)
						tmp.draw(canvas);
				}
				i++;
			}

			//
		}
		//
		bombsPos = mapController.getBombsStatus();
		for (BombStatus bombpos : bombsPos) {
			if (!bombpos.isDead()) {
				Bomb current = new Bomb(bomb, bombpos.getX(), bombpos.getY(),
						bombpos.getI());
				current.draw(canvas);
			}
		}
		//
	}

	private boolean checkAdjacent(int position, int currentPos) {
		return currentPos == position || currentPos == position - 1
				|| currentPos == position + 1
				|| currentPos == position - OTHER_LINE_STEP
				|| currentPos == position + OTHER_LINE_STEP;
	}

	// check if there's a ghost
	public boolean checkGhost(BombermanStatus bS, int position) {

		int currentPos;

		for (GhostStatus ghost : ghostsPos) {
			currentPos = ghost.getI();

			if (!ghost.isDead() && !bS.isIgnore()
					&& checkAdjacent(position, currentPos)) {
				bS.die();
				return true;
			}
		}
		return false;
	}

	public void bomb(int playerId) {
		mapController.newBomb(playerId);
	}

	// Getters and Setters
	public void setWall(Bitmap wall) {
		this.wall = Bitmap.createScaledBitmap(wall, width / H_STEP,
				height / 13, true);
	}

	public void setObstacle(Bitmap obstacle) {
		this.obstacle = Bitmap.createScaledBitmap(obstacle, width / H_STEP,
				height / 13, true);
	}

	public void setFloor(Bitmap floor) {
		this.floor = Bitmap.createScaledBitmap(floor, width / H_STEP,
				height / 13, true);
	}

	public void setBomberman2R(Bitmap bomberman2R) {
		this.bomberman2R = Bitmap.createScaledBitmap(bomberman2R, width
				/ H_STEP, height / 13, true);
	}

	public void setBomberman2L(Bitmap bomberman2L) {
		this.bomberman2L = Bitmap.createScaledBitmap(bomberman2L, width
				/ H_STEP, height / 13, true);
	}

	public void setBomberman2U(Bitmap bomberman2U) {
		this.bomberman2U = Bitmap.createScaledBitmap(bomberman2U, width
				/ H_STEP, height / 13, true);
	}

	public void setBomberman2D(Bitmap bomberman2D) {
		this.bomberman2D = Bitmap.createScaledBitmap(bomberman2D, width
				/ H_STEP, height / 13, true);
	}

	public void setBomberman3R(Bitmap bomberman3R) {
		this.bomberman3R = Bitmap.createScaledBitmap(bomberman3R, width
				/ H_STEP, height / 13, true);
	}

	public void setBomberman3L(Bitmap bomberman3L) {
		this.bomberman3L = Bitmap.createScaledBitmap(bomberman3L, width
				/ H_STEP, height / 13, true);
	}

	public void setBomberman3U(Bitmap bomberman3U) {
		this.bomberman3U = Bitmap.createScaledBitmap(bomberman3U, width
				/ H_STEP, height / 13, true);
	}

	public void setBomberman3D(Bitmap bomberman3D) {
		this.bomberman3D = Bitmap.createScaledBitmap(bomberman3D, width
				/ H_STEP, height / 13, true);
	}

	public void setBomberman4R(Bitmap bomberman4R) {
		this.bomberman4R = Bitmap.createScaledBitmap(bomberman4R, width
				/ H_STEP, height / 13, true);
	}

	public void setBomberman4L(Bitmap bomberman4L) {
		this.bomberman4L = Bitmap.createScaledBitmap(bomberman4L, width
				/ H_STEP, height / 13, true);
	}

	public void setBomberman4U(Bitmap bomberman4U) {
		this.bomberman4U = Bitmap.createScaledBitmap(bomberman4U, width
				/ H_STEP, height / 13, true);
	}

	public void setBomberman4D(Bitmap bomberman4D) {
		this.bomberman4D = Bitmap.createScaledBitmap(bomberman4D, width
				/ H_STEP, height / 13, true);
	}

	public void setBombermanR(Bitmap bombermanR) {
		this.bombermanR = Bitmap.createScaledBitmap(bombermanR, width / H_STEP,
				height / 13, true);
	}

	public void setBombermanL(Bitmap bombermanL) {
		this.bombermanL = Bitmap.createScaledBitmap(bombermanL, width / H_STEP,
				height / 13, true);
	}

	public void setBombermanU(Bitmap bombermanU) {
		this.bombermanU = Bitmap.createScaledBitmap(bombermanU, width / H_STEP,
				height / 13, true);
	}

	public void setBombermanD(Bitmap bombermanD) {
		this.bombermanD = Bitmap.createScaledBitmap(bombermanD, width / H_STEP,
				height / 13, true);
	}

	public void setBomb(Bitmap bomb) {
		this.bomb = Bitmap.createScaledBitmap(bomb, width / H_STEP,
				height / 13, true);
	}

	public void setGhost(Bitmap ghost) {
		this.ghost = Bitmap.createScaledBitmap(ghost, width / H_STEP,
				height / 13, true);
	}

	public void setExplosion(Bitmap explosion) {
		this.explosion = Bitmap.createScaledBitmap(explosion, width / H_STEP,
				height / 13, true);

	}

	public void setMapController(MapController mapController) {
		this.mapController = mapController;
	}

	public Bomberman getBomberman(int playerId) {
		synchronized (bombermanLock) {
			if (bombermanObj.size() > playerId)
				return bombermanObj.get(playerId);
			else
				return null;
		}
	}

	public boolean isDead(int playerId) {
		return bombermansPos.get(playerId).isDead();
	}

	public List<Ghost> getGhosts() {
		return ghosts;
	}

	public void setMap(String s) {
		mapController.setMap(s);
	}

	public String getMap() {
		return mapController.getMap();
	}
	//
}
