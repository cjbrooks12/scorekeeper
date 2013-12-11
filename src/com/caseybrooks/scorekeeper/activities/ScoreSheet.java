package com.caseybrooks.scorekeeper.activities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.caseybrooks.scorekeeper.R;
import com.caseybrooks.scorekeeper.SaveGame;
import com.caseybrooks.scorekeeper.views.ScoreCell;

public class ScoreSheet extends Activity {

//Class Variable Initialization
//------------------------------------------------------------------------------
	LinearLayout addButtonsLayout, minusButtonsLayout, cellsLayout;
	List<ScoreCell> playersArray;
	int buttonConfig, gameId;
	int[] buttonValues;
	Button[] addButtons, minusButtons;
	String gameName;
	//ActionBar menuBar;
	
	final int[] defaultButtonValues = {1, 5, 10, 25, 100};
	
//Activity Lifecycle
//------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_sheet);
		
		getExtras();	
	}

//Intent Extras
//------------------------------------------------------------------------------
	private void getExtras() {
		Bundle extras = getIntent().getExtras();
		gameId = 0;
		//menuBar = getActionBar();
		//menuBar.setDisplayHomeAsUpEnabled(true);
		

		extraButtons(extras);
		extraScoreCells(extras);
		extraGameInfo(extras);
	}
		
	private void extraButtons(Bundle extras) {
		if(extras.containsKey("EXTRA_BUTTONS")) {
			buttonConfig = extras.getInt("EXTRA_BUTTONS");
		}
		else {
			buttonConfig = 31;
		}
		if(extras.containsKey("EXTRA_TOGGLE_VALUES")) {
			buttonValues = extras.getIntArray("EXTRA_TOGGLE_VALUES");
		}
		else {
			buttonValues = defaultButtonValues;
		}
		createButtons();
	}
		
	private void extraScoreCells(Bundle extras) {
		cellsLayout = (LinearLayout) findViewById(R.id.playerCells);
		playersArray = new ArrayList<ScoreCell>();
		if(extras.containsKey("EXTRA_PLAYERS")) {
			createPlayerCells(extras.getInt("EXTRA_PLAYERS"));
		}
		else if(extras.containsKey("LOAD_SHEET")) {
			ArrayList<String> names = new ArrayList<String>();
			ArrayList<Integer> scores = new ArrayList<Integer>();
			ArrayList<String> histories = new ArrayList<String>();
			names = extras.getStringArrayList("CELL_NAMES");
			scores = extras.getIntegerArrayList("CELL_SCORES");
			histories = extras.getStringArrayList("CELL_HISTORIES");
			
			for(int i = 0; i < names.size(); i++) {
				addCell(i, names.get(i), scores.get(i), histories.get(i));
			}
		}
		else {
			createPlayerCells(4);
		}
	}
	
	private void extraGameInfo(Bundle extras) {
		if(extras.containsKey("GAME_NAME")) {
			gameName = extras.getString("GAME_NAME");
			//menuBar.setTitle(gameName);
		}
		else {
			gameName = null;
			//menuBar.setTitle("Score Sheet");
		}
		
		if(extras.containsKey("GAME_ID")) {
			gameId = extras.getInt("GAME_ID");
		}
	}
	
//Initialization of Views
//------------------------------------------------------------------------------
	private void createButtons() {
    	addButtonsLayout = (LinearLayout) findViewById(R.id.addButtonsLayout);
    	minusButtonsLayout = (LinearLayout) findViewById(R.id.minusButtonsLayout);
    	addButtons = new Button[5];
    	minusButtons = new Button[5];
    	LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 75, 1.0f);
    	
    	for(int i = 0; i < 5; i++) {
    		if(getBit(buttonConfig, i)) {			
	    		addButtons[i] = new Button(this);
		    	addButtons[i].setId(i);
		    	addButtons[i].setText(Integer.toString(buttonValues[i]));
		    	addButtons[i].setLayoutParams(param);
		    	addButtons[i].setGravity(Gravity.CENTER); 
		    	addButtons[i].setTextSize(14);
		    	addButtons[i].setOnClickListener(buttonClickListener);
		    	addButtons[i].setBackgroundResource(R.drawable.button_card_background);
		    	addButtonsLayout.addView(addButtons[i]);
		    	
		    	minusButtons[i] = new Button(this);
		    	minusButtons[i].setId(i+5);
		    	minusButtons[i].setText(Integer.toString(buttonValues[i] * -1));
		    	minusButtons[i].setLayoutParams(param);
		    	minusButtons[i].setGravity(Gravity.CENTER); 
		    	minusButtons[i].setTextSize(14);
		    	minusButtons[i].setOnClickListener(buttonClickListener);
		    	minusButtons[i].setBackgroundResource(R.drawable.button_card_background);
		    	minusButtonsLayout.addView(minusButtons[i]);
    		}
    	}
    }
	
	private void createPlayerCells(int numberOfPlayers) {
		for(int i = 0; i < numberOfPlayers; i++) {
			addCell(i, "Player " + (i+1), 0, "0 ");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.score_sheet, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_undo:
	            return true;
	        case R.id.action_add:
	        	addCell(playersArray.size(), "Player " + (playersArray.size()+1), 0, "0 ");       	
	            return true;
	        case R.id.action_redo:
	            return true;
	        case R.id.action_save:
	        	saveScores();
	            return true;
	        case R.id.action_save_as:
	        	gameName = null;
	        	saveScores();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
//View Event Listeners
//------------------------------------------------------------------------------	
	private OnClickListener buttonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			for(int i = 0; i < playersArray.size(); i++) {
				if(playersArray.get(i).isChecked()) {
					if(id <= 4) {
						int addedScore = Integer.parseInt(addButtons[id].getText().toString());
						playersArray.get(i).addScore(Integer.toString(addedScore));
					}
					if(id > 4) {
						int addedScore = Integer.parseInt(minusButtons[id-5].getText().toString());
						playersArray.get(i).addScore(Integer.toString(addedScore));
					}
				}
			}
		}
	};
	
	private OnClickListener cellClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(playersArray.get(id).isChecked()) {
				playersArray.get(id).setUnchecked();
	    	}
	    	else {
	    		playersArray.get(id).setChecked();
	    	}
			for(int i = 0; i < playersArray.size(); i++) {
				if(playersArray.get(i) != playersArray.get(id)) {
					playersArray.get(i).setUnchecked();
				}
			}
		}
	};
	
	private OnLongClickListener longCellClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			int id = v.getId();
			Vibrator vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibrate.vibrate(5);
			playersArray.get(id).setChecked();
			return true;
		}
	};	
	
