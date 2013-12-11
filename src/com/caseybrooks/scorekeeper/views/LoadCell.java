package com.caseybrooks.scorekeeper.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caseybrooks.scorekeeper.R;

public class LoadCell extends LinearLayout {
//Data Members
//------------------------------------------------------------------------------
	private TextView gameName, numberOfPlayers, gameLeader;
	private TextView[] players;
	String fileName;	
	private int SQLRowId, gameId;

	//Constructors
//------------------------------------------------------------------------------
	public LoadCell(Context context) {
		super(context);
		
		LayoutInflater inflater = LayoutInflater.from(context);
	    inflater.inflate(R.layout.load_cell, this);
		
		loadViews();
	}
	
	public LoadCell(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflater = LayoutInflater.from(context);
	    inflater.inflate(R.layout.load_cell, this);
		
		loadViews();
	}
	
	public LoadCell(Context context, int rowid, int gameid, String gamename, int numberofplayers, String leader, String leaderscore) {
		super(context);
		
		LayoutInflater inflater = LayoutInflater.from(context);
	    inflater.inflate(R.layout.load_cell, this);
		
	    gameId = gameid;
	    SQLRowId = rowid;
	    
		loadViews();
		setFileName(gamename);
		setNumberOfPlayers(numberofplayers);
		setLeader(leader, leaderscore);
	}
	
	private void loadViews() {
		gameName = (TextView) findViewById(R.id.loadGameName);
		numberOfPlayers = (TextView) findViewById(R.id.loadPlayers);
		gameLeader = (TextView) findViewById(R.id.loadLeader);
	}
	
//Public Getters
//------------------------------------------------------------------------------
	public String getFileName() {
		return fileName;
	}
	
	public String getGameName() {
		return gameName.getText().toString();
	}
	
	public String[] getPlayers() {
		String[] returnPlayers = new String[players.length];
		for(int i = 0; i < players.length; i++) {
			returnPlayers[i] = players[i].getText().toString();
		}
		return returnPlayers;
	}
	
	public String getLeader() {
		return gameLeader.getText().toString();
	}
	
	public int getSQLRowId() {
		return SQLRowId;
	}


//Public Setters
//------------------------------------------------------------------------------
	public void setFileName(String s) {
		fileName = s;
	}
	
	public void setGameName(String s) {
		gameName.setText(s);
	}
	
	public void setNumberOfPlayers(int s) {
		numberOfPlayers.setText(s + " Players");
	}
	
	public void setLeader(String leader, String leaderscore) {
		gameLeader.setText(leader + " leads with " + leaderscore + " points.");
	}
	
	public void setSQLRowId(int sQLRowId) {
		SQLRowId = sQLRowId;
	}

	
}
