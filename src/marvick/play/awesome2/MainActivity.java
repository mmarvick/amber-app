package marvick.play.awesome2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
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
        
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        setText();
        
        final Button buttonNewCat = (Button) findViewById(R.id.buttonGetCat);
        buttonNewCat.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		try {
					newCat();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        });
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
    
    private void newCat() throws ClientProtocolException, IOException, JSONException {
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    	SharedPreferences.Editor editor = sharedPrefs.edit();
    	
    	new NetworkTask().execute("");

    }
    
    private class NetworkTask extends AsyncTask<String, Void, HttpResponse> {
        @Override
        protected HttpResponse doInBackground(String... params) {
        	
        	
            HttpGet request = new HttpGet("http://www.mmarvick.com/amber_app/new_cat.php");
            AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
            try {
                return client.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
            client.close();
            }
        }

        @Override
        protected void onPostExecute(HttpResponse result) {
            //Do something with result
            if (result != null) {
            	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            	SharedPreferences.Editor editor = sharedPrefs.edit();
            	
        	    HttpEntity entity = result.getEntity();
        	    String textResult = null;
				try {
					textResult = EntityUtils.toString(entity);
					Log.e("result", textResult);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
            	
            	JSONObject jObject = null;
				try {
					jObject = new JSONObject(textResult);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	
	        	try {
					editor.putString("cat_name", jObject.getString("name"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	try {
	        		boolean catBad = jObject.getBoolean("bad");
					editor.putBoolean("cat_bad", catBad);
					MediaPlayer meow = null;
					if (catBad) 
						meow = MediaPlayer.create(MainActivity.this.getApplicationContext(), R.raw.bad_meow);
					else
						meow = MediaPlayer.create(MainActivity.this.getApplicationContext(), R.raw.good_meow);
					meow.start();
	        	} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	editor.commit();
	        	setText();
            }
        }
    }
    
}
