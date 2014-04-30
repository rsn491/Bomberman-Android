package ist.meic.cm.bomberman.status;

import java.io.Serializable;

public abstract class Status implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5482510969138351088L;
	protected final static int OTHER_LINE_STEP = 21;
	protected int i;
	private int x;
	private int y;
	protected char[] mapArray;
	private boolean dead;
	private char orientation;
	
	public Status(int i, int x, int y, char[] mapArray) {
		this.mapArray = mapArray;
		this.i = i;
		this.x = x;
		this.y = y;
		this.orientation = 'r';
		this.dead = false;
	}

	public void up() {
		i -= 21;
		y -= 1;
		orientation = 'u';
	}
	
	public void left() {
		i -= 1;
		x -= 1;	
		orientation = 'l';
	}
	
	public void right() {
		i += 1;
		x += 1;
		orientation = 'r';
	}
	
	public void down() {
		i += 21;
		y += 1;
		orientation = 'd';
	}
	
	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public char getOrientation() {
		return orientation;
	}

	public void setOrientation(char orientation) {
		this.orientation = orientation;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public void die() {
		dead = true;
	}
	
}
