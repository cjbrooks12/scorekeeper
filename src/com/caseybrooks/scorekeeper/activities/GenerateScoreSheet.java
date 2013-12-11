package com.caseybrooks.scorekeeper.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.caseybrooks.scorekeeper.R;

public class GenerateScoreSheet extends Activity implements OnItemSelectedListener {

//Class Variables
//------------------------------------------------------------------------------
	ToggleButton[] toggles = new ToggleButton[5];
	Button generateButton, resetButton;
	int passed = 0;
	int[] toggleValues = {1, 5, 10, 25, 100};
	final int[] originalValues = {1, 5, 10, 25, 100};
	final Context context = this;
	int players;
	
	public static final String PREFS_NAME = "ScoreKeeperPreferences";

//Activity Lifecycle
//------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generate_score_sheet);
//		setupActionBar();		
			
		initialize();	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		for(int i = 0; i < 5; i++) {
			if(toggles[i].isChecked()) toggles[i].setChecked(false);
		}
		passed = 0;
	}

//Initialize Activity Layout
//------------------------------------------------------------------------------
	private void initialize() {
		Spinner spinner = (Spinner) findViewById(R.id.numberOfPlayersSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.numbers_1_8, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(3);
		spinner.setOnItemSelectedListener(this);
		
		toggles[0] 	= (ToggleButton) findViewById(R.id.generateToggle0);
		toggles[1] 	= (ToggleButton) findViewById(R.id.generateToggle1);
		toggles[2]	= (ToggleButton) findViewById(R.id.generateToggle2);
		toggles[3]	= (ToggleButton) findViewById(R.id.generateToggle3);
		toggles[4]	= (ToggleButton) findViewById(R.id.generateToggle4);

		for(int i = 0; i < 5; i++) {
			toggles[i].setOnClickListener(toggleClicked);
			toggles[i].setOnLongClickListener(toggleEdit);
			toggles[i].setId(i);
			
		}
		
		generateButton = (Button) findViewById(R.id.generateButton);
		generateButton.setOnClickListener(buttonClicked);
		
		resetButton = (Button) findViewById(R.id.generateReset);
		resetButton.setOnClickListener(resetToggles);
	}

//	private void setupActionBar() {
//		getActionBar().setDisplayHomeAsUpEnabled(true);
//	}
	
	//Click LIsteners
	//------------------------------------------------------------------------------
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		players = Integer.parseInt((String)parent.getItemAtPosition(pos));
    }
	
    public void onNothingSelected(AdapterView<?> parent) {   	
		players = 4;
    }
	
	private OnClickListener toggleClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
		    boolean on = ((ToggleButton) v).isChecked();
	    	if (on) {
		    	passed = (int) (passed + Math.pow(2,v.getId()));
		    }
		    else {
		    	passed = (int) (passed - Math.pow(2,v.getId()));
		    }
		}
	};
	
	private OnClickListener resetToggles = new OnClickListener() {
		@Override
		public void onClick(View v) {
			for(int i = 0; i < 5; i++) {
				toggleValues[i] = originalValues[i];
				toggles[i].setTextOn(Integer.toString(toggleValues[i]));
				toggles[i].setTextOff(Integer.toString(toggleValues[i]));
				toggles[i].setChecked(false);
			}
			passed = 0;
		}
	};
	
	private OnClickListener buttonClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			int gameId = settings.getInt("NEW_GAME_ID", 1);
			
			Intent intent = new Intent(getBaseContext(), ScoreSheet.class);
			Bundle extras = new Bundle();
			extras.putInt("EXTRA_BUTTONS", passed);
			extras.putInt("EXTRA_PLAYERS", players);
			extras.putIntArray("EXTRA_TOGGLE_VALUES", toggleValues);
			extras.putInt("GAME_ID", gameId);
			
			intent.putExtras(extras);
			settings.edit().putInt("NEW_GAME_ID", gameId + 1).commit();
			startActivity(intent);
		}
	};
	
	private OnLongClickListener toggleEdit = new OnLongClickListener() {
	
		@Override
		public boolean onLongClick(final View v) {
			((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(5);
			final ToggleButton tb = (ToggleButton) v;
			final EditText inputBox = new EditText(context);
			inputBox.setInputType(InputType.TYPE_CLASS_NUMBER);
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setCancelable(true);
			alertDialogBuilder.setTitle("Enter Button Value");
			alertDialogBuilder.setView(inputBox);
			alertDialogBuilder.setNeutralButton("Submit",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					if(inputBox.getText().toString().trim().length() > 0) {
						tb.setTextOn(inputBox.getText());
						tb.setTextOff(inputBox.getText());
						toggleValues[v.getId()] = Integer.parseInt(inputBox.getText().toString());
						
						if(tb.isChecked()) {
							((ToggleButton) v).setChecked(true);
						}
						else {
							((ToggleButton) v).setChecked(false);
						}
					}
					else {
						
					}
				}
			});
			
			
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			
			return false;
		}
	};
//------------------------------------------------------------------------------
}
