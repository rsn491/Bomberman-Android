package ist.meic.cm.bomberman.settings;

public class Settings {

	private String levelName;
	private int gameDuration;
	private int explosionTimeout;
	private int explosionDuration;
	private int explosionRange;
	private int robotSpeed;
	private int pointsRobot;
	private int pointsOpponent;

	// Default
	public Settings() {
		levelName = "Level1";
		gameDuration = 120;
		explosionTimeout = 3;
		explosionDuration = 3;
		explosionRange = 1;
		robotSpeed = 1;
		pointsRobot = 1;
		pointsOpponent = 5;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public int getGameDuration() {
		return gameDuration;
	}

	public void setGameDuration(int gameDuration) {
		this.gameDuration = gameDuration;
	}

	public int getExplosionTimeout() {
		return explosionTimeout;
	}

	public void setExplosionTimeout(int explosionTimeout) {
		this.explosionTimeout = explosionTimeout;
	}

	public int getExplosionDuration() {
		return explosionDuration;
	}

	public void setExplosionDuration(int explosionDuration) {
		this.explosionDuration = explosionDuration;
	}

	public int getExplosionRange() {
		return explosionRange;
	}

	public void setExplosionRange(int explosionRange) {
		this.explosionRange = explosionRange;
	}

	public int getRobotSpeed() {
		return robotSpeed;
	}

	public void setRobotSpeed(int robotSpeed) {
		this.robotSpeed = robotSpeed;
	}

	public int getPointsRobot() {
		return pointsRobot;
	}

	public void setPointsRobot(int pointsRobot) {
		this.pointsRobot = pointsRobot;
	}

	public int getPointsOpponent() {
		return pointsOpponent;
	}

	public void setPointsOpponent(int pointsOpponent) {
		this.pointsOpponent = pointsOpponent;
	}

}
