package ist.meic.cm.bomberman.p2p.manager;

import java.util.ArrayList;

import ist.meic.cm.bomberman.controller.MapController;

public class Game {

	private MapController mapController;
	private ArrayList<String> players;
	private boolean[] ready;
	private int maxNumPlayers;
	private int duration;

	public Game(MapController mapController, int duration) {
		this.mapController = mapController;
		players = new ArrayList<String>();
		ready = new boolean[4];
		this.duration = duration;
	}

	public void setReady(int playerId) {
		ready[playerId] = true;
	}

	public MapController getMapController() {
		return mapController;
	}

	public void setMapController(MapController mapController) {
		this.mapController = mapController;
	}

	public ArrayList<String> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<String> players) {
		this.players = players;
		maxNumPlayers++;
	}

	public int getMaxNumPlayers() {
		return maxNumPlayers;
	}

	public boolean[] getReady() {
		return ready;
	}

	public int removePlayer() {
		return --maxNumPlayers;
	}

	public int getDuration() {
		return duration;
	}

	public boolean is4Players() {
		return mapController.is4Players();
	}
}
