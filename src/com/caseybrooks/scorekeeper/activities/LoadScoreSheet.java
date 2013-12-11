package com.caseybrooks.scorekeeper.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caseybrooks.scorekeeper.R;
import com.caseybrooks.scorekeeper.SaveGame;
import com.caseybrooks.scorekeeper.ScoreCellSaveHelper;
import com.caseybrooks.scorekeeper.views.LoadCell;

public class LoadScoreSheet extends Activity {

	LinearLayout ll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_score_sheet);
		
		getFiles();
	}

	private void getFiles() {
		try {
			ll = (LinearLayout) findViewById(R.id.loadSheetLayout);
		
			List<String> paths = new ArrayList<String>();
			String path = Environment.getExternalStorageDirectory().getPath() + "/ScoreKeeper/saves";
			File directory = new File(path);
			File[] files = directory.listFiles();
			if(directory.exists() && files.length > 0) {
				for (int i = 0; i < files.length; ++i) {
				    paths.add(files[i].getAbsolutePath());
				    
				    SaveGame sg = new SaveGame();
				    Serializer serializer = new Persister();
					sg = serializer.read(SaveGame.class, files[i]);
					
					ArrayList<ScoreCellSaveHelper> cellHelpers = new ArrayList<ScoreCellSaveHelper>();
					cellHelpers = sg.getPlayers();
					
					List<Integer> playerScores = new ArrayList<Integer>();
					for(int j = 0; j < cellHelpers.size(); j++) {
						playerScores.add(cellHelpers.get(j).getScore());
					}
					Integer max = Collections.max(playerScores);
					int index = playerScores.indexOf(max);
					
					
					LoadCell lc = new LoadCell(this);
					lc.setGameName(sg.getGameName());
					lc.setFileName(paths.get(i));
					lc.setNumberOfPlayers(cellHelpers.size());
					//lc.setLeader(cellHelpers.get(index).getName() + " leads with " + max + " points");
					lc.setOnClickListener(loadCellListener);
					lc.setOnLongClickListener(longClick);
					ll.addView(lc);
				}
			}
			else {
				TextView tv = new TextView(this);
				tv.setText("No Saved Games");
				tv.setTextSize(36);
				ll.addView(tv);
			}
		}
		catch(Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	private OnClickListener loadCellListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			LoadCell lc = (LoadCell) v;
			loadScores(lc.getFileName());
		}
	};
	
	private OnLongClickListener longClick = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			final LoadCell lc = (LoadCell) v;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			builder.setCancelable(false);
			builder.setTitle("Delete \"" + lc.getGameName() + "\"?");
			builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String name = lc.getGameName();
					new File(lc.getFileName()).delete();					
					Toast.makeText(lc.getContext(), "Successfully deleted \"" + name + "\"", Toast.LENGTH_LONG).show();
					ll.removeView(lc);
					lc.invalidate();
				}
		    });
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					
				}
		    });
			
			AlertDialog dialog = builder.create();
			dialog.show();
			
			return true;
		}
	};

	private void loadScores(String path) {
		try {
			ArrayList<String> names = new ArrayList<String>();
			ArrayList<Integer> scores = new ArrayList<Integer>();
			ArrayList<String> histories = new ArrayList<String>();
			ArrayList<ScoreCellSaveHelper> cellHelpers = new ArrayList<ScoreCellSaveHelper>();
			File folder = new File(path);
			if(!folder.exists()) {
				folder.mkdirs();
			}
			if(folder.exists()) {
				SaveGame sg = new SaveGame();
			
				Serializer serializer = new Persister();
				sg = serializer.read(SaveGame.class, folder);
				
				cellHelpers = sg.getPlayers();
				
				for(int i = 0; i < cellHelpers.size(); i++) {
					names.add(cellHelpers.get(i).getName());
					scores.add(cellHelpers.get(i).getScore());
					histories.add(cellHelpers.get(i).getHistory());
				}
				
				Intent intent = new Intent(this, ScoreSheet.class);
				Bundle extras = new Bundle();
				extras.putStringArrayList("CELL_NAMES", names);
				extras.putIntegerArrayList("CELL_SCORES", scores);
				extras.putStringArrayList("CELL_HISTORIES", histories);
				extras.putBoolean("LOAD_SHEET", true);
				extras.putIntArray("EXTRA_TOGGLE_VALUES", sg.getButtons());
				extras.putInt("EXTRA_BUTTONS", sg.getButtonConfig());
				extras.putString("GAME_NAME", sg.getGameName());
				extras.putInt("GAME_ID", sg.getGameId());
				intent.putExtras(extras);
	
				Toast.makeText(this, "Load Successful", Toast.LENGTH_LONG).show();
				startActivity(intent);
			}
		}
		catch(Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

//Contextual ActionBar
//------------------------------------------------------------------------------
//	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
//
//	    @Override
//	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//	        MenuInflater inflater = mode.getMenuInflater();
//	        inflater.inflate(R.menu.context_menu, menu);
//	        return true;
//	    }
//
//	    @Override
//	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//	        return false; // Return false if nothing is done
//	    }
//
//	    // Called when the user selects a contextual menu item
//	    @Override
//	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//	        switch (item.getItemId()) {
//	            case R.id.contextual_delete:
//	                mode.finish();
//	                return true;
//	            case R.id.contextual_share:
//	                mode.finish();
//	                return true;
//	            default:
//	                return false;
//	        }
//	    }
//
//	    // Called when the user exits the action mode
//	    @Override
//	    public void onDestroyActionMode(ActionMode mode) {
//	        mActionMode = null;
//	    }
//	};
//	
}
