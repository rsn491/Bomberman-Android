package ist.meic.cm.bomberman.status;

import java.io.Serializable;

public class BombStatus extends Status  implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3659885502327170289L;
	private BombermanStatus bomberman;
	
	public BombStatus(BombermanStatus bomberman, char[] mapArray) {
		super(bomberman.getI(), bomberman.getX(), bomberman.getY(), mapArray);
		this.setBomberman(bomberman);
		// TODO Auto-generated constructor stub
	}

	public BombermanStatus getBomberman() {
		return bomberman;
	}

	public void setBomberman(BombermanStatus bomberman) {
		this.bomberman = bomberman;
	}

}
