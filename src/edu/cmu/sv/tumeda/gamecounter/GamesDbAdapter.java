package edu.cmu.sv.tumeda.gamecounter;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
//The adapter that interfaces with the db. Will handle all the db related activities.
public class GamesDbAdapter {
	DbHelper dbHelper;
	Context context;
    private static final String TAG = "GamesDbAdapter";
	  static final String DATABASE = "games.db";
	  static final int VERSION = 3;
	  static final String TABLE = "games";
	  static final String C_ID = "_id";
	  static final String C_TITLE = "title";
	  static final String C_STARTING_POINTS = "starting_points";
	  static final String C_NUM_OF_PLAYERS = "num_of_players";
	  static final String C_AMOUNTS = "amounts";
	  static final String C_ALLOW_CUST_VALS = "allow_custom_values";
	  static final String C_POINTS_NAME = "points_name";
	  
      

    private static class DbHelper extends SQLiteOpenHelper {
    	  static final String TAG = "DbHelper";
    	  
       
    	  DbHelper(Context context) {
            super(context, DATABASE, null, VERSION);
        }
    	  //Create the schema for the db
        @Override
        public void onCreate(SQLiteDatabase db) {
        	//Log.i(TAG, "Creating database: " + DATABASE);
    		db.execSQL("create table " + TABLE + " (" + C_ID + " integer primary key autoincrement, "
    				+ C_TITLE + " text not null, " + C_STARTING_POINTS + " integer, " + C_NUM_OF_PLAYERS + " integer, "
    				+ C_AMOUNTS +" text, " + C_ALLOW_CUST_VALS + " integer, " + C_POINTS_NAME + " text)");
        }
        //Upgrade the db with a new schema
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	 //Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                     //+ newVersion + ", which will destroy all old data");
        	db.execSQL("drop table " + TABLE);
    		this.onCreate(db);
        }
    }
    //Create the adapter with db helper
	public GamesDbAdapter(Context context){
		this.context = context;
		this.dbHelper = new DbHelper(context);
	}
	//Close the dbhelper	
	public void close(){
		dbHelper.close();
	}

	//Create a new game rules given a set of values
	public long createGame(ContentValues values) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		long returnLong;
		try{
			returnLong = db.insert(TABLE, null, values);
		} finally {
			db.close();
		}
        return returnLong;
    }
	//Delete the game from the db
	 public boolean deleteGame(long rowId) {
		 SQLiteDatabase db = this.dbHelper.getWritableDatabase();
	        return db.delete(TABLE, C_ID + "=" + rowId, null) > 0;
	 }
	 //Return all games
	 public Cursor fetchAllGames() {
		 SQLiteDatabase db = this.dbHelper.getWritableDatabase();
	        return db.query(TABLE, new String[] {C_ID, C_TITLE,
	                C_STARTING_POINTS,C_NUM_OF_PLAYERS,C_AMOUNTS,C_ALLOW_CUST_VALS,C_POINTS_NAME}, null, null, null, null, null);
	 }
	 //Find the game that matches the given row id and return the cursor
	 public Cursor fetchGame(long rowId) throws SQLException {
		 SQLiteDatabase db = this.dbHelper.getWritableDatabase();
	        Cursor cursor =
	            db.query(true, TABLE, new String[] {C_ID, C_TITLE,
		                C_STARTING_POINTS,C_NUM_OF_PLAYERS,C_AMOUNTS,C_ALLOW_CUST_VALS,C_POINTS_NAME}, C_ID + "=" + rowId, null,
	                    null, null, null, null);
	        if (cursor != null) {
	            cursor.moveToFirst();	           
	        }
	        
	        return cursor;

	 }
	 //Update the game rules with given set of values
	 public boolean updateGame(ContentValues values,long rowId) {
		 	//Log.d(TAG,"Update on " + values);
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
	        return db.update(TABLE, values, C_ID + "=" + rowId, null) > 0;
	 }
}
