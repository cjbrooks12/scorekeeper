package com.caseybrooks.scorekeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.caseybrooks.scorekeeper.views.LoadCell;
import com.caseybrooks.scorekeeper.views.ScoreCell;

public class SaveGamesDatabase {
	private static final String DATABASE_NAME = "saved_games";
	private static final int DATABASE_VERSION = 1;
	
	//common data
	public static final String KEY_ROWID = "_id";
	public static final String KEY_GAME_ID = "game_id";
	
	//players data
	public static final String KEY_PLAYER_NAME = "player_name";
	public static final String KEY_SCORE = "score";
	public static final String KEY_HISTORY = "history";
	
	private static final String DATABASE_PLAYERS_TABLE = "players";
	
	//games data
	public static final String KEY_GAME_NAME = "game_name";
	public static final String KEY_NUMBER_OF_PLAYERS = "number_of_players";
	public static final String KEY_LEADER = "leader";
	public static final String KEY_LEADER_SCORE = "leader_score";
	private static final String DATABASE_GAME_TABLE = "games";
	
	private DbHelper helper;
	private final Context context;
	private SQLiteDatabase database;
	
	private static int gameId;
	
	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATABASE_PLAYERS_TABLE + " (" +
				KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				KEY_GAME_ID + " TEXT NOT NULL, " +
				KEY_PLAYER_NAME + " TEXT NOT NULL, " +
				KEY_SCORE + " TEXT NOT NULL, " +
				KEY_HISTORY + " TEXT NOT NULL);");
			
			db.execSQL("CREATE TABLE " + DATABASE_GAME_TABLE + " (" +
				KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				KEY_GAME_ID + " TEXT NOT NULL, " +
				KEY_GAME_NAME + " TEXT NOT NULL, " + 
				KEY_NUMBER_OF_PLAYERS + " TEXT NOT NULL, " +
				KEY_LEADER + " TEXT NOT NULL, " +
				KEY_LEADER_SCORE + " TEXT NOT NULL);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_PLAYERS_TABLE);
			db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_GAME_TABLE);
			onCreate(db);
		}	
	}
	
	public SaveGamesDatabase(Context c, int id) {
		context = c;
		gameId = id;
	}
	
	public SaveGamesDatabase open() throws SQLException {
		helper = new DbHelper(context);
		database = helper.getWritableDatabase();
		return this;
	}
	
	public void saveGame(String gamegame, ScoreCell[] players) {
		
	}
	
	public void close() {
		helper.close();
	}
    
//Database for all players
//------------------------------------------------------------------------------
	public long createPlayerEntry(String name, int score, int[] history) {
		String history_string = "";
		for(int i = 0; i < history.length; i++) {
			history_string = history_string + history[i] + " ";
		}
		
		ContentValues cv = new ContentValues();
		cv.put(KEY_GAME_ID, gameId);
		cv.put(KEY_PLAYER_NAME, name);
		cv.put(KEY_SCORE, Integer.toString(score));
		cv.put(KEY_HISTORY, history_string);
		
		
		return database.insert(DATABASE_PLAYERS_TABLE, null, cv);
	}

	public ArrayList<ScoreCell> getPlayerData() {
		String[] columns = new String[] {KEY_ROWID, KEY_GAME_ID, KEY_PLAYER_NAME, KEY_SCORE, KEY_HISTORY};
		Cursor c = database.query(DATABASE_PLAYERS_TABLE, columns, null, null, null, null, null);
		ArrayList<ScoreCell> playerCells = new ArrayList<ScoreCell>();
		
		int iRow = c.getColumnIndex(KEY_ROWID);
		int iGameId = c.getColumnIndex(KEY_GAME_ID);
		int iName = c.getColumnIndex(KEY_PLAYER_NAME);
		int iScore = c.getColumnIndex(KEY_SCORE);
		int iHistory = c.getColumnIndex(KEY_HISTORY);
		
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {	
			if(Integer.parseInt(c.getString(iGameId)) == gameId) {
				ScoreCell sc = new ScoreCell(context, c.getInt(iRow), c.getString(iName), Integer.parseInt(c.getString(iScore)), c.getString(iHistory));
				playerCells.add(sc);
			}
		}
		
		return playerCells;
	}

	public void updatePlayerEntry(int rowid, String name, int score, int[] history) {
		ContentValues cvUpdate = new ContentValues();
		String history_string = "";
		for(int i = 0; i < history.length; i++) {
			history_string = history_string + history[i] + " ";
		}
		
		cvUpdate.put(KEY_PLAYER_NAME, name);
		cvUpdate.put(KEY_SCORE, Integer.toString(score));
		cvUpdate.put(KEY_HISTORY, history_string);
		database.update(DATABASE_PLAYERS_TABLE, cvUpdate, KEY_ROWID + "=" + rowid, null);
	}

	public void deletePlayerEntry(int rowid) {
		database.delete(DATABASE_PLAYERS_TABLE, KEY_ROWID + "=" + rowid, null);
	}
	
