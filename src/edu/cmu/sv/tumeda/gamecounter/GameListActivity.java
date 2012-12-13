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
	//We create the ui for our list of games.
    @Override
    public void onCreate(Bundle savedInstanceState) {
       
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.gamelist);   
        gameCounter = (GameCounterApplication) getApplication();		
        
        //Populate the list with our games
        fillData();
        //Set up context menu to work with our list of games
        registerForContextMenu(getListView());
        //Set the font for everything
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Celeste_Hand.ttf");  

        //Set the help button
        Button helpButton = (Button)findViewById(R.id.listHelp);
        helpButton.setOnClickListener(helpListener);
        
        //Iterate through all views and replace the font.
        View rootView = findViewById(android.R.id.list);
        ViewGroup viewGroup = (ViewGroup) rootView.getParent();
        for(int i = 0; i < viewGroup.getChildCount(); i++){
        	View v = viewGroup.getChildAt(i);
        	try{
            	((TextView) v).setTypeface(font);
            	((TextView) v).setTextSize(25);
        	}catch(Exception e){

        	}
        }
        //Set the button for creating a new game
        //create a new activity and start it up.
        Button newRulesButton = (Button)findViewById(R.id.newGameRulesButton);
        newRulesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent i = new Intent(view.getContext(), NewGameActivity.class);
       	     	startActivityForResult(i, ACTIVITY_CREATE);
            }

        });
    }
    //Fill our list with all the game rules we have.
    private void fillData() {
    	//Fetch all the game rules from the DB
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
    //On help button press, display a help dialog
    private OnClickListener helpListener = new OnClickListener(){
		public void onClick(View v){
			helpDialog(v);			
		}
	};
    private void helpDialog(View v){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	//Configure the alert
    	builder.setMessage("Click and hold a game title to see more options!")
    	       .setCancelable(false)
    	       .setPositiveButton("Close", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
   	                dialog.cancel();
    	           }
    	       });
    	//Show the alert     
    	AlertDialog alert = builder.create();    	
    	alert.show();    	
	} 
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor cursor = ((Cursor) l.getItemAtPosition(position));
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
    //Reopulate list after we finish activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
    
    //Context menu if a list item is held down. 
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
        menu.add(0, NEWGAME_ID, 0, R.string.menu_newgame);

    }
    //Handle what happens if we select a context menu option
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