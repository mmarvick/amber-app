package marvick.play.awesome2;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final int RESULT_SETTINGS = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setText();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        	case R.id.settings:
        	    startActivityForResult(new Intent(this, SettingsActivity.class), RESULT_SETTINGS);
        		return true;
            case R.id.language:
            	startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS), RESULT_SETTINGS);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    } 
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult (requestCode, resultCode, data);
    	
    	switch (requestCode) {
    	case RESULT_SETTINGS:
    		setText();
    		break;
    	}
    	
    }
    
    private void setText() {
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    	Resources res = getResources();
    	StringBuilder builder = new StringBuilder();
    	
    	builder.append(res.getString(R.string.pre_cat));
    	builder.append(sharedPrefs.getString("cat_name", "Amber"));
    	builder.append(res.getString(R.string.post_cat));
    	
    	TextView catIntro = (TextView) findViewById(R.id.textView1);
    	catIntro.setText(builder.toString());
    	
    	TextView catDescrip = (TextView) findViewById(R.id.textView2);
    	if (sharedPrefs.getBoolean("cat_bad", true)) {
    		catDescrip.setText(res.getString(R.string.description_bad));
    	} else {
    		catDescrip.setText(res.getString(R.string.description_good));
    	}
    	
    }
    
}
