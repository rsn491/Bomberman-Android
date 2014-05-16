package ist.meic.cm.bomberman.maps;

import java.io.Serializable;

public class MapModels implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5873259269817872119L;

	private final static String MAP1 = "WWWWWWWWWWWWWWWWWWWWn" + 
			"W-2-----G----O-----Wn" + 
			"WWOW-W-W-W-W-W-W-W-Wn" + 
			"W-O--------G-------Wn" + 
			"WWOWOW-W-W-W-WOW-W-Wn" + 
			"W--O--------O-G--3-Wn" +
			"WWOW-W-W-W-W-W-W-W-Wn" + 
			"W-OOOO---1-------O-Wn" + 
			"WWOWOWOWOW-W-W-W-W-Wn" + 
			"W--OOOO--G--OOO----Wn" + 
			"WW-W-WOWOW-WOWOWOWOWn" + 
			"W------OOO--OOO-OG-Wn" + 
			"WWWWWWWWWWWWWWWWWWWW";
	private final static String MAP2 = "WWWWWWWWWWWWWWWWWWWWn" + 
			"W-2-----G----O-----Wn" + 
			"WWOW-W-W-W-W-W-W-W-Wn" + 
			"W-O--O-----G-------Wn" + 
			"WWOWOW-W-W-W-WOW-W-Wn" + 
			"W--O-----1--O-G---3Wn" +
			"WW-W-W-W-W-W-W-W-W-Wn" + 
			"W--O-O--O--O-O---O-Wn" + 
			"WWOWOWOWOW-W-W-W-W-Wn" + 
			"W--O-OO--G---W-----Wn" + 
			"WW-WGWOWOW-WOWOWOWOWn" + 
			"W4------GOO-O-O-OG-Wn" + 
			"WWWWWWWWWWWWWWWWWWWW";
	private final static String MAP3 = "WWWWWWWWWWWWWWWWWWWWn" + 
			"W--O--------O-G---3Wn" + 
			"WWOW-W-W-W-W-W-W-W-Wn" + 
			"W-OOOO---1--------OWn" + 
			"WWOWOWOWOW-W-W-W-W-Wn" + 
			"W-2-----G----O-----Wn" +
			"WWOW-W-W-W-W-W-W-W-Wn" + 
			"W-O--------G-------Wn" + 
			"WWOWOW-W-W-W-WOW-W-Wn" + 
			"W--OOOO--G--OOO----Wn" + 
			"WW-W-WOWOW-WOWOWOWOWn" + 
			"W-----4OOO--OO----GWn" + 
			"WWWWWWWWWWWWWWWWWWWW";
	
	private String currentMap;
	private boolean fourPlayers;

	public MapModels(String levelName) {
		if(levelName.equals("Level1")){
			currentMap = MAP1;
			fourPlayers = false;
		} else if(levelName.equals("Level2")){
			currentMap = MAP2;
			fourPlayers = true;
		} else if(levelName.equals("Level3")){
			currentMap = MAP3;
			fourPlayers = true;
		}
	}

	public  String getMap() {
		return currentMap;
	}

	public boolean is4Players() {
		
		return fourPlayers;
	}
}
