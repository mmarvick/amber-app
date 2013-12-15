package marvick.play.awesome2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
 
public class SettingsActivity extends PreferenceActivity {

	private String oldCatName;
	private EditTextPreference catName;
	private SharedPreferences.OnSharedPreferenceChangeListener listener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        
        createListener();
        
        catName = (EditTextPreference) findPreference("cat_name");
        oldCatName = catName.getText();     
    }
    
    private void createListener() {
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				Log.e("change","happened");
				
		    	String value = sharedPreferences.getString("cat_name", "NULL");
		    	Log.e("newVal", value);
		        if (value.equals("")) {
		        	Log.e("ifelse", "if");
		        	catName.setText(oldCatName); 
		        	setPreferenceScreen(null);
		        	addPreferencesFromResource(R.xml.settings);
		        	
		        	Log.e("Name", catName.getText());
		        	/*new AlertDialog.Builder(SettingsActivity.this)
		            .setTitle("Blank Name")
		            .setMessage("You cannot set a blank name.")
		            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) { 
		                    // continue with delete
		                }
		             })
		             .show(); */
		        }
		        else {
		        	oldCatName = value;
		        }
				
			};
        };
        
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(listener);	
    }

}