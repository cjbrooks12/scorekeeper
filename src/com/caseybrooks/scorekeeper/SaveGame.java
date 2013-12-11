package com.caseybrooks.scorekeeper;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.caseybrooks.scorekeeper.views.ScoreCell;

@Root(name="game")
public class SaveGame {
//Data Members
//------------------------------------------------------------------------------
	@Attribute(name="id")
	private int gameId;
	
	@Element
	private String gameName;
	
	@Element(required=false)
	private String gameType;
	
	@ElementList
	private ArrayList<ScoreCellSaveHelper> players = new ArrayList<ScoreCellSaveHelper>();
	
	@ElementArray(name="buttons")
	private int[] gameButtons;
	
	@Element
	private int buttonConfig;
	
//Setters
//------------------------------------------------------------------------------
	public void setId(int id) {
		gameId = id;
	}
	
	public void setGameType(String n) {
		gameType = n;
	}
	
	public void setGameName(String n) {
		gameName = n;
	}
	
	public void addPlayers(ScoreCell sc) {
		ScoreCellSaveHelper cell = new ScoreCellSaveHelper();
		cell.setName(sc.getName());
		cell.setScore(sc.getScore());
		cell.setHistory(sc.getHistory());
		players.add(cell);
	}
	
	public void setButtons(int[] b) {
		gameButtons = b;
	}
	
	public void setButtonConfig(int b) {
		buttonConfig = b;
	}
	
//Getters
//------------------------------------------------------------------------------
	public int getGameId() {
		return gameId;
	}
	
	public String getGameName() {
		return gameName;
	}
	
	public String getGameType() {
		return gameType;
	}
	
	public ArrayList<ScoreCellSaveHelper> getPlayers() {
		return players;
	}
	
	public int[] getButtons() {
		return gameButtons;
	}
	
	public int getButtonConfig() {
		return buttonConfig;
	}
}