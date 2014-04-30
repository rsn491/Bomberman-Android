package ist.meic.cm.bomberman.maps;

import java.io.Serializable;

public class MapModels implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5873259269817872119L;

	private final static String MAP1 = "WWWWWWWWWWWWWWWWWWWWn" + 
			"W1------G----O-----Wn" + 
			"WWOW-W-W-W-W-W-W-W-Wn" + 
			"W-O--------G-------Wn" + 
			"WWOWOW-W-W-W-WOW-W-Wn" + 
			"W--O--------O-G--3-Wn" +
			"WWOW-W-W-W-W-W-W-W-Wn" + 
			"W-OOOO---2-------O-Wn" + 
			"WWOWOWOWOW-W-W-W-W-Wn" + 
			"W--OOOO--G--OOO----Wn" + 
			"WW-W-WOWOW-WOWOWOW-Wn" + 
			"W------OOO--OOO-OG-Wn" + 
			"WWWWWWWWWWWWWWWWWWWW";
	
	private String currentMap;

	public MapModels(String levelName) {
		if(levelName.equals("Level1"))
			currentMap = MAP1;
	}

	public  String getMap() {
		return currentMap;
	}

	
}
