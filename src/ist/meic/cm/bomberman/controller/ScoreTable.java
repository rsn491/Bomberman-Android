package ist.meic.cm.bomberman.controller;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.util.HashMap;

public class ScoreTable implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6020673959591592054L;
	private HashMap<Integer,Integer> scoreMap;

	@SuppressLint("UseSparseArrays")
	public ScoreTable() {
		this.scoreMap = new HashMap<Integer, Integer>();
	}

	public void addPlayer(int playerId) {
		scoreMap.put(playerId, 0);
	}

	public int getScore(int playerId) {
		if(scoreMap.containsKey(playerId))
			return scoreMap.get(playerId);
		return 0;
	}

	public void killedGhost(int playerId) {
		int oldscore;

		if(scoreMap.containsKey(playerId)) {
			oldscore = scoreMap.get(playerId);
			scoreMap.put(playerId, oldscore + 1);
		}	
	}

	public void killedBomberman(int playerId) {
		int oldscore;

		if(scoreMap.containsKey(playerId)) {
			oldscore = scoreMap.get(playerId);
			scoreMap.put(playerId, oldscore + 1);
		}	
	}

}
