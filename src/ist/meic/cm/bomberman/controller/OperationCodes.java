package ist.meic.cm.bomberman.controller;

public enum OperationCodes {

	UP(1), DOWN(2), LEFT(3), RIGHT(4), BOMB(5), MAP(6), MOVE(7);

	private int value;

	OperationCodes(int value) {
		this.setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