//Helper Methods
//------------------------------------------------------------------------------ 	 		
	public boolean getBit(int number, int position) {
	   if(((number >> position) & 1) == 1) {
		   return true;
	   }
	   else return false;
	}
	
	public void addCell(int cellId, String cellName, int cellScore, String cellHistory) {
		//SwipeGestureListener gestureListener = new SwipeGestureListener(this);
		ScoreCell sc = new ScoreCell(this);
		sc.setId(cellId);
		sc.setName(cellName);
		sc.setScore(cellScore);
		sc.setHistory(cellHistory);
		sc.setOnClickListener(cellClickListener);
		sc.setOnLongClickListener(longCellClickListener);
		//sc.setOnTouchListener(gestureListener);
		playersArray.add(sc);
		cellsLayout.addView(sc);
		
		
		ScrollView sv = (ScrollView) findViewById(R.id.scroll);
		sv.fullScroll(ScrollView.FOCUS_DOWN);
	}
	
	public void saveScores() {
		try {
			String path = Environment.getExternalStorageDirectory().getPath() + "/ScoreKeeper/saves";
			File folder = new File(path);
			Boolean append = false;
				
			if(!folder.exists()) {
				folder.mkdirs();
			}
			if(folder.exists()) {
				File xmlFile = new File(path, "savedgames" + gameId + ".xml");
				final OutputStream out = new BufferedOutputStream(new FileOutputStream(xmlFile, append));				
				
				if(gameName == null) {
					final EditText inputBox = new EditText(this);
					inputBox.setHint("Score Sheet");
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
					alertDialogBuilder.setCancelable(true);
					alertDialogBuilder.setTitle("Save As");
					alertDialogBuilder.setView(inputBox);
					alertDialogBuilder.setNeutralButton("Submit",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
								try {
									gameName = "Score Sheet";
									SaveGame sg = new SaveGame();
									sg.setGameType("Tally");
									sg.setId(gameId);
									if(inputBox.getText().toString().trim().length() > 0) {
										gameName = inputBox.getText().toString().trim();
									}
									//menuBar.setTitle(gameName);
									
									sg.setGameName(gameName);
									sg.setButtons(buttonValues);
									sg.setButtonConfig(buttonConfig);
									
									for(int i = 0; i < playersArray.size(); i++) {
										sg.addPlayers(playersArray.get(i));
									}
									
									Serializer serializer = new Persister();
									serializer.write(sg, out);
									
						            
						            Toast.makeText(getBaseContext(), "Save Succssful", Toast.LENGTH_SHORT).show();
								}
								catch (Exception e) {
									e.printStackTrace();
									Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
								}
						}
					});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}
				else {
					try {
						SaveGame sg = new SaveGame();
						sg.setGameType("Tally");
						sg.setId(gameId);
						
						sg.setGameName(gameName);
						sg.setButtons(buttonValues);
						sg.setButtonConfig(buttonConfig);
						
						for(int i = 0; i < playersArray.size(); i++) {
							sg.addPlayers(playersArray.get(i));
						}
						
						Serializer serializer = new Persister();
			            serializer.write(sg, out);
						
			            Toast.makeText(getBaseContext(), "Save Succssful", Toast.LENGTH_SHORT).show();
					}
					catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
					}
				}
			}
		}	
		catch(Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
//Swipe Gesture Stuff
//------------------------------------------------------------------------------
	
	/*class SwipeGestureListener extends SimpleOnGestureListener implements OnTouchListener {
		Context context;
		GestureDetector gDetector;
		static final int SWIPE_MIN_DISTANCE = 120;
		static final int SWIPE_MAX_OFF_PATH = 250;
		static final int SWIPE_THRESHOLD_VELOCITY = 3;
		
		public SwipeGestureListener() {
			super();
		}

		public SwipeGestureListener(Context context) {
			this(context, null);
		}
		
		public SwipeGestureListener(Context context, GestureDetector gDetector) {
			if (gDetector == null)
			 gDetector = new GestureDetector(context, this);
			
			this.context = context;
			this.gDetector = gDetector;
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
				return false;
			}
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
				Toast.makeText(ScoreSheet.this, "swipe RightToLeft", Toast.LENGTH_SHORT).show();
			}
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
				Toast.makeText(ScoreSheet.this, "swipe LeftToright", Toast.LENGTH_SHORT).show();
			}
		return super.onFling(e1, e2, velocityX, velocityY);
		}
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			return gDetector.onTouchEvent(event);
		}
		
		public GestureDetector getDetector() {
			return gDetector;
		}
	}*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
