package edu.cmu.sv.tumeda.gamecounter;



import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
//Application class for interfacing with the backend details. 
//Holds and interfaces with the db for the rest of the app.

public class GameCounterApplication extends Application {

	private GamesDbAdapter dbAdapter;
	private static final String TAG = GameCounterApplication.class.getSimpleName();

	//Lazy initialize the dbAdapter.Its a singleton we only want one.
	public GamesDbAdapter getGamesDbAdapter(){
		if(dbAdapter==null){
			dbAdapter = new GamesDbAdapter(this);
		}
		return dbAdapter;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//Log.i(TAG,"onCreated");
	}
	@Override
	public void onTerminate() {
		super.onTerminate();
		//Log.i(TAG,"onTerminated");
	}
	//Create a new game rules given some values. Put it in the db.
	public long createGame(String title, int startingPoints, int numOfPlayers,String amounts,int allowCustomValues) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(GamesDbAdapter.C_TITLE, title);
        initialValues.put(GamesDbAdapter.C_STARTING_POINTS,startingPoints);
        initialValues.put(GamesDbAdapter.C_NUM_OF_PLAYERS,numOfPlayers);
        initialValues.put(GamesDbAdapter.C_AMOUNTS,amounts);
        initialValues.put(GamesDbAdapter.C_ALLOW_CUST_VALS,allowCustomValues);
        long returningId = this.getGamesDbAdapter().createGame(initialValues);
        //Log.d(TAG,"Created game successfully. Game id is " + returningId);
        return returningId;
    }
	//Delete a game from the db
	 public boolean deleteGame(long rowId) {
	        return this.getGamesDbAdapter().deleteGame(rowId);
	 }
	 //Get all of the games from the db.
	 public Cursor fetchAllGames() {

	        return this.getGamesDbAdapter().fetchAllGames();
	 }
	 //Get single specified game from the db.
	 public Cursor fetchGame(long rowId) throws SQLException {
	        Cursor cursor = this.getGamesDbAdapter().fetchGame(rowId);
	        return cursor;
	 }
	 //Update the rules of the specified game
	 public boolean updateGame(long rowId, String title, int startingPoints, int numOfPlayers,String amounts) {
	        ContentValues updatedValues = new ContentValues();
	        updatedValues.put(GamesDbAdapter.C_TITLE, title);
	        updatedValues.put(GamesDbAdapter.C_STARTING_POINTS,startingPoints);
	        updatedValues.put(GamesDbAdapter.C_NUM_OF_PLAYERS,numOfPlayers);
	        updatedValues.put(GamesDbAdapter.C_AMOUNTS,amounts);

	        return this.getGamesDbAdapter().updateGame(updatedValues,rowId);

	}
	 //Reset the game scores for a game.
	public void resetGame(long id) {
		//Get the game we need
        Cursor cursor = this.getGamesDbAdapter().fetchGame(id);
        String key = cursor.getString(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_TITLE));
        int numOfPlayers = cursor.getInt(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_NUM_OF_PLAYERS));

        //Create an array of the reset scores
		JSONObject json = new JSONObject(); 
		ArrayList<String> playerScores = new ArrayList<String>();
  		int starting_points = cursor.getInt(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_STARTING_POINTS));
		for(int i = 0; i<numOfPlayers ; i++){
			playerScores.add(""+starting_points);
		}
        try {
			json.put("uniqueArrays2", new JSONArray(playerScores));
		} catch (JSONException e) {
			e.printStackTrace();
		} 
        String amounts = json.toString();
        
        //Store the new reset scores
        SharedPreferences sharedPreferences = getSharedPreferences(key,MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,amounts);
        editor.commit();
        //Store the game as not started or "new"
        SharedPreferences sharedPreferences2 = getSharedPreferences(key+"Names",MODE_WORLD_READABLE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        editor2.putString(key+"Names","false");
        editor2.commit();
		
	}
}
