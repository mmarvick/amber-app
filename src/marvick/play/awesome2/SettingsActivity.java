package marvick.play.awesome2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
 
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
				
		    	String value = sharedPreferences.getString("cat_name", "NULL");
		        if (value.equals("")) {
		        	catName.setText(oldCatName); 
		        	setPreferenceScreen(null);
		        	addPreferencesFromResource(R.xml.settings);
		        	
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