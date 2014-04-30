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

	private final static int H_STEP = 20;
	private static final int OTHER_LINE_STEP = 21;

	public Map(int width, int height) {
		this.width = width;
		this.height = height;

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

		// Draws Bomberman(s)
		if (!InGame.isSinglePlayer() && bombermansPos != null) {
			myStatus = bombermansPos.get(InGame.getId());
			bombermansPos = mapController.getBombermansStatus();
			bombermansPos.set(InGame.getId(), myStatus);

		} else
			bombermansPos = mapController.getBombermansStatus();

		bombermanObj = new LinkedList<Bomberman>();

		int i = 0;
		for (BombermanStatus bombermanPos : bombermansPos) {

			switch (bombermanPos.getOrientation()) {
			case 'u':
				bombermanObj.add(new Bomberman(this, bombermanU, bombermanPos
						.getI()));
				break;
			case 'l':
				bombermanObj.add(new Bomberman(this, bombermanL, bombermanPos
						.getI()));
				break;
			case 'r':
				bombermanObj.add(new Bomberman(this, bombermanR, bombermanPos
						.getI()));
				break;
			case 'd':
				bombermanObj.add(new Bomberman(this, bombermanD, bombermanPos
						.getI()));
				break;
			default:
				break;
			}
			bombermanObj.get(i).setX(bombermanPos.getX());
			bombermanObj.get(i).setY(bombermanPos.getY());
			bombermanObj.get(i).draw(canvas);
			i++;
		}
		//

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

	// check if there's a bomberman
	public boolean checkBomberman(int position) {
		int currentPos;
		for (BombermanStatus bomberman : bombermansPos) {
			currentPos = bomberman.getI();
			if (checkAdjacent(position, currentPos))
				return true;
		}
		return false;
	}

	private boolean checkAdjacent(int position, int currentPos) {
		return currentPos == position || currentPos == position - 1
				|| currentPos == position + 1
				|| currentPos == position - OTHER_LINE_STEP
				|| currentPos == position + OTHER_LINE_STEP;
	}

	// check if there's a ghost
	public boolean checkGhost(int position) {
		int currentPos;
		for (Ghost ghost : ghosts) {
			currentPos = ghost.getCurrentPos();
			if (checkAdjacent(position, currentPos))
				return true;
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
		if(bombermanObj.size()>0)
		return bombermanObj.get(playerId);
		else return null;
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
