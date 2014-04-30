package ist.meic.cm.bomberman.controller;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Index implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7370543892123753992L;
	protected final static int OTHER_LINE_STEP = 21;
	private int i;
	private int x;
	private int y;
	private char[] mapArray;
	private boolean dead;
	private char orientation;
	
	public Index(int i, int x, int y, char[] mapArray) {
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
	
	public void randomMove() {
		List<PossibleMoves> possibleMoves = new LinkedList<PossibleMoves>();

		if (checkUP())
			possibleMoves.add(PossibleMoves.UP);
		if (checkDOWN())
			possibleMoves.add(PossibleMoves.DOWN);
		if (checkLEFT())
			possibleMoves.add(PossibleMoves.LEFT);
		if (checkRIGHT())
			possibleMoves.add(PossibleMoves.RIGHT);

		Random rd = new Random();
		int nextMove = rd.nextInt(possibleMoves.size());

		switch (possibleMoves.get(nextMove)) {
		case UP:
			up();
			break;
		case DOWN:
			down();
			break;
		case RIGHT:
			right();
			break;
		case LEFT:
			left();
			break;
		default:
			break;
		}

	}
	
	protected boolean checkUP() {
		if (mapArray[i - OTHER_LINE_STEP] == 'E')
			this.die();

		return mapArray[i - OTHER_LINE_STEP] != 'W'
				&& mapArray[i - OTHER_LINE_STEP] != 'O';
	}

	protected boolean checkLEFT() {
		if (mapArray[i - 1] == 'E')
			this.die();

		return mapArray[i - 1] != 'W'
				&& mapArray[i - 1] != 'O';
	}

	protected boolean checkRIGHT() {
		if (mapArray[i + 1] == 'E')
			this.die();

		return mapArray[i + 1] != 'W'
				&& mapArray[i + 1] != 'O';
	}

	protected boolean checkDOWN() {
		if (mapArray[i + OTHER_LINE_STEP] == 'E')
			this.die();

		return mapArray[i + OTHER_LINE_STEP] != 'W'
				&& mapArray[i + OTHER_LINE_STEP] != 'O';
	}
	
}
