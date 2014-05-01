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

	// KEYS
	public static final String MAP = "map";
	public static final String DURATION = "duration";
	public static final String RS = "robotSpeed";
	public static final String ET = "explosionTimeout";
	public static final String ED = "explosionDuration";
	public static final String ER = "explosionRange";
	public static final String PR = "pointsRobot";
	public static final String PO = "pointsOpponent";

	// DefaultValues
	public static final String MAP_DEFAULT = "Level1";
	public static final String DURATION_DEFAULT = "120";
	public static final String RS_DEFAULT = "1";
	public static final String ET_DEFAULT = "3";
	public static final String ED_DEFAULT = "4";
	public static final String ER_DEFAULT = "1";
	public static final String PR_DEFAULT = "1";
	public static final String PO_DEFAULT = "1";

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Map: " + levelName + "\n");
		builder.append("Game Duration: " + gameDuration + "\n");
		builder.append("Robot Speed: " + robotSpeed + "\n");
		builder.append("Explosion Timeout: " + explosionTimeout + "\n");
		builder.append("Explosion Duration: " + explosionDuration + "\n");
		builder.append("Explosion Range: " + explosionRange + "\n");
		builder.append("Points Robot: " + pointsRobot + "\n");
		builder.append("Points Opponent: " + pointsOpponent);
		return builder.toString();
	}
}
