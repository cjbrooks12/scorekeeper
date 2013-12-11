package com.caseybrooks.scorekeeper.views;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Vibrator;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caseybrooks.scorekeeper.R;

public class ScoreCell extends LinearLayout {

//Class Variable Initialization
//------------------------------------------------------------------------------
	private TextView cellName, cellScore, cellHistory;
	private View cellBarTwo, cellBarThree;
	private LinearLayout cellScoreLayout, cardLayout;
	private boolean cellSelected, scoreClickable;
	private int temporaryScore, SQLRowId;
	private Context cellContext = this.getContext();
	
	//data for Simple XML serialization
	private String name;
	private int score;
	private String history;
	 
//Public Constructors
//------------------------------------------------------------------------------
	public ScoreCell(Context context) {
        super(context);
        
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.score_cell, this);
 
        loadViews();
        
        setName("New Player");
        setScore(0);
        setHistory("0 ");
    }
	
	public ScoreCell(Context context, int rowId, String name, int score, String history) {
        super(context);
        
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.score_cell, this);
 
        loadViews();
        
        SQLRowId = rowId;
        setName(name);
        setScore(score);
        setHistory(history);
    }
 
    public ScoreCell(Context context, AttributeSet attrs) {
        super(context, attrs);
 
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.score_cell, this);
 
        loadViews();
    }
 
//Initialization
//------------------------------------------------------------------------------
    private void loadViews() {
    	cellBarTwo = findViewById(R.id.cellBarTwo);
    	cellBarThree = findViewById(R.id.cellBarThree);
    	cellName = (TextView) findViewById(R.id.cellName);
        cellScore = (TextView) findViewById(R.id.cellScore);
        cellHistory = (TextView) findViewById(R.id.cellHistory);
        cellScoreLayout = (LinearLayout) findViewById(R.id.cellScoreLayout);
        cardLayout = (LinearLayout) findViewById(R.id.cellLayout);
        
        cellName.setOnLongClickListener(cellNameLongListener);
        cellScore.setOnClickListener(cellScoreListener);
        cellScore.setOnLongClickListener(cellScoreLongListener);
        
        history = "";
        temporaryScore = 0;
        score = 0;
        cellSelected = false;
		scoreClickable = false;
    }
    
    private OnLongClickListener cellNameLongListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			Vibrator vibrate = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
			vibrate.vibrate(5);
			
			final EditText inputBox = new EditText(cellContext);
			inputBox.setHint("name");
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cellContext);
			alertDialogBuilder.setCancelable(true);
			alertDialogBuilder.setTitle("Enter Player Name");
			alertDialogBuilder.setView(inputBox);
			alertDialogBuilder.setNeutralButton("Submit",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					if(inputBox.getText().toString().trim().length() > 0) {
						setName(inputBox.getText().toString().trim());
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
	
	private OnClickListener cellScoreListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(scoreClickable) {
				cellScore.removeCallbacks(runnable);
				saveScore();
			}
		}
	};
	
	private OnLongClickListener cellScoreLongListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			Vibrator vibrate = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
			vibrate.vibrate(5);
		    
		    final EditText inputBox = new EditText(cellContext);
			inputBox.setInputType(InputType.TYPE_CLASS_NUMBER);
			inputBox.setHint("score");
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cellContext);
			alertDialogBuilder.setCancelable(true);
			alertDialogBuilder.setTitle("Enter Player Score");
			alertDialogBuilder.setView(inputBox);
			alertDialogBuilder.setNeutralButton("Submit",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					if(inputBox.getText().toString().trim().length() > 0) {
						setScore(Integer.parseInt(inputBox.getText().toString().trim()));
						setHistory(inputBox.getText().toString().trim());
						temporaryScore = 0;
					}
					else {
						
					}
				}
			});
			
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		
			return true;
		}
	};
	
	Runnable runnable = new Runnable() {
	    public void run() {
	    	saveScore();
	    }

	};
 
//Public Setter Methods
//------------------------------------------------------------------------------ 
    public void setName(String n) {
    	name = n;
        cellName.setText(name);
    }
    
    public void setScore(int s) {
        	score = s;
        	cellScore.setText(Integer.toString(score));
    }
    
    public void addScore(String score) {
    	if(cellSelected) {
        	temporaryScore += Integer.parseInt(score);
        	cellScore.setText(Integer.toString(temporaryScore));
        	cellScore.setTextColor(Color.parseColor("#ff33b5e5"));
        	cellScore.removeCallbacks(runnable);
			scoreClickable = true;
        	cellScore.postDelayed(runnable, 5000);
        }
    }
    
    public void saveScore() {
    	score += temporaryScore;
		cellScore.setText(Integer.toString(score));
    	cellScore.setTextColor(Color.BLACK);        	
    	history = String.format("%-6s", temporaryScore) + history;
    	cellHistory.setText(history);
    	temporaryScore = 0;
		scoreClickable = false;
    }
    
    public void setHistory(String h) {
    	history = h;
        cellHistory.setText(history);
    }
    
    public void setChecked() {
		cellSelected = true;


		cellBarTwo.setBackgroundColor(Color.parseColor("#ff33b5e5"));
		cellBarThree.setBackgroundColor(Color.parseColor("#ff33b5e5"));

		cellScoreLayout.setBackgroundColor(Color.parseColor("#dddddd"));
		cardLayout.setBackgroundResource(R.drawable.card_background_selected);
	}
	
	public void setUnchecked() {
		cellSelected = false;

		cellBarTwo.setBackgroundColor(Color.TRANSPARENT);
		cellBarThree.setBackgroundColor(Color.parseColor("#ffff8800"));

		cellScoreLayout.setBackgroundColor(Color.TRANSPARENT);
		cardLayout.setBackgroundResource(R.drawable.card_background);
	}
	
	public void setTouch() {
		cardLayout.setBackgroundResource(R.drawable.card_background_selected);
	}
	
	public void setUnTouch() {
		cardLayout.setBackgroundResource(R.drawable.card_background);
	}
	
	public void setSQLRowId(int sQLRowId) {
		SQLRowId = sQLRowId;
	} 
	
//Public Getter methods
//------------------------------------------------------------------------------
    public boolean isChecked() {
    	return cellSelected;
    }
    
    public String getName() {
    	return name;
    }  
    
    public int getScore() {
        return score;
    }
    
    public String getHistory() {
    	return history;
    }

	public int getSQLRowId() {
		return SQLRowId;
	}
}
