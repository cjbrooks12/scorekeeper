package com.caseybrooks.scorekeeper;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="player")
public class ScoreCellSaveHelper {
	
//data members
//------------------------------------------------------------------------------
		@Element
		private String name;
		@Element
		private int score;
		@Element
		private String history;
		
		
//constructors
//------------------------------------------------------------------------------
		public ScoreCellSaveHelper() {
			name = "New Player";
			score = 0;
			history = "";
		}
		
		public ScoreCellSaveHelper(String n, int s, String h) {
			name = n;
			score = s;
			history = h;
		}
		
//setters
//------------------------------------------------------------------------------
		public void setName(String n) {
			name = n;
		}
		
		public void setScore(int s) {
			score = s;
		}
		
		public void setHistory(String h) {
			history = h;
		}
		
//getters
//------------------------------------------------------------------------------
		public String getName() {
			return name;
		}
		
		public int getScore() {
			return score;
		}
		
		public String getHistory() {
			return history;
		}
}
