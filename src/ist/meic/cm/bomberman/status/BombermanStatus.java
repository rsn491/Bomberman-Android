package ist.meic.cm.bomberman.status;

import java.io.Serializable;

public class BombermanStatus extends Status implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7540473135344364993L;
	private int score;
	private boolean canBomb;

	public BombermanStatus(int i, int x, int y, char[] charArray) {
		super(i, x, y, charArray);
		setCanBomb(true);
		// TODO Auto-generated constructor stub
	}

	public void increaseScore(int points) {
		score += points;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isCanBomb() {
		return canBomb;
	}

	public void setCanBomb(boolean canBomb) {
		this.canBomb = canBomb;
	}

}
