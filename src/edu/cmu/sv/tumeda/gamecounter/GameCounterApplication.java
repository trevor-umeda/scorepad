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

public class GameCounterApplication extends Application {

	private GamesDbAdapter dbAdapter;
	private static final String TAG = GameCounterApplication.class.getSimpleName();

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
	 public boolean deleteGame(long rowId) {

	        return this.getGamesDbAdapter().deleteGame(rowId);
	    }
	 public Cursor fetchAllGames() {

	        return this.getGamesDbAdapter().fetchAllGames();
	    }
	 public Cursor fetchGame(long rowId) throws SQLException {

	        Cursor cursor = this.getGamesDbAdapter().fetchGame(rowId);
	        return cursor;

	    }
	 public boolean updateGame(long rowId, String title, int startingPoints, int numOfPlayers,String amounts) {
	        ContentValues updatedValues = new ContentValues();
	        updatedValues.put(GamesDbAdapter.C_TITLE, title);
	        updatedValues.put(GamesDbAdapter.C_STARTING_POINTS,startingPoints);
	        updatedValues.put(GamesDbAdapter.C_NUM_OF_PLAYERS,numOfPlayers);
	        updatedValues.put(GamesDbAdapter.C_AMOUNTS,amounts);

	        return this.getGamesDbAdapter().updateGame(updatedValues,rowId);

	    }

	public void resetGame(long id) {
		// TODO Auto-generated method stub
        Cursor cursor = this.getGamesDbAdapter().fetchGame(id);
        String key = cursor.getString(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_TITLE));
        int numOfPlayers = cursor.getInt(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_NUM_OF_PLAYERS));

		JSONObject json = new JSONObject(); 
		ArrayList<String> playerScores = new ArrayList<String>();
  		int starting_points = cursor.getInt(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_STARTING_POINTS));

		for(int i = 0; i<numOfPlayers ; i++){
			playerScores.add(""+starting_points);
		}
        try {
			json.put("uniqueArrays2", new JSONArray(playerScores));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        String amounts = json.toString();
        //Log.d(TAG,"saved amounts " + amounts);
        SharedPreferences sharedPreferences = getSharedPreferences(key,MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,amounts);
        editor.commit();
        
        SharedPreferences sharedPreferences2 = getSharedPreferences(key+"Names",MODE_WORLD_READABLE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        editor2.putString(key+"Names","false");
        editor2.commit();
		
	}
}
