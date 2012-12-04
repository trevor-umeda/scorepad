package edu.cmu.sv.tumeda.gamecounter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.sv.tumeda.gamecounter.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class GameIncrementerActivity extends Activity {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
    private static final int ACTIVITY_POINTS=2;

	private LinearLayout layout;
	private Boolean incrementing;
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Put your code here
		setContentView(R.layout.gameview);
        layout = (LinearLayout) this.findViewById(R.id.gameview);
        ToggleButton incrementToggle = new ToggleButton(this);
		
		incrementToggle.setTextOn("Increment");
		incrementToggle.setTextOff("Decrement");
		incrementToggle.setChecked(true);
		incrementing = true;
		incrementToggle.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				 // Perform action on clicks
		        if (((ToggleButton) v).isChecked()) {
		        	incrementing = true;
		        } else {
		        	//Log.d("GameCounterActivity","incrementing should be false now");
		        	incrementing = false;
		        }						
			}
			
		});
		layout.addView(incrementToggle);

		Bundle extras = getIntent().getExtras();
		String amounts = extras.getString("amounts");
		final int playerNum = extras.getInt("playerNumber");
		
		JSONObject json;
		JSONArray items = null;
		try {
			json = new JSONObject(amounts);
			items = json.optJSONArray("uniqueArrays");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(items != null){
			for(int i = 0; i < items.length(); i ++){
				try {
					final String returnInt= (String)items.get(i);
					
					final String item = returnInt;
					Button amountButton = new Button(this);
					amountButton.setText(item);
					amountButton.setOnClickListener(new View.OnClickListener() {

						public void onClick(View view) {
							Intent in = new Intent();
							if(!incrementing){
								
								final String signedInt = "-" + (String) item;
								//Log.d("GameCounterActivity","returnInt is " + signedInt);
								in.putExtra("returnAmount", signedInt);
							}
							else{
								final String signedInt = (String) item;
								in.putExtra("returnAmount", signedInt);
							}


							in.putExtra("playerNumber", playerNum);
					        setResult(ACTIVITY_POINTS,in);//Here I am Setting the Requestcode 1, you can put according to your requirement
					        finish();	
			        	}

					});
					layout.addView(amountButton);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		 
	}
}