//Database for games, which are used to select players
//------------------------------------------------------------------------------
	public long createGameEntry(String gameName, int numberOfPlayers, String leader, int leaderScore) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_GAME_ID, gameId);
		cv.put(KEY_GAME_NAME, gameName);
		cv.put(KEY_NUMBER_OF_PLAYERS, Integer.toString(numberOfPlayers));
		cv.put(KEY_LEADER, leader);
		cv.put(KEY_LEADER_SCORE, Integer.toString(leaderScore));
		
		return database.insert(DATABASE_GAME_TABLE, null, cv);
	}

	public ArrayList<LoadCell> getGameData(int gameid) {
		String[] columns = new String[] {KEY_ROWID, KEY_GAME_ID, KEY_PLAYER_NAME, KEY_SCORE, KEY_HISTORY};
		Cursor c = database.query(DATABASE_PLAYERS_TABLE, columns, null, null, null, null, null);
		ArrayList<LoadCell> savedGames = new ArrayList<LoadCell>();
		
		int iRow = c.getColumnIndex(KEY_ROWID);
		int iGameId = c.getColumnIndex(KEY_GAME_ID);
		int iGameName = c.getColumnIndex(KEY_GAME_NAME);
		int iNumberOfPlayers = c.getColumnIndex(KEY_NUMBER_OF_PLAYERS);
		int iLeader = c.getColumnIndex(KEY_LEADER);
		int iLeaderScore = c.getColumnIndex(KEY_LEADER_SCORE);
		
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {	
			if(Integer.parseInt(c.getString(iGameId)) == gameid) {
				LoadCell lc = new LoadCell(context, c.getInt(iRow), Integer.parseInt(c.getString(iGameId)), 
						c.getString(iGameName), Integer.parseInt(c.getString(iNumberOfPlayers)), c.getString(iLeader), 
						c.getString(iLeaderScore));
				savedGames.add(lc);
			}
		}
		
		return savedGames;
	}

	public void updateGameEntry(int rowid, int gameId, String gameName, int numberOfPlayers, String leader, String leaderScore) {
		ContentValues cvUpdate = new ContentValues();
		cvUpdate.put(KEY_GAME_ID, gameId);
		cvUpdate.put(KEY_GAME_NAME, gameName);
		cvUpdate.put(KEY_NUMBER_OF_PLAYERS, Integer.toString(numberOfPlayers));
		cvUpdate.put(KEY_LEADER, leader);
		cvUpdate.put(KEY_LEADER_SCORE, leaderScore);
		
		database.update(DATABASE_PLAYERS_TABLE, cvUpdate, KEY_ROWID + "=" + rowid, null);
	}

	public void deleteGameEntry(int rowid) {
		database.delete(DATABASE_PLAYERS_TABLE, KEY_ROWID + "=" + rowid, null);
	}
}
