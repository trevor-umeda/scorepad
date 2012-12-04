package edu.cmu.sv.tumeda.gamecounter;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

        @Override
        public void onCreate(SQLiteDatabase db) {
        	//Log.i(TAG, "Creating database: " + DATABASE);
    		db.execSQL("create table " + TABLE + " (" + C_ID + " integer primary key autoincrement, "
    				+ C_TITLE + " text not null, " + C_STARTING_POINTS + " integer, " + C_NUM_OF_PLAYERS + " integer, "
    				+ C_AMOUNTS +" text, " + C_ALLOW_CUST_VALS + " integer, " + C_POINTS_NAME + " text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	 //Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                     //+ newVersion + ", which will destroy all old data");
        	db.execSQL("drop table " + TABLE);
    		this.onCreate(db);
        }
    }
	public GamesDbAdapter(Context context){

		this.context = context;
		this.dbHelper = new DbHelper(context);
		//Log.i(TAG,"Initialized data");
	}
	
	
	public void close(){
		dbHelper.close();
	}
	
	public long createGame(ContentValues values) {
		//Log.d(TAG,"insertOrIgnore on " + values);
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		long returnLong;
		try{
			returnLong = db.insert(TABLE, null, values);
		} finally {
			db.close();
		}
		//Log.d(TAG,"inserted with returning id key: " + returnLong);
        return returnLong;
    }
	 public boolean deleteGame(long rowId) {
		 SQLiteDatabase db = this.dbHelper.getWritableDatabase();

	        return db.delete(TABLE, C_ID + "=" + rowId, null) > 0;
	    }
	 public Cursor fetchAllGames() {
		 SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		 //Log.d(TAG,"Fetching allllll the games");
	        return db.query(TABLE, new String[] {C_ID, C_TITLE,
	                C_STARTING_POINTS,C_NUM_OF_PLAYERS,C_AMOUNTS,C_ALLOW_CUST_VALS,C_POINTS_NAME}, null, null, null, null, null);
	    }
	 public Cursor fetchGame(long rowId) throws SQLException {
		 SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		 //Log.d(TAG,"Fetched game of id: " + rowId);
	        Cursor cursor =

	            db.query(true, TABLE, new String[] {C_ID, C_TITLE,
		                C_STARTING_POINTS,C_NUM_OF_PLAYERS,C_AMOUNTS,C_ALLOW_CUST_VALS,C_POINTS_NAME}, C_ID + "=" + rowId, null,
	                    null, null, null, null);
	        if (cursor != null) {
	            cursor.moveToFirst();
	            //Log.d(TAG,"found game of name: "+ cursor.getString(
	         //           cursor.getColumnIndexOrThrow(GamesDbAdapter.C_TITLE)));
	        }
	        
	        return cursor;

	    }
	 public boolean updateGame(ContentValues values,long rowId) {
		 	//Log.d(TAG,"Update on " + values);
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();

	        return db.update(TABLE, values, C_ID + "=" + rowId, null) > 0;
	    }
}
