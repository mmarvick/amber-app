package marvick.play.awesome2;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class Cat {
	public static final int GENERATE_NEW = 0;
	public static final int LOAD_CURRENT = 1;
	private String name;
	private boolean bad;
	private Context context;
	private MainActivity mainActivity;
	private SharedPreferences sharedPrefs;
	
	private Cat (final Context context, MainActivity mainActivity, int loadType) {
		this.context = context;
		this.mainActivity = mainActivity;
    	this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    	if (loadType == 0)
    		new GetOnlineCat().execute();
    	if (loadType == 1)
    		loadCatPrefs();
	}
	
	public static Cat loadCat(Context context, MainActivity mainActivity) {
		return new Cat(context, mainActivity, LOAD_CURRENT);
	}
	
	public static Cat generateCat(Context context, MainActivity mainActivity) {
		return new Cat(context, mainActivity, GENERATE_NEW);
	}
	
	public String getName() {
		return name;
	}
	
	public boolean getBad() {
		return bad;
	}
	
	private void loadCatPrefs() {
		name = sharedPrefs.getString("cat_name", null);
		bad = sharedPrefs.getBoolean("cat_bad", true);
	}
	
	private class GetOnlineCat extends AsyncTask<String, Void, HttpResponse> {
        @Override
        protected HttpResponse doInBackground(String... params) {
        	
        	
            HttpGet request = new HttpGet("http://www.mmarvick.com/amber_app/new_cat.php");
            AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
            try {
                return client.execute(request);
            } catch (IOException e) {
	        	//INSERT AN ALERT HERE
	            
                return null;
            } finally {
            client.close();
            }
        }

        @Override
        protected void onPostExecute(HttpResponse result) {
            //Do something with result
            if (result != null) {

            	
        	    HttpEntity entity = result.getEntity();
        	    String textResult = null;
				try {
					textResult = EntityUtils.toString(entity);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					loadCatPrefs();
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					loadCatPrefs();
					return;
				}
            	
            	JSONObject jObject = null;
            	
				try {
					jObject = new JSONObject(textResult);
					name = jObject.getString("name");
					bad = jObject.getBoolean("bad");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					loadCatPrefs();
					return;
				}
				finishCreation();
            }
        }
	}
	
	private void finishCreation() {
    	SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString("cat_name", name);
		editor.putBoolean("cat_bad", bad);
		
		MediaPlayer meow = null;
		if (bad) 
			meow = MediaPlayer.create(context, R.raw.bad_meow);
		else
			meow = MediaPlayer.create(context, R.raw.good_meow);
		meow.start();
		
		editor.commit();
		mainActivity.onInit();
	}
}
