package edu.cmu.sv.tumeda.gamecounter;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


public class NewGameActivity extends Activity {
    private static final String TAG = "NewGameActivity";

	private EditText mTitleText;
    private EditText mStartingAmountText;
    private Spinner spinner;
    private Spinner amountsSpinner;
    private ArrayList<EditText> amountList;
    private Long mRowId;
    private int incrementingId;
    private GameCounterApplication gameCounter;
    private int allowCustomValues;
	//Set up the ui elements
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 gameCounter = (GameCounterApplication)getApplication();
		 //Set the basic content stuff
	        setContentView(R.layout.newgames);
	        setTitle(R.string.titleNewGame);
	        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Celeste_Hand.ttf");  

	        //Set title for game form
	        mTitleText = (EditText) findViewById(R.id.title);
	        mTitleText.setSingleLine();			
			mTitleText.setOnKeyListener(new OnKeyListener() {
	            public boolean onKey(View v, int keyCode, KeyEvent event) {
	                // If the event is a key-down event on the "enter" button
	                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&(
	                    (keyCode == KeyEvent.KEYCODE_ENTER)))  {
	                  // Perform action on key press
	                	mTitleText.setBackgroundDrawable(null);
	                  return true;
	                }
	                return false;
	            }				
	        });		
			//Create form for starting amount of points
	        mStartingAmountText = (EditText) findViewById(R.id.starting_points);
	        mStartingAmountText.setSingleLine();
	        mStartingAmountText.setImeOptions(EditorInfo.IME_ACTION_DONE);
	        mStartingAmountText.setOnEditorActionListener(
	        		new EditText.OnEditorActionListener(){
	        			@Override
	        			public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
	        				//On enter hide the textbox to signify its done
	        				if(actionId == EditorInfo.IME_ACTION_DONE){
	        			     	mStartingAmountText.setBackgroundDrawable(null);
	        			     	InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	        		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	        			     	return true;
	        				}
	        				return false;
	        			}
	        		});
	        mStartingAmountText.setOnKeyListener(new OnKeyListener() {
	            public boolean onKey(View v, int keyCode, KeyEvent event) {
	                // If the event is a key-down event on the "enter" button
	                if ((event.getAction() == KeyEvent.ACTION_DOWN) && 
	                    (keyCode == KeyEvent.KEYCODE_ENTER) )  {
	                  // Perform action on key press
	                	mStartingAmountText.setBackgroundDrawable(null);
	                  return true;
	                }
	                return false;
	            }				
	        });		
	        //Create spinner for num of players
	        spinner = (Spinner) findViewById(R.id.spinner);	        
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	                this, R.array.numbers_array, android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner.setAdapter(adapter);	       
	        
	        //Create spinner for amounts
	        amountsSpinner = (Spinner) findViewById(R.id.spinner2);	        
	        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
	                this, R.array.numbers_array, android.R.layout.simple_spinner_item);
	        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        amountsSpinner.setAdapter(adapter);
	        amountsSpinner.setOnItemSelectedListener(amountsSpinnerListener);
	        amountList = new ArrayList<EditText>();
	     
	        //Confirm button create
	        Button confirmButton = (Button) findViewById(R.id.confirm);
	        confirmButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                setResult(RESULT_OK);
	                finish();
	            }
	        });
	        
	        allowCustomValues = 0;
	       //Custom amount input form
	        final EditText edittext = (EditText) findViewById(R.id.txt_submit_name);
	        edittext.setTypeface(font);
	        edittext.setSingleLine();
	        edittext.setOnEditorActionListener(
	        		new EditText.OnEditorActionListener(){
	        			@Override
	        			public boolean onEditorAction(TextView v, int actionId, KeyEvent event){	        				
	        				if(actionId == EditorInfo.IME_ACTION_DONE){
	        			     	edittext.setBackgroundDrawable(null);
	        			     	InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	        		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	        			     	return true;
	        				}
	        				return false;
	        			}
	        		});
	        amountList.add(edittext);
	        incrementingId = 1;
	       
	        //Go through everything and apply the font
	        View rootView = findViewById(R.id.ScrollView01);
	        ViewGroup viewGroup = (ViewGroup) rootView;
	        ViewGroup viewGroup2 = (ViewGroup) viewGroup.getChildAt(0);
	        for(int i = 0; i < viewGroup2.getChildCount(); i++){
	        	View v = viewGroup2.getChildAt(i);

	        	try{
	            	((TextView) v).setTypeface(font);
	            	((TextView) v).setTextSize(25);

	        	}catch(Exception e){
	        	}

	        }
	        //If it's the first time making a game. Display a popup with help
	        checkFirstTime(rootView);

	        rootView = findViewById(R.id.lbl_submit_name);
	        ((TextView) rootView).setTypeface(font);
	       
	}
	private AdapterView.OnItemSelectedListener amountsSpinnerListener = new AdapterView.OnItemSelectedListener(){	
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
	        Object item = parent.getItemAtPosition(pos);
	        int numOfAmounts = Integer.parseInt(item.toString());
	        LinearLayout submitScoreLayout = (LinearLayout)findViewById(R.id.additional_value);	       			
	        submitScoreLayout.removeAllViews();
	        amountList.removeAll(amountList);
	        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Celeste_Hand.ttf");

	        //For the amount chosen, create that many custom amount form boxes
	        for(int i = 0; i < numOfAmounts; i++){	        	 	 				            	 
	             // Create new LayoutInflater - this has to be done this way, as you can't directly inflate an XML without creating an inflater object first
	             LayoutInflater inflater = getLayoutInflater();
	             View additionalView = inflater.inflate(R.layout.additional_value, null);
	             TextView newEditLabel = (TextView) additionalView.findViewById(R.id.lbl_submit_name);
	             newEditLabel.setTypeface(font);
	             EditText newEdit = (EditText) additionalView.findViewById(R.id.txt_submit_name);
	             newEdit.setTypeface(font);
	             newEdit.setId(R.id.txt_submit_name + incrementingId);
	             incrementingId++;
	             submitScoreLayout.addView(additionalView);
	             newEdit.setSingleLine();	             
	             newEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
	             if(i == numOfAmounts-1){
		             newEdit.setImeOptions(EditorInfo.IME_ACTION_DONE); 
	             }
	             final EditText finalEdit = newEdit;
	             finalEdit.setOnEditorActionListener(
	 	        		new EditText.OnEditorActionListener(){
	 	        			@Override
	 	        			public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
	 	        				
	 	        				if(actionId == EditorInfo.IME_ACTION_DONE){
	 	        			     	InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	 	        		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	 	        			     	return true;
	 	        				}
	 	        				return false;
	 	        			}
	 	        		});
	             //Add the layout to the list
	             amountList.add(finalEdit);	         
	        }
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
					
		}
	};
	//Show help dialog
	private OnClickListener helpListener = new OnClickListener(){
		public void onClick(View v){
			helpDialog(v);			
		}
	};
	private OnClickListener addBoxListener = new OnClickListener(){
		public void onClick(View v){
	        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Celeste_Hand.ttf");  

			// Get a reference to the score_name_entry object in score.xml
            LinearLayout submitScoreLayout = (LinearLayout)findViewById(R.id.additional_value);
             
            // Create new LayoutInflater - this has to be done this way, as you can't directly inflate an XML without creating an inflater object first
            LayoutInflater inflater = getLayoutInflater();
            View additionalView = inflater.inflate(R.layout.additional_value, null);
            
            //Set up the layout for the added amounts form box
            TextView newEditLabel = (TextView) additionalView.findViewById(R.id.lbl_submit_name);
            newEditLabel.setTypeface(font);
            EditText newEdit = (EditText) additionalView.findViewById(R.id.txt_submit_name);
            newEdit.setTypeface(font);
            newEdit.setId(R.id.txt_submit_name + incrementingId);
            incrementingId++;
            submitScoreLayout.addView(additionalView);
            newEdit.setSingleLine();
            newEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
            final EditText finalEdit = newEdit;
            finalEdit.setOnEditorActionListener(
	        		new EditText.OnEditorActionListener(){
	        			@Override
	        			public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
	        				
	        				if(actionId == EditorInfo.IME_ACTION_DONE){
	        			     	//finalEdit.setBackgroundDrawable(null);
	        			     	InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	        		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	        			     	return true;
	        				}
	        				return false;
	        			}
	        		});
            
            EditText lastEdit = amountList.get(amountList.size()-1);
            lastEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            //Add the form to the list of amount forms
            amountList.add(finalEdit);
                     
		}
	};
	public void onCheckboxClicked(View v) {
        // Perform action on clicks, depending on whether it's now checked
		LinearLayout submitScoreLayout = (LinearLayout)findViewById(R.id.additional_value);
               
		 if (((CheckBox) v).isChecked()) {
			 submitScoreLayout.removeAllViews();
			 amountList.removeAll(amountList);
			 allowCustomValues = 1;
		 } else {
			 // Create new LayoutInflater - this has to be done this way, as you can't directly inflate an XML without creating an inflater object first
	        LayoutInflater inflater = getLayoutInflater();
	        View additionalView = inflater.inflate(R.layout.additional_value, null);
	        EditText newEdit = (EditText) additionalView.findViewById(R.id.txt_submit_name);
	        //EditText newEdit = (EditText) findViewById(R.layout.additional_value);
	        newEdit.setId(R.id.txt_submit_name + incrementingId);
	        incrementingId++;
	        submitScoreLayout.addView(additionalView);
            final EditText finalEdit = newEdit;

            amountList.add(finalEdit);
            allowCustomValues = 0;

	        }
    }
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(GamesDbAdapter.C_ID, mRowId);
    }
	
	//If the user pauses this or exits, save the settings
	@Override
    protected void onPause() {
        super.onPause();
        //If the user didn't put in a title or starting amount, the game ain't legit
        if(!(mTitleText.getText().toString().matches("")) && !(mStartingAmountText.getText().toString().matches("")));
        {
        	if(mTitleText.getText().toString().matches("")){
        		//Log.d(TAG,"titltext is " +mTitleText.getText().toString());
        	}
        	else if(mStartingAmountText.getText().toString().matches("")){
        		//Log.d(TAG,"starting amount is blank...");
        	}
        	else {
        		saveState();
        	}
        }
    }
	//Create
	private void helpDialog(View v){
		//Set basic layout stuff.
		final Dialog helpDialog = new Dialog(v.getContext());
		helpDialog.setContentView(R.layout.helpdialog);
    	RelativeLayout helpLayout = (RelativeLayout) helpDialog.findViewById(R.id.helpRelativeDialog);
	    Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Celeste_Hand.ttf");  

		helpDialog.setTitle("Help");
		helpDialog.show();
		
		//Apply the font to everything
		ViewGroup viewGroup = (ViewGroup) helpLayout;       
        for(int i = 0; i < viewGroup.getChildCount(); i++){
        	View helpView = viewGroup.getChildAt(i);	        	
        	try{
        		((TextView )helpView).setTypeface(font);
        		((TextView )helpView).setTextSize(25);
        		((TextView )helpView).setTextColor(Color.BLACK);
        	}catch(Exception e){
        	}

        }
        //Set stuff for the close button
		Button closeButton = (Button) helpDialog.findViewById(R.id.helpClose);
		closeButton.setTypeface(font);
		closeButton.setText("Close");
		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				helpDialog.dismiss();
			}
				
		});
	}
	//Check to see if this is the first time creating a game
	private void checkFirstTime(View v){
		//Check the share preferences to confirm
		SharedPreferences myPrefs = this.getSharedPreferences("firstTime",MODE_WORLD_READABLE);
        String firstTimeBoolean = myPrefs.getString("firstTime","");
        //If there is nothing there then we know it's the first time.
        if(firstTimeBoolean.equals("")){
        	//Show the help dialog and set the user no longer being the first time creating a game .
        	helpDialog(v);
        	SharedPreferences sharedPreferences = getSharedPreferences("firstTime",MODE_WORLD_READABLE);
	        SharedPreferences.Editor editor = sharedPreferences.edit();
	        editor.putString("firstTime","true");
	        editor.commit();
        }
	}
	
	private void saveState() {
		//Get information from form to save to db
        String title = mTitleText.getText().toString();
        int startingAmount = Integer.parseInt(mStartingAmountText.getText().toString());
        int totalPlayers = Integer.parseInt(spinner.getSelectedItem().toString());
        
        //Parse the amounts to save into the db
        ArrayList<String> items = new ArrayList<String>();
        for(EditText amount : amountList){
        	items.add(amount.getText().toString());
        }
        
        JSONObject json = new JSONObject(); 
        try {
			json.put("uniqueArrays", new JSONArray(items));
		} catch (JSONException e) {
			e.printStackTrace();
		} 
        String amounts = json.toString();
       //Save the games
        long id = gameCounter.createGame(title, startingAmount,totalPlayers,amounts,allowCustomValues);
            if (id > 0) {
                mRowId = id;
   
            }   
		}
//Deprecated    
//    public class MyOnItemSelectedListener implements OnItemSelectedListener {
//
//        public void onItemSelected(AdapterView<?> parent,
//            View view, int pos, long id) {
//          Toast.makeText(parent.getContext(), "The planet is " +
//              parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
//        }
//
//        public void onNothingSelected(AdapterView parent) {
//          // Do nothing.
//        }
//    }
}
