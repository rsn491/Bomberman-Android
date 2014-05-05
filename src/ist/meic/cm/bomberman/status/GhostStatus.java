package ist.meic.cm.bomberman.status;

import ist.meic.cm.bomberman.controller.PossibleMoves;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;

public class GhostStatus extends Status implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7936323306937478514L;

	public GhostStatus(int i, int x, int y, char[] mapArray) {
		super(i, x, y, mapArray);
	}

	public void randomMove() {
		LinkedList<PossibleMoves> possibleMoves = new LinkedList<PossibleMoves>();

		if (checkUP())
			possibleMoves.add(PossibleMoves.UP);
		if (checkDOWN())
			possibleMoves.add(PossibleMoves.DOWN);
		if (checkLEFT())
			possibleMoves.add(PossibleMoves.LEFT);
		if (checkRIGHT())
			possibleMoves.add(PossibleMoves.RIGHT);

		int size = possibleMoves.size();
		if (size > 0) {
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

		return mapArray[i - 1] != 'W' && mapArray[i - 1] != 'O';
	}

	protected boolean checkRIGHT() {
		if (mapArray[i + 1] == 'E')
			this.die();

		return mapArray[i + 1] != 'W' && mapArray[i + 1] != 'O';
	}

	protected boolean checkDOWN() {
		if (mapArray[i + OTHER_LINE_STEP] == 'E')
			this.die();

		return mapArray[i + OTHER_LINE_STEP] != 'W'
				&& mapArray[i + OTHER_LINE_STEP] != 'O';
	}

}
