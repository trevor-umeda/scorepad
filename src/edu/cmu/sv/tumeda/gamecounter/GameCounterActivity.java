package edu.cmu.sv.tumeda.gamecounter;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.sv.tumeda.gamecounter.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

public class GameCounterActivity extends Activity {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	static final int DIALOG_PAUSED_ID = 0;
	static final int DIALOG_COUNTER_ID = 1;
	
	String pointsName;
    private static final int ACTIVITY_POINTS=2;
	private LinearLayout layout;
	private LinearLayout layout2;
	
	private GameCounterApplication gameCounter;
	GamesDbAdapter dbAdapter;
    private static final String TAG = "GameCounterActivity";
    ArrayList<Boolean> incrementing;
    Boolean incrementCheck;
    ArrayList<Button> playerButtons;
    ArrayList<String> playerScores;
    ArrayList<EditText> customAmounts;
    ArrayList<EditText> inputPlayerNames;
    ArrayList<TextView> playerPointsText;
    ArrayList<String> playerNames;
	Drawable outline;
    Drawable underline;

    JSONArray items;
    JSONArray items2;
    TableLayout table;
    TableRow currentRow;
    int allowCustValues;
    int itemsLength;
    Typeface font; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		gameCounter = (GameCounterApplication) getApplication();
        setContentView(R.layout.gameview);
        layout = (LinearLayout) findViewById(R.id.gameviewmulti2);
        table = (TableLayout) findViewById(R.id.TableLayout01);
        Display display = getWindowManager().getDefaultDisplay();
        
        outline = getResources().getDrawable(R.drawable.outline);
        underline = getResources().getDrawable(R.drawable.underline);
        
        Point size = new Point();
        int width = size.x;
        int height = display.getHeight();
        //Log(TAG,"get height of layout " + height);     
        incrementing = new ArrayList<Boolean>();
        playerButtons = new ArrayList<Button>();
        playerScores = new ArrayList<String>();
        playerPointsText = new ArrayList<TextView>();
        long gameId;
        if (savedInstanceState == null) {
         Bundle extras = getIntent().getExtras();
            if(extras == null) {
                gameId= (Long) null;
            } else {
            	
                gameId= extras.getLong(GamesDbAdapter.C_ID);
                
            }
        } else {
            Bundle extras = getIntent().getExtras();

            gameId= extras.getLong(GamesDbAdapter.C_ID);
        }
        //Log(TAG,"Gameid is " + gameId);

        
        Cursor cursor = gameCounter.fetchGame(gameId);
        startManagingCursor(cursor);
        
        pointsName = cursor.getString(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_POINTS_NAME));
        TextView text1 = new TextView(this);
        final String gameName = cursor.getString(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_TITLE));
        

               
        text1.setText("Game Name: " + gameName);
        font = Typeface.createFromAsset(getAssets(), "fonts/Celeste_Hand.ttf");  
        text1.setTextSize(30);
        text1.setTypeface(font);
        text1.setPadding(0, 10, 0, 0);
        
        layout.addView(text1);
       
        TextView text2 = new TextView(this);
        int numOfPlayers = cursor.getInt(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_NUM_OF_PLAYERS));
        table.setWeightSum((numOfPlayers+1)/2);
        int buttonDivider = (numOfPlayers%2) ;
        ////Log(TAG,"button divider height = " + buttonDivider);
        int buttonHeight = height/((numOfPlayers/2)+1);
        
        ////Log(TAG,"button heigh is " + buttonHeight);
        playerNames = new ArrayList<String>();
		
        
		for(int i = 0; i < numOfPlayers; i++){
			playerNames.add("Player " + (i + 1));
		}
        setNames(gameName, numOfPlayers);
        ////Log(TAG,""+numOfPlayers);
