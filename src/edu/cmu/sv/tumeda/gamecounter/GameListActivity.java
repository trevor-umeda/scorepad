package edu.cmu.sv.tumeda.gamecounter;



import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class GameListActivity extends ListActivity {
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
	private GameCounterApplication gameCounter;
	GamesDbAdapter dbAdapter;
	Cursor cursor;
	ListView listGames;
    private static final String TAG = "GameListActivity";
	private static final int DELETE_ID = 2;
	private static final int NEWGAME_ID = 3;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
       
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.gamelist);   
        gameCounter = (GameCounterApplication) getApplication();		
        
        fillData();
        registerForContextMenu(getListView());

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Celeste_Hand.ttf");  

        Button helpButton = (Button)findViewById(R.id.listHelp);
        helpButton.setOnClickListener(helpListener);
        
        View rootView = findViewById(android.R.id.list);
        ViewGroup viewGroup = (ViewGroup) rootView.getParent();
        for(int i = 0; i < viewGroup.getChildCount(); i++){
        	View v = viewGroup.getChildAt(i);

        	try{
            	((TextView) v).setTypeface(font);
            	((TextView) v).setTextSize(25);
            	//.d(TAG,"At " + i + " font set");
        	}catch(Exception e){
        		//Log.d(TAG,"wasn't a textview at " + i);
        	}

        }
       
        
        Button newRulesButton = (Button)findViewById(R.id.newGameRulesButton);
        newRulesButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	Intent i = new Intent(view.getContext(), NewGameActivity.class);
       	     startActivityForResult(i, ACTIVITY_CREATE);
            }

        });
    }
    private void fillData() {
        Cursor gamesCursor = gameCounter.fetchAllGames();
        startManagingCursor(gamesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{GamesDbAdapter.C_TITLE};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.game_row};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter games = 
            new SimpleCursorAdapter(this, R.layout.gamesrow, gamesCursor, from, to){
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Celeste_Hand.ttf");  

                final View row = super.getView(position, convertView, parent);
                ((TextView)row).setTypeface(font);
                
                return row;
            }
        };
        setListAdapter(games);
    }
    private OnClickListener helpListener = new OnClickListener(){
		public void onClick(View v){
			helpDialog(v);
			
		}
	};
    private void helpDialog(View v){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Click and hold a game title to see more options!")
    	       .setCancelable(false)
    	       .setPositiveButton("Close", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
   	                dialog.cancel();
    	           }
    	       });
    	     
    	AlertDialog alert = builder.create();
    	
    	alert.show();
    	//		final Dialog helpDialog = new Dialog(v.getContext());
//		helpDialog.setContentView(R.layout.helplistdialog);
//    	RelativeLayout helpLayout = (RelativeLayout) helpDialog.findViewById(R.id.helpRelativeDialog);
////   	 	Drawable outline = getResources().getDrawable(R.drawable.android_bg2);
////
////    	helpLayout.setBackgroundDrawable(outline);
//	    Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Celeste_Hand.ttf");  
//
//		helpDialog.setTitle("Help");
//		helpDialog.show();
//		
//		
//		ViewGroup viewGroup = (ViewGroup) helpLayout;
//        
//        for(int i = 0; i < viewGroup.getChildCount(); i++){
//        	View helpView = viewGroup.getChildAt(i);
//        	
//        	try{
//            ((TextView )helpView).setTypeface(font);
//            ((TextView )helpView).setTextSize(25);
//            ((TextView )helpView).setTextColor(Color.BLACK);
//
//        	}catch(Exception e){
//        	}
//
//        }
//		Button closeButton = (Button) helpDialog.findViewById(R.id.helpClose);
////		helpLayout.addView(closeButton);
//		closeButton.setTypeface(font);
//		closeButton.setText("Close");
//		closeButton.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				helpDialog.dismiss();
//			}
//				
//		});
	} 
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Log.d(TAG,"row id of: " + id);
        Cursor cursor = ((Cursor) l.getItemAtPosition(position));
        //Log.d(TAG,"Something else " +  cursor.getString(cursor.getColumnIndexOrThrow(GamesDbAdapter.C_ID)));
        Intent i = new Intent(this, GameCounterActivity.class);
        i.putExtra(GamesDbAdapter.C_ID, id);
        startActivityForResult(i, ACTIVITY_EDIT);

    }
    
    
    /*
     * Menu stuff
     */
    // Called first time user clicks on the menu button
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    
    //Called when an options item is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    	case R.id.newGameRules:
    		 Intent i = new Intent(this, NewGameActivity.class);
    	     startActivityForResult(i, ACTIVITY_CREATE);    		
    	     break;
    	}
    	
    	return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
        menu.add(0, NEWGAME_ID, 0, R.string.menu_newgame);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	switch(item.getItemId()) {

            case DELETE_ID:                
                gameCounter.deleteGame(info.id);
                fillData();
                return true;
            case NEWGAME_ID:               
                gameCounter.resetGame(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }
    
}