package marvick.play.awesome2;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final int RESULT_SETTINGS = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (((MyApp)getApplicationContext()).getActiveCat() == null) {
        	((MyApp)getApplicationContext()).setActiveCat(Cat.loadCatOnLoad(getApplicationContext(), MainActivity.this));
        	
        }
        
        onInit();
        
        final Button buttonNewCat = (Button) findViewById(R.id.buttonGetCat);
        buttonNewCat.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		((MyApp)getApplicationContext()).setActiveCat(Cat.generateCat(getApplicationContext(), MainActivity.this));
        	}
        });
    }


    public void onInit() {
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
        	    /*startActivityForResult(new Intent(this, SettingsActivity.class), RESULT_SETTINGS); */
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
    		onInit();
    		break;
    	}
    	
    }
    
    private void setText() {
    	Resources res = getResources();
    	StringBuilder builder = new StringBuilder();
    	
    	builder.append(res.getString(R.string.pre_cat));
    	builder.append(((MyApp)getApplicationContext()).getActiveCat().getName());
    	builder.append(res.getString(R.string.post_cat));
    	
    	TextView catIntro = (TextView) findViewById(R.id.cat_intro);
    	catIntro.setText(builder.toString());
    	
    	TextView catDescrip = (TextView) findViewById(R.id.cat_descrip);
    	if (((MyApp)getApplicationContext()).getActiveCat().getBad()) {
    		catDescrip.setText(res.getString(R.string.description_bad));
    	} else {
    		catDescrip.setText(res.getString(R.string.description_good));
    	} 	
    }
}