//        text2.setText("Number of Players " + numOfPlayers);
  //      layout.addView(text2);
        text2.setTypeface(font);
        String amounts = cursor.getString(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_AMOUNTS));
		////Log(TAG,"amounts is " + cursor.getString(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_AMOUNTS)));
		
		SharedPreferences myPrefs = this.getSharedPreferences(gameName,MODE_WORLD_READABLE);
        String arrayScores = myPrefs.getString(gameName,"");
        //Log(TAG,"array scores is " + arrayScores);        
        
		JSONObject json;
		JSONObject json2;
		
		items = null;
		
		try {
			json = new JSONObject(amounts);
			//Log(TAG,"testing json " + json.toString());
			
			items = json.optJSONArray("uniqueArrays");
			//Log(TAG,"prelim items is " + items);
			if(!arrayScores.equals(""))
			{
				json2 = new JSONObject(arrayScores);
				items2 = json2.optJSONArray("uniqueArrays2");
			}

		} catch (JSONException e) {
			//Log(TAG,"SOMETHING WENT WRONG " + e.toString());
		
			e.printStackTrace();
		}
		itemsLength = items.length();
		allowCustValues = cursor.getInt(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_ALLOW_CUST_VALS));

		
		/*
		 * Begin Creating the Layout
		 */		
	
        if(numOfPlayers > 2){
        	customAmounts = new ArrayList<EditText>();
        	
        	
        	for(int j = 0; j< numOfPlayers; j++){
        		if(j%2 == 0){
        			currentRow = new TableRow(this);
        		}
        		final int currentPlayerNum = j;
        		if(items.length()<=6){
        			final TextView playerPoints = new TextView(this);
            		int starting_points = cursor.getInt(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_STARTING_POINTS));
            		String score;
        			try {
        				
        				if(items2 != null){
        					score = (String) items2.get(currentPlayerNum);    				
        					starting_points = Integer.parseInt(score);
        				}
        				
        			} catch (JSONException e1) {
        				// TODO Auto-generated catch block
        				e1.printStackTrace();
        			}
        			ViewGroup playerInfoLayout;
					
        			playerScores.add(""+starting_points);
            		//Log(TAG,""+starting_points);
            		playerPoints.setText(playerNames.get(currentPlayerNum) + ":"+ starting_points );
            		playerPoints.setTextSize(20);
            		playerPoints.setTypeface(font);
            		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
   					     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
   					params.setMargins(0, 10, 0, 0);
   					int idCounter = 0;
            		playerPoints.setId(100*(1+j));
            		idCounter++;
    					playerPointsText.add(playerPoints);	    					    				
    					//playerInfoLayout.addView(pointButton,tableLinParams);
    					//playerInfoLayout.addView(pointButton2,tableLinParams);
    					ToggleButton incrementToggle = new ToggleButton(this);
    				       Drawable toggle = getResources().getDrawable(R.drawable.toggle);

    			    	 incrementToggle.setBackgroundDrawable(toggle);
    	        		incrementToggle.setTextOn("");
    	        		incrementToggle.setTextOff("");
    	        		incrementToggle.setChecked(true);
    	        		incrementing.add(true);
    	        		incrementToggle.setWidth(150);
    	        		incrementToggle.setId(100*(j+1)+idCounter);
    	        		idCounter++;
    	        		incrementToggle.setOnClickListener(new OnClickListener(){

    						@Override
    						public void onClick(View v) {
    							 // Perform action on clicks
    					        if (((ToggleButton) v).isChecked()) {
    					        	incrementing.set(currentPlayerNum, true);
    					        } else {
    					        	incrementing.set(currentPlayerNum, false);
    					        }						
    						}
    	        			
    	        		});
   						 playerInfoLayout = new LinearLayout(this);
   						 ((LinearLayout) playerInfoLayout).setOrientation(LinearLayout.VERTICAL);
   						 playerInfoLayout.addView(playerPoints);
 						playerInfoLayout.addView(incrementToggle,params);   	    				

						if(allowCustValues == 1){


    						EditText custAmount  = new EditText(this);
        					Drawable underline = getResources().getDrawable(R.drawable.underline);

    	                	custAmount.setInputType(InputType.TYPE_CLASS_PHONE);
    	                	custAmount.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
    	                	custAmount.setId(100*(j+1)+idCounter);
    	                	idCounter++;
    	                	custAmount.setWidth(200);
    	                	custAmount.setBackgroundDrawable(underline);
    	                	custAmount.setTypeface(font);
    	                	
    	                	customAmounts.add(custAmount);
    	                	Drawable outline = getResources().getDrawable(R.drawable.outline);
    	                	Button custAmountButton = new Button(this);
    	                	custAmountButton.setId(100*(j+1)+idCounter);
    	                	idCounter++;
    	                	custAmountButton.setText("Input");
    	                	custAmountButton.setBackgroundDrawable(outline);
    	                	custAmountButton.setWidth(200);
    	                 	//Log(TAG,custAmount.getId() + " and also " + custAmountButton.getId());
//    	                 	
    	                	playerInfoLayout.addView(custAmount,params);
    	                	playerInfoLayout.addView(custAmountButton,params);            		

    	                	custAmountButton.setOnClickListener(new OnClickListener(){
    	                		@Override
    	                		public void onClick(View v) {
    	                			// TODO Auto-generated method stub
    	                			final String modifiedPoints = customAmounts.get(currentPlayerNum).getText().toString();
    	                			//Log(TAG,"modified Points is " + modifiedPoints);
    	                			String test = (String) playerPoints.getText();
    	    		        		String[] splitPoints = test.split(":");
    	    		        		if(incrementing.get(currentPlayerNum)){
    				        			String newAmount =""+(Integer.parseInt(splitPoints[1]) + Integer.parseInt(modifiedPoints));
    	    		        			playerPoints.setText(playerNames.get(currentPlayerNum)+ ":" + newAmount);
    				        			playerScores.set(currentPlayerNum,newAmount);
    				        			saveScores(gameName);

    				        		}else{
    				        			String newAmount = ""+(Integer.parseInt(splitPoints[1]) - Integer.parseInt(modifiedPoints));
    				        			playerPoints.setText(playerNames.get(currentPlayerNum) + ":" + newAmount);
    				        			playerScores.set(currentPlayerNum,newAmount);
    				        			saveScores(gameName);
    				        		}

    	                		}
    	                		
    	                	});
    	                }else{
    	                	LinearLayout buttonsLayout = new LinearLayout(this);
    	                	buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
    	                	buttonsLayout.setLayoutParams(params);
    	                	for(int i = 0; i < items.length(); i ++){
    	        				try {
    	        					if(i % 3 == 0 && i > 0){
    	        	                	playerInfoLayout.addView(buttonsLayout);	

    	        						buttonsLayout = new LinearLayout(this);
    	        	                	buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
    	        	                	
    	        					}
    	        					final String item = (String) items.get(i);
    	        					Button amountButton = new Button(this);
    	        					amountButton.setText(item);
    	        					amountButton.setTextSize(20);
    	        					amountButton.setWidth(75);
    	        					amountButton.setHeight(50);
    	        					amountButton.setId(100+(1+j) + idCounter);
    	        					amountButton.setTypeface(font);
    	        					idCounter++;
    	        			    	 Drawable outline = getResources().getDrawable(R.drawable.outline);
    	        			    	 amountButton.setBackgroundDrawable(outline);

    	        					amountButton.setOnClickListener(new View.OnClickListener() {

    	        						public void onClick(View view) {
    	        							String test = (String) playerPoints.getText();
    	        			        		String[] splitPoints = test.split(":");
    	        			        		if(incrementing.get(currentPlayerNum)){
    	        			        			String newAmount = "" + (Integer.parseInt(splitPoints[1]) + Integer.parseInt(item));
    	        			        			playerPoints.setText(playerNames.get(currentPlayerNum)+ ":"+  newAmount);        			        		
    	        			        			playerScores.set(currentPlayerNum,newAmount);
    	        			        			saveScores(gameName);

    	        			        		}else{
    	        			        			String newAmount = "" + (Integer.parseInt(splitPoints[1]) - Integer.parseInt(item));
    	        			        			playerPoints.setText(playerNames.get(currentPlayerNum) + ":"+ newAmount);
    	        			        			playerScores.set(currentPlayerNum,newAmount);
    	        			        			saveScores(gameName);
    	        			        		}
    	        			        	}

    	        					});
//    	        					
    	        				
    	        					buttonsLayout.addView(amountButton);
    	        					
    	        				} catch (JSONException e) {
    	        					// TODO Auto-generated catch block
    	        					e.printStackTrace();
    	        				}
    	        				
    	        			}
    	                	
    	                	playerInfoLayout.addView(buttonsLayout);	
    	                }
   			    	 Drawable bg = getResources().getDrawable(R.drawable.notepad_bg);

    					playerInfoLayout.setBackgroundDrawable(bg);
    					currentRow.addView(playerInfoLayout);
    					
    					currentRow.setGravity(Gravity.CENTER_VERTICAL);
    					if(j%2 == 1 || j == numOfPlayers-1 ){
    						table.addView(currentRow,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,1.0f));
    					}
    					
            	}
            	else{	
        		
				final Button playerButton = new Button(this);
			  String score;
      		int starting_points = cursor.getInt(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_STARTING_POINTS));
			try {
				
				if(items2 != null){
					score = (String) items2.get(currentPlayerNum);					
					starting_points = Integer.parseInt(score);
				}
				
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
      		playerScores.add(""+starting_points);
      			
				playerButton.setText(playerNames.get(currentPlayerNum) +": " + starting_points);
				  Drawable image = getResources().getDrawable(R.drawable.notepad);
				 playerButton.setTextSize(35);
				 playerButton.setTypeface(font);
				playerButton.setBackgroundDrawable(image);
				
				playerButton.setWidth(width/4);
      			playerButton.setHeight(buttonHeight);
        		final String finalItems = amounts;
        		
				playerButton.setOnClickListener(new View.OnClickListener() {

					public void onClick(View view) {
	
				        Intent i = new Intent(view.getContext(), GameIncrementerActivity.class);
				        i.putExtra("amounts", finalItems);
				        i.putExtra("playerNumber", currentPlayerNum);
				        //startActivityForResult(i, ACTIVITY_POINTS);
				     // custom dialog
						final Dialog dialog = new Dialog(view.getContext());
						dialog.setContentView(R.layout.incrementdialog);
						dialog.setTitle(playerNames.get(currentPlayerNum) + " Score Changer");
				    	 layout2 = (LinearLayout) dialog.findViewById(R.id.incrementDialog);
				         
				    	 final TextView dialogDescription = (TextView)dialog.findViewById(R.id.incrementTitle);

				    	 dialogDescription.setTypeface(font);
				    	 Drawable outline = getResources().getDrawable(R.drawable.toggle);
				    	 dialogDescription.setTextSize(20);
				    	 ToggleButton incrementToggle = (ToggleButton)dialog.findViewById(R.id.incrementToggle);
//					         incrementToggle.setBackgroundDrawable(outline);
//				    	 incrementToggle.setTextOn("Increment");
//				 		incrementToggle.setTextOff("Decrement");
//				 		incrementToggle.setChecked(true);
				 		incrementCheck = true;
				 		incrementToggle.setTypeface(font);
				 		incrementToggle.setOnClickListener(new OnClickListener(){

				 			@Override
				 			public void onClick(View v) {
				 				 // Perform action on clicks
				 		        if (((ToggleButton) v).isChecked()) {
				 		        	incrementCheck = true;
							    	 dialogDescription.setText("Click the amount to increment by");

				 		        } else {
				 		        	//Log("GameCounterActivity","incrementing should be false now");
				 		        	incrementCheck = false;
							    	 dialogDescription.setText("Click the amount to decrement by");

				 		        }						
				 			}
				 			
				 		});

				 		
		        		if(items != null){
		        			//Log(TAG,"items length "+items.length());
		        			for(int k = 0; k < items.length(); k ++){
		        				try {
		        					final String item = (String) items.get(k);
		        					//Log(TAG,"item is " + item);
		        					  Drawable image2 = getResources().getDrawable(R.drawable.outline);

		        					Button amountButton = new Button(view.getContext());
		        					amountButton.setBackgroundDrawable(image2);
		        					amountButton.setText(item);
		        					amountButton.setWidth(200);
		        					amountButton.setTypeface(font);
		        					amountButton.setTextSize(25);
		        					amountButton.setOnClickListener(new View.OnClickListener() {

		        						public void onClick(View view) {
		        							String test = (String) playerButton.getText();
		        			        		String[] splitPoints = test.split(": ");
		        			        		if(incrementCheck){
		        			     
		        			        			String newAmount = ""+ (Integer.parseInt(splitPoints[1]) + Integer.parseInt(item));
		        			        			playerButton.setText(playerNames.get(currentPlayerNum) +": " + newAmount);
		        			        			playerScores.set(currentPlayerNum,newAmount);
		        			        			saveScores(gameName);
		        			        			dialog.dismiss();

		        			        		}else{
		        			        			String newAmount = ""+(Integer.parseInt(splitPoints[1]) - Integer.parseInt(item));
		        			        			playerButton.setText(playerNames.get(currentPlayerNum) +": " +newAmount);
		        			        			playerScores.set(currentPlayerNum,newAmount);
		        			        			saveScores(gameName);
		        			        			dialog.dismiss();

		        			        		}
		        			        	}

		        					});
		        					LinearLayout layout = new LinearLayout(view.getContext());
		        					layout.setOrientation(LinearLayout.VERTICAL);

		        					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		        					     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		        					params.gravity = Gravity.CENTER;
		        					params.setMargins(0, 20, 0, 0);

		        					layout2.addView(amountButton,params);
		        				} catch (JSONException e) {
		        					// TODO Auto-generated catch block
		        					e.printStackTrace();
		        				}
		        			}
		        		}
		                if(allowCustValues == 1){
		                	
		                	final EditText custAmount  = new EditText(view.getContext());
      					  Drawable underline = getResources().getDrawable(R.drawable.underline);

		                	custAmount.setBackgroundDrawable(underline);
		                	custAmount.setWidth(300);
		                	custAmount.setInputType(InputType.TYPE_CLASS_PHONE);
		                	custAmount.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
		                	customAmounts.add(custAmount);
		                	

		                	Button custAmountButton = new Button(view.getContext());
	      				    outline = getResources().getDrawable(R.drawable.outline);
		                	
		                	custAmountButton.setText("Input");
		                	custAmountButton.setTextSize(18);
		                	custAmountButton.setBackgroundDrawable(outline);
		                	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	        					     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	        					params.gravity = Gravity.CENTER;
	        					params.setMargins(0, 20, 0, 0);
		                	
		                	layout2.addView(custAmount,params);
		                	layout2.addView(custAmountButton,params);
		            				          
		                	custAmountButton.setOnClickListener(new OnClickListener(){
		                		@Override
		                		public void onClick(View v) {
		                			// TODO Auto-generated method stub
		                			final String modifiedPoints =  custAmount.getText().toString();

		                			String test = (String) playerButton.getText();
        			        		String[] splitPoints = test.split(": ");
        			        		if(incrementCheck){
        			        			String newAmount = ""+ (Integer.parseInt(splitPoints[1]) + Integer.parseInt(modifiedPoints));
        			        			playerButton.setText(playerNames.get(currentPlayerNum) +": " + newAmount);
        			        			playerScores.set(currentPlayerNum,newAmount);
        			        			saveScores(gameName);
        								dialog.dismiss();

        			        		}else{
        			        			String newAmount = "" + (Integer.parseInt(splitPoints[1]) - Integer.parseInt(modifiedPoints));
        			        			playerButton.setText(playerNames.get(currentPlayerNum) +": " + newAmount);
        			        			playerScores.set(currentPlayerNum,newAmount);
        			        			saveScores(gameName);
        			        			dialog.dismiss();

        			        		}

		                		}
		                		
		                	});
		                }
						dialog.show();
						}

				});
				currentRow.addView(playerButton);
				currentRow.setGravity(Gravity.CENTER_VERTICAL);
				playerButtons.add(playerButton);
				if(j%2 == 1 || j == numOfPlayers-1){
					table.addView(currentRow,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,1.0f));
				}
        	}
        }
        }
        
        /*
		 * Create the layout for 2 people!
		 */
        else{
        	customAmounts = new ArrayList<EditText>();
        	
        	for(int j = 0; j < numOfPlayers ; j++){
        		LinearLayout playerInfoLayout = (LinearLayout)findViewById(R.id.player_info);
        		 
                // Create new LayoutInflater - this has to be done this way, as you can't directly inflate an XML without creating an inflater object first
                LayoutInflater inflater = getLayoutInflater();
                View playersInfoView = inflater.inflate(R.layout.player_info, null);
                LinearLayout buttonsLayout = (LinearLayout) playersInfoView.findViewById(R.id.amountsList);
               // playerInfoLayout.addView(playersInfo);
                
        		final int currentPlayerNum = j;
        		final TextView playerPoints = new TextView(this);
        		int starting_points = cursor.getInt(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_STARTING_POINTS));
        		String score;
    			try {
    				
    				if(items2 != null){
    					score = (String) items2.get(currentPlayerNum);    				
    					starting_points = Integer.parseInt(score);
    				}
    				
    			} catch (JSONException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}
    			playerScores.add(""+starting_points);
        		//Log(TAG,""+starting_points);
        		playerPoints.setText(playerNames.get(currentPlayerNum) + ":"+ starting_points );
        		playerPoints.setTextSize(20);
        		 playerInfoLayout.addView(playerPoints);
					playerPointsText.add(playerPoints);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
   					     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
   					params.gravity = Gravity.CENTER;
   					params.setMargins(0, 20, 0, 0);
        		ToggleButton incrementToggle = new ToggleButton(this);
		    	 Drawable toggle = getResources().getDrawable(R.drawable.toggle);
		    	 incrementToggle.setBackgroundDrawable(toggle);
        		incrementToggle.setTextOn("");
        		incrementToggle.setTextOff("");
        		incrementToggle.setChecked(true);
        		incrementing.add(true);
        		incrementToggle.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						 // Perform action on clicks
				        if (((ToggleButton) v).isChecked()) {
				        	incrementing.set(currentPlayerNum, true);
				        } else {
				        	incrementing.set(currentPlayerNum, false);
				        }						
					}
        			
        		});
        		 playerInfoLayout.addView(incrementToggle,params);
        		
        		//Log(TAG,"cursor index for amounts is " + cursor.getColumnIndexOrThrow(GamesDbAdapter.C_AMOUNTS));
        		
        		 items = null;
        		
        		try {
        			json = new JSONObject(amounts);
        			items = json.optJSONArray("uniqueArrays");

        		} catch (JSONException e) {
        			e.printStackTrace();
        		}
        		if(items != null){
        			for(int i = 0; i < items.length(); i ++){
        				try {
        					final String item = (String) items.get(i);
        					Button amountButton = new Button(this);
        					amountButton.setText(item);
        					amountButton.setWidth(200);
        			    	 Drawable outline = getResources().getDrawable(R.drawable.outline);
        			    	 amountButton.setBackgroundDrawable(outline);

        					amountButton.setOnClickListener(new View.OnClickListener() {

        						public void onClick(View view) {
        							String test = (String) playerPoints.getText();
        			        		String[] splitPoints = test.split(":");
        			        		if(incrementing.get(currentPlayerNum)){
        			        			String newAmount = "" + (Integer.parseInt(splitPoints[1]) + Integer.parseInt(item));
        			        			playerPoints.setText(playerNames.get(currentPlayerNum)+ ":"+  newAmount);        			        		
        			        			playerScores.set(currentPlayerNum,newAmount);
        			        			saveScores(gameName);

        			        		}else{
        			        			String newAmount = "" + (Integer.parseInt(splitPoints[1]) - Integer.parseInt(item));
        			        			playerPoints.setText(playerNames.get(currentPlayerNum) + ":"+ newAmount);
        			        			playerScores.set(currentPlayerNum,newAmount);
        			        			saveScores(gameName);
        			        		}
        			        	}

        					});
        					buttonsLayout.addView(amountButton,params);

        				} catch (JSONException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        				
        			}
        			

        		}
                if(allowCustValues == 1){
                	
                	EditText custAmount  = new EditText(this);
                	
                	custAmount.setInputType(InputType.TYPE_CLASS_PHONE);
                	custAmount.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
                	custAmount.setWidth(200);
                	customAmounts.add(custAmount);
                	Button custAmountButton = new Button(this);
                	custAmountButton.setText("Input");
                	custAmountButton.setWidth(200);
                	custAmount.setBackgroundDrawable(underline);
                	custAmountButton.setBackgroundDrawable(outline);
                	buttonsLayout.addView(custAmount,params);
                	buttonsLayout.addView(custAmountButton,params);            		

                	custAmountButton.setOnClickListener(new OnClickListener(){
                		@Override
                		public void onClick(View v) {
                			// TODO Auto-generated method stub
                			final String modifiedPoints = customAmounts.get(currentPlayerNum).getText().toString();
                			//Log(TAG,"modified Points is " + modifiedPoints);
                			String test = (String) playerPoints.getText();
    		        		String[] splitPoints = test.split(":");
    		        		if(incrementing.get(currentPlayerNum)){
			        			String newAmount =""+(Integer.parseInt(splitPoints[1]) + Integer.parseInt(modifiedPoints));
    		        			playerPoints.setText(playerNames.get(currentPlayerNum)+ ":" + newAmount);
			        			playerScores.set(currentPlayerNum,newAmount);
			        			saveScores(gameName);

			        		}else{
			        			String newAmount = ""+(Integer.parseInt(splitPoints[1]) - Integer.parseInt(modifiedPoints));
			        			playerPoints.setText(playerNames.get(currentPlayerNum) + ":" + newAmount);
			        			playerScores.set(currentPlayerNum,newAmount);
			        			saveScores(gameName);
			        		}

                		}
                		
                	});
                }
//		    	 Drawable bg = getResources().getDrawable(R.drawable.notepad_bg);
//                playersInfoView.setBackgroundDrawable(bg);
//                
				playerInfoLayout.addView(playersInfoView);

        	}
          
		}

        cursor.close();

    }
	
	protected Dialog onCreateDialog(int id) {
		Context mContext = getApplicationContext();
		Dialog dialog = new Dialog(mContext);
	    switch(id) {
	    case DIALOG_PAUSED_ID:
	        // do the work to define the pause Dialog
	        break;
	    case DIALOG_COUNTER_ID:
	    	dialog.setContentView(R.layout.gameview);
	    	layout = (LinearLayout) this.findViewById(R.id.gameview);
	    	ToggleButton incrementToggle = new ToggleButton(this);
			
			incrementToggle.setTextOn("Increment");
			incrementToggle.setTextOff("Decrement");
			incrementToggle.setChecked(true);
			 incrementCheck = true;
			incrementToggle.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					 // Perform action on clicks
			        if (((ToggleButton) v).isChecked()) {
			        	incrementCheck = true;
			        } else {
			        	//Log("GameCounterActivity","incrementing should be false now");
			        	incrementCheck = false;
			        }						
				}
				
			});
			layout.addView(incrementToggle);

	    	break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log("CheckStartActivity","onActivityResult and resultCode = "+resultCode);
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = data.getExtras();
		String returnAmount = extras.getString("returnAmount");
		final int playerNum = extras.getInt("playerNumber");
		Button playerButton = playerButtons.get(playerNum);
		
		//Log(TAG,"adding " + Integer.parseInt(returnAmount));
        if(resultCode==ACTIVITY_POINTS){
        	String buttonText = (String) playerButton.getText();
    		String[] splitPoints = buttonText.split(": ");
   			playerButton.setText("Player "+ playerNum +": " + (Integer.parseInt(splitPoints[1]) + Integer.parseInt(returnAmount)));
        }
        else{
        }
    }
	
	private void saveScores(String key){
		JSONObject json = new JSONObject(); 
        try {
			json.put("uniqueArrays2", new JSONArray(playerScores));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        String amounts = json.toString();
        //Log(TAG,"saved amounts " + amounts);
        SharedPreferences sharedPreferences = getSharedPreferences(key,MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,amounts);
        editor.commit();
        
	}
	
	private void setNames(final String title, int numOfPlayers) {
		SharedPreferences myPrefs = this.getSharedPreferences(title+"Names",MODE_WORLD_READABLE);
        String nameBoolean = myPrefs.getString(title+"Names","");
		if(!nameBoolean.equals("true")){
			
			final ArrayList<EditText> inputPlayerNames = new ArrayList<EditText>();
			
			final int totalPlayers = numOfPlayers;
			final Dialog nameDialog = new Dialog(this);
			nameDialog.setContentView(R.layout.namedialog);
			nameDialog.setTitle("Input Player Names");
	    	 layout2 = (LinearLayout) nameDialog.findViewById(R.id.nameDialog);
	         
	    	 final TextView dialogDescription = new TextView(this);
	    	 dialogDescription.setText("Input Player Names");
	    	 dialogDescription.setTypeface(font);
	    	 dialogDescription.setTextSize(20);
	    	 LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					params.setMargins(22, 10, 0, 10);
	    	layout2.addView(dialogDescription,params);
    			for(int k = 0; k < numOfPlayers; k ++){
    				EditText playerName = new EditText(this);

    				playerName.setHint("Player " + (k + 1));
    				playerName.setTypeface(font);
    				playerName.setTextSize(20);
    				playerName.setBackgroundDrawable(underline);
    				playerName.setWidth(300);
    				playerName.setSingleLine();
    				playerName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
    				if(k == numOfPlayers - 1){
    					playerName.setImeOptions(EditorInfo.IME_ACTION_DONE);
    				}
    				layout2.addView(playerName,params);
    				
    				inputPlayerNames.add(playerName);
    			}
    			Button amountButton = new Button(this);
				amountButton.setText("Enter");
		    	Drawable outline = getResources().getDrawable(R.drawable.outline);
		    	amountButton.setBackgroundDrawable(outline);
				amountButton.setWidth(200);
				amountButton.setGravity(Gravity.CENTER);
				amountButton.setTypeface(font);
				amountButton.setTextSize(20);
				amountButton.setOnClickListener(new View.OnClickListener() {

					public void onClick(View view) {
						for(int m = 0; m < totalPlayers; m++){
							EditText inputName = inputPlayerNames.get(m);
							String stringInputName = inputName.getText().toString();
							if(stringInputName.equals("")){
								stringInputName = "Player " + (m+1);
							}
							if(totalPlayers >= 4 && itemsLength >= 6){
							Button currentButton = playerButtons.get(m);
							String buttonText = currentButton.getText().toString();
							String[] splitButtonText = buttonText.split(":");
							String finalText = stringInputName+":"+ splitButtonText[1];
							currentButton.setText(finalText);
							}
							else{
			        			TextView currentTextView = playerPointsText.get(m);
								String nameText = currentTextView.getText().toString();
								String[] splitNameText = nameText.split(":");
								String finalText = stringInputName+":" + splitNameText[1];
			        			currentTextView.setText(finalText);

							}
							playerNames.set(m,stringInputName);
							//Log(TAG,"Position " + m + " is " + stringInputName);
						}
		    			for(int k = 0; k < playerNames.size(); k++){
		    				//Log(TAG,"Also names " + playerNames.get(k));
		    			}
		    			JSONObject json = new JSONObject(); 
		    			try {
		    				json.put("uniqueNameArrays", new JSONArray(playerNames));
		    			} catch (JSONException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			} 
		    			String names = json.toString();			
		    	        
		    	        //Log(TAG,"array of names is " + names);
		    	        
		    			SharedPreferences sharedPreferences = getSharedPreferences(title+"Names",MODE_WORLD_READABLE);
		    	        SharedPreferences.Editor editor = sharedPreferences.edit();
		    	        editor.putString(title+"NamesArray",names);
		    	        editor.commit();
						nameDialog.dismiss();
;
		        	}

				});
				layout2.addView(amountButton,params);

    			nameDialog.show();
    			    	        
    			SharedPreferences sharedPreferences = getSharedPreferences(title+"Names",MODE_WORLD_READABLE);
    	        SharedPreferences.Editor editor = sharedPreferences.edit();
    	        editor.putString(title+"Names","true");
    	        editor.commit();

    		}
		else{
	        String namesString = myPrefs.getString(title+"NamesArray","");
	        //Log(TAG,namesString);
	        try {
				JSONObject json = new JSONObject(namesString);
				//Log(TAG,"testing json " + json.toString());
				
				JSONArray names = json.optJSONArray("uniqueNameArrays");
				//Log(TAG,"prelim items is " + names);
				for(int i = 0; i <names.length();i++){
					playerNames.set(i,names.getString(i));

					//Log(TAG,"Player   name " +names.getString(i));
					//Log(TAG,"Playername " +playerNames.get(i));
				}

			} catch (JSONException e) {
				//Log(TAG,"SOMETHING WENT WRONG " + e.toString());
			
				e.printStackTrace();
			}

		}
            		
	}
}
