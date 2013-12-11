package com.caseybrooks.scorekeeper.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.caseybrooks.scorekeeper.R;
import com.caseybrooks.scorekeeper.SaveGamesDatabase;

public class Main extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		SaveGamesDatabase sgdb = new SaveGamesDatabase(this, 10);
		sgdb.open();
		int[] stuff = {1, 2, 3};
		sgdb.createPlayerEntry("Casey", 9000, stuff);
		sgdb.createGameEntry("My Game", 4, "Casey", 9000);
		sgdb.close();
		
	}
	
	public void activitySelection(View v) {
		Intent intent;
		switch(v.getId()) {
		case R.id.launchNewScoresheet:
			intent = new Intent(this, GenerateScoreSheet.class);
			startActivity(intent);
			break;
		case R.id.launchSavedScoresheet:
			intent = new Intent(this, LoadScoreSheet.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
